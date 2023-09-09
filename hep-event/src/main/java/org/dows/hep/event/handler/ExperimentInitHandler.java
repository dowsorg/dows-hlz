package org.dows.hep.event.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.base.evaluate.EvaluateEnabledEnum;
import org.dows.hep.api.base.indicator.request.ExperimentRsCalculateAndCreateReportHealthScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCopyCrowdsAndRiskModelRequestRs;
import org.dows.hep.api.base.indicator.request.RsCopyIndicatorFuncRequestRs;
import org.dows.hep.api.base.indicator.request.RsCopyPersonIndicatorRequestRs;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.exception.ExperimentInitHanlderException;
import org.dows.hep.api.tenant.casus.response.CaseAccountGroupResponse;
import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.api.tenant.experiment.request.ExperimentGroupSettingRequest;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.organization.request.CaseOrgRequest;
import org.dows.hep.api.user.organization.response.CaseOrgResponse;
import org.dows.hep.biz.base.indicator.RsCopyBiz;
import org.dows.hep.biz.base.indicator.RsExperimentCalculateBiz;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.biz.eval.EvalHealthIndexBiz;
import org.dows.hep.biz.event.EventScheduler;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.biz.request.ExperimentTaskParamsRequest;
import org.dows.hep.biz.snapshot.SnapshotManager;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.biz.task.ExperimentBeginTask;
import org.dows.hep.biz.task.ExptSchemeExpireTask;
import org.dows.hep.biz.task.handler.ExperimentBeginTaskHandler;
import org.dows.hep.biz.tenant.experiment.ExperimentCaseInfoManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentQuestionnaireManageBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentSchemeManageBiz;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实验初始化
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentInitHandler extends AbstractEventHandler implements EventHandler<ExperimentGroupSettingRequest> {
    private final ExperimentCaseInfoManageBiz experimentCaseInfoManageBiz;
    private final ExperimentSchemeManageBiz experimentSchemeManageBiz;
    private final ExperimentQuestionnaireManageBiz experimentQuestionnaireManageBiz;
    private final ExperimentSchemeBiz experimentSchemeBiz;
    private final OrgBiz orgBiz;
    private final ExperimentManageBiz experimentManageBiz;
    //todo 记得优化@jx
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;

    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验计时器
    private final ExperimentTimerService experimentTimerService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ExperimentTaskScheduleService experimentTaskScheduleService;

    private final RsCopyBiz rsCopyBiz;

    private final ExperimentSettingService experimentSettingService;
    private final RsExperimentCalculateBiz rsExperimentCalculateBiz;

    private final EvalHealthIndexBiz evalHealthIndexBiz;

    private final ExperimentBeginTaskHandler experimentBeginTaskHandler;

    @Override
    public void exec(ExperimentGroupSettingRequest request) throws ExecutionException, InterruptedException {
        CompletableFuture cf= CompletableFuture.runAsync(()->coreExec(request));
        cf.get();
    }
    @SneakyThrows
    public void coreExec(ExperimentGroupSettingRequest request) {
        String experimentInstanceId = request.getExperimentInstanceId();
        String caseInstanceId = request.getCaseInstanceId();
        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.error("实验id:{} 在数据库不存在", experimentInstanceId);
                    throw new ExperimentInitHanlderException(String.format("初始化实验前端传过来的id:%s 对应的实验不存在", experimentInstanceId));
                });
        String appId = experimentInstanceEntity.getAppId();
        List<ExperimentSettingEntity> experimentSettingEntityList = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        AtomicReference<ExperimentSetting.SandSetting> sandSettingAtomicReference = new AtomicReference<>();
        AtomicReference<ExperimentSetting.SchemeSetting> schemeSettingAtomicReference = new AtomicReference<>();
        AtomicBoolean hasSchemeSettingAtomicBoolean  = new AtomicBoolean(Boolean.FALSE);
        AtomicBoolean hasSandSettingAtomicBoolean  = new AtomicBoolean(Boolean.FALSE);
        experimentSettingEntityList.forEach(experimentSettingEntity -> {
            if (StringUtils.equals(ExperimentSetting.SchemeSetting.class.getName(), experimentSettingEntity.getConfigKey())) {
                hasSchemeSettingAtomicBoolean.set(Boolean.TRUE);
                schemeSettingAtomicReference.set(JSONUtil.toBean(experimentSettingEntity.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class));
            } else if (StringUtils.equals(ExperimentSetting.SandSetting.class.getName(), experimentSettingEntity.getConfigKey())) {
                hasSandSettingAtomicBoolean.set(Boolean.TRUE);
                sandSettingAtomicReference.set(JSONUtil.toBean(experimentSettingEntity.getConfigJsonVals(), ExperimentSetting.SandSetting.class));
            }
        });
        // 设置实验开始定时器
        setExperimentBeginTimerTask(request);
        // 初始化实验 `设置小组` 个数
        createGroupEvent(request);
        // 初始化实验 `复制机构和人物`
        copyExperimentPersonAndOrgEvent(request);
        // 初始化实验 `社区基本信息`
        experimentCaseInfoManageBiz.preHandleCaseInfo(experimentInstanceId, caseInstanceId);
        // 初始化实验 `方案设计` 数据
        experimentSchemeManageBiz.preHandleExperimentScheme(experimentInstanceId, caseInstanceId);
        // 初始化实验 `知识答题` 数据
        experimentQuestionnaireManageBiz.preHandleExperimentQuestionnaire(experimentInstanceId, caseInstanceId);
        // 设置方案设计截止时间提交定时器
        if (hasSchemeSettingAtomicBoolean.get()) {
            setExptSchemeExpireTask(schemeSettingAtomicReference, request);
        }
        /* runsix:初始化实验 '复制人物指标以及人物指标的公式到实验' */
        if (hasSandSettingAtomicBoolean.get()) {
            ExperimentSetting.SandSetting sandSetting = sandSettingAtomicReference.get();
            Integer periods = sandSetting.getPeriods();
            rsCopyBiz.rsCopyPersonIndicator(RsCopyPersonIndicatorRequestRs
                    .builder()
                    .appId(appId)
                    .experimentInstanceId(experimentInstanceId)
                    .caseInstanceId(caseInstanceId)
                    .periods(periods)
                    .build());
            /* runsix:初始化实验 '复制人群类型以及死亡原因以及公式到实验' */
            rsCopyBiz.rsCopyCrowdsAndRiskModel(RsCopyCrowdsAndRiskModelRequestRs
                    .builder()
                    .appId(appId)
                    .experimentInstanceId(experimentInstanceId)
                    .build());
            /* runsix:初始化实验 '复制功能点到实验' */
            rsCopyBiz.rsCopyIndicatorFunc(RsCopyIndicatorFuncRequestRs
                    .builder()
                    .appId(appId)
                    .experimentInstanceId(experimentInstanceId)
                    .caseInstanceId(caseInstanceId)
                    .build());
            /* runsix:复制实验，拿到第0期的数据 */
            //rsExperimentCalculateBiz.experimentRsCalculateAndCreateReportHealthScore(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs
            evalHealthIndexBiz.evalPersonHealthIndexOld(ExperimentRsCalculateAndCreateReportHealthScoreRequestRs
                    .builder()
                    .appId(appId)
                    .experimentId(experimentInstanceId)
                    .periods(0)
                    .funcType(EnumEvalFuncType.START)
                    .build());
        }
        //复制操作指标和突发事件
        SnapshotManager.Instance().write( new SnapshotRequest(appId,experimentInstanceId), true);
        if(ConfigExperimentFlow.SWITCH2SysEvent){
            //启用新流程
            EventScheduler.Instance().scheduleSysEvent(appId, experimentInstanceId, 1);
        }
    }


    private void setExptSchemeExpireTask(AtomicReference<ExperimentSetting.SchemeSetting> schemeSettingAtomicReference, ExperimentGroupSettingRequest request) {
        if(ConfigExperimentFlow.SWITCH2SysEvent){
            //启用新流程
            return;
        }

        //保存任务进计时器表，防止重启后服务挂了，一个任务每个实验每一期只能有一条数据
        ExperimentSetting.SchemeSetting schemeSetting = schemeSettingAtomicReference.get();
        Date schemeEndTime = schemeSetting.getSchemeEndTime();

        ExperimentTaskScheduleEntity entity = ExperimentTaskScheduleEntity.builder()
                .experimentTaskTimerId(idGenerator.nextIdStr())
                .experimentInstanceId(request.getExperimentInstanceId())
                .taskBeanCode(EnumExperimentTask.exptSchemeExpireTask.getDesc())
                .taskParams(JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                        .experimentInstanceId(request.getExperimentInstanceId())
                        .build()))
                .appId(request.getAppId())
                .executeTime(schemeEndTime)
                .executed(false)
                .build();
        experimentTaskScheduleService.save(entity);

        //执行定时任务
        ExptSchemeExpireTask exptSchemeExpireTask = new ExptSchemeExpireTask(experimentTaskScheduleService, experimentSchemeBiz, request.getExperimentInstanceId());
        taskScheduler.schedule(exptSchemeExpireTask, schemeEndTime);
    }



    /**
     * 实验开始定时器
     *
     * @param experimentGroupSettingRequest
     */
    public void setExperimentBeginTimerTask(ExperimentGroupSettingRequest experimentGroupSettingRequest) {
        if(ConfigExperimentFlow.SWITCH2SysEvent){
            //启用新流程
            return;
        }

        //保存任务进计时器表，防止重启后服务挂了，一个任务每个实验每一期只能有一条数据
        ExperimentTaskScheduleEntity beginEntity = new ExperimentTaskScheduleEntity();
        ExperimentTaskScheduleEntity beginTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentBeginTask.getDesc())
                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentGroupSettingRequest.getExperimentInstanceId())
                .isNull(ExperimentTaskScheduleEntity::getPeriods)
                .one();
        if (beginTaskScheduleEntity != null && !ReflectUtil.isObjectNull(beginTaskScheduleEntity)) {
            BeanUtil.copyProperties(beginTaskScheduleEntity, beginEntity);
            beginEntity.setExecuteTime(experimentGroupSettingRequest.getStartTime());
            beginEntity.setTaskParams(JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                    .experimentInstanceId(experimentGroupSettingRequest.getExperimentInstanceId())
                    .build()));
            beginEntity.setExecuted(false);
        } else {
            beginEntity = ExperimentTaskScheduleEntity.builder()
                            .experimentTaskTimerId(idGenerator.nextIdStr())
                            .experimentInstanceId(experimentGroupSettingRequest.getExperimentInstanceId())
                            .taskBeanCode(EnumExperimentTask.experimentBeginTask.getDesc())
                            .taskParams(JSON.toJSONString(ExperimentTaskParamsRequest.builder()
                            .experimentInstanceId(experimentGroupSettingRequest.getExperimentInstanceId())
                            .build()))
                            .appId(experimentGroupSettingRequest.getAppId())
                            .executeTime(experimentGroupSettingRequest.getStartTime())
                            .executed(false)
                            .build();
        }
        experimentTaskScheduleService.saveOrUpdate(beginEntity);

        //执行定时任务
        /*ExperimentBeginTask experimentBeginTask = new ExperimentBeginTask(
                experimentInstanceService, experimentParticipatorService, experimentTimerService, applicationEventPublisher,
                experimentTaskScheduleService, experimentGroupSettingRequest.getExperimentInstanceId());*/
        ExperimentBeginTask experimentBeginTask =
                new ExperimentBeginTask(experimentGroupSettingRequest.getExperimentInstanceId(), experimentBeginTaskHandler);
        /**
         * 设定定时任务
         * todo 设定一个TimeTask,通过timer到时间执行一次，考虑重启情况，写数据库，针对出现的情况，更具时间重新schedule,先用事件处理，后期优化
         */
        taskScheduler.schedule(experimentBeginTask, experimentGroupSettingRequest.getStartTime());
    }

    /**
     * todo 如果实验重启，这里在其他地方就获取不到，需要调整
     *
     * @param experimentGroupSettingRequest
     */
    public void createGroupEvent(ExperimentGroupSettingRequest experimentGroupSettingRequest) {
        ExperimentContext experimentContext = new ExperimentContext();
        experimentContext.setExperimentId(experimentGroupSettingRequest.getExperimentInstanceId());
        experimentContext.setExperimentName(experimentGroupSettingRequest.getExperimentName());
        experimentContext.setState(EnumExperimentState.UNBEGIN);
        //设置小组个数
        experimentContext.setGroupCount(experimentGroupSettingRequest.getGroupSettings().size());
        ExperimentContext.set(experimentContext);
    }

    /**
     * copy 实验人物计事件
     */
    public void copyExperimentPersonAndOrgEvent(ExperimentGroupSettingRequest experimentGroupSettingRequest) {
        // 复制人物与机构到实验中
        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentGroupSettingRequest.getExperimentInstanceId())
                .eq(ExperimentInstanceEntity::getDeleted, false)
                .one();
        IPage<CaseOrgResponse> caseOrgResponseIPage = orgBiz.listOrgnization(CaseOrgRequest.builder().pageNo(1).pageSize(10)
                .caseInstanceId(experimentInstanceEntity.getCaseInstanceId())
                .status(EvaluateEnabledEnum.ENABLED.getCode())
                .build());
        List<CaseOrgResponse> responseList = caseOrgResponseIPage.getRecords();
        List<CreateExperimentRequest> requestList = new ArrayList<>();
        if (responseList != null && responseList.size() > 0) {
            responseList.forEach(response -> {
                //1、通过案例机构ID找到机构ID下面的人物
                Page<CaseAccountGroupResponse> groupResponseIPage = orgBiz.listPerson(AccountGroupRequest.builder()
                        .status(EvaluateEnabledEnum.ENABLED.getCode())
                        .appId(experimentGroupSettingRequest.getAppId())
                        .pageNo(1)
                        .pageSize(999)
                        .build(), response.getCaseOrgId());
                List<CaseAccountGroupResponse> accountGroupResponses = groupResponseIPage.getRecords();
                List<AccountInstanceResponse> instanceResponses = new ArrayList<>();
                if (accountGroupResponses != null && accountGroupResponses.size() > 0) {
                    accountGroupResponses.forEach(accountGroup -> {
                        AccountInstanceResponse instanceResponse = AccountInstanceResponse.builder()
                                .accountId(accountGroup.getAccountId())
                                .build();
                        instanceResponses.add(instanceResponse);
                    });
                }
                CreateExperimentRequest request = CreateExperimentRequest.builder()
                        .experimentInstanceId(experimentGroupSettingRequest.getExperimentInstanceId())
                        .caseOrgId(response.getCaseOrgId())
                        .appId(experimentGroupSettingRequest.getAppId())
                        .teachers(instanceResponses)
                        .build();
                requestList.add(request);
            });
        }
        experimentManageBiz.copyExperimentPersonAndOrg(requestList);
    }
}
