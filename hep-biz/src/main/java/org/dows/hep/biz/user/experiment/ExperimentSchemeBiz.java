package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.enums.EnumExperimentGroupStatus;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.enums.EnumParticipatorType;
import org.dows.hep.api.event.ExptSchemeStartEvent;
import org.dows.hep.api.event.ExptSchemeSubmittedEvent;
import org.dows.hep.api.event.ExptSchemeSyncEvent;
import org.dows.hep.api.event.source.ExptSchemeStartEventSource;
import org.dows.hep.api.event.source.ExptSchemeSubmittedEventSource;
import org.dows.hep.api.event.source.ExptSchemeSyncEventSource;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptReviewStateEnum;
import org.dows.hep.api.user.experiment.ExptSchemeStateEnum;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeAllotRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeItemRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeSubmitRequest;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.event.sysevent.SysEventInvoker;
import org.dows.hep.biz.request.ExperimentTaskParamsRequest;
import org.dows.hep.biz.schedule.TaskScheduler;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:实验:实验方案
 * @date 2023年4月23日 上午9:44:34
 */
@Slf4j
@AllArgsConstructor
@Service
public class ExperimentSchemeBiz {
    private final IdGenerator idGenerator;
    private final ExperimentSchemeService experimentSchemeService;
    private final ExperimentSchemeScoreService experimentSchemeScoreService;
    private final ExperimentGroupService experimentGroupService;
    private final ExperimentSettingService experimentSettingService;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentGroupBiz experimentGroupBiz;
    private final ExperimentSchemeItemBiz experimentSchemeItemBiz;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentTaskScheduleService experimentTaskScheduleService;
    private final TaskScheduler taskScheduler;


