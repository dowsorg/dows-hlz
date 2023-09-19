package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.edw.HepOperateTypeEnum;
import org.dows.edw.domain.HepFollowUp;
import org.dows.edw.domain.HepHealthExamination;
import org.dows.edw.domain.HepHealthTherapy;
import org.dows.hep.api.base.indicator.request.ExperimentMonitorFollowupCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsExperimentCalculateFuncRequest;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.edw.request.HepOperateSetRequest;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.ExperimentIndicatorViewBaseInfoRsException;
import org.dows.hep.api.user.experiment.response.ExptOrgFlowReportResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeVO;
import org.dows.hep.biz.base.indicator.ExperimentOrgModuleBiz;
import org.dows.hep.biz.base.indicator.RsExperimentCalculateBiz;
import org.dows.hep.biz.dao.ExperimentFollowupPlanDao;
import org.dows.hep.biz.dao.ExperimentOrgNoticeDao;
import org.dows.hep.biz.dao.OperateFlowDao;
import org.dows.hep.biz.edw.InterveneHandler;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.followupplan.FollowupPlanCache;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.properties.MongoProperties;
import org.dows.hep.service.ExperimentIndicatorInstanceRsService;
import org.dows.hep.service.ExperimentIndicatorViewMonitorFollowupReportRsService;
import org.dows.hep.service.ExperimentIndicatorViewMonitorFollowupRsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/9/17 13:29
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FollowupBiz {

    private final IdGenerator idGenerator;
    private final ExperimentIndicatorViewMonitorFollowupRsService experimentIndicatorViewMonitorFollowupRsService;
    private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;



    private final ExperimentIndicatorViewMonitorFollowupReportRsService experimentIndicatorViewMonitorFollowupReportRsService;

    private final RsExperimentCalculateBiz rsExperimentCalculateBiz;

    private final ExperimentOrgModuleBiz experimentOrgModuleBiz;

    private final OperateFlowDao operateFlowDao;

    private final EvalPersonBiz evalPersonBiz;

    private final QueryPersonBiz queryPersonBiz;

    private final ExperimentFollowupPlanDao experimentFollowupPlanDao;

    private final ExperimentOrgNoticeDao experimentOrgNoticeDao;
    private final InterveneHandler interveneHandler;
    private final MongoProperties mongoProperties;

    public void monitorFollowupCheck(ExperimentMonitorFollowupCheckRequestRs experimentMonitorFollowupCheckRequestRs)  {

        Integer periods = experimentMonitorFollowupCheckRequestRs.getPeriods();
        String experimentGroupId = experimentMonitorFollowupCheckRequestRs.getExperimentGroupId();
        String experimentOrgId = experimentMonitorFollowupCheckRequestRs.getExperimentOrgId();
        String experimentPersonId = experimentMonitorFollowupCheckRequestRs.getExperimentPersonId();
        String indicatorFuncId = experimentMonitorFollowupCheckRequestRs.getIndicatorFuncId();
        String appId = experimentMonitorFollowupCheckRequestRs.getAppId();
        String experimentId = experimentMonitorFollowupCheckRequestRs.getExperimentId();
        String indicatorViewMonitorFollowupId = experimentMonitorFollowupCheckRequestRs.getIndicatorViewMonitorFollowupId();
        Integer intervalDay = experimentMonitorFollowupCheckRequestRs.getIntervalDay();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(intervalDay, true) || intervalDay <= 1)
                .throwMessage("请选择随访频率");

        LoginContextVO voLogin = ShareBiz.getLoginUser();
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
        ExperimentFollowupPlanEntity rowPlan = experimentFollowupPlanDao.getByExperimentPersonId(experimentPersonId, indicatorFuncId).orElse(null);
        AssertUtil.trueThenThrow(null != rowPlan && ShareUtil.XObject.nullSafeEquals(operateFlowId, rowPlan.getOperateFlowId()))
                .throwMessage("挂号期内只能随访一次");
        ExperimentIndicatorViewMonitorFollowupRsEntity experimentIndicatorViewMonitorFollowupRsEntity = experimentIndicatorViewMonitorFollowupRsService.lambdaQuery()
                .eq(ExperimentIndicatorViewMonitorFollowupRsEntity::getExperimentIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId)
                .last("limit 1")
                .one();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentIndicatorViewMonitorFollowupRsEntity))
                .throwMessage("未找到监测随访表");

        if (null == rowPlan) {
            rowPlan = ExperimentFollowupPlanEntity.builder()
                    .experimentFollowupPlanId(idGenerator.nextIdStr())
                    .appId(appId)
                    .experimentInstanceId(exptValidator.getExperimentInstanceId())
                    .experimentGroupId(exptValidator.getExperimentGroupId())
                    .experimentPersonId(exptValidator.getExperimentPersonId())
                    .experimentOrgId(experimentOrgId)
                    .indicatorFuncId(indicatorFuncId)
                    .operateAccountId(voLogin.getAccountId())
                    .operateAccountName(voLogin.getAccountName())
                    .operateFlowId(operateFlowId)
                    .build();
        }
        boolean planChanged=false;
        if (ShareUtil.XObject.nullSafeNotEquals(rowPlan.getDueDays(), intervalDay)
                || ShareUtil.XObject.nullSafeNotEquals(rowPlan.getIndicatorFollowupId(), indicatorViewMonitorFollowupId)) {
            planChanged=true;
            rowPlan.setIndicatorFollowupId(indicatorViewMonitorFollowupId)
                    .setIndicatorFollowupName(experimentIndicatorViewMonitorFollowupRsEntity.getName())
                    .setDueDays(intervalDay)
                    .setSetAtDay(timePoint.getGameDay())
                    .setSetAtTime(dateNow)
                    .setTodoDay(timePoint.getGameDay() + intervalDay)
                    .setFollowupTime(dateNow)
                    .setFollowupTimes(Optional.ofNullable(rowPlan.getFollowupTimes()).orElse(0) + 1);
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
        } catch (Exception ex) {
            log.error(String.format("monitorFollowupCheck experimentId:%s personId:%s",
                    exptValidator.getExperimentInstanceId(), exptValidator.getExperimentPersonId()), ex);
            AssertUtil.justThrow(String.format("功能点结算失败：%s", ex.getMessage()), ex);
        }

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
            queryPersonBiz.fillIndicatorValMap(kExperimentIndicatorInstanceIdVValMap, periods, experimentPersonId, experimentIndicatorInstanceIdSet);
        }
        String indicatorCurrentValArray = null;
        List<String> indicatorCurrentValArrayList = new ArrayList<>();
        List<String> contentNameList = Arrays.stream(ivmfContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
        List<String> indicatorInstanceIdArrayList = Arrays.stream(ivmfContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());

        final Integer loopNum = Math.min(contentNameList.size(), indicatorInstanceIdArrayList.size());
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
        ExperimentIndicatorViewMonitorFollowupReportRsEntity rowReport = ExperimentIndicatorViewMonitorFollowupReportRsEntity
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

        //服务记录

        final ExperimentIndicatorFuncRsResponse func = getIndicatorFunc(experimentOrgId, indicatorFuncId);
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
        ExperimentMonitorFollowupRsResponse folowupReport = this.get(rowPlan,rowReport, operateFlowId, periods);
        AssertUtil.trueThenThrow(ShareUtil.XObject.anyEmpty(folowupReport, () -> folowupReport.getExperimentIndicatorViewMonitorFollowupReportRsResponse()))
                .throwMessage("获取报告数据失败");
        ExptOrgReportNodeVO reportNode = new ExptOrgReportNodeVO()
                .setIndicatorFuncId(exptValidator.getIndicatorFuncId())
                .setIndicatorFuncName(func.getIndicatorFuncName())
                .setIndicatorCategoryId(func.getIndicatorCategoryId())
                .setNodeData(new ExptOrgReportNodeDataVO().setMonitorFollowup(folowupReport.getExperimentIndicatorViewMonitorFollowupReportRsResponse()));
        ExptOrgFlowReportResponse report = new ExptOrgFlowReportResponse()
                .setOperateFlowId(saveFlow.getOperateFlowId())
                .setOperateTime(saveFlow.getOperateTime())
                .setOperateGameDay(saveFlow.getOperateGameDay())
                .setReportName(String.format("%s报告", saveFlow.getReportLabel()))
                .setNodes(List.of(reportNode));
        try {
            saveFlowSnap.setRecordJson(JacksonUtil.toJson(report, true));
        } catch (Exception ex) {
            AssertUtil.justThrow(String.format("机构报告数据编制失败：%s", ex.getMessage()), ex);
        }


        ExperimentOrgNoticeEntity topNotice=experimentOrgNoticeDao.getTopFollowupNotice(experimentPersonId,
                ExperimentOrgNoticeEntity::getExperimentOrgNoticeId,
                ExperimentOrgNoticeEntity::getActionState);
        if(null!=topNotice&&topNotice.getActionState()== EnumEventActionState.DONE.getCode()){
            topNotice=null;
        }
        final ExperimentFollowupPlanEntity savePlan = rowPlan;
        final ExperimentOrgNoticeEntity saveNotice=topNotice;
        // 保存数据到mongodb
        boolean useMongo = mongoProperties != null && mongoProperties.getEnable() != null && mongoProperties.getEnable();
        if(useMongo){
            HepOperateSetRequest hepOperateSetRequest = HepOperateSetRequest.builder()
                    .type(HepOperateTypeEnum.getNameByCode(HepFollowUp.class))
                    .experimentInstanceId(Long.valueOf(experimentMonitorFollowupCheckRequestRs.getExperimentId()))
                    .experimentGroupId(Long.valueOf(experimentMonitorFollowupCheckRequestRs.getExperimentGroupId()))
                    .operatorId(Long.valueOf(voLogin.getAccountId()))
                    .orgTreeId(Long.valueOf(experimentMonitorFollowupCheckRequestRs.getExperimentOrgId()))
                    .flowId(experimentMonitorFollowupCheckRequestRs.getOperateFlowId())
                    .personId(Long.valueOf(experimentMonitorFollowupCheckRequestRs.getExperimentPersonId()))
                    .orgName(experimentMonitorFollowupCheckRequestRs.getOrgName())
                    .functionName(experimentMonitorFollowupCheckRequestRs.getFunctionName())
                    .functionCode(experimentMonitorFollowupCheckRequestRs.getIndicatorFuncId())
                    .data(experimentMonitorFollowupCheckRequestRs.getData())
                    .period(experimentMonitorFollowupCheckRequestRs.getPeriods())
                    .onDate(null)
                    .onDay(null)
                    .build();
            interveneHandler.write(hepOperateSetRequest, HepFollowUp.class);
            return;
        }
        if (!operateFlowDao.tranSave(saveFlow, List.of(saveFlowSnap), false, () -> {
            if(null!=saveNotice){
                AssertUtil.falseThenThrow(experimentOrgNoticeDao.setTopFollowupNoticeAction(saveNotice.getExperimentOrgNoticeId(),
                                EnumEventActionState.NONE.getCode()))
                        .throwMessage("随访通知更新失败");
            }
            AssertUtil.falseThenThrow(experimentFollowupPlanDao.saveOrUpdate(savePlan))
                    .throwMessage("随访计划保存失败");
            AssertUtil.falseThenThrow(experimentIndicatorViewMonitorFollowupReportRsService.saveOrUpdate(rowReport))
                    .throwMessage("随访内容保存失败");
            return true;
        })){
            AssertUtil.justThrow("数据保存失败");
        }
        if(planChanged) {
            FollowupPlanCache.Instance().putPlan(ExperimentCacheKey.create(appId, experimentId), rowPlan);
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

    public ExperimentMonitorFollowupRsResponse get(String indicatorFuncId, String experimentPersonId, Integer periods) {
        ExperimentFollowupPlanEntity rowPlan = experimentFollowupPlanDao.getByExperimentPersonId(experimentPersonId, indicatorFuncId).orElse(null);
        if(null==rowPlan){
            List<ExperimentIndicatorViewMonitorFollowupRsResponse> experimentIndicatorViewMonitorFollowupRsResponseList = new ArrayList<>();
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
                queryPersonBiz.fillIndicatorValMap(kExperimentIndicatorInstanceIdVValMap, periods, experimentPersonId, experimentIndicatorInstanceIdSet);

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
                        final Integer loopNum = Math.min(contentNameList.size(), indicatorInstanceIdArrayList.size());
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
            return ExperimentMonitorFollowupRsResponse
                    .builder()
                    .experimentIndicatorViewMonitorFollowupRsResponseList(experimentIndicatorViewMonitorFollowupRsResponseList)
                    .build();
        }

        return get(rowPlan,null,null,periods);

    }

    private ExperimentMonitorFollowupRsResponse get(ExperimentFollowupPlanEntity rowPlan,ExperimentIndicatorViewMonitorFollowupReportRsEntity rowReport, String operateFlowId,Integer periods) {
        final String experimentPersonId=rowPlan.getExperimentPersonId();
        final String indicatorFuncId=rowPlan.getIndicatorFuncId();
        if(ShareUtil.XObject.isEmpty(operateFlowId)){
            ExperimentPersonEntity rowPerson = ExperimentPersonCache.Instance().getPerson(rowPlan.getExperimentInstanceId(), rowPlan.getExperimentPersonId());
            operateFlowId = ShareBiz.checkRunningOperateFlowId(rowPlan.getAppId(), rowPlan.getExperimentInstanceId(), rowPerson.getExperimentOrgId(), experimentPersonId);
        }
        ExperimentIndicatorViewMonitorFollowupPlanRsResponse experimentIndicatorViewMonitorFollowupPlanRsResponse = ExperimentIndicatorViewMonitorFollowupPlanRsResponse
                .builder()
                .experimentIndicatorViewMonitorFollowupPlanId(rowPlan.getExperimentFollowupPlanId())
                .experimentId(rowPlan.getExperimentInstanceId())
                .appId(rowPlan.getAppId())
                .indicatorFuncId(rowPlan.getIndicatorFuncId())
                .experimentPersonId(rowPlan.getExperimentPersonId())
                .operateFlowId(rowPlan.getOperateFlowId())
                .intervalDay(rowPlan.getDueDays())
                .experimentIndicatorViewMonitorFollowupId(rowPlan.getIndicatorFollowupId())
                .canFollowUp(ShareUtil.XObject.nullSafeNotEquals(operateFlowId, rowPlan.getOperateFlowId()))
                .build();
        List<ExperimentIndicatorViewMonitorFollowupRsResponse> experimentIndicatorViewMonitorFollowupRsResponseList = new ArrayList<>();
        ExperimentIndicatorViewMonitorFollowupReportRsResponse experimentIndicatorViewMonitorFollowupReportRsResponse = null;


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
            queryPersonBiz.fillIndicatorValMap(kExperimentIndicatorInstanceIdVValMap, periods, experimentPersonId, experimentIndicatorInstanceIdSet);

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
                    final Integer loopNum = Math.min(contentNameList.size(), indicatorInstanceIdArrayList.size());
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


        if(null==rowReport) {
            rowReport = rowReport = experimentIndicatorViewMonitorFollowupReportRsService.lambdaQuery()
                    //.eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getPeriod, periods)
                    .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getExperimentPersonId, experimentPersonId)
                    .eq(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getIndicatorFuncId, indicatorFuncId)
                    .orderByDesc(ExperimentIndicatorViewMonitorFollowupReportRsEntity::getPeriod, ExperimentIndicatorViewMonitorFollowupReportRsEntity::getCount)
                    .last(EnumString.LIMIT_1.getStr())
                    .one();
        }
        if (Objects.isNull(rowReport)) {
            experimentIndicatorViewMonitorFollowupReportRsResponse = ExperimentIndicatorViewMonitorFollowupReportRsResponse
                    .builder()
                    .experimentIndicatorViewMonitorFollowupRsResponseList(experimentIndicatorViewMonitorFollowupRsResponseList)
                    .build();
        } else {
            List<ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse> experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList = new ArrayList<>();
            String ivmfContentNameArray = rowReport.getIvmfContentNameArray();
            List<String> contentNameList = Arrays.stream(ivmfContentNameArray.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
            String ivmfContentRefIndicatorInstanceIdArray = rowReport.getIvmfContentRefIndicatorInstanceIdArray();
            List<String> indicatorInstanceIdArrayList = Arrays.stream(ivmfContentRefIndicatorInstanceIdArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
            String ivmfIndicatorCurrentValArray = rowReport.getIvmfIndicatorCurrentValArray();
            List<String> indicatorInstanceCurrentValArrayList = Arrays.stream(ivmfIndicatorCurrentValArray.split(EnumString.JIN.getStr())).collect(Collectors.toList());
            final Integer loopNum = Math.min(Math.min(contentNameList.size(), indicatorInstanceIdArrayList.size()), indicatorInstanceCurrentValArrayList.size());
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
                    .experimentIndicatorViewMonitorFollowupId(rowReport.getExperimentIndicatorViewMonitorFollowupId())
                    .name(rowReport.getName())
                    .experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList(experimentIndicatorViewMonitorFollowupFollowupContentRsResponseList)
                    .build();
            List<ExperimentIndicatorViewMonitorFollowupRsResponse> reportExperimentIndicatorViewMonitorFollowupRsResponseList = new ArrayList<>();
            reportExperimentIndicatorViewMonitorFollowupRsResponseList.add(experimentIndicatorViewMonitorFollowupRsResponse);
            experimentIndicatorViewMonitorFollowupReportRsResponse = ExperimentIndicatorViewMonitorFollowupReportRsResponse
                    .builder()
                    .experimentIndicatorViewMonitorFollowupRsResponseList(reportExperimentIndicatorViewMonitorFollowupRsResponseList)
                    .build();

        }
        return ExperimentMonitorFollowupRsResponse
                .builder()
                .experimentIndicatorViewMonitorFollowupPlanRsResponse(experimentIndicatorViewMonitorFollowupPlanRsResponse)
                .experimentIndicatorViewMonitorFollowupRsResponseList(experimentIndicatorViewMonitorFollowupRsResponseList)
                .experimentIndicatorViewMonitorFollowupReportRsResponse(experimentIndicatorViewMonitorFollowupReportRsResponse)
                .build();
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



}
