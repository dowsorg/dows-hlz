package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentHealthProblemCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentHealthProblemReportResponseRs;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.eval.EvalJudgeScoreBiz;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.*;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemReportRsEntity;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemRsEntity;
import org.dows.hep.service.*;
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
public class ExperimentIndicatorJudgeHealthProblemReportRsBiz {
  private final ExperimentIndicatorJudgeHealthProblemReportRsService experimentIndicatorJudgeHealthProblemReportRsService;
  private final ExperimentIndicatorJudgeHealthProblemRsService experimentIndicatorJudgeHealthProblemRsService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;
  private final ExperimentIndicatorExpressionInfluenceRsService experimentIndicatorExpressionInfluenceRsService;

  private final EvalJudgeScoreBiz evalJudgeScoreBiz;
  public static ExperimentHealthProblemReportResponseRs experimentHealthProblemReport2ResponseRs(ExperimentIndicatorJudgeHealthProblemReportRsEntity experimentIndicatorJudgeHealthProblemReportRsEntity) {
    if (Objects.isNull(experimentIndicatorJudgeHealthProblemReportRsEntity)) {
      return null;
    }
    return ExperimentHealthProblemReportResponseRs
        .builder()
        .experimentIndicatorJudgeHealthProblemId(experimentIndicatorJudgeHealthProblemReportRsEntity.getExperimentIndicatorJudgeHealthProblemId())
        .indicatorCategoryNameArray(experimentIndicatorJudgeHealthProblemReportRsEntity.getIndicatorCategoryNameArray())
        .name(experimentIndicatorJudgeHealthProblemReportRsEntity.getName())
        .deleted(experimentIndicatorJudgeHealthProblemReportRsEntity.getDeleted())
        .dt(experimentIndicatorJudgeHealthProblemReportRsEntity.getDt())
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public void healthProblemCheck(ExperimentHealthProblemCheckRequestRs experimentHealthProblemCheckRequestRs) {
    List<ExperimentIndicatorJudgeHealthProblemReportRsEntity> experimentIndicatorJudgeHealthProblemReportRsEntityList = new ArrayList<>();
    Integer periods = experimentHealthProblemCheckRequestRs.getPeriods();
    String experimentPersonId = experimentHealthProblemCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentHealthProblemCheckRequestRs.getIndicatorFuncId();
    List<String> experimentIndicatorJudgeHealthProblemIdList = experimentHealthProblemCheckRequestRs.getExperimentIndicatorJudgeHealthProblemIdList();
    String appId = experimentHealthProblemCheckRequestRs.getAppId();
    String experimentId = experimentHealthProblemCheckRequestRs.getExperimentId();
    String experimentOrgId = experimentHealthProblemCheckRequestRs.getExperimentOrgId();

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
    Map<String, ExperimentIndicatorJudgeHealthProblemRsEntity> kExperimentIndicatorJudgeHealthProblemIdVExperimentIndicatorJudgeHealthProblemRsEntityMap = new HashMap<>();
    if (experimentIndicatorJudgeHealthProblemIdList.isEmpty()) {
      experimentIndicatorJudgeHealthProblemReportRsService.lambdaUpdate()
              .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getAppId, appId)
              .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getExperimentId, experimentId)
              .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getPeriod, periods)
              .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getIndicatorFuncId, indicatorFuncId)
              .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getExperimentPersonId, experimentPersonId)
              .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getOperateFlowId, operateFlowId)
              .remove();
      return;
    }
    final Map<String, BigDecimal> mapJudgeItems=new HashMap<>();
    experimentIndicatorJudgeHealthProblemRsService.lambdaQuery()
            .eq(ExperimentIndicatorJudgeHealthProblemRsEntity::getAppId, appId)
            .in(ExperimentIndicatorJudgeHealthProblemRsEntity::getExperimentIndicatorJudgeHealthProblemId, experimentIndicatorJudgeHealthProblemIdList)
            .list()
            .forEach(experimentIndicatorJudgeHealthProblemRsEntity -> {
              mapJudgeItems.put(experimentIndicatorJudgeHealthProblemRsEntity.getIndicatorJudgeHealthProblemId(),BigDecimal.ZERO);
              kExperimentIndicatorJudgeHealthProblemIdVExperimentIndicatorJudgeHealthProblemRsEntityMap.put(experimentIndicatorJudgeHealthProblemRsEntity.getExperimentIndicatorJudgeHealthProblemId(), experimentIndicatorJudgeHealthProblemRsEntity);
            });
    AtomicInteger atomicIntegerCount = new AtomicInteger(1);
    ExperimentIndicatorJudgeHealthProblemReportRsEntity experimentIndicatorJudgeHealthProblemReportRsEntity = experimentIndicatorJudgeHealthProblemReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getOperateFlowId, operateFlowId)
        .orderByDesc(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getCount)
        .last(EnumString.LIMIT_1.getStr())
        .one();
    if (Objects.nonNull(experimentIndicatorJudgeHealthProblemReportRsEntity)) {
      Integer count = experimentIndicatorJudgeHealthProblemReportRsEntity.getCount();
      atomicIntegerCount.set(count+1);
    }
    kExperimentIndicatorJudgeHealthProblemIdVExperimentIndicatorJudgeHealthProblemRsEntityMap.forEach((experimentIndicatorJudgeHealthProblemId, experimentIndicatorJudgeHealthProblemRsEntity) -> {
      experimentIndicatorJudgeHealthProblemReportRsEntityList.add(
          ExperimentIndicatorJudgeHealthProblemReportRsEntity
              .builder()
              .experimentIndicatorJudgeHealthProblemReportId(idGenerator.nextIdStr())
              .experimentId(experimentId)
              .appId(appId)
              .period(periods)
              .indicatorFuncId(indicatorFuncId)
              .experimentPersonId(experimentPersonId)
              .operateFlowId(operateFlowId)
              .name(experimentIndicatorJudgeHealthProblemRsEntity.getName())
              .count(atomicIntegerCount.get())
              .experimentIndicatorJudgeHealthProblemId(experimentIndicatorJudgeHealthProblemId)
              .indicatorCategoryNameArray(experimentIndicatorJudgeHealthProblemRsEntity.getIndicatorCategoryNameArray())
              .build()
      );
    });
    AssertUtil.falseThenThrow(evalJudgeScoreBiz.saveJudgeScore4Func(exptValidator, flowValidator.getOperateFlowId(), timePoint,
                    mapJudgeItems, EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE))
            .throwMessage("操作准确度得分保存失败");
    experimentIndicatorJudgeHealthProblemReportRsService.saveOrUpdateBatch(experimentIndicatorJudgeHealthProblemReportRsEntityList);
  }

  public List<ExperimentHealthProblemReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId, Integer periods) {
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    if(ShareUtil.XObject.isEmpty(operateFlowId)){
      return Collections.emptyList();
    }
    ExperimentIndicatorJudgeHealthProblemReportRsEntity experimentIndicatorJudgeHealthProblemReportRsEntity = experimentIndicatorJudgeHealthProblemReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getOperateFlowId, operateFlowId)
        .orderByDesc(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getCount)
        .last(EnumString.LIMIT_1.getStr())
        .one();
    if (Objects.isNull(experimentIndicatorJudgeHealthProblemReportRsEntity)) {
      return new ArrayList<>();
    }
    Integer count = experimentIndicatorJudgeHealthProblemReportRsEntity.getCount();
    return experimentIndicatorJudgeHealthProblemReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getOperateFlowId, operateFlowId)
        .eq(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getCount, count)
        .orderByAsc(ExperimentIndicatorJudgeHealthProblemReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorJudgeHealthProblemReportRsBiz::experimentHealthProblemReport2ResponseRs)
        .collect(Collectors.toList());
  }
}