    /**
     * @param experimentInstanceId - 实验实例ID
     * @param experimentGroupId    - 实验小组ID
     * @param accountId            - 账号ID
     * @param needAuth             - 是否需要添加权限标记返回
     * @return org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse
     * @author fhb
     * @description 获取方案设计
     * @date 2023/7/27 10:46
     */
    public ExperimentSchemeResponse getScheme(String experimentInstanceId,
                                              String experimentGroupId,
                                              String accountId,
                                              boolean needAuth) {
        if (StrUtil.isBlank(experimentGroupId) || StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // scheme
        ExperimentSchemeEntity entity = getScheme(experimentInstanceId, experimentGroupId);
        ExperimentSchemeResponse result = BeanUtil.copyProperties(entity, ExperimentSchemeResponse.class);

        // scheme-item
        List<ExperimentSchemeItemResponse> itemList = experimentSchemeItemBiz.listBySchemeId(entity.getExperimentSchemeId());
        if (needAuth) {
            setAuthority(itemList, experimentInstanceId, experimentGroupId, accountId);
        }
        List<ExperimentSchemeItemResponse> itemTreeList = convertList2Tree(itemList);
        result.setItemList(itemTreeList);

        return result;
    }

    /**
     * @param experimentInstanceId - 实验实例ID
     * @return org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse
     * @author fhb
     * @description 列出方案设计
     * @date 2023/7/9 15:07
     */
    public List<ExperimentSchemeResponse> listScheme(String experimentInstanceId) {
        if (StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        List<ExperimentSchemeResponse> result = new ArrayList<>();

        // list scheme
        List<ExperimentSchemeEntity> schemeList = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        if (CollUtil.isEmpty(schemeList)) {
            return result;
        }

        // list scheme-item
        List<String> schemeIdList = schemeList.stream()
                .map(ExperimentSchemeEntity::getExperimentSchemeId)
                .toList();
        List<ExperimentSchemeItemResponse> schemeItemResponseList = experimentSchemeItemBiz.listBySchemeIds(schemeIdList);
        if (CollUtil.isEmpty(schemeItemResponseList)) {
            return result;
        }

        // result
        List<ExperimentSchemeResponse> schemeResponseList = BeanUtil.copyToList(schemeList, ExperimentSchemeResponse.class);
        Map<String, List<ExperimentSchemeItemResponse>> collect = schemeItemResponseList.stream().collect(Collectors.groupingBy(ExperimentSchemeItemResponse::getExperimentSchemeId));
        schemeResponseList.forEach(schemeResponse -> {
            String experimentSchemeId = schemeResponse.getExperimentSchemeId();
            List<ExperimentSchemeItemResponse> schemeItem = collect.get(experimentSchemeId);
            List<ExperimentSchemeItemResponse> itemTreeList = convertList2Tree(schemeItem);
            schemeResponse.setItemList(itemTreeList);
            result.add(schemeResponse);
        });

        return result;
    }

    /**
     * @param experimentInstanceId - 实验实例ID
     * @param experimentGroupId    - 实验小组ID
     * @return org.dows.hep.api.user.experiment.response.ExperimentSchemeStateResponse
     * @author fhb
     * @description 获取方案设计状态
     * @date 2023/7/6 17:51
     */
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

        // handle expt-status
        handleExptStatus(request.getExperimentInstanceId(), EnumExperimentState.ONGOING);

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

        long remainingTime = finalEndTime - current;
        result.setSchemeEndTime(String.valueOf(schemeEndTime));
        result.setRemainingTime(String.valueOf(remainingTime));
        result.setSchemeEndTimestamp(schemeEndTime.getTime());
        result.setRemainingTimestamp(remainingTime);
        return result;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 保存
     * @date 2023/6/7 13:50
     */
    @DSTransactional
    public Boolean updateScheme(String experimentSchemeItemId, String questionResult, String submitAccountId, String videoAnswer) {
        if (StrUtil.isBlank(experimentSchemeItemId) || StrUtil.isBlank(questionResult) || StrUtil.isBlank(submitAccountId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }
        if (questionResult.length() > 10000) {
            throw new BizException("提交方案设计时，答案长度应为0-10000");
        }

        ExperimentSchemeItemEntity schemeItem = getSchemeItem(experimentSchemeItemId);
        String experimentSchemeId = Optional.ofNullable(schemeItem)
                .map(ExperimentSchemeItemEntity::getExperimentSchemeId)
                .orElse("");
        String itemAccountId = Optional.ofNullable(schemeItem)
                .map(ExperimentSchemeItemEntity::getAccountId)
                .orElse("");
        // check
        ExperimentSchemeEntity schemeEntity = cannotUpdateAfterSubmit(experimentSchemeId);
        cannotUpdateIf0UsableTime(schemeEntity.getExperimentSchemeId());
        if (!submitAccountId.equals(itemAccountId)) {
            throw new BizException(ExperimentESCEnum.NO_AUTHORITY);
        }
        // update schemeVideo
        if (StrUtil.isNotBlank(videoAnswer)) {
            experimentSchemeService.lambdaUpdate()
                    .set(ExperimentSchemeEntity::getVideoAnswer, videoAnswer)
                    .eq(ExperimentSchemeEntity::getExperimentSchemeId, experimentSchemeId)
                    .update();
        }
        // update item
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
    @DSTransactional
    public Boolean updateSchemeBatch(ExperimentSchemeRequest request, String submitAccountId) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // check
        checkRequestQuestionResultLength(request);
        ExperimentSchemeEntity schemeEntity = cannotUpdateAfterSubmit(request.getExperimentSchemeId());
        cannotUpdateIf0UsableTime(schemeEntity.getExperimentSchemeId());
        filterIfNoPermission(request, submitAccountId);
        // update schemeVideo
        String videoAnswer = request.getVideoAnswer();
        if (StrUtil.isNotBlank(videoAnswer)) {
            experimentSchemeService.lambdaUpdate()
                    .set(ExperimentSchemeEntity::getVideoAnswer, videoAnswer)
                    .eq(ExperimentSchemeEntity::getExperimentSchemeId, request.getExperimentSchemeId())
                    .update();
        }
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
    public Boolean submitScheme(ExperimentSchemeSubmitRequest submitRequest) {
        Assert.notNull(submitRequest, "提交方案设计请求参数不能为空");
        String experimentInstanceId = submitRequest.getExperimentInstanceId();
        String experimentGroupId = submitRequest.getExperimentGroupId();
        String accountId = submitRequest.getAccountId();

        // check submit status
        String experimentSchemeId = cannotSubmitAfterSubmit(experimentInstanceId, experimentGroupId);

        // check auth
        checkIsCaptain(experimentInstanceId, experimentGroupId, accountId);

        // submit
        return submitScheme(experimentInstanceId, experimentGroupId, experimentSchemeId);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId    - 实验小组ID
     * @return java.util.concurrent.CompletableFuture<java.lang.Void>
     * @author
     * @description
     * @date 2023/7/27 15:19
     */
    public CompletableFuture<Void> setAutoSubmitTaskWhen0RemainingTime(String exptInstanceId, String exptGroupId) {
        return CompletableFuture.runAsync(() -> {
            ExperimentSchemeEntity schemeEntity = getScheme(exptInstanceId, exptGroupId);
            if (BeanUtil.isEmpty(schemeEntity)) {
                throw new BizException("实验方案设计设置自动提交时，获取方案设计数据异常");
            }

            // 获取方案设计结束的那个时间
            Date beginTime = schemeEntity.getBeginTime();
            ExperimentSchemeSettingResponse schemeDuration = getSchemeDuration(schemeEntity.getExperimentSchemeId());
            long remainingTime = Long.parseLong(schemeDuration.getRemainingTime());
            DateTime endTime = DateUtil.offset(beginTime, DateField.MILLISECOND, (int) remainingTime);

            if (ConfigExperimentFlow.SWITCH2SysEvent) {
                //启用新流程
                SysEventInvoker.Instance().triggeringSchemaGroupEnd(endTime, null, exptInstanceId, exptGroupId);
            } else {
                //保存任务进计时器表，防止重启后服务挂了，一个任务每个实验每一期只能有一条数据
                ExperimentTaskScheduleEntity taskEntity = ExperimentTaskScheduleEntity.builder()
                        .experimentTaskTimerId(idGenerator.nextIdStr())
                        .experimentGroupId(exptGroupId)
                        .experimentInstanceId(exptInstanceId)
                        .taskBeanCode(EnumExperimentTask.exptSchemeFinishTask.getDesc())
                        .taskParams(JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                                .experimentInstanceId(exptInstanceId)
                                .experimentGroupId(exptGroupId)
                                .build()))
                        .appId("3")
                        .executeTime(endTime)
                        .executed(false)
                        .build();
                experimentTaskScheduleService.save(taskEntity);
                //执行定时任务
                //ExptSchemeFinishTask exptSchemeFinishTask = new ExptSchemeFinishTask(experimentTaskScheduleService, this, exptInstanceId, exptGroupId);
                //taskScheduler.schedule(exptSchemeFinishTask, endTime);
            }
        });
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId    - 实验小组ID
     * @author fhb
     * @description 当剩余时间为0时提交
     * @date 2023/7/27 14:39
     */
    public boolean submitWhen0RemainingTime(String exptInstanceId, String exptGroupId) {
        ExperimentSchemeEntity entity = getScheme(exptInstanceId, exptGroupId);
        if (BeanUtil.isEmpty(entity)) {
            throw new BizException("实验方案设计到时自动提交时，获取方案设计数据异常");
        }

        // 自动提交方案设计
        String experimentSchemeId = entity.getExperimentSchemeId();
        return submitScheme(exptInstanceId, exptGroupId, experimentSchemeId);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @return java.lang.Boolean
     * @author fhb
     * @description 方案设计截止时间到了时，批量提交试卷（不再对截止时间校验，调用方校验好）
     * @date 2023/7/26 11:00
     */
    public Boolean submitBatchWhenExpire(String exptInstanceId) {
        if (StrUtil.isBlank(exptInstanceId)) {
            throw new BizException("方案设计截止时间提交时，实验ID数据异常");
        }

        // 更新方案设计状态
        boolean res1 = experimentSchemeService.lambdaUpdate()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, exptInstanceId)
                .set(ExperimentSchemeEntity::getState, ExptSchemeStateEnum.SUBMITTED.getCode()) // 1-已提交
                .update();

        // 更新方案设计评分表状态
        List<ExperimentSchemeEntity> schemeList = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, exptInstanceId)
                .list();
        List<String> schemeIdList = schemeList.stream()
                .map(ExperimentSchemeEntity::getExperimentSchemeId)
                .toList();
        experimentSchemeScoreService.lambdaUpdate()
                .in(ExperimentSchemeScoreEntity::getExperimentSchemeId, schemeIdList)
                .set(ExperimentSchemeScoreEntity::getReviewState, ExptReviewStateEnum.UNREVIEWED.getCode())
                .update();

        // 更新实验小组状态或实验状态
        if (containsSandSetting(exptInstanceId)) {
            handleExptAllGroupStatus(exptInstanceId, EnumExperimentGroupStatus.ASSIGN_DEPARTMENT);
        } else {
            handleExptAllGroupStatus(exptInstanceId, EnumExperimentGroupStatus.WAIT_SCHEMA);
            handleExptStatus(exptInstanceId, EnumExperimentState.FINISH);
            handleExptParticipator(exptInstanceId, EnumExperimentState.FINISH);
        }

        // sync info
        syncInfo(exptInstanceId);

        return res1;
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @return java.util.List<org.dows.hep.api.user.experiment.response.ExptSchemeScoreRankResponse>
     * @author fhb
     * @description 获取实验下方案设计排行榜
     * @date 2023/7/19 13:39
     */
    public List<ExptSchemeScoreRankResponse> listExptSchemeScoreRank(String exptInstanceId) {
        // list expt-scheme
        List<ExperimentSchemeEntity> schemeList = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, exptInstanceId)
                .list();
        if (CollUtil.isEmpty(schemeList)) {
            throw new BizException("获取方案设计排行榜时，实验方案设计数据为空");
        }
        // list expt-group
        List<ExperimentGroupEntity> groupList = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, exptInstanceId)
                .list();
        if (CollUtil.isEmpty(groupList)) {
            throw new BizException("获取方案设计排行榜时，实验小组数据为空");
        }


        List<ExptSchemeScoreRankResponse> result = new ArrayList<>();
        // 方案设计根据评分排序
        List<ExperimentSchemeEntity> schemeSortedList = schemeList.stream().sorted((v1, v2) -> {
            Float v1Score = v1.getScore() == null ? 0.00f : v1.getScore();
            Float v2Score = v2.getScore() == null ? 0.00f : v2.getScore();
            return (int) (v2Score - v1Score);
        }).toList();
        // 小组转为map
        Map<String, ExperimentGroupEntity> groupIdMapEntity = groupList.stream()
                .collect(Collectors.toMap(ExperimentGroupEntity::getExperimentGroupId, item -> item));
        // build result
        schemeSortedList.forEach(scheme -> {
            String experimentGroupId = scheme.getExperimentGroupId();
            Float score = scheme.getScore() == null ? 0.00f : scheme.getScore();
            ExptSchemeScoreRankResponse rank = ExptSchemeScoreRankResponse.builder()
                    .groupId(experimentGroupId)
                    .groupNo(groupIdMapEntity.get(experimentGroupId).getGroupNo())
                    .groupName(groupIdMapEntity.get(experimentGroupId).getGroupName())
                    .score(String.valueOf(score))
                    .build();
            result.add(rank);
        });
        return result;
    }

    private boolean submitScheme(String exptInstanceId, String exptGroupId, String experimentSchemeId) {
        boolean updSchemeRes = experimentSchemeService.lambdaUpdate()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, experimentSchemeId)
                .set(ExperimentSchemeEntity::getState, ExptSchemeStateEnum.SUBMITTED.getCode()) // 1-已提交
                .update();
        if (updSchemeRes) {
            log.info(String.format("提交小组方案设计时，更新方案设计状态成功，实验ID=%s, 小组ID=%s, 方案设计ID=%s", exptInstanceId, exptGroupId, experimentSchemeId));
        } else {
            log.info(String.format("提交小组方案设计时，更新方案设计状态失败，实验ID=%s, 小组ID=%s, 方案设计ID=%s", exptInstanceId, exptGroupId, experimentSchemeId));
        }

        boolean updScoreRes = experimentSchemeScoreService.lambdaUpdate()
                .eq(ExperimentSchemeScoreEntity::getExperimentSchemeId, experimentSchemeId)
                .set(ExperimentSchemeScoreEntity::getReviewState, ExptReviewStateEnum.UNREVIEWED.getCode())
                .update();
        if (updScoreRes) {
            log.info(String.format("提交小组方案设计时，更新方案设计评分表状态成功，实验ID=%s, 小组ID=%s, 方案设计ID=%s", exptInstanceId, exptGroupId, experimentSchemeId));
        } else {
            log.info(String.format("提交小组方案设计时，更新方案设计评分表状态失败，实验ID=%s, 小组ID=%s, 方案设计ID=%s", exptInstanceId, exptGroupId, experimentSchemeId));
        }

        // 处理小组状态
        if (containsSandSetting(exptInstanceId)) {
            handleGroupStatus(exptGroupId, EnumExperimentGroupStatus.ASSIGN_DEPARTMENT);
        } else {
            handleGroupStatus(exptGroupId, EnumExperimentGroupStatus.WAIT_SCHEMA);
        }

        // sync submitted
        syncSubmittedGroupScheme(exptInstanceId, exptGroupId);
        return updSchemeRes && updScoreRes;
    }

    private String cannotSubmitAfterSubmit(String experimentInstanceId, String experimentGroupId) {
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

    private void cannotUpdateIf0UsableTime(String exptSchemeId) {
        ExperimentSchemeSettingResponse schemeDuration = getSchemeDuration(exptSchemeId);
        String remainingTimeStr = Optional.ofNullable(schemeDuration)
                .map(ExperimentSchemeSettingResponse::getRemainingTime)
                .orElse("0");
        long remainingTime = Long.parseLong(remainingTimeStr);
        if (0 == remainingTime) {
            throw new BizException("方案设计实验作答时间已结束");
        }
    }

    private ExperimentSchemeEntity cannotUpdateAfterSubmit(String experimentSchemeId) {
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

    private void checkRequestQuestionResultLength(ExperimentSchemeRequest request) {
        String videoAnswer = request.getVideoAnswer();
        if (StrUtil.isNotBlank(videoAnswer) && videoAnswer.length() > 10000) {
            throw new BizException("保存方案设计时，视频答案长度应为0-10000");
        }

        List<ExperimentSchemeItemRequest> itemList = request.getItemList();
        if (CollUtil.isNotEmpty(itemList)) {
            for (ExperimentSchemeItemRequest item : itemList) {
                checkItemResultLength(item);
            }
        }
    }

    private void checkItemResultLength(ExperimentSchemeItemRequest item) {
        if (item.getQuestionResult() != null && item.getQuestionResult().length() > 10000) {
            throw new BizException("答案长度应为0-10000");
        }
        if (item.getChildren() != null) {
            for (ExperimentSchemeItemRequest child : item.getChildren()) {
                checkItemResultLength(child);
            }
        }
    }

    private void checkIsCaptain(String experimentInstanceId, String experimentGroupId, String accountId) {
        Boolean isCaptain = isCaptain(experimentInstanceId, experimentGroupId, accountId);
        if (!isCaptain) {
            throw new BizException(ExperimentESCEnum.NO_AUTHORITY);
        }
    }

    public Boolean isCaptain(String experimentInstanceId, String experimentGroupId, String accountId) {
        Long count = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentParticipatorEntity::getAccountId, accountId)
                .eq(ExperimentParticipatorEntity::getParticipatorType, EnumParticipatorType.CAPTAIN.getCode())
                .count();
        if (count == null || count == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
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

    private Boolean handleExptAllGroupStatus(String exptInstanceId, EnumExperimentGroupStatus groupStatus) {
        LambdaUpdateWrapper<ExperimentGroupEntity> updateWrapper = new LambdaUpdateWrapper<ExperimentGroupEntity>()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, exptInstanceId)
                .set(ExperimentGroupEntity::getGroupState, groupStatus.getCode());
        return experimentGroupService.update(updateWrapper);
    }

    private Boolean handleExptStatus(String experimentInstanceId, EnumExperimentState enumExperimentState) {
        LambdaUpdateWrapper<ExperimentInstanceEntity> updateWrapper = new LambdaUpdateWrapper<ExperimentInstanceEntity>()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .set(ExperimentInstanceEntity::getState, enumExperimentState.getState());
        return experimentInstanceService.update(updateWrapper);
    }

    private Boolean handleExptParticipator(String exptInstanceId, EnumExperimentState enumExperimentState) {
        LambdaUpdateWrapper<ExperimentParticipatorEntity> wrapper = new LambdaUpdateWrapper<ExperimentParticipatorEntity>()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, exptInstanceId)
                .set(ExperimentParticipatorEntity::getState, enumExperimentState.getState());
        return experimentParticipatorService.update(wrapper);
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

    // todo 后续改为调用 exptSettingBiz
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

    private void syncSubmittedGroupScheme(String experimentInstanceId, String experimentGroupId) {
        List<String> accountIds = listGroupAccountId(experimentInstanceId, experimentGroupId);
        applicationEventPublisher.publishEvent(new ExptSchemeSubmittedEvent(
                ExptSchemeSubmittedEventSource.builder()
                        .accountIds(accountIds)
                        .build()
        ));
    }

    // todo @experimentGroupBiz 提供批量查询方法
    private void syncInfo(String experimentInstanceId) {
        // 获取实验所有小组成员的账号
        List<String> accountIds = new ArrayList<>();
        List<ExperimentGroupResponse> exptGroups = experimentGroupBiz.listGroup(experimentInstanceId);
        if (CollUtil.isEmpty(exptGroups)) {
            return;
        }
        for (ExperimentGroupResponse group : exptGroups) {
            String experimentGroupId = group.getExperimentGroupId();
            List<String> groupAccountIds = listGroupAccountId(experimentInstanceId, experimentGroupId);
            if (CollUtil.isEmpty(groupAccountIds)) {
                continue;
            }
            accountIds.addAll(groupAccountIds);
        }

        // 同步信息
        applicationEventPublisher.publishEvent(new ExptSchemeSubmittedEvent(
                ExptSchemeSubmittedEventSource.builder()
                        .accountIds(accountIds)
                        .build()
        ));
    }

    private ExperimentSchemeItemEntity getSchemeItem(String experimentSchemeItemId) {
        return experimentSchemeItemBiz.getById(experimentSchemeItemId);
    }

    // todo 后续改为使用 exptSettingBiz 调用
    private boolean containsSandSetting(String experimentInstanceId) {
        String sandSetting = "";
        List<ExperimentSettingEntity> experimentSettings = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        for (ExperimentSettingEntity expSetting : experimentSettings) {
            String configKey = expSetting.getConfigKey();
            if (ExperimentSetting.SandSetting.class.getName().equals(configKey)) {
                sandSetting = expSetting.getConfigJsonVals();
            }
        }
        if (StrUtil.isNotBlank(sandSetting)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}