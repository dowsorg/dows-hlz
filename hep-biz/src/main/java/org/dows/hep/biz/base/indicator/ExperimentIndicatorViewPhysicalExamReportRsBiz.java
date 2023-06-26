package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentPhysicalExamCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentPhysicalExamReportResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.ExperimentIndicatorViewPhysicalExamRsException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

  public static ExperimentPhysicalExamReportResponseRs experimentPhysicalExamReport2ResponseRs(ExperimentIndicatorViewPhysicalExamReportRsEntity experimentIndicatorViewPhysicalExamReportRsEntity) {
    if (Objects.isNull(experimentIndicatorViewPhysicalExamReportRsEntity)) {
      return null;
    }
    return ExperimentPhysicalExamReportResponseRs
        .builder()
        .name(experimentIndicatorViewPhysicalExamReportRsEntity.getName())
        .fee(experimentIndicatorViewPhysicalExamReportRsEntity.getFee())
        .currentVal(experimentIndicatorViewPhysicalExamReportRsEntity.getCurrentVal())
        .unit(experimentIndicatorViewPhysicalExamReportRsEntity.getUnit())
        .resultExplain(experimentIndicatorViewPhysicalExamReportRsEntity.getResultExplain())
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public void physicalExamCheck(ExperimentPhysicalExamCheckRequestRs experimentPhysicalExamCheckRequestRs) {
    List<ExperimentIndicatorViewPhysicalExamReportRsEntity> experimentIndicatorViewPhysicalExamReportRsEntityList = new ArrayList<>();
    /* runsix:TODO 这个期数后期根据张亮接口拿 */
    Integer period = 1;
    String experimentPersonId = experimentPhysicalExamCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentPhysicalExamCheckRequestRs.getIndicatorFuncId();
    List<String> experimentIndicatorViewPhysicalExamIdList = experimentPhysicalExamCheckRequestRs.getExperimentIndicatorViewPhysicalExamIdList();
    String appId = experimentPhysicalExamCheckRequestRs.getAppId();
    String experimentId = experimentPhysicalExamCheckRequestRs.getExperimentId();
    String resultExplain = null;

    Map<String, ExperimentIndicatorViewPhysicalExamRsEntity> kExperimentIndicatorViewPhysicalExamIdVExperimentIndicatorViewPhysicalExamRsEntityMap = new HashMap<>();
    Set<String> indicatorInstanceIdSet = new HashSet<>();
    if (!experimentIndicatorViewPhysicalExamIdList.isEmpty()) {
      experimentIndicatorViewPhysicalExamRsService.lambdaQuery()
          .eq(ExperimentIndicatorViewPhysicalExamRsEntity::getAppId, appId)
          .in(ExperimentIndicatorViewPhysicalExamRsEntity::getExperimentIndicatorViewPhysicalExamId, experimentIndicatorViewPhysicalExamIdList)
          .list()
          .forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
            indicatorInstanceIdSet.add(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId());
            kExperimentIndicatorViewPhysicalExamIdVExperimentIndicatorViewPhysicalExamRsEntityMap.put(experimentIndicatorViewPhysicalExamRsEntity.getExperimentIndicatorViewPhysicalExamId(), experimentIndicatorViewPhysicalExamRsEntity);
          });
    }
    Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    Map<String, List<String>> kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdListMap = new HashMap<>();
    Set<String> experimentIndicatorExpressionIdSet = new HashSet<>();
    if (!indicatorInstanceIdSet.isEmpty()) {
      experimentIndicatorInstanceRsService.lambdaQuery()
          .in(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, indicatorInstanceIdSet)
          .list()
          .forEach(experimentIndicatorInstanceRsEntity -> {
            kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.put(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(), experimentIndicatorInstanceRsEntity);
          });
      experimentIndicatorValRsService.lambdaQuery()
          .eq(ExperimentIndicatorValRsEntity::getExperimentId, experimentId)
          .eq(ExperimentIndicatorValRsEntity::getPeriods, period)
          .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
          .list()
          .forEach(experimentIndicatorValRsEntity -> {
            kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity);
          });
      experimentIndicatorExpressionRefRsService.lambdaQuery()
          .in(ExperimentIndicatorExpressionRefRsEntity::getReasonId, indicatorInstanceIdSet)
          .list()
          .forEach(experimentIndicatorExpressionRefRsEntity -> {
            String reasonId = experimentIndicatorExpressionRefRsEntity.getReasonId();
            String indicatorExpressionId = experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
            List<String> experimentIndicatorExpressionIdList = kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdListMap.get(reasonId);
            if (Objects.isNull(experimentIndicatorExpressionIdList)) {
              experimentIndicatorExpressionIdList = new ArrayList<>();
            }
            experimentIndicatorExpressionIdList.add(indicatorExpressionId);
            kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdListMap.put(reasonId, experimentIndicatorExpressionIdList);
            experimentIndicatorExpressionIdSet.add(indicatorExpressionId);
          });
    }
    Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
    Set<String> sourceExperimentIndicatorExpressionIdSet = new HashSet<>();
    if (!experimentIndicatorExpressionIdSet.isEmpty()) {
      experimentIndicatorExpressionRsService.lambdaQuery()
          .eq(ExperimentIndicatorExpressionRsEntity::getExperimentId, experimentId)
          .eq(ExperimentIndicatorExpressionRsEntity::getSource, EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getType())
          .in(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, experimentIndicatorExpressionIdSet)
          .list()
          .forEach(experimentIndicatorExpressionRsEntity -> {
            sourceExperimentIndicatorExpressionIdSet.add(experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId());
            kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.put(experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId(), experimentIndicatorExpressionRsEntity);
          });
    }
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    if (!sourceExperimentIndicatorExpressionIdSet.isEmpty()) {
      experimentIndicatorExpressionItemRsService.lambdaQuery()
          .eq(ExperimentIndicatorExpressionItemRsEntity::getAppId, appId)
          .eq(ExperimentIndicatorExpressionItemRsEntity::getExperimentId, experimentId)
          .in(ExperimentIndicatorExpressionItemRsEntity::getIndicatorExpressionId, sourceExperimentIndicatorExpressionIdSet)
          .list()
          .forEach(experimentIndicatorExpressionItemRsEntity -> {
            String indicatorExpressionId = experimentIndicatorExpressionItemRsEntity.getIndicatorExpressionId();
            List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(indicatorExpressionId);
            if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList)) {
              experimentIndicatorExpressionItemRsEntityList = new ArrayList<>();
            }
            experimentIndicatorExpressionItemRsEntityList.add(experimentIndicatorExpressionItemRsEntity);
            kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.put(indicatorExpressionId, experimentIndicatorExpressionItemRsEntityList);
          });
    }
    /* runsix:sort */
    kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.forEach((indicatorExpressionId, experimentIndicatorExpressionItemRsEntityList) -> {
      experimentIndicatorExpressionItemRsEntityList.sort(Comparator.comparingInt(ExperimentIndicatorExpressionItemRsEntity::getSeq));
    });
    /* runsix:TODO 公式解析放到后面 */
    kExperimentIndicatorViewPhysicalExamIdVExperimentIndicatorViewPhysicalExamRsEntityMap.forEach((experimentIndicatorViewPhysicalExamId, experimentIndicatorViewPhysicalExamRsEntity) -> {
      String indicatorInstanceId = experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId();
      String currentVal = null;
      String unit = null;
      ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(indicatorInstanceId);
      ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
      if (Objects.nonNull(experimentIndicatorValRsEntity)) {
        currentVal = experimentIndicatorValRsEntity.getCurrentVal();
      }
      if (Objects.nonNull(experimentIndicatorInstanceRsEntity)) {
        unit = experimentIndicatorInstanceRsEntity.getUnit();
      }
      experimentIndicatorViewPhysicalExamReportRsEntityList.add(
          ExperimentIndicatorViewPhysicalExamReportRsEntity
              .builder()
              .experimentIndicatorViewPhysicalExamReportId(idGenerator.nextIdStr())
              .experimentId(experimentId)
              .appId(appId)
              .period(period)
              .indicatorFuncId(indicatorFuncId)
              .experimentPersonId(experimentPersonId)
              .name(experimentIndicatorViewPhysicalExamRsEntity.getName())
              .fee(experimentIndicatorViewPhysicalExamRsEntity.getFee())
              .currentVal(currentVal)
              .unit(unit)
              .resultExplain(resultExplain)
              .build()
      );
    });
    experimentIndicatorViewPhysicalExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewPhysicalExamReportRsEntityList);
  }

  public List<ExperimentPhysicalExamReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId) {
    /* runsix:TODO 期数当前写死为1,后期从张亮获取 */
    Integer period = 1;
    return experimentIndicatorViewPhysicalExamReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getExperimentPersonId, experimentPersonId)
        .orderByDesc(ExperimentIndicatorViewPhysicalExamReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorViewPhysicalExamReportRsBiz::experimentPhysicalExamReport2ResponseRs)
        .collect(Collectors.toList());
  }
}
