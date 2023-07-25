package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentHealthGuidanceCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentHealthGuidanceReportResponseRs;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthGuidanceReportRsEntity;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthGuidanceRsEntity;
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
public class ExperimentIndicatorJudgeHealthGuidanceReportRsBiz {
  private final ExperimentIndicatorJudgeHealthGuidanceReportRsService experimentIndicatorJudgeHealthGuidanceReportRsService;
  private final ExperimentIndicatorJudgeHealthGuidanceRsService experimentIndicatorJudgeHealthGuidanceRsService;
  private final IdGenerator idGenerator;
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

    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
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
      atomicIntegerCount.set(count+1);
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
