package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.RsIndicatorExpressionException;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsExperimentIndicatorExpressionBiz {
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;

  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;

  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;
  private final RsUtilBiz rsUtilBiz;

  /* runsix:期数反转使用 */
  public void reCalculateAllExperimentIndicatorInstance(
      Map<String, List<ExperimentIndicatorInstanceRsEntity>> kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
      Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap

  ) {
    if (Objects.isNull(kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap)
        || Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
        || Objects.isNull(kReasonIdVExperimentIndicatorExpressionRsEntityListMap)
        || Objects.isNull(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap)
        || Objects.isNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap)
    ) {return;}
    kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.forEach((kExperimentPersonId, experimentIndicatorInstanceRsEntityList) -> {
      experimentIndicatorInstanceRsEntityList.sort(Comparator.comparingInt(ExperimentIndicatorInstanceRsEntity::getRecalculateSeq));
      /* runsix:吴治霖那块的改变 */
      experimentIndicatorInstanceRsEntityList.forEach(experimentIndicatorInstanceRsEntity -> {
        String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        Double changeVal = experimentIndicatorInstanceRsEntity.getChangeVal();
        if (Objects.nonNull(experimentIndicatorValRsEntity) && NumberUtils.isCreatable(experimentIndicatorValRsEntity.getCurrentVal())) {
          experimentIndicatorValRsEntity.setCurrentVal(String.valueOf(Double.parseDouble(experimentIndicatorValRsEntity.getCurrentVal()) + changeVal));
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorInstanceId, experimentIndicatorValRsEntity);
        }
      });
      experimentIndicatorInstanceRsEntityList.forEach(experimentIndicatorInstanceRsEntity -> {
        String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity)) {return;}
        AtomicReference<String> newCurrentValAtomicReference = new AtomicReference<>();
        List<ExperimentIndicatorExpressionRsEntity> experimentIndicatorExpressionRsEntityList = kReasonIdVExperimentIndicatorExpressionRsEntityListMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorExpressionRsEntityList) || experimentIndicatorExpressionRsEntityList.isEmpty()) {return;}
        /* runsix:指标管理的指标，只允许有一个公式 */
        ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = experimentIndicatorExpressionRsEntityList.get(0);
        String experimentIndicatorExpressionId = experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
        if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {return;}
        ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = null;
        String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
        if (StringUtils.isNotBlank(minIndicatorExpressionItemId) && Objects.nonNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId))) {
          minExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId);
        }
        ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = null;
        String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
        if (StringUtils.isNotBlank(maxIndicatorExpressionItemId) && Objects.nonNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId))) {
          maxExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId);
        }
        this.parseExperimentIndicatorExpression(
            EnumIndicatorExpressionField.EXPERIMENT.getField(),
            EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource(),
            EnumIndicatorExpressionScene.EXPERIMENT_RE_CALCULATE.getScene(),
            newCurrentValAtomicReference,
            new HashMap<>(),
            DatabaseCalIndicatorExpressionRequest.builder().build(),
            CaseCalIndicatorExpressionRequest.builder().build(),
            ExperimentCalIndicatorExpressionRequest
                .builder()
                .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
                .experimentIndicatorExpressionRsEntity(experimentIndicatorExpressionRsEntity)
                .experimentIndicatorExpressionItemRsEntityList(experimentIndicatorExpressionItemRsEntityList)
                .minExperimentIndicatorExpressionItemRsEntity(minExperimentIndicatorExpressionItemRsEntity)
                .maxExperimentIndicatorExpressionItemRsEntity(maxExperimentIndicatorExpressionItemRsEntity)
                .build()
        );
        String newCurrentVal = newCurrentValAtomicReference.get();
        if (StringUtils.isBlank(newCurrentVal)) {return;}
        experimentIndicatorValRsEntity.setCurrentVal(newCurrentVal);
        kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorInstanceId, experimentIndicatorValRsEntity);
      });
    });
  }

  public void populateKExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValMap(
      Map<String, Map<String, ExperimentIndicatorValRsEntity>> kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      Set<String> experimentPersonIdSet,
      Integer periods
  ) {
    if (Objects.isNull(kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
        || Objects.isNull(experimentPersonIdSet) || experimentPersonIdSet.isEmpty()
    ) {
      return;
    }
    if (Objects.isNull(periods)) {
      /* runsix:TODO 等张亮期数接口 */
      periods = 1;
    }
    experimentPersonIdSet.forEach(experimentPersonId -> {
      kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(
          experimentPersonId, new HashMap<>()
      );
    });

    Map<String, Set<String>> kExperimentPersonIdVExperimentIndicatorInstanceIdSetMap = new HashMap<>();
    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    experimentIndicatorInstanceRsService.lambdaQuery()
        .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());

          String experimentPersonId = experimentIndicatorInstanceRsEntity.getExperimentPersonId();
          Set<String> experimentIndicatorInstanceIdSet0 = kExperimentPersonIdVExperimentIndicatorInstanceIdSetMap.get(experimentPersonId);
          if (Objects.isNull(experimentIndicatorInstanceIdSet0)) {
            experimentIndicatorInstanceIdSet0 = new HashSet<>();
          }
          experimentIndicatorInstanceIdSet0.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
          kExperimentPersonIdVExperimentIndicatorInstanceIdSetMap.put(experimentPersonId, experimentIndicatorInstanceIdSet0);
        });

    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    if (experimentIndicatorInstanceIdSet.isEmpty()) {
      return;
    }
    experimentIndicatorValRsService.lambdaQuery()
      .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
      .list()
      .forEach(experimentIndicatorValRsEntity -> {
        kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity);
    });


    kExperimentPersonIdVExperimentIndicatorInstanceIdSetMap.forEach((experimentPersonId, experimentIndicatorInstanceIdSet1) -> {
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap0 = kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentPersonId);
      experimentIndicatorInstanceIdSet1.forEach(experimentIndicatorInstanceId -> {
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.nonNull(experimentIndicatorValRsEntity)) {
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap0.put(experimentIndicatorInstanceId, experimentIndicatorValRsEntity);
        }
      });

      kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(
          experimentPersonId, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap0
      );
    });
  }

  public void populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap,
      Set<String> experimentIndicatorExpressionIdSet) {
    if (Objects.isNull(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap)) {
      return;
    }
    if (Objects.isNull(experimentIndicatorExpressionIdSet) || experimentIndicatorExpressionIdSet.isEmpty()) {
      return;
    }
    experimentIndicatorExpressionItemRsService.lambdaQuery()
        .in(ExperimentIndicatorExpressionItemRsEntity::getIndicatorExpressionId, experimentIndicatorExpressionIdSet)
        .list()
        .forEach(experimentIndicatorExpressionItemRsEntity -> {
          String experimentIndicatorExpressionId = experimentIndicatorExpressionItemRsEntity.getIndicatorExpressionId();
          List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.get(experimentIndicatorExpressionId);
          if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList)) {
            experimentIndicatorExpressionItemRsEntityList = new ArrayList<>();
          }
          experimentIndicatorExpressionItemRsEntityList.add(experimentIndicatorExpressionItemRsEntity);
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.put(experimentIndicatorExpressionId, experimentIndicatorExpressionItemRsEntityList);
        });
  }

  public void populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(
      Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentReasonIdVExperimentIndicatorExpressionRefListMap,
      Set<String> reasonIdSet
  ) {
    if (Objects.isNull(kExperimentReasonIdVExperimentIndicatorExpressionRefListMap) || Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()) {
      return;
    }
    experimentIndicatorExpressionRefRsService.lambdaQuery()
        .in(ExperimentIndicatorExpressionRefRsEntity::getReasonId, reasonIdSet)
        .list()
        .forEach(experimentIndicatorExpressionRefRsEntity -> {
          String reasonId = experimentIndicatorExpressionRefRsEntity.getReasonId();
          List<ExperimentIndicatorExpressionRefRsEntity> experimentIndicatorExpressionRefRsEntityList = kExperimentReasonIdVExperimentIndicatorExpressionRefListMap.get(reasonId);
          if (Objects.isNull(experimentIndicatorExpressionRefRsEntityList)) {
            experimentIndicatorExpressionRefRsEntityList = new ArrayList<>();
          }
          experimentIndicatorExpressionRefRsEntityList.add(experimentIndicatorExpressionRefRsEntity);
          kExperimentReasonIdVExperimentIndicatorExpressionRefListMap.put(reasonId, experimentIndicatorExpressionRefRsEntityList);
        });
  }

  public void populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
      Map<String, Map<String, Boolean>> kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap,
      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap,
      Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
  ) {
    if (Objects.isNull(kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap)
        || Objects.isNull(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap)
        || Objects.isNull(kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)
        || Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
    ) {
      return;
    }
    kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap.forEach((experimentPersonId, kExperimentIndicatorExpressionIdVAtomicBooleanMap) -> {
      if (Objects.isNull(kExperimentIndicatorExpressionIdVAtomicBooleanMap) || kExperimentIndicatorExpressionIdVAtomicBooleanMap.isEmpty()) {
        return;
      }
      kExperimentIndicatorExpressionIdVAtomicBooleanMap.forEach((experimentIndicatorExpressionId, resultBoolean) -> {
        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.get(experimentIndicatorExpressionId);
        if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {
          resultBoolean = Boolean.FALSE;
        } else {
          ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = experimentIndicatorExpressionItemRsEntityList.get(0);
          String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
          String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
          String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
          if (StringUtils.isAllBlank(conditionExpression, conditionNameList, conditionValList)) {
            resultBoolean = Boolean.FALSE;
          } else {
            try {
              rsUtilBiz.checkConditionNameAndValSize(conditionNameList, conditionValList);
              List<String> conditionNameSplitList = rsUtilBiz.getConditionNameSplitList(conditionNameList);
              List<String> conditionValSplitList = rsUtilBiz.getConditionValSplitList(conditionValList);
              StandardEvaluationContext context = new StandardEvaluationContext();
              boolean needParse = true;
              for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
                String indicatorInstanceId = conditionValSplitList.get(i);
                Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceId = kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
                if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceId) || Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceId.get(indicatorInstanceId))) {
                  log.error("experimentPersonId:{}, indicatorInstanceId:{} has no experimentIndicatorInstanceId", experimentPersonId, indicatorInstanceId);
                  resultBoolean = Boolean.FALSE;
                  needParse = false;
                  break;
                } else {
                  String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceId.get(indicatorInstanceId);
                  ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
                  if (Objects.isNull(experimentIndicatorValRsEntity)) {
                    resultBoolean = Boolean.FALSE;
                    needParse = false;
                    break;
                  } else {
                    String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
                    boolean isValDigital = NumberUtils.isCreatable(currentVal);
                    if (isValDigital) {
                      context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)));
                    } else {
                      context.setVariable(conditionNameSplitList.get(i), currentVal);
                    }
                  }
                }
              }
              if (!needParse) {
                return;
              }
              ExpressionParser parser = new SpelExpressionParser();
              Expression expression = parser.parseExpression(conditionExpression);
              Boolean conditionResult = expression.getValue(context, Boolean.class);
              resultBoolean = Boolean.TRUE.equals(conditionResult);
            } catch (Throwable throwable) {
              log.error("experimentIndicatorExpressionId:{}, checkConditionNameAndValSize failed", experimentIndicatorExpressionId);
              resultBoolean = Boolean.FALSE;
            }
          }
        }
        kExperimentIndicatorExpressionIdVAtomicBooleanMap.put(experimentIndicatorExpressionId, resultBoolean);
        kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap.put(experimentPersonId, kExperimentIndicatorExpressionIdVAtomicBooleanMap);
      });
    });
  }



  /* runsix:indicatorExpression is just a condition  */
  private void ePIEConditionType(
      Map<String, Map<String, Boolean>> kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap,
      Integer period
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap) || kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap.isEmpty()) {
      return;
    }
    Set<String> experimentPersonIdSet = new HashSet<>();
    Set<String> experimentIndicatorExpressionIdSet = new HashSet<>();
    kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap.forEach((experimentPersonId, kExperimentIndicatorExpressionIdVResultBooleanMap) -> {
      experimentPersonIdSet.add(experimentPersonId);
      kExperimentIndicatorExpressionIdVResultBooleanMap.forEach((experimentIndicatorExpressionId, resultBoolean) -> {
        experimentIndicatorExpressionIdSet.add(experimentIndicatorExpressionId);
      });
    });

    Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    CompletableFuture<Void> populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMapByExperimentPersonIdSetCF = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorInstanceBiz.populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap(
          kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, experimentPersonIdSet);
    });
    populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMapByExperimentPersonIdSetCF.get();

    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.values().forEach(IndicatorInstanceIdVExperimentIndicatorInstanceIdMap -> {
      if (Objects.nonNull(IndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
        IndicatorInstanceIdVExperimentIndicatorInstanceIdMap.values().stream().filter(StringUtils::isNotBlank).forEach(experimentIndicatorInstanceIdSet::add);
      }
    });
    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> populateKExperimentIndicatorInstanceIdVExperimentIndicatorValMapCF = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorInstanceBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorValMap(
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, period, experimentIndicatorInstanceIdSet);
    });
    populateKExperimentIndicatorInstanceIdVExperimentIndicatorValMapCF.get();

    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap = new HashMap<>();
    CompletableFuture<Void> populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMapCF = CompletableFuture.runAsync(() -> {
      populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap, experimentIndicatorExpressionIdSet);
    });
    populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMapCF.get();

    CompletableFuture<Void> populateKExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMapCF = CompletableFuture.runAsync(() -> {
      populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
          kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap,
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap,
          kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
      );
    });
    populateKExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMapCF.get();
  }

  private String ePIEResultUsingExperimentIndicatorInstanceId(
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
  ) {
    try {
      String resultExpression = experimentIndicatorExpressionItemRsEntity.getResultExpression();
      String resultNameList = experimentIndicatorExpressionItemRsEntity.getResultNameList();
      List<String> resultNameSplitList = rsUtilBiz.getResultNameSplitList(resultNameList);
      String resultValList = experimentIndicatorExpressionItemRsEntity.getResultValList();
      List<String> resultValSplitList = rsUtilBiz.getResultValSplitList(resultValList);
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      if (StringUtils.isBlank(resultExpression)) {
        return RsUtilBiz.RESULT_DROP;
      }
      for (int i = 0; i <= resultNameSplitList.size() - 1; i++) {
        String experimentIndicatorInstanceId = resultValSplitList.get(i);
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity) || StringUtils.isBlank(experimentIndicatorValRsEntity.getCurrentVal())) {
          return RsUtilBiz.RESULT_DROP;
        }
        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(resultNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(resultNameSplitList.get(i), currentVal);
        }
      }
      return expression.getValue(context, String.class);
    } catch(Exception e) {
      log.error("RsIndicatorExpressionBiz.ePIEResultUsingExperimentIndicatorInstanceId", e);
      return RsUtilBiz.RESULT_DROP;
    }
  }

  private String ePIEResultUsingIndicatorInstanceId(
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
  ) {
    try {
      String resultExpression = experimentIndicatorExpressionItemRsEntity.getResultExpression();
      String resultNameList = experimentIndicatorExpressionItemRsEntity.getResultNameList();
      List<String> resultNameSplitList = rsUtilBiz.getResultNameSplitList(resultNameList);
      String resultValList = experimentIndicatorExpressionItemRsEntity.getResultValList();
      List<String> resultValSplitList = rsUtilBiz.getResultValSplitList(resultValList);
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      if (StringUtils.isBlank(resultExpression)) {
        return RsUtilBiz.RESULT_DROP;
      }
      for (int i = 0; i <= resultNameSplitList.size() - 1; i++) {
        String indicatorInstanceId = resultValSplitList.get(i);
        String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
        if (StringUtils.isBlank(experimentIndicatorInstanceId)) {
          return RsUtilBiz.RESULT_DROP;
        }
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity) || StringUtils.isBlank(experimentIndicatorValRsEntity.getCurrentVal())) {
          return RsUtilBiz.RESULT_DROP;
        }
        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(resultNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(resultNameSplitList.get(i), currentVal);
        }
      }
      return expression.getValue(context, String.class);
    } catch(Exception e) {
      log.error("RsIndicatorExpressionBiz.ePIEResultUsingExperimentIndicatorInstanceId", e);
      return RsUtilBiz.RESULT_DROP;
    }
  }

  private boolean ePIEConditionUsingExperimentIndicatorInstanceId(
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
      ) {
    try {
      String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
      String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
      List<String> conditionNameSplitList = rsUtilBiz.getConditionNameSplitList(conditionNameList);
      String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
      List<String> conditionValSplitList = rsUtilBiz.getConditionValSplitList(conditionValList);
      if (StringUtils.isBlank(conditionExpression)) {
        return true;
      }
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(conditionExpression);
      for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
        String experimentIndicatorInstanceId = conditionValSplitList.get(i);
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity) || StringUtils.isBlank(experimentIndicatorValRsEntity.getCurrentVal())) {
          return false;
        }
        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(conditionNameSplitList.get(i), currentVal);
        }
      }
      return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    } catch(Exception e) {
      log.error("RsIndicatorExpressionBiz.ePIEConditionUsingExperimentIndicatorInstanceId", e);
      return false;
    }
  }

  private boolean ePIEConditionUsingIndicatorInstanceId(
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
  ) {
    try {
      String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
      String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
      List<String> conditionNameSplitList = rsUtilBiz.getConditionNameSplitList(conditionNameList);
      String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
      List<String> conditionValSplitList = rsUtilBiz.getConditionValSplitList(conditionValList);
      if (StringUtils.isBlank(conditionExpression)) {
        return true;
      }
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(conditionExpression);
      for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
        String indicatorInstanceId = conditionValSplitList.get(i);
        String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
        if (StringUtils.isBlank(experimentIndicatorInstanceId)) {
          return false;
        }
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity) || StringUtils.isBlank(experimentIndicatorValRsEntity.getCurrentVal())) {
          return false;
        }
        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(conditionNameSplitList.get(i), currentVal);
        }
      }
      return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    } catch(Exception e) {
      log.error("RsIndicatorExpressionBiz.ePIEConditionUsingIndicatorInstanceId", e);
      return false;
    }
  }

  /**
   * runsix method process
   * only handle digit
  */
  private void minAndMaxHandle(
      AtomicReference<String> resultAtomicReference,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
  ) {
    String result = resultAtomicReference.get();
    if (StringUtils.isBlank(result)) {
      return;
    }
    if (!NumberUtils.isCreatable(result)) {
      return;
    }
    try {
      String min = minExperimentIndicatorExpressionItemRsEntity.getResultRaw();
      if (NumberUtils.isCreatable(min) && BigDecimal.valueOf(Double.parseDouble(result)).compareTo(BigDecimal.valueOf(Double.parseDouble(min))) < 0) {
        result = min;
      }
      String max = maxExperimentIndicatorExpressionItemRsEntity.getResultRaw();
      if (NumberUtils.isCreatable(max) && BigDecimal.valueOf(Double.parseDouble(result)).compareTo(BigDecimal.valueOf(Double.parseDouble(min))) > 0) {
        result = max;
      }
      resultAtomicReference.set(result);
    } catch (Exception e) {
      log.error("RsIndicatorExpressionBiz.minAndMaxHandle", e);
      return;
    }
  }

  /**
   * runsix method process
   * 1.如果解析的结果是空，则将结果赋值为指标默认值
   * 2.上下限处理
  */
  private void physicalExamHandleParsedResult(
      AtomicReference<String> resultAtomicReference,
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
  ) {
    String result = resultAtomicReference.get();
    /* runsix:1.如果解析的结果是空，则将结果赋值为指标默认值 */
    if (StringUtils.isBlank(result)) {
      /* runsix: TODO must optimize */
      String principalId = experimentIndicatorExpressionRsEntity.getPrincipalId();
      ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = experimentIndicatorInstanceRsService.lambdaQuery()
          .eq(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId, principalId)
          .one();
      if (Objects.isNull(experimentIndicatorInstanceRsEntity)) {
        resultAtomicReference.set("");
      } else {
        resultAtomicReference.set(experimentIndicatorInstanceRsEntity.getDef());
      }
    }
    /* runsix:2.上下限处理 */
    minAndMaxHandle(resultAtomicReference, minExperimentIndicatorExpressionItemRsEntity, maxExperimentIndicatorExpressionItemRsEntity);
  }

  private void riskModelHandleParsedResult(
      AtomicReference<String> resultAtomicReference,
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
  ) {

  }

  private void supportExamHandleParsedResult(
      AtomicReference<String> resultAtomicReference,
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
  ) {

  }

  private void handleParsedResult(
      AtomicReference<String> resultAtomicReference,
      Integer scene,
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
  ) {
    EnumIndicatorExpressionScene enumIndicatorExpressionScene = EnumIndicatorExpressionScene.getByScene(scene);
    /* runsix:如果公式使用场景不明确，不做特殊处理，直接返回 */
    if (Objects.isNull(enumIndicatorExpressionScene)) {
      return;
    }
    switch (enumIndicatorExpressionScene) {
      case PHYSICAL_EXAM -> physicalExamHandleParsedResult(
          resultAtomicReference, experimentIndicatorExpressionRsEntity, minExperimentIndicatorExpressionItemRsEntity, maxExperimentIndicatorExpressionItemRsEntity
      );
      case SUPPORT_EXAM -> supportExamHandleParsedResult(
          resultAtomicReference, experimentIndicatorExpressionRsEntity, minExperimentIndicatorExpressionItemRsEntity, maxExperimentIndicatorExpressionItemRsEntity
      );
      case RISK_MODEL -> riskModelHandleParsedResult(
          resultAtomicReference, experimentIndicatorExpressionRsEntity, minExperimentIndicatorExpressionItemRsEntity, maxExperimentIndicatorExpressionItemRsEntity
      );
      default -> {
        /* runsix:如果公式使用场景不明确，不做特殊处理，直接返回 */
        return;
      }
    }
  }

  /**
   * runsix method process
   * 实验解析指标公式-解析ExperimentIndicatorExpressionItemRsEntity单条结果
   * 1.按顺序解析每一个公式
   * 2.处理解析后的结果
  */
  public void ePIEResultUsingExperimentIndicatorInstanceIdCombineWithHandle(
      Integer scene, AtomicReference<String> resultAtomicReference,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
      ) {
    /* runsix:1.按顺序解析每一个公式 */
    experimentIndicatorExpressionItemRsEntityList.sort(Comparator.comparingInt(ExperimentIndicatorExpressionItemRsEntity::getSeq));
    for (int i = 0; i <= experimentIndicatorExpressionItemRsEntityList.size()-1; i++) {
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = experimentIndicatorExpressionItemRsEntityList.get(i);
      boolean hasResult = ePIEResultUsingExperimentIndicatorInstanceIdCombineWithoutHandle(
          resultAtomicReference, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, experimentIndicatorExpressionItemRsEntity
      );
      if (hasResult) {
        /* runsix:2.处理解析后的结果 */
        handleParsedResult(
            resultAtomicReference, scene, experimentIndicatorExpressionRsEntity, minExperimentIndicatorExpressionItemRsEntity, maxExperimentIndicatorExpressionItemRsEntity
        );
        break;
      }
    }
  }

  public void ePIEResultUsingIndicatorInstanceIdCombineWithHandle(
      Integer scene, AtomicReference<String> resultAtomicReference,
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
  ) {
    /* runsix:1.按顺序解析每一个公式 */
    experimentIndicatorExpressionItemRsEntityList.sort(Comparator.comparingInt(ExperimentIndicatorExpressionItemRsEntity::getSeq));
    for (int i = 0; i <= experimentIndicatorExpressionItemRsEntityList.size()-1; i++) {
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = experimentIndicatorExpressionItemRsEntityList.get(i);
      boolean hasResult = ePIEResultUsingIndicatorInstanceIdCombineWithoutHandle(
          resultAtomicReference, kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, experimentIndicatorExpressionItemRsEntity
      );
      if (hasResult) {
        /* runsix:2.处理解析后的结果 */
        handleParsedResult(
            resultAtomicReference, scene, experimentIndicatorExpressionRsEntity, minExperimentIndicatorExpressionItemRsEntity, maxExperimentIndicatorExpressionItemRsEntity
        );
        break;
      }
    }
  }

  /**
   * runsix method process
   * 1.解析条件
   * 2.如果条件不满足，不解析结果，继续下一个
   * 3.如果一个公式有结果就跳出
  */
  private boolean ePIEResultUsingExperimentIndicatorInstanceIdCombineWithoutHandle(
      AtomicReference<String> resultAtomicReference,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity
  ) {
    boolean parsedCondition = ePIEConditionUsingExperimentIndicatorInstanceId(experimentIndicatorExpressionItemRsEntity, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap);
    /* runsix:2.如果条件不满足，不解析结果，继续下一个 */
    if (!parsedCondition) {
      return false;
    }

    /* runsix:3.如果一个公式有结果就跳出 */
    String parsedResult = ePIEResultUsingExperimentIndicatorInstanceId(experimentIndicatorExpressionItemRsEntity, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap);
    if (RsUtilBiz.RESULT_DROP.equals(parsedResult)) {
      return false;
    }
    resultAtomicReference.set(parsedResult);
    return true;
  }

  private boolean ePIEResultUsingIndicatorInstanceIdCombineWithoutHandle(
      AtomicReference<String> resultAtomicReference,
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity
  ) {
    boolean parsedCondition = ePIEConditionUsingIndicatorInstanceId(kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, experimentIndicatorExpressionItemRsEntity, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap);
    /* runsix:2.如果条件不满足，不解析结果，继续下一个 */
    if (!parsedCondition) {
      return false;
    }

    /* runsix:3.如果一个公式有结果就跳出 */
    String parsedResult = ePIEResultUsingIndicatorInstanceId(kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, experimentIndicatorExpressionItemRsEntity, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap);
    if (RsUtilBiz.RESULT_DROP.equals(parsedResult)) {
      return false;
    }
    resultAtomicReference.set(parsedResult);
    return true;
  }

  private void ePIEIndicatorManagement(
      Integer scene, AtomicReference<String> resultAtomicReference,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
  ) {
    ePIEResultUsingExperimentIndicatorInstanceIdCombineWithHandle(
        scene, resultAtomicReference,
        kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
        experimentIndicatorExpressionRsEntity,
        experimentIndicatorExpressionItemRsEntityList,
        minExperimentIndicatorExpressionItemRsEntity,
        maxExperimentIndicatorExpressionItemRsEntity
        );
  }

  /* runsix:TODO 这个可以留到下个版本做 */
  private void ePIEIndicatorJudgeRiskFactor() {
  }

  /* runsix:now it is just a condition */
  private void ePIECrowds(
      AtomicReference<String> resultAtomicReference,
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
  ) {
    if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {return;}
    /* runsix:人群类型只能有一个公式，并且公式只有一个条件 */
    boolean result = ePIEConditionUsingIndicatorInstanceId(
        kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
        experimentIndicatorExpressionItemRsEntityList.get(0),
        kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
    );
    resultAtomicReference.set(String.valueOf(result));
  }

  /* runsix:目前计算危险模型与指标管理的指标一样 */
  private void ePIERiskModel(Integer scene, AtomicReference<String> resultAtomicReference, Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity, List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList, ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity, ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity) {
    ePIEResultUsingIndicatorInstanceIdCombineWithHandle(
        scene,
        resultAtomicReference,
        kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
        kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
        experimentIndicatorExpressionRsEntity,
        experimentIndicatorExpressionItemRsEntityList,
        minExperimentIndicatorExpressionItemRsEntity,
        maxExperimentIndicatorExpressionItemRsEntity
    );
  }

  public void populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(
      Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap,
      Set<String> experimentIndicatorExpressionItemIdSet
  ) {
    if (Objects.isNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap)
        || Objects.isNull(experimentIndicatorExpressionItemIdSet) || experimentIndicatorExpressionItemIdSet.isEmpty()
    ) {
      return;
    }
    experimentIndicatorExpressionItemRsService.lambdaQuery()
        .in(ExperimentIndicatorExpressionItemRsEntity::getExperimentIndicatorExpressionItemId, experimentIndicatorExpressionItemIdSet)
        .list()
        .forEach(experimentIndicatorExpressionItemRsEntity -> {
          kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.put(
              experimentIndicatorExpressionItemRsEntity.getExperimentIndicatorExpressionItemId(), experimentIndicatorExpressionItemRsEntity
          );
        });
  }

  public void parseExperimentIndicatorExpression(
      Integer field, Integer source, Integer scene,
      AtomicReference<String> resultAtomicReference,
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      DatabaseCalIndicatorExpressionRequest databaseCalIndicatorExpressionRequest,
      CaseCalIndicatorExpressionRequest caseCalIndicatorExpressionRequest,
      ExperimentCalIndicatorExpressionRequest experimentCalIndicatorExpressionRequest
      ) {
    rsUtilBiz.checkField(field);
    rsUtilBiz.checkScene(scene);
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = rsUtilBiz.checkSource(source);

    Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap = databaseCalIndicatorExpressionRequest.getKIndicatorInstanceIdVIndicatorRuleEntityMap();
    IndicatorExpressionEntity indicatorExpressionEntity = databaseCalIndicatorExpressionRequest.getIndicatorExpressionEntity();
    List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = databaseCalIndicatorExpressionRequest.getIndicatorExpressionItemEntityList();
    IndicatorExpressionItemEntity minIndicatorExpressionItemEntity = databaseCalIndicatorExpressionRequest.getMinIndicatorExpressionItemEntity();
    IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity = databaseCalIndicatorExpressionRequest.getMaxIndicatorExpressionItemEntity();

    Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap = caseCalIndicatorExpressionRequest.getKCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap();
    CaseIndicatorExpressionEntity caseIndicatorExpressionEntity = caseCalIndicatorExpressionRequest.getCaseIndicatorExpressionEntity();
    List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = caseCalIndicatorExpressionRequest.getCaseIndicatorExpressionItemEntityList();
    CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity = caseCalIndicatorExpressionRequest.getMinCaseIndicatorExpressionItemEntity();
    CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity = caseCalIndicatorExpressionRequest.getMaxCaseIndicatorExpressionItemEntity();

    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = experimentCalIndicatorExpressionRequest.getKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap();
    ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = experimentCalIndicatorExpressionRequest.getExperimentIndicatorExpressionRsEntity();
    List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = experimentCalIndicatorExpressionRequest.getExperimentIndicatorExpressionItemRsEntityList();
    ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = experimentCalIndicatorExpressionRequest.getMinExperimentIndicatorExpressionItemRsEntity();
    ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = experimentCalIndicatorExpressionRequest.getMaxExperimentIndicatorExpressionItemRsEntity();

    switch (enumIndicatorExpressionSource) {
      case INDICATOR_MANAGEMENT -> ePIEIndicatorManagement(
          scene,
          resultAtomicReference,
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
          experimentIndicatorExpressionRsEntity,
          experimentIndicatorExpressionItemRsEntityList,
          minExperimentIndicatorExpressionItemRsEntity,
          maxExperimentIndicatorExpressionItemRsEntity
      );
      case INDICATOR_JUDGE_RISK_FACTOR -> ePIEIndicatorJudgeRiskFactor();
      case CROWDS -> ePIECrowds(resultAtomicReference, kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, experimentIndicatorExpressionItemRsEntityList, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap);
      case RISK_MODEL -> ePIERiskModel(
          scene,
          resultAtomicReference,
          kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
          experimentIndicatorExpressionRsEntity,
          experimentIndicatorExpressionItemRsEntityList,
          minExperimentIndicatorExpressionItemRsEntity,
          maxExperimentIndicatorExpressionItemRsEntity);
      default -> {
        log.error("RsIndicatorExpressionBiz.experimentParseIndicatorExpression source:{} is illegal", source);
        throw new RsIndicatorExpressionException("公式来源不合法");
      }
    }
  }

  public void populateParseParam(
      Set<String> reasonIdSet,
      Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
      Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()
        || Objects.isNull(kReasonIdVExperimentIndicatorExpressionRsEntityListMap)
        || Objects.isNull(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap)
        || Objects.isNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap)
    ) {return;}

    CompletableFuture<Void> cfPopulateKExperimentReasonIdVExperimentIndicatorExpressionRsEntityListMap = CompletableFuture.runAsync(() -> {
      try {
        populateKExperimentReasonIdVExperimentIndicatorExpressionRsEntityListMap(kReasonIdVExperimentIndicatorExpressionRsEntityListMap, reasonIdSet);
      } catch (Exception e) {
        log.error("RsIndicatorExpressionBiz.populateParseParam error", e);
        throw new RsIndicatorExpressionException("填充解析公式参数出错，请及时与管理员联系");
      }
    });
    cfPopulateKExperimentReasonIdVExperimentIndicatorExpressionRsEntityListMap.get();

    Set<String> experimentIndicatorExpressionIdSet = new HashSet<>();
    kReasonIdVExperimentIndicatorExpressionRsEntityListMap.forEach((reasonId, experimentIndicatorExpressionRsEntityList) -> {
      experimentIndicatorExpressionRsEntityList.forEach(experimentIndicatorExpressionRsEntity -> {
        experimentIndicatorExpressionIdSet.add(experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId());
      });
    });
    if (experimentIndicatorExpressionIdSet.isEmpty()) {return;}

    Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = CompletableFuture.runAsync(() -> {
      populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap, experimentIndicatorExpressionIdSet);
    });
    cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get();

    CompletableFuture<Void> cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap = CompletableFuture.runAsync(() -> {
      populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, experimentIndicatorExpressionIdSet);
    });
    cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.get();

    Set<String> experimentIndicatorExpressionItemIdSet = new HashSet<>();
    kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.forEach((experimentIndicatorExpressionId, experimentIndicatorExpressionItemRsEntityList) -> {
      experimentIndicatorExpressionItemRsEntityList.forEach(experimentIndicatorExpressionItemRsEntity -> {
        experimentIndicatorExpressionItemIdSet.add(experimentIndicatorExpressionItemRsEntity.getExperimentIndicatorExpressionItemId());
      });
    });
    if (experimentIndicatorExpressionItemIdSet.isEmpty()) {return;}

    CompletableFuture<Void> cfPopulateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = CompletableFuture.runAsync(() -> {
      populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(
          kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap, experimentIndicatorExpressionItemIdSet);
    });
    cfPopulateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get();
  }

  /* runsix:指标管理的指标id以及对应的指标公式（只有一条） */
  public void populateKExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap(
      Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap,
      Set<String> experimentIndicatorInstanceIdSet
  ) {
    if (Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap)
        || Objects.isNull(experimentIndicatorInstanceIdSet) || experimentIndicatorInstanceIdSet.isEmpty()
    ) {return;}
    Set<String> experimentIndicatorExpressionIdSet = new HashSet<>();
    Map<String, String> kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdMap = new HashMap<>();
    experimentIndicatorExpressionRefRsService.lambdaQuery()
        .in(ExperimentIndicatorExpressionRefRsEntity::getReasonId, experimentIndicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorExpressionRefRsEntity -> {
          experimentIndicatorExpressionIdSet.add(experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId());
          /* runsix:因为只有一条，所以这样操作 */
          kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdMap.put(
              experimentIndicatorExpressionRefRsEntity.getReasonId(), experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId()
          );
        });
    if (experimentIndicatorExpressionIdSet.isEmpty()) {return;}
    Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
    experimentIndicatorExpressionRsService.lambdaQuery()
        .in(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, experimentIndicatorExpressionIdSet)
        .list()
        .forEach(experimentIndicatorExpressionRsEntity -> {
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.put(experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId(), experimentIndicatorExpressionRsEntity);
        });
    kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdMap.forEach((experimentIndicatorInstanceId, experimentIndicatorExpressionId) -> {
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get(experimentIndicatorExpressionId);
      if (Objects.nonNull(experimentIndicatorExpressionRsEntity)) {
        kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.put(experimentIndicatorInstanceId, experimentIndicatorExpressionRsEntity);
      }
    });
  }

  public void populateKExperimentReasonIdVExperimentIndicatorExpressionRsEntityListMap(
      Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
      Set<String> experimentReasonIdSet
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(kReasonIdVExperimentIndicatorExpressionRsEntityListMap)
        || Objects.isNull(experimentReasonIdSet) || experimentReasonIdSet.isEmpty()
    ) {return;}
    /* runsix:init kReasonIdVExperimentIndicatorExpressionRsEntityListMap for lambda */
    experimentReasonIdSet.forEach(experimentReasonId -> {
      kReasonIdVExperimentIndicatorExpressionRsEntityListMap.put(experimentReasonId, new ArrayList<>());
    });

    Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentReasonIdVExperimentIndicatorExpressionRefListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap = CompletableFuture.runAsync(() -> {
      populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(kExperimentReasonIdVExperimentIndicatorExpressionRefListMap, experimentReasonIdSet);
    });
    cfPopulateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap.get();

    Set<String> experimentIndicatorExpressionIdSet = new HashSet<>();
    kExperimentReasonIdVExperimentIndicatorExpressionRefListMap.forEach((experimentReasonId, experimentIndicatorExpressionRefList) -> {
      experimentIndicatorExpressionRefList.forEach(experimentIndicatorExpressionRefRsEntity -> {
        experimentIndicatorExpressionIdSet.add(experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId());
      });
    });
    if (experimentIndicatorExpressionIdSet.isEmpty()) {return;}

    Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = CompletableFuture.runAsync(() -> {
      populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap, experimentIndicatorExpressionIdSet);
    });
    cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get();

    kExperimentReasonIdVExperimentIndicatorExpressionRefListMap.forEach((experimentReasonId, experimentIndicatorExpressionRefList) -> {
      List<ExperimentIndicatorExpressionRsEntity> experimentIndicatorExpressionRsEntityList = kReasonIdVExperimentIndicatorExpressionRsEntityListMap.get(experimentReasonId);
      experimentIndicatorExpressionRefList.forEach(experimentIndicatorExpressionRefRsEntity -> {
        String experimentIndicatorExpressionId = experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
        ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get(experimentIndicatorExpressionId);
        if (Objects.nonNull(experimentIndicatorExpressionRsEntity)) {
          experimentIndicatorExpressionRsEntityList.add(experimentIndicatorExpressionRsEntity);
        }
      });
      kReasonIdVExperimentIndicatorExpressionRsEntityListMap.put(experimentReasonId, experimentIndicatorExpressionRsEntityList);
    });
  }

  public void populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(
      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
      Set<String> experimentIndicatorExpressionIdSet
  ) {
    if (Objects.isNull(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap)
        || Objects.isNull(experimentIndicatorExpressionIdSet)
        || experimentIndicatorExpressionIdSet.isEmpty()
    ) {
      return;
    }
    experimentIndicatorExpressionItemRsService.lambdaQuery()
        .in(ExperimentIndicatorExpressionItemRsEntity::getIndicatorExpressionId, experimentIndicatorExpressionIdSet)
        .list()
        .forEach(experimentIndicatorExpressionItemRsEntity -> {
          String experimentIndicatorExpressionId = experimentIndicatorExpressionItemRsEntity.getIndicatorExpressionId();
          List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
          if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList)) {
            experimentIndicatorExpressionItemRsEntityList = new ArrayList<>();
          }
          experimentIndicatorExpressionItemRsEntityList.add(experimentIndicatorExpressionItemRsEntity);
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.put(experimentIndicatorExpressionId, experimentIndicatorExpressionItemRsEntityList);
        });
  }

  public void populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap(
      Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap,
      Set<String> experimentIndicatorExpressionIdSet
  ) {
    if (Objects.isNull(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap)
        || Objects.isNull(experimentIndicatorExpressionIdSet) || experimentIndicatorExpressionIdSet.isEmpty()
    ) {
      return;
    }
    experimentIndicatorExpressionRsService.lambdaQuery()
        .in(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, experimentIndicatorExpressionIdSet)
        .list()
        .forEach(experimentIndicatorExpressionRsEntity -> {
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.put(experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId(), experimentIndicatorExpressionRsEntity);
        });
  }

  public void populateKExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap(
      Map<String, ExperimentIndicatorValRsEntity> kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap,
      Set<String> experimentPersonIdSet,
      Integer periods
  ) {
    if (Objects.isNull(kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap) || Objects.isNull(experimentPersonIdSet) || experimentPersonIdSet.isEmpty()) {
      return;
    }
    if (Objects.isNull(periods)) {
      /* runsix:TODO 以后改为当前期的 */
      periods = 1;
    }
    Set<String> healthExperimentIndicatorInstanceIdSet = new HashSet<>();
    Map<String, String> kExperimentPersonIdVHealthExperimentIndicatorInstanceIdMap = new HashMap<>();
    experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
        .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          healthExperimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
          kExperimentPersonIdVHealthExperimentIndicatorInstanceIdMap.put(
              experimentIndicatorInstanceRsEntity.getExperimentPersonId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId()
          );
        });
    if (healthExperimentIndicatorInstanceIdSet.isEmpty()) {
      return;
    }
    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
        .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, healthExperimentIndicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorValRsEntity -> {
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity);
        });
    kExperimentPersonIdVHealthExperimentIndicatorInstanceIdMap.forEach((experimentPersonId, healthExperimentIndicatorInstanceId) -> {
      ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(healthExperimentIndicatorInstanceId);
      if (Objects.nonNull(experimentIndicatorValRsEntity)) {
        kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap.put(experimentPersonId, experimentIndicatorValRsEntity);
      }
    });
  }
}
