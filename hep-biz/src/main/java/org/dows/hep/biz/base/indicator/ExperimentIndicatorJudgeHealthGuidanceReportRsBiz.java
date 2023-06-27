package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentHealthGuidanceCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentHealthGuidanceReportResponseRs;
import org.dows.hep.api.enums.EnumString;
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
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;
  private final ExperimentIndicatorExpressionInfluenceRsService experimentIndicatorExpressionInfluenceRsService;

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
    /* runsix:TODO 这个期数后期根据张亮接口拿 */
    Integer period = 1;
    String experimentPersonId = experimentHealthGuidanceCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentHealthGuidanceCheckRequestRs.getIndicatorFuncId();
    List<String> experimentIndicatorJudgeHealthGuidanceIdList = experimentHealthGuidanceCheckRequestRs.getExperimentIndicatorJudgeHealthGuidanceIdList();
    String appId = experimentHealthGuidanceCheckRequestRs.getAppId();
    String experimentId = experimentHealthGuidanceCheckRequestRs.getExperimentId();
    String experimentOrgId = experimentHealthGuidanceCheckRequestRs.getExperimentOrgId();

    String operateFlowId = "1";
    /* runsix:TODO 等实验能跑，才用吴治霖这个 */
//    ExptOrgFlowValidator exptOrgFlowValidator = ExptOrgFlowValidator.create(appId, experimentId, experimentOrgId, experimentPersonId);
//    exptOrgFlowValidator.checkOrgFlow(true);
//    String operateFlowId = exptOrgFlowValidator.getOperateFlowId();
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
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getPeriod, period)
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
              .period(period)
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

  public List<ExperimentHealthGuidanceReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId) {
    /* runsix:TODO 期数当前写死为1,后期从张亮获取 */
    Integer period = 1;
    String operateFlowId = "1";
    experimentPersonId = "1";
//    ExptOrgFlowValidator exptOrgFlowValidator = ExptOrgFlowValidator.create(appId, experimentId, experimentOrgId, experimentPersonId);
//    exptOrgFlowValidator.checkOrgFlow(true);
//    String operateFlowId = exptOrgFlowValidator.getOperateFlowId();
    return experimentIndicatorJudgeHealthGuidanceReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorJudgeHealthGuidanceReportRsEntity::getPeriod, period)
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
