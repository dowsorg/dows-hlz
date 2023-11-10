package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.ExperimentMonitorFollowupCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsExperimentCalculateFuncRequest;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.api.enums.EnumExperimentOrgReportFlag;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.ExperimentIndicatorViewBaseInfoRsException;
import org.dows.hep.api.exception.ExperimentIndicatorViewMonitorFollowupReportRsException;
import org.dows.hep.api.user.experiment.response.ExptOrgFlowReportResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeVO;
import org.dows.hep.biz.dao.OperateFlowDao;
import org.dows.hep.biz.eval.EvalPersonBiz;
import org.dows.hep.biz.eval.QueryPersonBiz;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorViewMonitorFollowupReportRsBiz {
    private final IdGenerator idGenerator;
    private final ExperimentIndicatorViewMonitorFollowupRsService experimentIndicatorViewMonitorFollowupRsService;
    private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
    private final ExperimentIndicatorValRsService experimentIndicatorValRsService;

    private final ExperimentIndicatorViewMonitorFollowupPlanRsService experimentIndicatorViewMonitorFollowupPlanRsService;
    private final ExperimentIndicatorViewMonitorFollowupReportRsService experimentIndicatorViewMonitorFollowupReportRsService;
    private final ApplicationContext applicationContext;
    private final RsExperimentCalculateBiz rsExperimentCalculateBiz;

    private final ExperimentOrgModuleBiz experimentOrgModuleBiz;

    private final OperateFlowDao operateFlowDao;

    private final EvalPersonBiz evalPersonBiz;

    private final QueryPersonBiz queryPersonBiz;



    public static ExperimentIndicatorViewMonitorFollowupPlanRsResponse experimentIndicatorViewMonitorFollowupPlanRs2Response(ExperimentIndicatorViewMonitorFollowupPlanRsEntity experimentIndicatorViewMonitorFollowupPlanRsEntity) {
        if (Objects.isNull(experimentIndicatorViewMonitorFollowupPlanRsEntity)) {
            return null;
        }
        return ExperimentIndicatorViewMonitorFollowupPlanRsResponse
                .builder()
                .experimentIndicatorViewMonitorFollowupPlanId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getExperimentIndicatorViewMonitorFollowupPlanId())
                .experimentId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getExperimentId())
                .appId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getAppId())
                .period(experimentIndicatorViewMonitorFollowupPlanRsEntity.getPeriods())
                .indicatorFuncId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getIndicatorFuncId())
                .experimentPersonId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getExperimentPersonId())
                .operateFlowId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getOperateFlowId())
                .intervalDay(experimentIndicatorViewMonitorFollowupPlanRsEntity.getIntervalDay())
                .experimentIndicatorViewMonitorFollowupId(experimentIndicatorViewMonitorFollowupPlanRsEntity.getExperimentIndicatorViewMonitorFollowupId())
                .deleted(experimentIndicatorViewMonitorFollowupPlanRsEntity.getDeleted())
                .dt(experimentIndicatorViewMonitorFollowupPlanRsEntity.getDt())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void monitorFollowupCheck(ExperimentMonitorFollowupCheckRequestRs experimentMonitorFollowupCheckRequestRs) throws ExecutionException, InterruptedException {
        ExperimentIndicatorViewMonitorFollowupReportRsEntity experimentIndicatorViewMonitorFollowupReportRsEntity = null;
        Integer periods = experimentMonitorFollowupCheckRequestRs.getPeriods();
        String experimentGroupId = experimentMonitorFollowupCheckRequestRs.getExperimentGroupId();
        String experimentOrgId = experimentMonitorFollowupCheckRequestRs.getExperimentOrgId();
        String experimentPersonId = experimentMonitorFollowupCheckRequestRs.getExperimentPersonId();
        String indicatorFuncId = experimentMonitorFollowupCheckRequestRs.getIndicatorFuncId();
        String appId = experimentMonitorFollowupCheckRequestRs.getAppId();
        String experimentId = experimentMonitorFollowupCheckRequestRs.getExperimentId();
        String indicatorViewMonitorFollowupId = experimentMonitorFollowupCheckRequestRs.getIndicatorViewMonitorFollowupId();
        Integer intervalDay = experimentMonitorFollowupCheckRequestRs.getIntervalDay();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(intervalDay, true))
                .throwMessage("请选择随访频率");

        ExptOrgFuncRequest funcRequest = (ExptOrgFuncRequest) new ExptOrgFuncRequest().setIndicatorFuncId(indicatorFuncId)
                .setExperimentInstanceId(experimentId)
                .setExperimentOrgId(experimentOrgId)
                .setExperimentPersonId(experimentPersonId)
                .setExperimentGroupId(experimentGroupId)
                .setPeriods(periods)
                .setAppId(appId);
        ExptRequestValidator exptValidator = ExptRequestValidator.create(funcRequest)
                .checkExperimentOrg();
        final LocalDateTime ldtNow = LocalDateTime.now();
        final Date dateNow = ShareUtil.XDate.localDT2Date(ldtNow);
        ExperimentTimePoint timePoint = exptValidator.getTimePoint(true, ldtNow, true);
        ExptOrgFlowValidator flowValidator = ExptOrgFlowValidator.create(exptValidator)
                .checkOrgFlowRunning(periods);
        final String operateFlowId = flowValidator.getOperateFlowId();


        ExperimentIndicatorViewMonitorFollowupPlanRsEntity experimentIndicatorViewMonitorFollowupPlanRsEntity = experimentIndicatorViewMonitorFollowupPlanRsService.lambdaQuery()
                .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getAppId, appId)
                .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getExperimentId, experimentId)
                .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getPeriods, periods)
                .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getIndicatorFuncId, indicatorFuncId)
                .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getExperimentPersonId, experimentPersonId)
                .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getExperimentIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
                .orderByDesc(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getId)
                .last("limit 1")
                .one();
        if (Objects.isNull(experimentIndicatorViewMonitorFollowupPlanRsEntity)) {
            experimentIndicatorViewMonitorFollowupPlanRsService.saveOrUpdate(ExperimentIndicatorViewMonitorFollowupPlanRsEntity
                    .builder()
                    .experimentIndicatorViewMonitorFollowupPlanId(idGenerator.nextIdStr())
                    .experimentId(experimentId)
                    .appId(appId)
                    .periods(periods)
                    .indicatorFuncId(indicatorFuncId)
                    .experimentPersonId(experimentPersonId)
                    .operateFlowId(operateFlowId)
                    .intervalDay(intervalDay)
                    .experimentIndicatorViewMonitorFollowupId(indicatorViewMonitorFollowupId)
                    .build());
            /**
             * todo 解耦，根据随访计划启动定时调度
             */
            //applicationContext.publishEvent(new FollowupEvent(experimentMonitorFollowupCheckRequestRs));
        }
        /* runsix:监测随访是一个触发计算时间点 */
        try {

            if (ConfigExperimentFlow.SWITCH2EvalCache) {

                evalPersonBiz.evalOrgFunc(RsExperimentCalculateFuncRequest.builder()
                        .appId(appId)
                        .experimentId(experimentId)
                        .periods(periods)
                        .experimentPersonId(experimentPersonId)
                        .funcType(EnumEvalFuncType.FUNCFollowup)
                        .build());
            } else {
                rsExperimentCalculateBiz.experimentReCalculateFunc(RsExperimentCalculateFuncRequest
                        .builder()
                        .appId(appId)
                        .experimentId(experimentId)
                        .periods(periods)
                        .experimentPersonId(experimentPersonId)
                        .build());
            }
        }catch (Exception ex) {
            log.error(String.format("monitorFollowupCheck experimentId:%s personId:%s",
                    exptValidator.getExperimentInstanceId(), exptValidator.getExperimentPersonId()), ex);
            AssertUtil.justThrow(String.format("功能点结算失败：%s",ex.getMessage()),ex);
        }

        ExperimentIndicatorViewMonitorFollowupRsEntity experimentIndicatorViewMonitorFollowupRsEntity = experimentIndicatorViewMonitorFollowupRsService.lambdaQuery()
                .eq(ExperimentIndicatorViewMonitorFollowupRsEntity::getExperimentIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.error("ExperimentIndicatorViewMonitorFollowupReportRsBiz.monitorFollowupCheck param ExperimentMonitorFollowupCheckRequestRs indicatorViewMonitorFollowupId:{} is illegal", indicatorViewMonitorFollowupId);
                    throw new ExperimentIndicatorViewMonitorFollowupReportRsException(EnumESC.VALIDATE_EXCEPTION);
                });
        String caseId = experimentIndicatorViewMonitorFollowupRsEntity.getCaseId();
        String name = experimentIndicatorViewMonitorFollowupRsEntity.getName();
        String ivmfContentNameArray = experimentIndicatorViewMonitorFollowupRsEntity.getIvmfContentNameArray();
        String ivmfContentRefIndicatorInstanceIdArray = experimentIndicatorViewMonitorFollowupRsEntity.getIvmfContentRefIndicatorInstanceIdArray();
        AtomicInteger atomicIntegerCount = new AtomicInteger(1);
        ExperimentIndicatorViewMonitorFollowupReportRsEntity lastExperimentIndicatorViewMonitorFollowupReportRsEntity = experimentIndicatorViewMonitorFollowupReportRsService.lambdaQuery()
                .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getAppId, appId)
                .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentId, experimentId)
                .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getPeriod, periods)
                .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getIndicatorFuncId, indicatorFuncId)
                .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
                .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentPersonId, experimentPersonId)
                .orderByDesc(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getCount)
                .last(EnumString.LIMIT_1.getStr())
                .one();
        if (Objects.nonNull(lastExperimentIndicatorViewMonitorFollowupReportRsEntity)) {
            Integer count = lastExperimentIndicatorViewMonitorFollowupReportRsEntity.getCount();
            atomicIntegerCount.set(count + 1);
        }

        Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
        Map<String, String> kExperimentIndicatorInstanceIdVValMap = new HashMap<>();
        Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
        Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
        experimentIndicatorInstanceRsService.lambdaQuery()
                .select(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorName, ExperimentIndicatorInstanceRsEntity::getUnit)
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
                .list()
                .forEach(experimentIndicatorInstanceRsEntity -> {
                    kInstanceIdVExperimentIndicatorInstanceIdMap.put(experimentIndicatorInstanceRsEntity.getIndicatorInstanceId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
                    experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
                    kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.put(
                            experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(), experimentIndicatorInstanceRsEntity);
                });
        if (!experimentIndicatorInstanceIdSet.isEmpty()) {
            queryPersonBiz.fillIndicatorValMap(kExperimentIndicatorInstanceIdVValMap,periods,experimentPersonId, experimentIndicatorInstanceIdSet);
           /* experimentIndicatorValRsService.lambdaQuery()
                    .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                    .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
                    .list()
                    .forEach(experimentIndicatorValRsEntity -> {
                        kExperimentIndicatorInstanceIdVValMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity.getCurrentVal());
                    });*/
        }
        String indicatorCurrentValArray = null;
        List<String> indicatorCurrentValArrayList = new ArrayList<>();
        List<String> contentNameList = Arrays.stream(ivmfContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
        List<String> indicatorInstanceIdArrayList = Arrays.stream(ivmfContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());

        final Integer loopNum=Math.min(contentNameList.size(), indicatorInstanceIdArrayList.size() );
        for (int i = 0; i < loopNum; i++) {
            String indicatorInstanceIdArray = indicatorInstanceIdArrayList.get(i);
            List<String> indicatorInstanceIdList = Arrays.stream(indicatorInstanceIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
            List<String> indicatorCurrentValList = new ArrayList<>();
            indicatorInstanceIdList.forEach(indicatorInstanceId -> {
                String experimentIndicatorInstanceId = kInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
                String indicatorCurrentVal = kExperimentIndicatorInstanceIdVValMap.get(experimentIndicatorInstanceId);
                indicatorCurrentValList.add(indicatorCurrentVal);
            });
            String indicatorCurrentValArrayInside = String.join(EnumString.COMMA.getStr(), indicatorCurrentValList);
            indicatorCurrentValArrayList.add(indicatorCurrentValArrayInside);
        }
        indicatorCurrentValArray = String.join(EnumString.JIN.getStr(), indicatorCurrentValArrayList);
        experimentIndicatorViewMonitorFollowupReportRsEntity = ExperimentIndicatorViewMonitorFollowupReportRsEntity
                .builder()
                .experimentIndicatorViewMonitorFollowupReportId(idGenerator.nextIdStr())
                .experimentId(experimentId)
                .caseId(caseId)
                .appId(appId)
                .period(periods)
                .indicatorFuncId(indicatorFuncId)
                .experimentPersonId(experimentPersonId)
                .operateFlowId(operateFlowId)
                .count(atomicIntegerCount.get())
                .experimentIndicatorViewMonitorFollowupId(indicatorViewMonitorFollowupId)
                .name(name)
                .ivmfContentNameArray(ivmfContentNameArray)
                .ivmfContentRefIndicatorInstanceIdArray(ivmfContentRefIndicatorInstanceIdArray)
                .ivmfIndicatorCurrentValArray(indicatorCurrentValArray)
                .build();
        experimentIndicatorViewMonitorFollowupReportRsService.saveOrUpdate(experimentIndicatorViewMonitorFollowupReportRsEntity);

        //服务记录
        LoginContextVO voLogin = ShareBiz.getLoginUser();
        final ExperimentIndicatorFuncRsResponse func=getIndicatorFunc(experimentOrgId,indicatorFuncId);
        OperateFlowEntity saveFlow = OperateFlowEntity.builder()
                .id(null)
                .operateFlowId(idGenerator.nextIdStr())
                .appId(exptValidator.getAppId())
                .experimentInstanceId(exptValidator.getExperimentInstanceId())
                .experimentGroupId(exptValidator.getExperimentGroupId())
                .experimentOrgId(exptValidator.getExperimentOrgId())
                .experimentPersonId(exptValidator.getExperimentPersonId())
                .operateAccountId(voLogin.getAccountId())
                .operateAccountName(voLogin.getAccountName())
                .periods(periods)
                .reportFlag(EnumExperimentOrgReportFlag.FOLLOWUP.getCode())
                .reportSrcId(indicatorViewMonitorFollowupId)
                .reportLabel(exptValidator.getCachedExptOrg().get().getExperimentOrgName())
                .reportDescr(func.getIndicatorFuncName())
                .startTime(dateNow)
                .endTime(dateNow)
                .operateTime(dateNow)
                .operateGameDay(timePoint.getGameDay())
                .build();
        OperateFlowSnapEntity saveFlowSnap = OperateFlowSnapEntity.builder()
                .appId(appId)
                .snapTime(dateNow)
                .build();
        ExperimentMonitorFollowupRsResponse folowupReport=this.get(indicatorFuncId, experimentPersonId, periods);
        if(ShareUtil.XObject.noneEmpty(folowupReport,()->folowupReport.getExperimentIndicatorViewMonitorFollowupReportRsResponse())) {
            ExptOrgReportNodeVO reportNode=new ExptOrgReportNodeVO()
                    .setIndicatorFuncId(exptValidator.getIndicatorFuncId())
                    .setIndicatorFuncName(func.getIndicatorFuncName())
                    .setIndicatorCategoryId(func.getIndicatorCategoryId() )
                    .setNodeData(new ExptOrgReportNodeDataVO().setMonitorFollowup(folowupReport.getExperimentIndicatorViewMonitorFollowupReportRsResponse()));
            ExptOrgFlowReportResponse report=new ExptOrgFlowReportResponse()
                    .setOperateFlowId(saveFlow.getOperateFlowId())
                    .setOperateTime(saveFlow.getOperateTime())
                    .setOperateGameDay(saveFlow.getOperateGameDay())
                    .setReportName(String.format("%s报告",saveFlow.getReportLabel()))
                    .setNodes(List.of(reportNode));
            try {
                saveFlowSnap.setRecordJson(JacksonUtil.toJson(report, true));
            } catch (Exception ex) {
                AssertUtil.justThrow(String.format("机构报告数据编制失败：%s", ex.getMessage()), ex);
            }
            operateFlowDao.tranSave(saveFlow, List.of(saveFlowSnap), false);
        }


    }
    private ExperimentIndicatorFuncRsResponse getIndicatorFunc(String experimentOrgId, String indicatorFuncId){
        ExperimentIndicatorFuncRsResponse rst=new ExperimentIndicatorFuncRsResponse();
        rst.setIndicatorFuncId(indicatorFuncId);
        List<ExperimentOrgModuleRsResponse> modules=experimentOrgModuleBiz.getByExperimentOrgIdAndExperimentPersonId(experimentOrgId);
        if(ShareUtil.XObject.isEmpty(modules)){
            return rst;
        }
        for(ExperimentOrgModuleRsResponse module:modules){
            if(ShareUtil.XObject.isEmpty(module.getExperimentIndicatorFuncRsResponseList())){
                continue;
            }
            for(ExperimentIndicatorFuncRsResponse func:module.getExperimentIndicatorFuncRsResponseList()){
                if(indicatorFuncId.equals(func.getIndicatorFuncId())){
                    return func;
                }
            }
        }
        return rst;
    }

    /**
     * runsix method process
     * 1.plan does not exist
     * 1.1 get experimentIndicatorViewMonitorFollowupRsResponseList
     * 2.plan exist
     * 1.2 return last experimentIndicatorViewMonitorFollowupReportRsResponse
     */
    public ExperimentMonitorFollowupRsResponse get(String indicatorFuncId, String experimentPersonId, Integer periods) {
        ExperimentIndicatorViewMonitorFollowupPlanRsResponse experimentIndicatorViewMonitorFollowupPlanRsResponse = null;
        List<ExperimentIndicatorViewMonitorFollowupRsResponse> experimentIndicatorViewMonitorFollowupRsResponseList = new ArrayList<>();
        ExperimentIndicatorViewMonitorFollowupReportRsResponse experimentIndicatorViewMonitorFollowupReportRsResponse = null;
        ExperimentIndicatorViewMonitorFollowupPlanRsEntity experimentIndicatorViewMonitorFollowupPlanRsEntity = experimentIndicatorViewMonitorFollowupPlanRsService.lambdaQuery()
                //.eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getPeriods, periods)
                .eq(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getExperimentPersonId, experimentPersonId)
                .orderByDesc(ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getPeriods,ExperimentIndicatorViewMonitorFollowupPlanRsEntity::getId)
                .last("limit 1")
                .one();

        experimentIndicatorViewMonitorFollowupPlanRsResponse = experimentIndicatorViewMonitorFollowupPlanRs2Response(experimentIndicatorViewMonitorFollowupPlanRsEntity);
        Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
        Map<String, String> kExperimentIndicatorInstanceIdVValMap = new HashMap<>();
        Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
        Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
        experimentIndicatorInstanceRsService.lambdaQuery()
                .select(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, ExperimentIndicatorInstanceRsEntity::getIndicatorName, ExperimentIndicatorInstanceRsEntity::getUnit)
                .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
                .list()
                .forEach(experimentIndicatorInstanceRsEntity -> {
                    kInstanceIdVExperimentIndicatorInstanceIdMap.put(experimentIndicatorInstanceRsEntity.getIndicatorInstanceId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
                    experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
                    kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.put(
                            experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(), experimentIndicatorInstanceRsEntity);
                });
        if (!experimentIndicatorInstanceIdSet.isEmpty()) {
            queryPersonBiz.fillIndicatorValMap(kExperimentIndicatorInstanceIdVValMap,periods,experimentPersonId,experimentIndicatorInstanceIdSet);
          /*  experimentIndicatorValRsService.lambdaQuery()
                    .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                    .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
                    .list()
                    .forEach(experimentIndicatorValRsEntity -> {
                        kExperimentIndicatorInstanceIdVValMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity.getCurrentVal());
                    });*/
        }
        experimentIndicatorViewMonitorFollowupRsService.lambdaQuery()
                .eq(ExperimentIndicatorViewMonitorFollowupRsEntity::getIndicatorFuncId, indicatorFuncId)
                .list()
                .forEach(experimentIndicatorViewMonitorFollowupRsEntity -> {
                    List<ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse> experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList = new ArrayList<>();
                    String ivmfContentNameArray = experimentIndicatorViewMonitorFollowupRsEntity.getIvmfContentNameArray();
                    if (StringUtils.isBlank(ivmfContentNameArray)) {
                        return;
                    }
                    List<String> contentNameList = Arrays.stream(ivmfContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
                    String ivmfContentRefIndicatorInstanceIdArray = experimentIndicatorViewMonitorFollowupRsEntity.getIvmfContentRefIndicatorInstanceIdArray();
                    if (StringUtils.isBlank(ivmfContentRefIndicatorInstanceIdArray)) {
                        return;
                    }
                    List<String> indicatorInstanceIdArrayList = Arrays.stream(ivmfContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
                    final Integer loopNum=Math.min(contentNameList.size(), indicatorInstanceIdArrayList.size() );
                    for (int i = 0; i < loopNum; i++) {
                        String indicatorInstanceIdArray = indicatorInstanceIdArrayList.get(i);
                        List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList = new ArrayList<>();
                        populateExperimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList, indicatorInstanceIdArray,
                                kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVValMap);
                        ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse experimentIndicatorViewMonitorFollowupFollowupContentRsResponse = ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse
                                .builder()
                                .name(contentNameList.get(i))
                                .experimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList)
                                .build();
                        experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList.add(experimentIndicatorViewMonitorFollowupFollowupContentRsResponse);
                    }
                    ExperimentIndicatorViewMonitorFollowupRsResponse experimentIndicatorViewMonitorFollowupRsResponse = ExperimentIndicatorViewMonitorFollowupRsResponse
                            .builder()
                            .experimentIndicatorViewMonitorFollowupId(experimentIndicatorViewMonitorFollowupRsEntity.getExperimentIndicatorViewMonitorFollowupId())
                            .name(experimentIndicatorViewMonitorFollowupRsEntity.getName())
                            .experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList(experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList)
                            .build();
                    experimentIndicatorViewMonitorFollowupRsResponseList.add(experimentIndicatorViewMonitorFollowupRsResponse);
                });
        if (Objects.isNull(experimentIndicatorViewMonitorFollowupPlanRsEntity)) {
        } else {
            ExperimentIndicatorViewMonitorFollowupReportRsEntity experimentIndicatorViewMonitorFollowupReportRsEntity = experimentIndicatorViewMonitorFollowupReportRsService.lambdaQuery()
                    //.eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getPeriod, periods)
                    .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentPersonId, experimentPersonId)
                    .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getIndicatorFuncId, indicatorFuncId)
                    .orderByDesc(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getPeriod,ExperimentIndicatorViewMonitorFollowupReportRsEntity::getCount)
                    .last(EnumString.LIMIT_1.getStr())
                    .one();
            if (Objects.isNull(experimentIndicatorViewMonitorFollowupReportRsEntity)) {
                experimentIndicatorViewMonitorFollowupReportRsResponse = ExperimentIndicatorViewMonitorFollowupReportRsResponse
                        .builder()
                        .experimentIndicatorViewMonitorFollowupRsResponseList(experimentIndicatorViewMonitorFollowupRsResponseList)
                        .build();
            } else {
                List<ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse> experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList = new ArrayList<>();
                String ivmfContentNameArray = experimentIndicatorViewMonitorFollowupReportRsEntity.getIvmfContentNameArray();
                List<String> contentNameList = Arrays.stream(ivmfContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
                String ivmfContentRefIndicatorInstanceIdArray = experimentIndicatorViewMonitorFollowupReportRsEntity.getIvmfContentRefIndicatorInstanceIdArray();
                List<String> indicatorInstanceIdArrayList = Arrays.stream(ivmfContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
                String ivmfIndicatorCurrentValArray = experimentIndicatorViewMonitorFollowupReportRsEntity.getIvmfIndicatorCurrentValArray();
                List<String> indicatorInstanceCurrentValArrayList = Arrays.stream(ivmfIndicatorCurrentValArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
                final Integer loopNum=Math.min( Math.min(contentNameList.size(), indicatorInstanceIdArrayList.size()) , indicatorInstanceCurrentValArrayList.size());
                for (int i = 0; i < loopNum; i++) {
                    String indicatorInstanceIdArray = indicatorInstanceIdArrayList.get(i);
                    String indicatorCurrentValArray = indicatorInstanceCurrentValArrayList.get(i);
                    List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList = new ArrayList<>();
                    populateExperimentIndicatorInstanceRsResponseListMF(experimentIndicatorInstanceRsResponseList, indicatorInstanceIdArray,
                            kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, indicatorCurrentValArray);
                    ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse experimentIndicatorViewMonitorFollowupFollowupContentRsResponse = ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse
                            .builder()
                            .name(contentNameList.get(i))
                            .experimentIndicatorInstanceRsResponseList(experimentIndicatorInstanceRsResponseList)
                            .build();
                    experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList.add(experimentIndicatorViewMonitorFollowupFollowupContentRsResponse);
                }
                ExperimentIndicatorViewMonitorFollowupRsResponse experimentIndicatorViewMonitorFollowupRsResponse = ExperimentIndicatorViewMonitorFollowupRsResponse
                        .builder()
                        .experimentIndicatorViewMonitorFollowupId(experimentIndicatorViewMonitorFollowupReportRsEntity.getExperimentIndicatorViewMonitorFollowupId())
                        .name(experimentIndicatorViewMonitorFollowupReportRsEntity.getName())
                        .experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList(experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList)
                        .build();
                List<ExperimentIndicatorViewMonitorFollowupRsResponse> reportExperimentIndicatorViewMonitorFollowupRsResponseList = new ArrayList<>();
                reportExperimentIndicatorViewMonitorFollowupRsResponseList.add(experimentIndicatorViewMonitorFollowupRsResponse);
                experimentIndicatorViewMonitorFollowupReportRsResponse = ExperimentIndicatorViewMonitorFollowupReportRsResponse
                        .builder()
                        .experimentIndicatorViewMonitorFollowupRsResponseList(reportExperimentIndicatorViewMonitorFollowupRsResponseList)
                        .build();
            }
        }
        return ExperimentMonitorFollowupRsResponse
                .builder()
                .experimentIndicatorViewMonitorFollowupPlanRsResponse(experimentIndicatorViewMonitorFollowupPlanRsResponse)
                .experimentIndicatorViewMonitorFollowupRsResponseList(experimentIndicatorViewMonitorFollowupRsResponseList)
                .experimentIndicatorViewMonitorFollowupReportRsResponse(experimentIndicatorViewMonitorFollowupReportRsResponse)
                .build();
    }

    public void populateExperimentIndicatorInstanceRsResponseList(
            List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList,
            String indicatorInstanceIdArray,
            Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
            Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap,
            Map<String, String> kExperimentIndicatorInstanceIdVValMap
    ) {
        List<String> indicatorInstanceIdList = Arrays.stream(indicatorInstanceIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
        indicatorInstanceIdList.forEach(indicatorInstanceId -> {
            ExperimentIndicatorInstanceRsResponse experimentIndicatorInstanceRsResponse = populateExperimentIndicatorInstanceRsResponse(
                    indicatorInstanceId, kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVValMap);
            experimentIndicatorInstanceRsResponseList.add(experimentIndicatorInstanceRsResponse);
        });
    }

    public ExperimentIndicatorInstanceRsResponse populateExperimentIndicatorInstanceRsResponse(
            String indicatorInstanceId,
            Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
            Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap,
            Map<String, String> kExperimentIndicatorInstanceIdVValMap) {
        String experimentIndicatorInstanceId = kInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorInstanceRsEntity)) {
            log.warn("populateExperimentIndicatorInstanceRsResponse indicatorInstanceId:{} is illegal, mapped no experimentIndicatorInstanceRsEntity", indicatorInstanceId);
            throw new ExperimentIndicatorViewBaseInfoRsException(EnumESC.VALIDATE_EXCEPTION);
        }
        return ExperimentIndicatorInstanceRsResponse.getExperimentIndicatorInstanceRsResponse(
                experimentIndicatorInstanceRsEntity.getIndicatorName(), kExperimentIndicatorInstanceIdVValMap.get(experimentIndicatorInstanceId), experimentIndicatorInstanceRsEntity.getUnit()
        );
    }

    public void populateExperimentIndicatorInstanceRsResponseListMF(
            List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList,
            String indicatorInstanceIdArray,
            Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
            Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap,
            String indicatorCurrentValArray
    ) {
        List<String> indicatorInstanceIdList = Arrays.stream(indicatorInstanceIdArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
        List<String> indicatorCurrentValList = Arrays.stream(indicatorCurrentValArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
        for (int i = 0; i <= indicatorInstanceIdList.size() - 1; i++) {
            String indicatorInstanceId = indicatorInstanceIdList.get(i);
            String indicatorCurrentVal = indicatorCurrentValList.get(i);
            ExperimentIndicatorInstanceRsResponse experimentIndicatorInstanceRsResponse = populateExperimentIndicatorInstanceRsResponseMF(
                    indicatorInstanceId, kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, kInstanceIdVExperimentIndicatorInstanceIdMap, indicatorCurrentVal);
            if (Objects.nonNull(experimentIndicatorInstanceRsResponse)) {
                experimentIndicatorInstanceRsResponseList.add(experimentIndicatorInstanceRsResponse);
            }
        }
    }

    public ExperimentIndicatorInstanceRsResponse populateExperimentIndicatorInstanceRsResponseMF(
            String indicatorInstanceId,
            Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
            Map<String, String> kInstanceIdVExperimentIndicatorInstanceIdMap,
            String currentVal) {
        String experimentIndicatorInstanceId = kInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorInstanceRsEntity)) {
            log.warn("method ExperimentIndicatorViewBaseInfoRsBiz.get populateExperimentIndicatorInstanceRsResponse indicatorInstanceId:{} is illegal, mapped no experimentIndicatorInstanceRsEntity", indicatorInstanceId);
            throw new ExperimentIndicatorViewBaseInfoRsException(EnumESC.VALIDATE_EXCEPTION);
        }
        return ExperimentIndicatorInstanceRsResponse.getExperimentIndicatorInstanceRsResponse(
                experimentIndicatorInstanceRsEntity.getIndicatorName(), currentVal, experimentIndicatorInstanceRsEntity.getUnit()
        );
    }
}
