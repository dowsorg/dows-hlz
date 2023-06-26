package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.PhysicalExamCheckRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.ExperimentIndicatorViewPhysicalExamRsException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorViewPhysicalExamReportRsBiz {
  private final ExperimentIndicatorViewPhysicalExamReportRsService experimentIndicatorViewPhysicalExamReportRsService;
  private final ExperimentIndicatorViewPhysicalExamRsService experimentIndicatorViewPhysicalExamRsService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;
  private final ExperimentIndicatorExpressionInfluenceRsService experimentIndicatorExpressionInfluenceRsService;
  @Transactional(rollbackFor = Exception.class)
  public void physicalExamCheck(PhysicalExamCheckRequestRs physicalExamCheckRequestRs) {
    /* runsix:TODO 这个期数后期根据张亮接口拿 */
    Integer period = 1;
    String experimentPersonId = physicalExamCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = physicalExamCheckRequestRs.getIndicatorFuncId();
    String experimentIndicatorViewPhysicalExamId = physicalExamCheckRequestRs.getExperimentIndicatorViewPhysicalExamId();
    String appId = physicalExamCheckRequestRs.getAppId();
    String experimentId = physicalExamCheckRequestRs.getExperimentId();
    String resultExplain = null;
    ExperimentIndicatorViewPhysicalExamRsEntity experimentIndicatorViewPhysicalExamRsEntity = experimentIndicatorViewPhysicalExamRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewPhysicalExamRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorViewPhysicalExamRsEntity::getExperimentIndicatorViewPhysicalExamId, experimentIndicatorViewPhysicalExamId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("ExperimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck experimentIndicatorViewPhysicalExamId:{} is illegal", experimentIndicatorViewPhysicalExamId);
          throw new ExperimentIndicatorViewPhysicalExamRsException(EnumESC.VALIDATE_EXCEPTION);
        });
    String indicatorInstanceId = experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId();
    ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, indicatorInstanceId)
        .oneOpt()
        .orElseThrow(() -> {
          log.error("ExperimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck experimentIndicatorInstanceId:{} is illegal", indicatorInstanceId);
          throw new ExperimentIndicatorViewPhysicalExamRsException(EnumESC.VALIDATE_EXCEPTION);
        });
    ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorValRsEntity::getPeriods, period)
        .eq(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, indicatorInstanceId)
        .oneOpt()
        .orElseThrow(() -> {
          log.error("ExperimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck experimentIndicatorInstanceId:{} is illegal", indicatorInstanceId);
          throw new ExperimentIndicatorViewPhysicalExamRsException(EnumESC.VALIDATE_EXCEPTION);
        });
    List<String> experimentIndicatorExpressionIdList = experimentIndicatorExpressionRefRsService.lambdaQuery()
        .eq(ExperimentIndicatorExpressionRefRsEntity::getReasonId, indicatorInstanceId)
        .list()
        .stream()
        .map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId)
        .collect(Collectors.toList());
    if (!experimentIndicatorExpressionIdList.isEmpty()) {
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = experimentIndicatorExpressionRsService.lambdaQuery()
          .eq(ExperimentIndicatorExpressionRsEntity::getExperimentId, experimentId)
          .eq(ExperimentIndicatorExpressionRsEntity::getSource, EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getType())
          .in(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, experimentIndicatorExpressionIdList)
          .oneOpt()
          .orElseThrow(() -> {
            log.error("ExperimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck source:{}, experimentIndicatorInstanceId:{} is illegal", EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getType(), indicatorInstanceId);
            throw new ExperimentIndicatorViewPhysicalExamRsException(EnumESC.VALIDATE_EXCEPTION);
          });
      String experimentIndicatorExpressionId = experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
      List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = experimentIndicatorExpressionItemRsService.lambdaQuery()
          .eq(ExperimentIndicatorExpressionItemRsEntity::getAppId, appId)
          .eq(ExperimentIndicatorExpressionItemRsEntity::getExperimentId, experimentId)
          .eq(ExperimentIndicatorExpressionItemRsEntity::getIndicatorExpressionId, experimentIndicatorExpressionId)
          .orderByAsc(ExperimentIndicatorExpressionItemRsEntity::getSeq)
          .list();
      /* runsix:TODO 公式解析放到后面 */
    }
    ExperimentIndicatorViewPhysicalExamReportRsEntity experimentIndicatorViewPhysicalExamReportRsEntity = ExperimentIndicatorViewPhysicalExamReportRsEntity
        .builder()
        .experimentIndicatorViewPhysicalExamReportId(idGenerator.nextIdStr())
        .experimentId(experimentId)
        .appId(appId)
        .period(period)
        .indicatorFuncId(indicatorFuncId)
        .experimentPersonId(experimentPersonId)
        .name(experimentIndicatorViewPhysicalExamRsEntity.getName())
        .fee(experimentIndicatorViewPhysicalExamRsEntity.getFee())
        .currentVal(experimentIndicatorValRsEntity.getCurrentVal())
        .unit(experimentIndicatorInstanceRsEntity.getUnit())
        .resultExplain(resultExplain)
        .build();
    experimentIndicatorViewPhysicalExamReportRsService.saveOrUpdate(experimentIndicatorViewPhysicalExamReportRsEntity);
  }
}
