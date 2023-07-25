package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentRiskFactorCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentRiskFactorReportResponseRs;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.entity.ExperimentIndicatorJudgeRiskFactorReportRsEntity;
import org.dows.hep.entity.ExperimentIndicatorJudgeRiskFactorRsEntity;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    Map<String, ExperimentIndicatorJudgeRiskFactorRsEntity> kExperimentIndicatorJudgeRiskFactorIdVExperimentIndicatorJudgeRiskFactorRsEntityMap = new HashMap<>();
    if (!experimentIndicatorJudgeRiskFactorIdList.isEmpty()) {
      experimentIndicatorJudgeRiskFactorRsService.lambdaQuery()
          .eq(ExperimentIndicatorJudgeRiskFactorRsEntity::getAppId, appId)
          .in(ExperimentIndicatorJudgeRiskFactorRsEntity::getExperimentIndicatorJudgeRiskFactorId, experimentIndicatorJudgeRiskFactorIdList)
          .list()
          .forEach(experimentIndicatorJudgeRiskFactorRsEntity -> {
            kExperimentIndicatorJudgeRiskFactorIdVExperimentIndicatorJudgeRiskFactorRsEntityMap.put(experimentIndicatorJudgeRiskFactorRsEntity.getExperimentIndicatorJudgeRiskFactorId(), experimentIndicatorJudgeRiskFactorRsEntity);
          });
    }
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
    experimentIndicatorJudgeRiskFactorReportRsService.saveOrUpdateBatch(experimentIndicatorJudgeRiskFactorReportRsEntityList);
  }

  public List<ExperimentRiskFactorReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId, Integer periods) {
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    return experimentIndicatorJudgeRiskFactorReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getOperateFlowId, operateFlowId)
        .orderByAsc(ExperimentIndicatorJudgeRiskFactorReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorJudgeRiskFactorReportRsBiz::experimentRiskFactorReport2ResponseRs)
        .collect(Collectors.toList());
  }
}
