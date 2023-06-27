package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.enums.EnumExperimentGroupStatus;
import org.dows.hep.api.event.ExptSchemeStartEvent;
import org.dows.hep.api.event.ExptSchemeSubmittedEvent;
import org.dows.hep.api.event.ExptSchemeSyncEvent;
import org.dows.hep.api.event.source.ExptSchemeStartEventSource;
import org.dows.hep.api.event.source.ExptSchemeSubmittedEventSource;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptSchemeStateEnum;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeAllotRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeItemRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.event.source.ExptSchemeSyncEventSource;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.entity.ExperimentSchemeItemEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentSchemeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:实验:实验方案
 * @date 2023年4月23日 上午9:44:34
 */
@AllArgsConstructor
@Service
public class ExperimentSchemeBiz {
    private final ExperimentSchemeService experimentSchemeService;
    private final ExperimentGroupService experimentGroupService;
    private final ExperimentSchemeItemBiz experimentSchemeItemBiz;
    private final ExperimentParticipatorBiz experimentParticipatorBiz;
    private final ExperimentGroupBiz experimentGroupBiz;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * @param
     * @param needAuth
     * @return
     * @说明: 获取实验方案
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public ExperimentSchemeResponse getScheme(String experimentInstanceId,
                                              String experimentGroupId,
                                              String accountId,
                                              boolean needAuth) {
        if (StrUtil.isBlank(experimentGroupId) || StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        ExperimentSchemeEntity entity = getScheme(experimentInstanceId, experimentGroupId);
        ExperimentSchemeResponse result = BeanUtil.copyProperties(entity, ExperimentSchemeResponse.class);

        List<ExperimentSchemeItemResponse> itemList = experimentSchemeItemBiz.listBySchemeId(entity.getExperimentSchemeId());
        if (needAuth) {
            setAuthority(itemList, experimentInstanceId, experimentGroupId, accountId);
        }
        List<ExperimentSchemeItemResponse> itemTreeList = convertList2Tree(itemList);
        result.setItemList(itemTreeList);

        return result;
    }

    public ExperimentSchemeStateResponse getSchemeState(String experimentInstanceId, String experimentGroupId) {
        if (StrUtil.isBlank(experimentGroupId) || StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        ExperimentSchemeStateResponse result = new ExperimentSchemeStateResponse();
        ExperimentSchemeEntity entity = getScheme(experimentInstanceId, experimentGroupId);
        if (BeanUtil.isEmpty(entity)) {
            result.setStateCode(-1);
            result.setStateDescr("方案设计不存在");
            return result;
        }

        Integer state = entity.getState();
        ExptSchemeStateEnum stateEnum = ExptSchemeStateEnum.getByCode(state);
        if (BeanUtil.isEmpty(stateEnum)) {
            result.setStateCode(-1);
            result.setStateDescr("方案设计状态异常");
            return result;
        }

        result.setStateCode(stateEnum.getCode());
        result.setStateDescr(stateEnum.getName());
        return result;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 分配给方案设计成员
     * @date 2023/6/13 15:27
     */
    @DSTransactional
    public Boolean allotSchemeMembers(ExperimentSchemeAllotRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // update
        handleExperimentSchemeAccount(request);

        // handle begin-time
        handleExperimentSchemeBeginTime(request);

        // handle group-status
        handleGroupStatus(request.getExperimentGroupId(), EnumExperimentGroupStatus.SCHEMA);

        // sync start
        syncStart(request.getExperimentInstanceId(), request.getExperimentGroupId());

        return Boolean.TRUE;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 返回方案设计剩余作答时间和截止时间
     * @date 2023/6/13 17:32
     */
    public ExperimentSchemeSettingResponse getSchemeDuration(String experimentSchemeId) {
        // 开始时间
        ExperimentSchemeEntity entity = getById(experimentSchemeId);
        Date schemeBeginTime = Optional.of(entity)
                .map(ExperimentSchemeEntity::getBeginTime)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.DATA_NULL));
        // 持续时间
        ExperimentSetting.SchemeSetting schemeSetting = getSchemeSetting(experimentSchemeId);
        Long duration = Optional.ofNullable(schemeSetting)
                .map(ExperimentSetting.SchemeSetting::getDuration)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_SETTING_IS_ERROR));
        // 截止时间
        Date schemeEndTime = Optional.of(schemeSetting)
                .map(ExperimentSetting.SchemeSetting::getSchemeEndTime)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_SETTING_IS_ERROR));

        // 相对与开始时间的结束时间
        Date endTime = DateUtil.offsetMinute(schemeBeginTime, duration.intValue());
        long finalEndTime = getNearDate(schemeEndTime, endTime).getTime();

        // check
        ExperimentSchemeSettingResponse result = new ExperimentSchemeSettingResponse();
        long current = DateUtil.current();
        if (current >= finalEndTime) {
            result.setSchemeEndTime(String.valueOf(finalEndTime));
            result.setRemainingTime(String.valueOf(0));
            return result;
        }
        result.setSchemeEndTime(String.valueOf(schemeEndTime));
        result.setRemainingTime(String.valueOf(finalEndTime - current));
        return result;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 保存
     * @date 2023/6/7 13:50
     */
    public Boolean updateScheme(String experimentSchemeItemId, String questionResult, String submitAccountId) {
        if (StrUtil.isBlank(experimentSchemeItemId) || StrUtil.isBlank(questionResult) || StrUtil.isBlank(submitAccountId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        ExperimentSchemeItemEntity schemeItem = getSchemeItem(experimentSchemeItemId);
        String experimentSchemeId = Optional.ofNullable(schemeItem)
                .map(ExperimentSchemeItemEntity::getExperimentSchemeId)
                .orElse("");
        String itemAccountId = Optional.ofNullable(schemeItem)
                .map(ExperimentSchemeItemEntity::getAccountId)
                .orElse("");
        // check
        ExperimentSchemeEntity schemeEntity = cannotOperateAfterSubmit(experimentSchemeId);
        if (!submitAccountId.equals(itemAccountId)) {
            throw new BizException(ExperimentESCEnum.NO_AUTHORITY);
        }
        // update
        boolean res1 = experimentSchemeItemBiz.update(experimentSchemeItemId, questionResult);
        // sync
        syncResult(schemeEntity.getExperimentInstanceId(), schemeEntity.getExperimentGroupId());

        return res1;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 批量保存
     * @date 2023/6/7 13:50
     */
    public Boolean updateSchemeBatch(ExperimentSchemeRequest request, String submitAccountId) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // check
        ExperimentSchemeEntity schemeEntity = cannotOperateAfterSubmit(request.getExperimentSchemeId());
        filterIfNoPermission(request, submitAccountId);
        // update
        List<ExperimentSchemeItemRequest> itemList = request.getItemList();
        boolean res1 = experimentSchemeItemBiz.updateBatch(itemList);
        // sync
        syncResult(schemeEntity.getExperimentInstanceId(), schemeEntity.getExperimentGroupId());

        return res1;
    }

    /**
     * @param
     * @return
     * @说明: 提交
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    @DSTransactional
    public Boolean submitScheme(String experimentInstanceId, String experimentGroupId, String accountId) {
        if (StrUtil.isBlank(experimentInstanceId) || StrUtil.isBlank(experimentGroupId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // check submit status
        String experimentSchemeId = cannotOperateAfterSubmit(experimentInstanceId, experimentGroupId);

        // check auth
        checkIsCaptain(experimentInstanceId, experimentGroupId, accountId);

        boolean res1 = experimentSchemeService.lambdaUpdate()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, experimentSchemeId)
                .set(ExperimentSchemeEntity::getState, 1) // 1-已提交
                .update();
        boolean res2 = handleGroupStatus(experimentGroupId, EnumExperimentGroupStatus.WAIT_SCHEMA);

        // sync submitted
        syncSubmitted(experimentInstanceId, experimentGroupId);

        return res1 && res2;
    }

    private String cannotOperateAfterSubmit(String experimentInstanceId, String experimentGroupId) {
        ExperimentSchemeEntity entity = getScheme(experimentInstanceId, experimentGroupId);
        if (BeanUtil.isEmpty(entity)) {
            throw new BizException(ExperimentESCEnum.SCHEME_NOT_NULL);
        }
        Integer state = entity.getState();
        if (Objects.equals(state, ExptSchemeStateEnum.SUBMITTED.getCode())) {
            throw new BizException(ExperimentESCEnum.SCHEME_HAS_BEEN_SUBMITTED);
        }
        return entity.getExperimentSchemeId();
    }

    private ExperimentSchemeEntity cannotOperateAfterSubmit(String experimentSchemeId) {
        ExperimentSchemeEntity entity = getById(experimentSchemeId);
        if (BeanUtil.isEmpty(entity)) {
            throw new BizException(ExperimentESCEnum.SCHEME_NOT_NULL);
        }
        Integer state = entity.getState();
        if (Objects.equals(state, ExptSchemeStateEnum.SUBMITTED.getCode())) {
            throw new BizException(ExperimentESCEnum.SCHEME_HAS_BEEN_SUBMITTED);
        }
        return entity;
    }

    private void checkIsCaptain(String experimentInstanceId, String experimentGroupId, String accountId) {
        Boolean isCaptain = experimentParticipatorBiz.isCaptain(experimentInstanceId, experimentGroupId, accountId);
        if (!isCaptain) {
            throw new BizException(ExperimentESCEnum.NO_AUTHORITY);
        }
    }

    private void filterIfNoPermission(ExperimentSchemeRequest request, String submitAccountId) {
        String experimentSchemeId = request.getExperimentSchemeId();
        List<ExperimentSchemeItemRequest> itemList = request.getItemList();

        // oneLevelAndSelfList
        List<ExperimentSchemeItemResponse> itemResponseList = experimentSchemeItemBiz.listBySchemeId(experimentSchemeId);
        if (CollUtil.isEmpty(itemResponseList)) {
            throw new BizException(ExperimentESCEnum.SCHEME_NOT_NULL);
        }
        List<ExperimentSchemeItemResponse> oneLevelAndSelfList = itemResponseList.stream()
                .filter(item -> {
                    boolean equals1 = "0".equals(item.getExperimentSchemeItemPid());
                    boolean equals2 = submitAccountId.equals(item.getAccountId());
                    return equals1 && equals2;
                }).toList();
        if (CollUtil.isEmpty(oneLevelAndSelfList)) {
            itemList = new ArrayList<>();
        }
        Map<String, String> collect = oneLevelAndSelfList.stream()
                .collect(Collectors.toMap(ExperimentSchemeItemResponse::getExperimentSchemeItemId, ExperimentSchemeItemResponse::getExperimentSchemeItemId));


        // set itemList
        List<ExperimentSchemeItemRequest> finalItemList = itemList.stream().filter(item -> collect.containsKey(item.getExperimentSchemeItemId())).toList();
        request.setItemList(finalItemList);
    }

    private void setAuthority(List<ExperimentSchemeItemResponse> itemList, String experimentInstanceId, String experimentGroupId, String accountId) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }

//        Boolean isCaptain = experimentParticipatorBiz.isCaptain(experimentInstanceId, experimentGroupId, accountId);

        itemList.forEach(item -> {
            String accountId1 = item.getAccountId();
            if (accountId.equals(accountId1)) {
                item.setCanEdit(Boolean.TRUE);
            } else {
                item.setCanEdit(Boolean.FALSE);
            }
        });
    }

    private List<ExperimentSchemeItemResponse> convertList2Tree(List<ExperimentSchemeItemResponse> nodes) {
        Map<String, ExperimentSchemeItemResponse> nodeMap = new HashMap<>();

        // 构建节点映射，方便根据id查找节点
        for (ExperimentSchemeItemResponse node : nodes) {
            nodeMap.put(node.getExperimentSchemeItemId(), node);
        }

        List<ExperimentSchemeItemResponse> tree = new ArrayList<>();

        // 遍历节点列表，将每个节点放入对应父节点的children中
        for (ExperimentSchemeItemResponse node : nodes) {
            String parentId = node.getExperimentSchemeItemPid();
            if ("0".equals(parentId)) {
                // 根节点
                tree.add(node);
            } else {
                ExperimentSchemeItemResponse parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.addChild(node);
                }
            }
        }

        return tree;
    }

    private void handleExperimentSchemeAccount(ExperimentSchemeAllotRequest request) {
        List<ExperimentSchemeAllotRequest.ParticipatorWithScheme> allotList = request.getAllotList();
        List<ExperimentSchemeItemRequest> itemList = new ArrayList<>();
        allotList.forEach(allotScheme -> {
            String accountId = allotScheme.getAccountId();
            List<String> experimentSchemeIds = allotScheme.getExperimentSchemeIds();
            if (CollUtil.isNotEmpty(experimentSchemeIds)) {
                experimentSchemeIds.forEach(experimentSchemeItemId -> {
                    ExperimentSchemeItemRequest itemRequest = ExperimentSchemeItemRequest.builder()
                            .experimentSchemeItemId(experimentSchemeItemId)
                            .accountId(accountId)
                            .build();
                    itemList.add(itemRequest);
                });
            }
        });
        experimentSchemeItemBiz.setAccountIdBatch(itemList);
    }

    private void handleExperimentSchemeBeginTime(ExperimentSchemeAllotRequest request) {
        String experimentInstanceId = request.getExperimentInstanceId();
        String experimentGroupId = request.getExperimentGroupId();
        ExperimentSchemeEntity scheme = getScheme(experimentInstanceId, experimentGroupId);
        if (BeanUtil.isEmpty(scheme)) {
            throw new BizException(ExperimentESCEnum.DATA_NULL);
        }

        ExperimentSchemeEntity result = ExperimentSchemeEntity.builder()
                .beginTime(new Date())
                .id(scheme.getId())
                .build();
        experimentSchemeService.updateById(result);
    }

    private Boolean handleGroupStatus(String experimentGroupId, EnumExperimentGroupStatus groupStatus) {
        LambdaUpdateWrapper<ExperimentGroupEntity> updateWrapper = new LambdaUpdateWrapper<ExperimentGroupEntity>()
                .eq(ExperimentGroupEntity::getExperimentGroupId, experimentGroupId)
                .set(ExperimentGroupEntity::getGroupState, groupStatus.getCode());
        return experimentGroupService.update(updateWrapper);
    }

    private ExperimentSchemeEntity getScheme(String experimentInstanceId, String experimentGroupId) {
        return experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentSchemeEntity::getExperimentGroupId, experimentGroupId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_NOT_NULL));
    }

    private ExperimentSchemeEntity getById(String experimentSchemeId) {
        return experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, experimentSchemeId)
                .oneOpt()
                .orElse(null);
    }

    private static Date getNearDate(Date schemeEndTime, Date endTime) {
        return DateUtil.compare(schemeEndTime, endTime) > 0 ? endTime : schemeEndTime;
    }

    private ExperimentSetting.SchemeSetting getSchemeSetting(String experimentSchemeId) {
        if (StrUtil.isBlank(experimentSchemeId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        ExperimentSchemeEntity entity = getById(experimentSchemeId);
        String schemeSetting = Optional.ofNullable(entity)
                .map(ExperimentSchemeEntity::getSchemeSetting)
                .orElse(null);
        if (StrUtil.isBlank(schemeSetting)) {
            return null;
        }
        return JSONUtil.toBean(schemeSetting, ExperimentSetting.SchemeSetting.class);
    }

    @NotNull
    private List<String> listGroupAccountId(String experimentInstanceId, String experimentGroupId) {
        List<ExperimentParticipatorResponse> groupMembers = experimentGroupBiz.listGroupMembers(experimentGroupId, experimentInstanceId);
        return groupMembers.stream()
                .map(ExperimentParticipatorResponse::getAccountId)
                .toList();
    }

    private void syncStart(String experimentInstanceId, String experimentGroupId) {
        List<String> accountIds = listGroupAccountId(experimentInstanceId, experimentGroupId);
        applicationEventPublisher.publishEvent(new ExptSchemeStartEvent(
                ExptSchemeStartEventSource.builder()
                        .accountIds(accountIds)
                        .build()
        ));
    }

    private void syncResult(String experimentInstanceId, String experimentGroupId) {
        List<String> accountIds = listGroupAccountId(experimentInstanceId, experimentGroupId);
        ExperimentSchemeResponse scheme = getScheme(experimentInstanceId, experimentGroupId, null, false);
        applicationEventPublisher.publishEvent(new ExptSchemeSyncEvent(
                ExptSchemeSyncEventSource.builder()
                        .accountIds(accountIds)
                        .experimentSchemeResponse(scheme)
                        .build()));
    }

    private void syncSubmitted(String experimentInstanceId, String experimentGroupId) {
        List<String> accountIds = listGroupAccountId(experimentInstanceId, experimentGroupId);
        applicationEventPublisher.publishEvent(new ExptSchemeSubmittedEvent(
                ExptSchemeSubmittedEventSource.builder()
                        .accountIds(accountIds)
                        .build()
        ));
    }

    private ExperimentSchemeItemEntity getSchemeItem(String experimentSchemeItemId) {
        return experimentSchemeItemBiz.getById(experimentSchemeItemId);
    }
}