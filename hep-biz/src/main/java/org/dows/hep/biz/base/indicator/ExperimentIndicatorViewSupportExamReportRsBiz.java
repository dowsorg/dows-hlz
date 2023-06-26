package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.ExperimentSupportExamCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentSupportExamReportResponseRs;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
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
public class ExperimentIndicatorViewSupportExamReportRsBiz {
  private final ExperimentIndicatorViewSupportExamReportRsService experimentIndicatorViewSupportExamReportRsService;
  private final ExperimentIndicatorViewSupportExamRsService experimentIndicatorViewSupportExamRsService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;
  private final ExperimentIndicatorExpressionInfluenceRsService experimentIndicatorExpressionInfluenceRsService;

  public static ExperimentSupportExamReportResponseRs experimentSupportExamReport2ResponseRs(ExperimentIndicatorViewSupportExamReportRsEntity experimentIndicatorViewSupportExamReportRsEntity) {
    if (Objects.isNull(experimentIndicatorViewSupportExamReportRsEntity)) {
      return null;
    }
    return ExperimentSupportExamReportResponseRs
        .builder()
        .name(experimentIndicatorViewSupportExamReportRsEntity.getName())
        .fee(experimentIndicatorViewSupportExamReportRsEntity.getFee())
        .currentVal(experimentIndicatorViewSupportExamReportRsEntity.getCurrentVal())
        .unit(experimentIndicatorViewSupportExamReportRsEntity.getUnit())
        .resultExplain(experimentIndicatorViewSupportExamReportRsEntity.getResultExplain())
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public void supportExamCheck(ExperimentSupportExamCheckRequestRs experimentSupportExamCheckRequestRs) {
    List<ExperimentIndicatorViewSupportExamReportRsEntity> experimentIndicatorViewSupportExamReportRsEntityList = new ArrayList<>();
    /* runsix:TODO 这个期数后期根据张亮接口拿 */
    Integer period = 1;
    String experimentPersonId = experimentSupportExamCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentSupportExamCheckRequestRs.getIndicatorFuncId();
    List<String> experimentIndicatorViewSupportExamIdList = experimentSupportExamCheckRequestRs.getExperimentIndicatorViewSupportExamIdList();
    String appId = experimentSupportExamCheckRequestRs.getAppId();
    String experimentId = experimentSupportExamCheckRequestRs.getExperimentId();
    String resultExplain = null;
    Map<String, ExperimentIndicatorViewSupportExamRsEntity> kExperimentIndicatorViewSupportExamIdVExperimentIndicatorViewSupportExamRsEntityMap = new HashMap<>();
    Set<String> indicatorInstanceIdSet = new HashSet<>();
    if (!experimentIndicatorViewSupportExamIdList.isEmpty()) {
      experimentIndicatorViewSupportExamRsService.lambdaQuery()
          .eq(ExperimentIndicatorViewSupportExamRsEntity::getAppId, appId)
          .in(ExperimentIndicatorViewSupportExamRsEntity::getExperimentIndicatorViewSupportExamId, experimentIndicatorViewSupportExamIdList)
          .list()
          .forEach(experimentIndicatorViewSupportExamRsEntity -> {
            indicatorInstanceIdSet.add(experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId());
            kExperimentIndicatorViewSupportExamIdVExperimentIndicatorViewSupportExamRsEntityMap.put(experimentIndicatorViewSupportExamRsEntity.getExperimentIndicatorViewSupportExamId(), experimentIndicatorViewSupportExamRsEntity);
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
    kExperimentIndicatorViewSupportExamIdVExperimentIndicatorViewSupportExamRsEntityMap.forEach((experimentIndicatorViewSupportExamId, experimentIndicatorViewSupportExamRsEntity) -> {
      String indicatorInstanceId = experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId();
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
      experimentIndicatorViewSupportExamReportRsEntityList.add(
          ExperimentIndicatorViewSupportExamReportRsEntity
              .builder()
              .experimentIndicatorViewSupportExamReportId(idGenerator.nextIdStr())
              .experimentId(experimentId)
              .appId(appId)
              .period(period)
              .indicatorFuncId(indicatorFuncId)
              .experimentPersonId(experimentPersonId)
              .name(experimentIndicatorViewSupportExamRsEntity.getName())
              .fee(experimentIndicatorViewSupportExamRsEntity.getFee())
              .currentVal(currentVal)
              .unit(unit)
              .resultExplain(resultExplain)
              .build()
      );
    });
    experimentIndicatorViewSupportExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewSupportExamReportRsEntityList);
  }

  public List<ExperimentSupportExamReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId) {
    /* runsix:TODO 期数当前写死为1,后期从张亮获取 */
    Integer period = 1;
    return experimentIndicatorViewSupportExamReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getExperimentPersonId, experimentPersonId)
        .orderByDesc(ExperimentIndicatorViewSupportExamReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorViewSupportExamReportRsBiz::experimentSupportExamReport2ResponseRs)
        .collect(Collectors.toList());
  }
}
