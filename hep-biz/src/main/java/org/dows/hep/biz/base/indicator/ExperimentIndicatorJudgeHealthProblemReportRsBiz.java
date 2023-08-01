package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentHealthProblemCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentHealthProblemReportResponseRs;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemReportRsEntity;
import org.dows.hep.entity.ExperimentIndicatorJudgeHealthProblemRsEntity;
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

    String operateFlowId = ShareBiz.assertRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    Map<String, ExperimentIndicatorJudgeHealthProblemRsEntity> kExperimentIndicatorJudgeHealthProblemIdVExperimentIndicatorJudgeHealthProblemRsEntityMap = new HashMap<>();
    if (!experimentIndicatorJudgeHealthProblemIdList.isEmpty()) {
      experimentIndicatorJudgeHealthProblemRsService.lambdaQuery()
          .eq(ExperimentIndicatorJudgeHealthProblemRsEntity::getAppId, appId)
          .in(ExperimentIndicatorJudgeHealthProblemRsEntity::getExperimentIndicatorJudgeHealthProblemId, experimentIndicatorJudgeHealthProblemIdList)
          .list()
          .forEach(experimentIndicatorJudgeHealthProblemRsEntity -> {
            kExperimentIndicatorJudgeHealthProblemIdVExperimentIndicatorJudgeHealthProblemRsEntityMap.put(experimentIndicatorJudgeHealthProblemRsEntity.getExperimentIndicatorJudgeHealthProblemId(), experimentIndicatorJudgeHealthProblemRsEntity);
          });
    }
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
