package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.formula.functions.T;
import org.dows.hep.api.base.indicator.request.RsCalculateCompetitiveScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateHealthScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateMoneyScoreRequestRs;
import org.dows.hep.api.base.indicator.response.GroupCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.GroupMoneyScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateMoneyScoreRsResponse;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsCalculateBiz {
  private final ExperimentCrowdsInstanceRsService experimentCrowdsInstanceRsService;
  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final ExperimentPersonService experimentPersonService;
  @Transactional(rollbackFor = Exception.class)
  public void rsCalculateHealthPointOld(RsCalculateHealthScoreRequestRs rsCalculateHealthScoreRequestRs) {
    /* runsix:TODO populate */
    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    String appId = rsCalculateHealthScoreRequestRs.getAppId();
    String experimentId = rsCalculateHealthScoreRequestRs.getExperimentId();
    List<String> experimentPersonIdList = rsCalculateHealthScoreRequestRs.getExperimentPersonIdList();
    Map<String, ExperimentCrowdsInstanceRsEntity> kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap = new HashMap<>();
    List<ExperimentCrowdsInstanceRsEntity> experimentCrowdsInstanceRsEntityList = new ArrayList<>();
    List<String> experimentCrowdsIdList = new ArrayList<>();
    experimentCrowdsInstanceRsService.lambdaQuery()
        .eq(ExperimentCrowdsInstanceRsEntity::getAppId, appId)
        .eq(ExperimentCrowdsInstanceRsEntity::getExperimentId, experimentId)
        .list()
        .forEach(experimentCrowdsInstanceRsEntity -> {
          String experimentCrowdsId = experimentCrowdsInstanceRsEntity.getExperimentCrowdsId();
          experimentCrowdsIdList.add(experimentCrowdsId);
          kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap.put(experimentCrowdsId, experimentCrowdsInstanceRsEntity);
          experimentCrowdsInstanceRsEntityList.add(experimentCrowdsInstanceRsEntity);
        });
    /* runsix:sort, guarantee sort */
    experimentCrowdsInstanceRsEntityList.sort(Comparator.comparing(ExperimentCrowdsInstanceRsEntity::getDt));
    /* runsix:if no experimentCrowdsInstanceRsEntityList in experiment, do not calculate */
    if (experimentCrowdsIdList.isEmpty()) {
      return;
    }
    Map<String, Set<String>> kExperimentReasonIdVExperimentIndicatorExpressionIdSetMap = new HashMap<>();
    Set<String> experimentIndicatorExpressionIdSet = new HashSet<>();
    experimentIndicatorExpressionRefRsService.lambdaQuery()
        .eq(ExperimentIndicatorExpressionRefRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorExpressionRefRsEntity::getExperimentId, experimentId)
        .in(ExperimentIndicatorExpressionRefRsEntity::getReasonId, experimentCrowdsIdList)
        .list()
        .forEach(experimentIndicatorExpressionRefRsEntity -> {
          String experimentIndicatorExpressionId = experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
          experimentIndicatorExpressionIdSet.add(experimentIndicatorExpressionId);
          String experimentReasonId = experimentIndicatorExpressionRefRsEntity.getReasonId();
          Set<String> experimentIndicatorExpressionIdSet1 = kExperimentReasonIdVExperimentIndicatorExpressionIdSetMap.get(experimentReasonId);
          if (Objects.isNull(experimentIndicatorExpressionIdSet1)) {
            experimentIndicatorExpressionIdSet1 = new HashSet<>();
          }
          experimentIndicatorExpressionIdSet1.add(experimentReasonId);
          kExperimentReasonIdVExperimentIndicatorExpressionIdSetMap.put(experimentReasonId, experimentIndicatorExpressionIdSet1);
        });
    /* runsix:if no experimentIndicatorExpressionIdSet, do not calculate */
    if (experimentIndicatorExpressionIdSet.isEmpty()) {
      return;
    }
    Set<String> experimentIndicatorExpressionItemIdSet = new HashSet<>();
    experimentIndicatorExpressionRsService.lambdaQuery()
        .eq(ExperimentIndicatorExpressionRsEntity::getExperimentId, experimentId)
        .in(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, experimentIndicatorExpressionIdSet)
        .list()
        .forEach(experimentIndicatorExpressionRsEntity -> {
          String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
          if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
            experimentIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
          }
          String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
          if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
            experimentIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
          }
        });
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    experimentIndicatorExpressionItemRsService.lambdaQuery()
        .eq(ExperimentIndicatorExpressionItemRsEntity::getExperimentId, experimentId)
        .in(ExperimentIndicatorExpressionItemRsEntity::getIndicatorExpressionId, experimentIndicatorExpressionIdSet)
        .list()
        .forEach(experimentIndicatorExpressionItemRsEntity -> {
          String experimentIndicatorExpressionItemId = experimentIndicatorExpressionItemRsEntity.getExperimentIndicatorExpressionItemId();
          experimentIndicatorExpressionItemIdSet.add(experimentIndicatorExpressionItemId);
          String experimentIndicatorExpressionId = experimentIndicatorExpressionItemRsEntity.getIndicatorExpressionId();
          List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
          if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList)) {
            experimentIndicatorExpressionItemRsEntityList = new ArrayList<>();
          }
          experimentIndicatorExpressionItemRsEntityList.add(experimentIndicatorExpressionItemRsEntity);
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.put(experimentIndicatorExpressionId, experimentIndicatorExpressionItemRsEntityList);
        });
    /* runsix:if no experimentIndicatorExpressionItemIdSet, do not calculate */
    if (!experimentIndicatorExpressionItemIdSet.isEmpty()) {
      return;
    }
    Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
    experimentIndicatorExpressionItemRsService.lambdaQuery()
        .eq(ExperimentIndicatorExpressionItemRsEntity::getExperimentId, experimentId)
        .in(ExperimentIndicatorExpressionItemRsEntity::getExperimentIndicatorExpressionItemId, experimentIndicatorExpressionItemIdSet)
        .list()
        .forEach(experimentIndicatorExpressionItemRsEntity -> {
          String experimentIndicatorExpressionItemId = experimentIndicatorExpressionItemRsEntity.getExperimentIndicatorExpressionItemId();
          kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.put(experimentIndicatorExpressionItemId, experimentIndicatorExpressionItemRsEntity);
        });
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentCrowdsIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    /* runsix:guarantee not null */
    experimentCrowdsIdList.forEach(experimentCrowdsId -> {
      kExperimentCrowdsIdVExperimentIndicatorExpressionItemRsEntityListMap.put(experimentCrowdsId, new ArrayList<>());
    });
    experimentCrowdsIdList.forEach(experimentCrowdsId -> {
      Set<String> experimentIndicatorExpressionIdSet1 = kExperimentReasonIdVExperimentIndicatorExpressionIdSetMap.get(experimentCrowdsId);
      if (Objects.isNull(experimentIndicatorExpressionIdSet1)) {
        return;
      }
      List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList1 = kExperimentCrowdsIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentCrowdsId);
      experimentIndicatorExpressionIdSet1.forEach(experimentIndicatorExpressionId -> {
        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
        if (Objects.nonNull(experimentIndicatorExpressionItemRsEntityList) && !experimentIndicatorExpressionItemRsEntityList.isEmpty()) {
          experimentIndicatorExpressionItemRsEntityList1.addAll(experimentIndicatorExpressionItemRsEntityList);
          kExperimentCrowdsIdVExperimentIndicatorExpressionItemRsEntityListMap.put(experimentCrowdsId, experimentIndicatorExpressionItemRsEntityList1);
        }
      });
    });
    Map<String, ExperimentCrowdsInstanceRsEntity> kExperimentPersonIdVExperimentCrowdsInstanceRsEntityMap = new HashMap<>();
    experimentPersonIdList.forEach(experimentPersonId -> {
      experimentCrowdsInstanceRsEntityList.forEach(experimentCrowdsInstanceRsEntity -> {
        /* runsix:if match any ExperimentCrowdsInstanceRsEntity, return */
        if (Objects.nonNull(kExperimentPersonIdVExperimentCrowdsInstanceRsEntityMap.get(experimentPersonId))) {
          return;
        }
        String experimentCrowdsId = experimentCrowdsInstanceRsEntity.getExperimentCrowdsId();
        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentCrowdsIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentCrowdsId);
        if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {
          return;
        }
        /* runsix:only first condition */
        ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = experimentIndicatorExpressionItemRsEntityList.get(0);
        String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
        String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
        String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
        if (StringUtils.isBlank(conditionNameList)) {
          StandardEvaluationContext context = new StandardEvaluationContext();
          ExpressionParser parser = new SpelExpressionParser();
          Expression expression = parser.parseExpression(conditionExpression);
          Boolean resultBoolean = false;
          try {
            resultBoolean = expression.getValue(context, Boolean.class);
          } catch (Exception e) {
            log.error("experimentIndicatorExpressionItem conditionExpression result is not Boolean, experimentIndicatorExpressionItemId:{}", experimentIndicatorExpressionItemRsEntity.getExperimentIndicatorExpressionItemId());
          }
          if (Boolean.TRUE.equals(resultBoolean)) {
            kExperimentPersonIdVExperimentCrowdsInstanceRsEntityMap.put(experimentPersonId, experimentCrowdsInstanceRsEntity);
          }
        } else {
          StandardEvaluationContext context = new StandardEvaluationContext();
          List<String> conditionNameListSpilt = Arrays.stream(conditionNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
          List<String> conditionValListSpilt = Arrays.stream(conditionValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
          for (int i = 0; i <= conditionValListSpilt.size()-1; i++) {
            ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(conditionValListSpilt.get(i));
            String val = experimentIndicatorValRsEntity.getCurrentVal();
            boolean isValDigital = NumberUtils.isCreatable(val);
            if (isValDigital) {
              context.setVariable(conditionNameListSpilt.get(i), Double.parseDouble(val));
            } else {
              val = v1WrapStrWithDoubleSingleQuotes(val);
              context.setVariable(conditionNameListSpilt.get(i), val);
            }
          }
          String conditionExpression1 = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
          ExpressionParser parser1 = new SpelExpressionParser();
          Expression expression = parser1.parseExpression(conditionExpression1);
          Boolean resultBoolean = false;
          try {
            resultBoolean = expression.getValue(context, Boolean.class);
          } catch (Exception e) {
            log.error("experimentIndicatorExpressionItem conditionExpression result is not Boolean, experimentIndicatorExpressionItemId:{}", experimentIndicatorExpressionItemRsEntity.getExperimentIndicatorExpressionItemId());
          }
          if (Boolean.TRUE.equals(resultBoolean)) {
            kExperimentPersonIdVExperimentCrowdsInstanceRsEntityMap.put(experimentPersonId, experimentCrowdsInstanceRsEntity);
          }
        }
      });
    });

  }

  @Transactional(rollbackFor = Exception.class)
  public RsCalculateMoneyScoreRsResponse rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs rsCalculateMoneyScoreRequestRs) {
    List<GroupMoneyScoreRsResponse> groupMoneyScoreRsResponseList = new ArrayList<>();
    return RsCalculateMoneyScoreRsResponse
        .builder()
        .groupMoneyScoreRsResponseList(groupMoneyScoreRsResponseList)
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public void rsCalculateHealthPointDev(RsCalculateHealthScoreRequestRs rsCalculateHealthScoreRequestRs) {
    Map<String, ExperimentCrowdsInstanceRsEntity> kExperimentPersonIdVExperimentCrowdsInstanceRsEntityMap = new HashMap();
    Map<String, List<ExperimentRiskModelRsEntity>> kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap = new HashMap<>();
    Map<String, List<ExperimentIndicatorExpressionRsEntity>> kExperimentRiskModelIdVExperimentIndicatorExpressionRsEntityListMap = new HashMap<>();
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
    Map<String, Double> kExperimentPersonIdVHealthPointMap = new HashMap<>();
    Map<String, ExperimentIndicatorValRsEntity> kExperimentPersonIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    List<ExperimentIndicatorValRsEntity> experimentIndicatorValRsEntityList = new ArrayList<>();
    kExperimentPersonIdVExperimentCrowdsInstanceRsEntityMap.forEach((experimentPersonId, experimentCrowdsInstanceRsEntity) -> {
      String experimentCrowdsId = experimentCrowdsInstanceRsEntity.getExperimentCrowdsId();
      List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntityList = kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.get(experimentCrowdsId);
      if (Objects.isNull(experimentRiskModelRsEntityList)) {
        return;
      }
      AtomicInteger totalRiskDeathProbabilityAtomicInteger = new AtomicInteger();
      Map<String, Integer> kExperimentRiskModelIdVRiskDeathProbabilityMap = new HashMap<>();
      Map<String, Integer> kExperimentRiskModelIdVScoreMap = new HashMap<>();
      experimentRiskModelRsEntityList.forEach(experimentRiskModelRsEntity -> {
        int totalRiskDeathProbability = totalRiskDeathProbabilityAtomicInteger.get();
        totalRiskDeathProbability += experimentRiskModelRsEntity.getRiskDeathProbability();
        totalRiskDeathProbabilityAtomicInteger.set(totalRiskDeathProbability);

        /* runsix:populate kExperimentRiskModelIdVRiskDeathProbabilityMap */
        kExperimentRiskModelIdVRiskDeathProbabilityMap.put(experimentRiskModelRsEntity.getExperimentRiskModelId(), experimentRiskModelRsEntity.getRiskDeathProbability());

        /* runsix: populate kExperimentRiskModelIdVScoreMap */
        String experimentRiskModelId = experimentRiskModelRsEntity.getExperimentRiskModelId();
        List<ExperimentIndicatorExpressionRsEntity> experimentIndicatorExpressionRsEntityList = kExperimentRiskModelIdVExperimentIndicatorExpressionRsEntityListMap.get(experimentRiskModelId);
        if (Objects.isNull(experimentIndicatorExpressionRsEntityList) || experimentIndicatorExpressionRsEntityList.isEmpty()) {
          return;
        }
        experimentIndicatorExpressionRsEntityList.forEach(experimentIndicatorExpressionRsEntity -> {
          String experimentIndicatorExpressionId = experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
          String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
          /* runsix:TODO  */
          ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = null;
          String minResultExpression = minExperimentIndicatorExpressionItemRsEntity.getResultExpression();
          String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
          /* runsix:TODO  */
          ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = null;
          String maxResultExpression = minExperimentIndicatorExpressionItemRsEntity.getResultExpression();
          List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
          if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {
            return;
          }
          experimentIndicatorExpressionItemRsEntityList.sort(Comparator.comparingInt(ExperimentIndicatorExpressionItemRsEntity::getSeq));
          String result = null;
          experimentIndicatorExpressionItemRsEntityList.forEach(experimentIndicatorExpressionItemRsEntity -> {
            if (StringUtils.isNotBlank(result)) {
              return;
            }
            /* runsix:TODO !!!!!!!!计算 */
            String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
            String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
            String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
            String resultExpression = experimentIndicatorExpressionItemRsEntity.getResultExpression();
            String resultNameList = experimentIndicatorExpressionItemRsEntity.getResultNameList();
            String resultValList = experimentIndicatorExpressionItemRsEntity.getResultValList();
          });
        });
      });
      AtomicReference<Double>  atomicReferenceFinalScore = new AtomicReference<>(0D);
      int totalRiskDeathProbability = totalRiskDeathProbabilityAtomicInteger.get();
      if (totalRiskDeathProbability == 0) {
        log.error("totalRiskDeathProbability is 0, experimentCrowdsId:{}", experimentCrowdsId);
      }
      kExperimentRiskModelIdVRiskDeathProbabilityMap.forEach((experimentRiskModelId, riskDeathProbability) -> {
        Integer score = kExperimentRiskModelIdVScoreMap.get(experimentRiskModelId);
        Double finalScore = atomicReferenceFinalScore.get();
        finalScore += riskDeathProbability/totalRiskDeathProbability*score;
        atomicReferenceFinalScore.set(finalScore);
      });
      kExperimentPersonIdVHealthPointMap.put(experimentPersonId, atomicReferenceFinalScore.get());
    });

    kExperimentPersonIdVHealthPointMap.forEach((experimentPersonId, healthPoint) -> {
      ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentPersonIdVExperimentIndicatorValRsEntityMap.get(experimentPersonId);
      experimentIndicatorValRsEntity.setCurrentVal(healthPoint.toString());
      experimentIndicatorValRsEntityList.add(experimentIndicatorValRsEntity);
    });
    experimentIndicatorValRsService.saveOrUpdateBatch(experimentIndicatorValRsEntityList);
  }

  private static String v1WrapStrWithDoubleSingleQuotes(String str) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(EnumString.SINGLE_QUOTES.getStr());
    stringBuffer.append(str);
    stringBuffer.append(EnumString.SINGLE_QUOTES.getStr());
    return stringBuffer.toString();
  }

  public RsCalculateCompetitiveScoreRsResponse rsCalculateCompetitiveScore(RsCalculateCompetitiveScoreRequestRs rsCalculateCompetitiveScoreRequestRs) {
    List<GroupCompetitiveScoreRsResponse> groupCompetitiveScoreRsResponseList = new ArrayList<>();
    String experimentId = rsCalculateCompetitiveScoreRequestRs.getExperimentId();
    Integer periods = rsCalculateCompetitiveScoreRequestRs.getPeriods();
    Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap = new HashMap<>();

    Set<String> experimentPersonIdSet = new HashSet<>();
    Map<String, String> kExperimentPersonIdVCasePersonIdMap = new HashMap<>();
    Map<String, List<ExperimentPersonEntity>> kExperimentGroupIdVExperimentPersonEntityListMap = new HashMap<>();
    experimentPersonService.lambdaQuery()
        .eq(ExperimentPersonEntity::getExperimentInstanceId, experimentId)
        .list()
        .forEach(experimentPersonEntity -> {
          String experimentPersonId = experimentPersonEntity.getExperimentPersonId();
          experimentPersonIdSet.add(experimentPersonId);

          String casePersonId = experimentPersonEntity.getCasePersonId();
          kExperimentPersonIdVCasePersonIdMap.put(experimentPersonId, casePersonId);

          String experimentGroupId = experimentPersonEntity.getExperimentGroupId();
          List<ExperimentPersonEntity> experimentPersonEntityList = kExperimentGroupIdVExperimentPersonEntityListMap.get(experimentGroupId);
          if (Objects.isNull(experimentPersonEntityList)) {
            experimentPersonEntityList = new ArrayList<>();
          }
          experimentPersonEntityList.add(experimentPersonEntity);
          kExperimentGroupIdVExperimentPersonEntityListMap.put(experimentGroupId, experimentPersonEntityList);
        });
    AtomicInteger groupExperimentPersonSizeAtomicInteger = new AtomicInteger(0);
    kExperimentGroupIdVExperimentPersonEntityListMap.forEach((ekExperimentGroupIdVExperimentPersonEntityListMap, experimentPersonEntityList) -> {
      if (groupExperimentPersonSizeAtomicInteger.get() != 0) {
        groupExperimentPersonSizeAtomicInteger.set(experimentPersonEntityList.size());
      }
    });

    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    Map<String, String> kExperimentIndicatorInstanceIdVExperimentPersonIdMap = new HashMap<>();
    Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
    if (!experimentPersonIdSet.isEmpty()) {
      experimentIndicatorInstanceRsService.lambdaQuery()
          .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentId)
          .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
          .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
          .list()
          .forEach(experimentIndicatorInstanceRsEntity -> {
            String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
            experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceId);

            String experimentPersonId = experimentIndicatorInstanceRsEntity.getExperimentPersonId();
            kExperimentIndicatorInstanceIdVExperimentPersonIdMap.put(experimentIndicatorInstanceId, experimentPersonId);

            kExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap.put(experimentPersonId, experimentIndicatorInstanceRsEntity);
          });
    }

    Map<String, ExperimentIndicatorValRsEntity> kHealthExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    Map<String, BigDecimal> kCasePersonIdVMinHealthScoreMap = new HashMap<>();
    Map<String, BigDecimal> kCasePersonIdVMaxHealthScoreMap = new HashMap<>();
    if (!experimentIndicatorInstanceIdSet.isEmpty()) {
      experimentIndicatorValRsService.lambdaQuery()
          .eq(ExperimentIndicatorValRsEntity::getExperimentId, experimentId)
          .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
          .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
          .list()
          .forEach(experimentIndicatorValRsEntity -> {
            String indicatorInstanceId = experimentIndicatorValRsEntity.getIndicatorInstanceId();
            kHealthExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(indicatorInstanceId, experimentIndicatorValRsEntity);

            String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
            BigDecimal currentBigDecimal = BigDecimal.valueOf(Double.parseDouble(currentVal));
            String experimentPersonId = kExperimentIndicatorInstanceIdVExperimentPersonIdMap.get(indicatorInstanceId);
            if (StringUtils.isNotBlank(experimentPersonId)) {
              String casePersonId = kExperimentPersonIdVCasePersonIdMap.get(experimentPersonId);
              if (StringUtils.isNotBlank(casePersonId)) {
                BigDecimal minBigDecimal = kCasePersonIdVMinHealthScoreMap.get(casePersonId);
                if (Objects.isNull(minBigDecimal)) {
                  minBigDecimal = currentBigDecimal;
                } else {
                  if (currentBigDecimal.compareTo(minBigDecimal) < 0) {
                    minBigDecimal = currentBigDecimal;
                  }
                }
                kCasePersonIdVMinHealthScoreMap.put(casePersonId, minBigDecimal);

                BigDecimal maxBigDecimal = kCasePersonIdVMaxHealthScoreMap.get(casePersonId);
                if (Objects.isNull(maxBigDecimal)) {
                  maxBigDecimal = currentBigDecimal;
                } else {
                  if (currentBigDecimal.compareTo(maxBigDecimal) > 0) {
                    maxBigDecimal = currentBigDecimal;
                  }
                }
                kCasePersonIdVMaxHealthScoreMap.put(casePersonId, maxBigDecimal);
              }
            }
          });
    }

    kExperimentGroupIdVExperimentPersonEntityListMap.forEach((experimentGroupId, experimentPersonEntityList) -> {
      AtomicReference<BigDecimal> atomicReferenceTotalHealthScore = new AtomicReference<>(BigDecimal.ZERO);
      experimentPersonEntityList.forEach(experimentPersonEntity -> {
        String experimentPersonId = experimentPersonEntity.getExperimentPersonId();
        String casePersonId = experimentPersonEntity.getCasePersonId();
        BigDecimal minHealthScore = kCasePersonIdVMinHealthScoreMap.get(casePersonId);
        BigDecimal maxHealthScore = kCasePersonIdVMaxHealthScoreMap.get(casePersonId);
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap.get(experimentPersonId);
        String defHealthScore = experimentIndicatorInstanceRsEntity.getDef();
        String healthExperimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kHealthExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(healthExperimentIndicatorInstanceId);
        String currentHealthScore = experimentIndicatorValRsEntity.getCurrentVal();
        BigDecimal resultHealthScore = rsCalculateCompetitiveScore(BigDecimal.valueOf(Double.parseDouble(currentHealthScore)), BigDecimal.valueOf(Double.parseDouble(defHealthScore)), minHealthScore, maxHealthScore);
        BigDecimal lastHealthScore = atomicReferenceTotalHealthScore.get();
        atomicReferenceTotalHealthScore.set(lastHealthScore.add(resultHealthScore));
      });
      kExperimentGroupIdVGroupCompetitiveScoreMap.put(experimentGroupId, atomicReferenceTotalHealthScore.get().divide(BigDecimal.valueOf(groupExperimentPersonSizeAtomicInteger.get()), 2, RoundingMode.DOWN));
    });
    kExperimentGroupIdVGroupCompetitiveScoreMap.forEach((experimentGroupId, groupCompetitiveScore) -> {
      groupCompetitiveScoreRsResponseList.add(GroupCompetitiveScoreRsResponse
          .builder()
          .experimentGroupId(experimentGroupId)
          .groupCompetitiveScore(groupCompetitiveScore)
          .build());
    });
    return RsCalculateCompetitiveScoreRsResponse
        .builder()
        .groupCompetitiveScoreRsResponseList(groupCompetitiveScoreRsResponseList)
        .build();
  }

  private BigDecimal rsCalculateCompetitiveScore(BigDecimal currentHealthScore, BigDecimal defHealthScore, BigDecimal minHealthScore, BigDecimal maxHealthScore) {
    BigDecimal resultBigDecimal = null;
    if (currentHealthScore.compareTo(defHealthScore) >= 0) {
      resultBigDecimal = (currentHealthScore.subtract(defHealthScore).divide(maxHealthScore.subtract(defHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100-60))).add(BigDecimal.valueOf(60));
    } else {
      resultBigDecimal = currentHealthScore.subtract(minHealthScore).divide(defHealthScore.subtract(minHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(60));
    }
    return resultBigDecimal;
  }

  public void rsCalculateHealthScore() {

  }
}
