package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentRiskFactorCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentRiskFactorReportResponseRs;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.eval.EvalJudgeScoreBiz;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.*;
import org.dows.hep.entity.ExperimentIndicatorJudgeRiskFactorReportRsEntity;
import org.dows.hep.entity.ExperimentIndicatorJudgeRiskFactorRsEntity;
import org.dows.hep.service.ExperimentIndicatorJudgeRiskFactorReportRsService;
import org.dows.hep.service.ExperimentIndicatorJudgeRiskFactorRsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class ExperimentIndicatorJudgeRiskFactorReportRsBiz {
  private final ExperimentIndicatorJudgeRiskFactorReportRsService experimentIndicatorJudgeRiskFactorReportRsService;
  private final ExperimentIndicatorJudgeRiskFactorRsService experimentIndicatorJudgeRiskFactorRsService;
  private final IdGenerator idGenerator;

  private final EvalJudgeScoreBiz evalJudgeScoreBiz;

  public static ExperimentRiskFactorReportResponseRs experimentRiskFactorReport2ResponseRs(ExperimentIndicatorJudgeRiskFactorReportRsEntity experimentIndicatorJudgeRiskFactorReportRsEntity) {
    if (Objects.isNull(experimentIndicatorJudgeRiskFactorReportRsEntity)) {
      return null;
    }
    return ExperimentRiskFactorReportResponseRs
        .builder()
        .experimentIndicatorJudgeRiskFactorId(experimentIndicatorJudgeRiskFactorReportRsEntity.getExperimentIndicatorJudgeRiskFactorId())
        .indicatorCategoryNameArray(experimentIndicatorJudgeRiskFactorReportRsEntity.getIndicatorCategoryNameArray())
        .name(experimentIndicatorJudgeRiskFactorReportRsEntity.getName())
        .deleted(experimentIndicatorJudgeRiskFactorReportRsEntity.getDeleted())
        .dt(experimentIndicatorJudgeRiskFactorReportRsEntity.getDt())
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public void riskFactorCheck(ExperimentRiskFactorCheckRequestRs experimentRiskFactorCheckRequestRs) {
    List<ExperimentIndicatorJudgeRiskFactorReportRsEntity> experimentIndicatorJudgeRiskFactorReportRsEntityList = new ArrayList<>();
    Integer periods = experimentRiskFactorCheckRequestRs.getPeriods();
    String experimentPersonId = experimentRiskFactorCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentRiskFactorCheckRequestRs.getIndicatorFuncId();
    List<String> experimentIndicatorJudgeRiskFactorIdList = experimentRiskFactorCheckRequestRs.getExperimentIndicatorJudgeRiskFactorIdList();
    String appId = experimentRiskFactorCheckRequestRs.getAppId();
    String experimentId = experimentRiskFactorCheckRequestRs.getExperimentId();
    String experimentOrgId = experimentRiskFactorCheckRequestRs.getExperimentOrgId();

    ExptOrgFuncRequest funcRequest = (ExptOrgFuncRequest) new ExptOrgFuncRequest()
            .setIndicatorFuncId(indicatorFuncId)
            .setExperimentInstanceId(experimentId)
            .setExperimentOrgId(experimentOrgId)
            .setExperimentPersonId(experimentPersonId)
            .setPeriods(periods)
            .setAppId(appId);
    ExptRequestValidator exptValidator = ExptRequestValidator.create(funcRequest)
            .checkExperimentPerson();
    final LocalDateTime ldtNow = LocalDateTime.now();
    ExperimentTimePoint timePoint = exptValidator.getTimePoint(true, ldtNow, true);
    ExptOrgFlowValidator flowValidator = ExptOrgFlowValidator.create(exptValidator)
            .checkOrgFlowRunning(timePoint.getPeriod());
    final String operateFlowId = flowValidator.getOperateFlowId();
    //String operateFlowId = ShareBiz.assertRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);

    Map<String, ExperimentIndicatorJudgeRiskFactorRsEntity> kExperimentIndicatorJudgeRiskFactorIdVExperimentIndicatorJudgeRiskFactorRsEntityMap = new HashMap<>();
    if (experimentIndicatorJudgeRiskFactorIdList.isEmpty()) {
      experimentIndicatorJudgeRiskFactorReportRsService.lambdaUpdate()
              .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getAppId, appId)
              .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentId, experimentId)
              .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getPeriod, periods)
              .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getIndicatorFuncId, indicatorFuncId)
              .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentPersonId, experimentPersonId)
              .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getOperateFlowId, operateFlowId)
              .remove();
      return;
    }
    final Map<String, BigDecimal> mapJudgeItems=new HashMap<>();
    experimentIndicatorJudgeRiskFactorRsService.lambdaQuery()
            .eq(ExperimentIndicatorJudgeRiskFactorRsEntity::getAppId, appId)
            .in(ExperimentIndicatorJudgeRiskFactorRsEntity::getExperimentIndicatorJudgeRiskFactorId, experimentIndicatorJudgeRiskFactorIdList)
            .list()
            .forEach(experimentIndicatorJudgeRiskFactorRsEntity -> {
              mapJudgeItems.put(experimentIndicatorJudgeRiskFactorRsEntity.getIndicatorJudgeRiskFactorId(),BigDecimal.ZERO);
              kExperimentIndicatorJudgeRiskFactorIdVExperimentIndicatorJudgeRiskFactorRsEntityMap.put(experimentIndicatorJudgeRiskFactorRsEntity.getExperimentIndicatorJudgeRiskFactorId(), experimentIndicatorJudgeRiskFactorRsEntity);
            });
    AtomicInteger atomicIntegerCount = new AtomicInteger(1);
    ExperimentIndicatorJudgeRiskFactorReportRsEntity experimentIndicatorJudgeRiskFactorReportRsEntity = experimentIndicatorJudgeRiskFactorReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getOperateFlowId, operateFlowId)
        .orderByDesc(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getCount)
        .last(EnumString.LIMIT_1.getStr())
        .one();
    if (Objects.nonNull(experimentIndicatorJudgeRiskFactorReportRsEntity)) {
      Integer count = experimentIndicatorJudgeRiskFactorReportRsEntity.getCount();
      atomicIntegerCount.set(count+1);
    }
    kExperimentIndicatorJudgeRiskFactorIdVExperimentIndicatorJudgeRiskFactorRsEntityMap.forEach((experimentIndicatorJudgeRiskFactorId, experimentIndicatorJudgeRiskFactorRsEntity) -> {
      experimentIndicatorJudgeRiskFactorReportRsEntityList.add(
          ExperimentIndicatorJudgeRiskFactorReportRsEntity
              .builder()
              .experimentIndicatorJudgeRiskFactorReportId(idGenerator.nextIdStr())
              .experimentId(experimentId)
              .appId(appId)
              .period(periods)
              .indicatorFuncId(indicatorFuncId)
              .experimentPersonId(experimentPersonId)
              .operateFlowId(operateFlowId)
              .name(experimentIndicatorJudgeRiskFactorRsEntity.getName())
              .count(atomicIntegerCount.get())
              .experimentIndicatorJudgeRiskFactorId(experimentIndicatorJudgeRiskFactorId)
              .indicatorCategoryNameArray(experimentIndicatorJudgeRiskFactorRsEntity.getIndicatorCategoryNameArray())
              .build()
      );
    });
    AssertUtil.falseThenThrow(evalJudgeScoreBiz.saveJudgeScore4Func(exptValidator, flowValidator.getOperateFlowId(), timePoint,
                    mapJudgeItems, EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE))
            .throwMessage("操作准确度得分保存失败");
    experimentIndicatorJudgeRiskFactorReportRsService.saveOrUpdateBatch(experimentIndicatorJudgeRiskFactorReportRsEntityList);
  }

  public List<ExperimentRiskFactorReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId, Integer periods) {
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    if(ShareUtil.XObject.isEmpty(operateFlowId)){
      return Collections.emptyList();
    }
    ExperimentIndicatorJudgeRiskFactorReportRsEntity experimentIndicatorJudgeRiskFactorReportRsEntity = experimentIndicatorJudgeRiskFactorReportRsService.lambdaQuery()
            .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getAppId, appId)
            .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentId, experimentId)
            .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getPeriod, periods)
            .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getIndicatorFuncId, indicatorFuncId)
            .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentPersonId, experimentPersonId)
            .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getOperateFlowId, operateFlowId)
            .orderByDesc(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getCount)
            .select(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getCount)
            .last(EnumString.LIMIT_1.getStr())
            .one();
    if (Objects.isNull(experimentIndicatorJudgeRiskFactorReportRsEntity)) {
      return new ArrayList<>();
    }
    Integer count = experimentIndicatorJudgeRiskFactorReportRsEntity.getCount();
    return experimentIndicatorJudgeRiskFactorReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getOperateFlowId, operateFlowId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getCount, count)
        .orderByAsc(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorJudgeRiskFactorReportRsBiz::experimentRiskFactorReport2ResponseRs)
        .collect(Collectors.toList());
  }
}
