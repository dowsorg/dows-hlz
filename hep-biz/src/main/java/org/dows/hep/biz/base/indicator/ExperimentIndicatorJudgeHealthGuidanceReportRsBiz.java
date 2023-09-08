package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentHealthGuidanceCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsExperimentCalculateFuncRequest;
import org.dows.hep.api.base.indicator.response.ExperimentHealthGuidanceReportResponseRs;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorFuncRsResponse;
import org.dows.hep.api.base.indicator.response.ExperimentOrgModuleRsResponse;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.user.experiment.response.ExptOrgFlowReportResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeVO;
import org.dows.hep.biz.dao.OperateFlowDao;
import org.dows.hep.biz.eval.EvalPersonBiz;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.orgreport.OrgReportComposer;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthGuidanceReportRsEntity;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthGuidanceRsEntity;
import org.dows.hep.entity.OperateFlowEntity;
import org.dows.hep.entity.OperateFlowSnapEntity;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthGuidanceReportRsService;
import org.dows.hep.service.ExperimentIndicatorJudgeHealthGuidanceRsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorJudgeHealthGuidanceReportRsBiz {
  private final ExperimentIndicatorJudgeHealthGuidanceReportRsService experimentIndicatorJudgeHealthGuidanceReportRsService;
  private final ExperimentIndicatorJudgeHealthGuidanceRsService experimentIndicatorJudgeHealthGuidanceRsService;

  private final ExperimentOrgModuleBiz experimentOrgModuleBiz;
  private final IdGenerator idGenerator;

  private final RsExperimentCalculateBiz rsExperimentCalculateBiz;

  private final OrgReportComposer orgReportComposer;

  private final OperateFlowDao operateFlowDao;

  private final EvalPersonBiz evalPersonBiz;

  public static ExperimentHealthGuidanceReportResponseRs experimentHealthGuidanceReport2ResponseRs(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity experimentIndicatorJudgeHealthGuidanceReportRsEntity) {
    if (Objects.isNull(experimentIndicatorJudgeHealthGuidanceReportRsEntity)) {
      return null;
    }
    return ExperimentHealthGuidanceReportResponseRs
        .builder()
        .experimentIndicatorJudgeHealthGuidanceId(experimentIndicatorJudgeHealthGuidanceReportRsEntity.getExperimentIndicatorJudgeHealthGuidanceId())
        .indicatorCategoryNameArray(experimentIndicatorJudgeHealthGuidanceReportRsEntity.getIndicatorCategoryNameArray())
        .name(experimentIndicatorJudgeHealthGuidanceReportRsEntity.getName())
        .deleted(experimentIndicatorJudgeHealthGuidanceReportRsEntity.getDeleted())
        .dt(experimentIndicatorJudgeHealthGuidanceReportRsEntity.getDt())
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public ExptOrgFlowReportResponse healthGuidanceCheck(ExperimentHealthGuidanceCheckRequestRs experimentHealthGuidanceCheckRequestRs) {
    List<ExperimentIndicatorJudgeHealthGuidanceReportRsEntity> experimentIndicatorJudgeHealthGuidanceReportRsEntityList = new ArrayList<>();
    Integer periods = experimentHealthGuidanceCheckRequestRs.getPeriods();
    String experimentPersonId = experimentHealthGuidanceCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentHealthGuidanceCheckRequestRs.getIndicatorFuncId();
    List<String> experimentIndicatorJudgeHealthGuidanceIdList = experimentHealthGuidanceCheckRequestRs.getExperimentIndicatorJudgeHealthGuidanceIdList();
    AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentIndicatorJudgeHealthGuidanceIdList))
            .throwMessage("请选择项目");

    String appId = experimentHealthGuidanceCheckRequestRs.getAppId();
    String experimentId = experimentHealthGuidanceCheckRequestRs.getExperimentId();
    String experimentOrgId = experimentHealthGuidanceCheckRequestRs.getExperimentOrgId();

    ExptOrgFuncRequest funcRequest = (ExptOrgFuncRequest) new ExptOrgFuncRequest().setIndicatorFuncId(indicatorFuncId)
            .setExperimentInstanceId(experimentId)
            .setExperimentOrgId(experimentOrgId)
            .setExperimentPersonId(experimentPersonId)
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
    Map<String, ExperimentIndicatorJudgeHealthGuidanceRsEntity> kExperimentIndicatorJudgeHealthGuidanceIdVExperimentIndicatorJudgeHealthGuidanceRsEntityMap = new HashMap<>();

    experimentIndicatorJudgeHealthGuidanceRsService.lambdaQuery()
            .eq(ExperimentIndicatorJudgeHealthGuidanceRsEntity::getAppId, appId)
            .in(ExperimentIndicatorJudgeHealthGuidanceRsEntity::getExperimentIndicatorJudgeHealthGuidanceId, experimentIndicatorJudgeHealthGuidanceIdList)
            .list()
            .forEach(experimentIndicatorJudgeHealthGuidanceRsEntity -> {
              kExperimentIndicatorJudgeHealthGuidanceIdVExperimentIndicatorJudgeHealthGuidanceRsEntityMap.put(experimentIndicatorJudgeHealthGuidanceRsEntity.getExperimentIndicatorJudgeHealthGuidanceId(), experimentIndicatorJudgeHealthGuidanceRsEntity);
            });
    AtomicInteger atomicIntegerCount = new AtomicInteger(1);
    ExperimentIndicatorJudgeHealthGuidanceReportRsEntity experimentIndicatorJudgeHealthGuidanceReportRsEntity = experimentIndicatorJudgeHealthGuidanceReportRsService.lambdaQuery()
            .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getAppId, appId)
            .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getExperimentId, experimentId)
            .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getPeriod, periods)
            .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getExperimentPersonId, experimentPersonId)
            .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getOperateFlowId, operateFlowId)
            .orderByDesc(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getCount)
            .last(EnumString.LIMIT_1.getStr())
            .one();
    if (Objects.nonNull(experimentIndicatorJudgeHealthGuidanceReportRsEntity)) {
      Integer count = experimentIndicatorJudgeHealthGuidanceReportRsEntity.getCount();
      atomicIntegerCount.set(count + 1);
    }
    kExperimentIndicatorJudgeHealthGuidanceIdVExperimentIndicatorJudgeHealthGuidanceRsEntityMap.forEach((experimentIndicatorJudgeHealthGuidanceId, experimentIndicatorJudgeHealthGuidanceRsEntity) -> {
      experimentIndicatorJudgeHealthGuidanceReportRsEntityList.add(
              ExperimentIndicatorJudgeHealthGuidanceReportRsEntity
                      .builder()
                      .experimentIndicatorJudgeHealthGuidanceReportId(idGenerator.nextIdStr())
                      .experimentId(experimentId)
                      .appId(appId)
                      .period(periods)
                      .indicatorFuncId(indicatorFuncId)
                      .experimentPersonId(experimentPersonId)
                      .operateFlowId(operateFlowId)
                      .name(experimentIndicatorJudgeHealthGuidanceRsEntity.getName())
                      .count(atomicIntegerCount.get())
                      .experimentIndicatorJudgeHealthGuidanceId(experimentIndicatorJudgeHealthGuidanceId)
                      .indicatorCategoryNameArray(experimentIndicatorJudgeHealthGuidanceRsEntity.getIndicatorCategoryNameArray())
                      .build()
      );
    });
    experimentIndicatorJudgeHealthGuidanceReportRsService.saveOrUpdateBatch(experimentIndicatorJudgeHealthGuidanceReportRsEntityList);
    //机构报告
    OperateFlowEntity flow = flowValidator.getExptFlow().get();
    LoginContextVO voLogin = ShareBiz.getLoginUser();
    final String indicatorFuncName=getIndicatorFuncName(exptValidator.getExperimentOrgId(),exptValidator.getIndicatorFuncId());
    OperateFlowEntity saveFlow = OperateFlowEntity.builder()
            .id(flow.getId())
            .operateFlowId(flow.getOperateFlowId())
            .operateAccountId(voLogin.getAccountId())
            .operateAccountName(voLogin.getAccountName())
            .periods(periods)
            .reportFlag(1)
            .reportLabel(exptValidator.getCachedExptOrg().get().getExperimentOrgName())
            .reportDescr(indicatorFuncName)
            .endTime(dateNow)
            .operateTime(dateNow)
            .operateGameDay(timePoint.getGameDay())
            .build();

    OperateFlowSnapEntity saveFlowSnap = OperateFlowSnapEntity.builder()
            .appId(exptValidator.getAppId())
            .snapTime(dateNow)
            .build();
    List<ExperimentHealthGuidanceReportResponseRs> reports=ShareUtil.XCollection.map(experimentIndicatorJudgeHealthGuidanceReportRsEntityList, ExperimentIndicatorJudgeHealthGuidanceReportRsBiz::experimentHealthGuidanceReport2ResponseRs);
    ExptOrgReportNodeVO newNode=new ExptOrgReportNodeVO()
            .setIndicatorFuncId(exptValidator.getIndicatorFuncId())
            .setIndicatorFuncName(indicatorFuncName)
            .setIndicatorCategoryId(EnumIndicatorCategory.JUDGE_MANAGEMENT_HEALTH_GUIDANCE.getCode())
            .setNodeData(new ExptOrgReportNodeDataVO().setJudgeHealthGuidance(reports));
    try {
      if(ConfigExperimentFlow.SWITCH2EvalCache) {

        evalPersonBiz.evalOrgFunc(RsExperimentCalculateFuncRequest.builder()
                .appId(exptValidator.getAppId())
                .experimentId(exptValidator.getExperimentInstanceId())
                .periods(timePoint.getPeriod())
                .experimentPersonId(exptValidator.getExperimentPersonId())
                .funcType(EnumEvalFuncType.FUNCHealthGuide)
                .build());
      }else {
        rsExperimentCalculateBiz.experimentReCalculateFunc(RsExperimentCalculateFuncRequest.builder()
                .appId(exptValidator.getAppId())
                .experimentId(exptValidator.getExperimentInstanceId())
                .periods(timePoint.getPeriod())
                .experimentPersonId(exptValidator.getExperimentPersonId())
                .build());
      }
    } catch (Exception ex) {
      log.error(String.format("healthGuidanceCheck experimentId:%s personId:%s",
              exptValidator.getExperimentInstanceId(), exptValidator.getExperimentPersonId()), ex);
    }
    ExptOrgFlowReportResponse report=null;
    try {
      report = orgReportComposer.composeReport(exptValidator, flowValidator.updateFlowOperate(timePoint), timePoint, newNode);
      saveFlowSnap.setRecordJson(JacksonUtil.toJson(report, true));
    } catch (Exception ex) {
      AssertUtil.justThrow(String.format("机构报告数据编制失败：%s", ex.getMessage()), ex);
    }
    operateFlowDao.tranSave(saveFlow, List.of(saveFlowSnap), false);
    return report;

  }
  private String getIndicatorFuncName(String experimentOrgId, String indicatorFuncId){
    List<ExperimentOrgModuleRsResponse> modules=experimentOrgModuleBiz.getByExperimentOrgIdAndExperimentPersonId(experimentOrgId);
    if(ShareUtil.XObject.isEmpty(modules)){
      return null;
    }
    for(ExperimentOrgModuleRsResponse module:modules){
      if(ShareUtil.XObject.isEmpty(module.getExperimentIndicatorFuncRsResponseList())){
        continue;
      }
      for(ExperimentIndicatorFuncRsResponse func:module.getExperimentIndicatorFuncRsResponseList()){
        if(indicatorFuncId.equals(func.getIndicatorFuncId())){
          return func.getIndicatorFuncName();
        }
      }
    }
    return "";
  }

  public List<ExperimentHealthGuidanceReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId, Integer periods) {
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    if(ShareUtil.XObject.isEmpty(operateFlowId)){
      return Collections.emptyList();
    }

    return experimentIndicatorJudgeHealthGuidanceReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getOperateFlowId, operateFlowId)
        .orderByAsc(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorJudgeHealthGuidanceReportRsBiz::experimentHealthGuidanceReport2ResponseRs)
        .collect(Collectors.toList());
  }
}
