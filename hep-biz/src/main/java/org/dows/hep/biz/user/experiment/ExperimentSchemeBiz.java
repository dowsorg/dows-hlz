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
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.ExperimentAllotSchemeRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeItemRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeSettingResponse;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentSchemeService;
import org.springframework.stereotype.Service;

import java.util.*;

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

    /**
     * @param
     * @return
     * @说明: 获取实验方案
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public ExperimentSchemeResponse getScheme(String experimentInstanceId, String experimentGroupId, String accountId) {
        if (StrUtil.isBlank(experimentGroupId) || StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        ExperimentSchemeEntity entity = getScheme(experimentInstanceId, experimentGroupId);
        ExperimentSchemeResponse result = BeanUtil.copyProperties(entity, ExperimentSchemeResponse.class);

        List<ExperimentSchemeItemResponse> itemList = experimentSchemeItemBiz.listBySchemeId(entity.getExperimentSchemeId());
        setAuthority(itemList, experimentInstanceId, experimentGroupId, accountId);
        List<ExperimentSchemeItemResponse> itemTreeList = convertList2Tree(itemList);
        result.setItemList(itemTreeList);

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
    public Boolean allotSchemeMembers(ExperimentAllotSchemeRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // update
        handleExperimentSchemeItem(request);

        // handle begin-time
        handleExperimentScheme(request);

        // handle group-status
        handleGroupStatus(request.getExperimentGroupId(), EnumExperimentGroupStatus.SCHEMA);

        return Boolean.TRUE;
    }

    /**
     * @author fhb
     * @description 返回方案设计剩余作答时间和截止时间
     * @date 2023/6/13 17:32
     * @param
     * @return
     */
    public ExperimentSchemeSettingResponse getSchemeDuration(String experimentSchemeId) {
        // 开始时间
        ExperimentSchemeEntity entity = getById(experimentSchemeId);
        Date schemeBeginTime = Optional.of(entity)
                .map(ExperimentSchemeEntity::getBeginTime)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.DATA_NULL));
        // 持续时间
        ExperimentSetting.SchemeSetting schemeSetting = getSchemeSetting(experimentSchemeId);
        Long duration = Optional.of(schemeSetting)
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
        result.setSchemeEndTime(String.valueOf(finalEndTime));
        result.setRemainingTime(String.valueOf(finalEndTime - current));
        return result;
    }

    /**
     * @author fhb
     * @description 获取方案设计设置
     * @date 2023/6/13 17:14
     * @param
     * @return
     */
    public ExperimentSetting.SchemeSetting getSchemeSetting(String experimentSchemeId) {
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

    /**
     * @param
     * @return
     * @author fhb
     * @description 保存
     * @date 2023/6/7 13:50
     */
    public Boolean updateScheme(ExperimentSchemeRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        checkState(request.getExperimentSchemeId());

        List<ExperimentSchemeItemRequest> itemList = request.getItemList();
        return experimentSchemeItemBiz.updateBatch(itemList);
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

        // check scheme
        ExperimentSchemeResponse scheme = getScheme(experimentInstanceId, experimentGroupId, accountId);
        String experimentSchemeId = Optional.of(scheme)
                .map(ExperimentSchemeResponse::getExperimentSchemeId)
                .orElse(null);
        if (StrUtil.isBlank(experimentSchemeId)) {
            throw new BizException(ExperimentESCEnum.SCHEME_NOT_NULL);
        }
        checkState(experimentSchemeId);

        // check auth
        Boolean isCaptain = experimentParticipatorBiz.isCaptain(experimentInstanceId, experimentGroupId, accountId);
        if (!isCaptain) {
            throw new BizException(ExperimentESCEnum.NO_AUTHORITY);
        }

        boolean res1 = experimentSchemeService.lambdaUpdate()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, experimentSchemeId)
                .set(ExperimentSchemeEntity::getState, 1) // 1-已提交
                .update();
        Boolean res2 = handleGroupStatus(experimentGroupId, EnumExperimentGroupStatus.WAIT_SCHEMA);

        return res1 && res2;
    }

    private void checkState(String request) {
        ExperimentSchemeEntity schemeEntity = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, request)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_NOT_NULL));
        Integer state = schemeEntity.getState();
        if (state == 1) {
            throw new BizException(ExperimentESCEnum.SCHEME_HAS_BEEN_SUBMITTED);
        }
    }

    private void setAuthority(List<ExperimentSchemeItemResponse> itemList, String experimentInstanceId, String experimentGroupId, String accountId) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }

        Boolean isCaptain = experimentParticipatorBiz.isCaptain(experimentInstanceId, experimentGroupId, accountId);

        itemList.forEach(item -> {
            if (isCaptain) {
                item.setCanEdit(Boolean.TRUE);
            } else {
                String accountId1 = item.getAccountId();
                if (accountId.equals(accountId1)) {
                    item.setCanEdit(Boolean.TRUE);
                } else {
                    item.setCanEdit(Boolean.FALSE);
                }
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
                    parent.getChildren().add(node);
                }
            }
        }

        return tree;
    }

    private void handleExperimentSchemeItem(ExperimentAllotSchemeRequest request) {
        List<ExperimentAllotSchemeRequest.ParticipatorWithScheme> allotList = request.getAllotList();
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
        experimentSchemeItemBiz.updateAccount(itemList);
    }

    private void handleExperimentScheme(ExperimentAllotSchemeRequest request) {
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
}