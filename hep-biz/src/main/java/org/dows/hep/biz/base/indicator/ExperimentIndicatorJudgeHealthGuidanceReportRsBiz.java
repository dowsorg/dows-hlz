package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentHealthGuidanceCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentHealthGuidanceReportResponseRs;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.user.experiment.response.ExptOrgFlowReportResponse;
import org.dows.hep.biz.dao.OperateFlowDao;
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
  private final IdGenerator idGenerator;

  private final OrgReportComposer orgReportComposer;

  private final OperateFlowDao operateFlowDao;
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
  public void healthGuidanceCheck(ExperimentHealthGuidanceCheckRequestRs experimentHealthGuidanceCheckRequestRs) {
    List<ExperimentIndicatorJudgeHealthGuidanceReportRsEntity> experimentIndicatorJudgeHealthGuidanceReportRsEntityList = new ArrayList<>();
    Integer periods = experimentHealthGuidanceCheckRequestRs.getPeriods();
    String experimentPersonId = experimentHealthGuidanceCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentHealthGuidanceCheckRequestRs.getIndicatorFuncId();
    List<String> experimentIndicatorJudgeHealthGuidanceIdList = experimentHealthGuidanceCheckRequestRs.getExperimentIndicatorJudgeHealthGuidanceIdList();
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
            .checkExperimentOrg()
            .checkIndicatorFunc();
    final LocalDateTime ldtNow = LocalDateTime.now();
    final Date dateNow = ShareUtil.XDate.localDT2Date(ldtNow);
    ExperimentTimePoint timePoint = exptValidator.getTimePoint(true, ldtNow, true);
    ExptOrgFlowValidator flowValidator = ExptOrgFlowValidator.create(exptValidator)
            .checkOrgFlowRunning(periods);
    final String operateFlowId = flowValidator.getOperateFlowId();
    Map<String, ExperimentIndicatorJudgeHealthGuidanceRsEntity> kExperimentIndicatorJudgeHealthGuidanceIdVExperimentIndicatorJudgeHealthGuidanceRsEntityMap = new HashMap<>();
    if (!experimentIndicatorJudgeHealthGuidanceIdList.isEmpty()) {
      experimentIndicatorJudgeHealthGuidanceRsService.lambdaQuery()
              .eq(ExperimentIndicatorJudgeHealthGuidanceRsEntity::getAppId, appId)
              .in(ExperimentIndicatorJudgeHealthGuidanceRsEntity::getExperimentIndicatorJudgeHealthGuidanceId, experimentIndicatorJudgeHealthGuidanceIdList)
              .list()
              .forEach(experimentIndicatorJudgeHealthGuidanceRsEntity -> {
                kExperimentIndicatorJudgeHealthGuidanceIdVExperimentIndicatorJudgeHealthGuidanceRsEntityMap.put(experimentIndicatorJudgeHealthGuidanceRsEntity.getExperimentIndicatorJudgeHealthGuidanceId(), experimentIndicatorJudgeHealthGuidanceRsEntity);
              });
    }
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
    OperateFlowEntity saveFlow = OperateFlowEntity.builder()
            .id(flow.getId())
            .operateFlowId(flow.getOperateFlowId())
            .operateAccountId(voLogin.getAccountId())
            .operateAccountName(voLogin.getAccountName())
            .periods(periods)
            .reportFlag(1)
            .reportLabel(exptValidator.getCachedExptOrg().get().getExperimentOrgName())
            .reportDescr(exptValidator.getIndicatorFuncName())
            .endTime(dateNow)
            .operateTime(dateNow)
            .operateGameDay(timePoint.getGameDay())
            .build();

    OperateFlowSnapEntity saveFlowSnap = OperateFlowSnapEntity.builder()
            .appId(exptValidator.getAppId())
            .snapTime(dateNow)
            .build();
    try {
      ExptOrgFlowReportResponse report = orgReportComposer.composeReport(exptValidator, flowValidator.updateFlowOperate(timePoint), timePoint, null);
      saveFlowSnap.setRecordJson(JacksonUtil.toJson(report, true));
    } catch (Exception ex) {
      AssertUtil.justThrow(String.format("机构报告数据编制失败：%s", ex.getMessage()), ex);
    }
    operateFlowDao.tranSave(saveFlow, List.of(saveFlowSnap), false);


  }

  public List<ExperimentHealthGuidanceReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId, Integer periods) {
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
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
