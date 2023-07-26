package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionItemRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.IndicatorExpressionException;
import org.dows.hep.api.exception.RsIndicatorExpressionBizException;
import org.dows.hep.api.exception.RsIndicatorExpressionException;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsIndicatorExpressionBiz {
  private final IndicatorRuleService indicatorRuleService;
  private final IndicatorExpressionRefService indicatorExpressionRefService;
  private final IndicatorExpressionService indicatorExpressionService;
  private final IndicatorExpressionItemService indicatorExpressionItemService;
  private final IndicatorExpressionInfluenceService indicatorExpressionInfluenceService;
  private final IdGenerator idGenerator;
  private final RsUtilBiz rsUtilBiz;


  public void populateIndicatorExpressionEntity(
      AtomicReference<IndicatorExpressionEntity> indicatorExpressionEntityAtomicReference,
      String indicatorExpressionId) {
    if (Objects.isNull(indicatorExpressionEntityAtomicReference) || StringUtils.isBlank(indicatorExpressionId)) {return;}
    IndicatorExpressionEntity indicatorExpressionEntity = indicatorExpressionService.lambdaQuery()
        .eq(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("IndicatorExpressionBiz.v2CreateOrUpdate indicatorExpressionId:{} is illegal", indicatorExpressionId);
          throw new IndicatorExpressionException(EnumESC.INDICATOR_EXPRESSION_ID_IS_ILLEGAL);
        });
    indicatorExpressionEntityAtomicReference.set(indicatorExpressionEntity);
  }

  public void populateKIndicatorExpressionIdVIndicatorExpressionItemEntityListMap(
      Map<String, List<IndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap,
      Set<String> indicatorExpressionIdSet
  ) {
    if (Objects.isNull(kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap)
        || Objects.isNull(indicatorExpressionIdSet) || indicatorExpressionIdSet.isEmpty()
    ) {return;}
    indicatorExpressionItemService.lambdaQuery()
        .in(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
        .list()
        .forEach(indicatorExpressionItemEntity -> {
          String indicatorExpressionId = indicatorExpressionItemEntity.getIndicatorExpressionId();
          if (StringUtils.isBlank(indicatorExpressionId)) {return;}
          List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
          if (Objects.isNull(indicatorExpressionItemEntityList)) {
            indicatorExpressionItemEntityList = new ArrayList<>();
          }
          indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
          kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.put(indicatorExpressionId, indicatorExpressionItemEntityList);
        });
  }

  public void populateKIndicatorInstanceIdVIndicatorExpressionInfluenceMap(
      Map<String, IndicatorExpressionInfluenceEntity> kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap,
      Set<String> indicatorInstanceIdSet
  ) {
    if (Objects.isNull(kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap)
        || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()
    ) {return;}
    indicatorExpressionInfluenceService.lambdaQuery()
        .in(IndicatorExpressionInfluenceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list()
        .forEach(indicatorExpressionInfluenceEntity -> {
          kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap.put(indicatorExpressionInfluenceEntity.getIndicatorInstanceId(), indicatorExpressionInfluenceEntity);
        });
  }

  /* runsix:自己不算，去掉自己 */
  public void populateInfluencedIndicatorInstanceIdSet(
      String principalId,
      Set<String> paramInfluencedIndicatorInstanceIdSet,
      List<String> conditionValListList,
      List<String> resultValListList
  ) {
    if (Objects.isNull(paramInfluencedIndicatorInstanceIdSet)
        || Objects.isNull(conditionValListList)
        || Objects.isNull(resultValListList)
    ) {return;}
    conditionValListList.forEach(conditionValList -> {
      if (StringUtils.isBlank(conditionValList)) {return;}
      paramInfluencedIndicatorInstanceIdSet.addAll(Arrays.stream(conditionValList.split(EnumString.COMMA.getStr())).filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
    });
    resultValListList.forEach(resultValList -> {
      if (StringUtils.isBlank(resultValList)) {return;}
      paramInfluencedIndicatorInstanceIdSet.addAll(Arrays.stream(resultValList.split(EnumString.COMMA.getStr())).filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
    });
    /* runsix:自己不算，去掉自己 */
    paramInfluencedIndicatorInstanceIdSet.remove(principalId);
  }

  public void checkCircleDependencyAndPopulateIndicatorExpressionInfluenceEntity(
      String appId,
      List<IndicatorExpressionInfluenceEntity> indicatorExpressionInfluenceEntityList,
      Integer source,
      String principalId,
      List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList,
      CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs,
      CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(indicatorExpressionInfluenceEntityList)
        || Objects.isNull(source) || !EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource().equals(source)
        || Objects.isNull(principalId)
    ) {return;}
    Map<String, IndicatorExpressionInfluenceEntity> kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap = new HashMap<>();
    Map<String, Set<String>> kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap = new HashMap<>();
    Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateAllInfluenceSet = CompletableFuture.runAsync(() -> {
      this.populateAllInfluenceSet(appId, kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap, kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap, kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap);
    });
    cfPopulateAllInfluenceSet.get();

    List<String> conditionValListList = new ArrayList<>();
    List<String> resultValListList = new ArrayList<>();
    this.populateCreateOrUpdateIndicatorExpressionItemRequestRsList(
        conditionValListList,
        resultValListList,
        createOrUpdateIndicatorExpressionItemRequestRsList,
        minCreateOrUpdateIndicatorExpressionItemRequestRs,
        maxCreateOrUpdateIndicatorExpressionItemRequestRs
    );

    Set<String> oldInfluencedIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(principalId);
    /* runsix:如果有影响关系，说明是已经存在的指标。先删除 */
    if (Objects.nonNull(oldInfluencedIndicatorInstanceIdSet)) {
      /* runsix:维护其它指标影响它 */
      oldInfluencedIndicatorInstanceIdSet.forEach(oldInfluencedIndicatorInstanceId -> {
        Set<String> influenceIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.get(oldInfluencedIndicatorInstanceId);
        if (Objects.nonNull(influenceIndicatorInstanceIdSet) && !influenceIndicatorInstanceIdSet.isEmpty()) {
          influenceIndicatorInstanceIdSet.remove(principalId);
          kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.put(oldInfluencedIndicatorInstanceId, influenceIndicatorInstanceIdSet);
        }
      });
    }

    /* runsix:后新增 new */
    Set<String> newInfluencedIndicatorInstanceIdSet = new HashSet<>();
    newInfluencedIndicatorInstanceIdSet.addAll(conditionValListList);
    newInfluencedIndicatorInstanceIdSet.addAll(resultValListList);
    newInfluencedIndicatorInstanceIdSet.remove(principalId);

    /* runsix:维护新的影响它的指标 */
    newInfluencedIndicatorInstanceIdSet.forEach(newInfluencedIndicatorInstanceId -> {
      Set<String> influenceIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.get(newInfluencedIndicatorInstanceId);
      if (Objects.isNull(influenceIndicatorInstanceIdSet)) {influenceIndicatorInstanceIdSet = new HashSet<>();}
      influenceIndicatorInstanceIdSet.add(principalId);
      kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.put(newInfluencedIndicatorInstanceId, influenceIndicatorInstanceIdSet);
    });

    /* runsix:旧的被影响指标都删掉，下面新增 */
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(principalId, newInfluencedIndicatorInstanceIdSet);
    /* runsix:它影响谁 */
    Set<String> principalInfluenceIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.get(principalId);
    if (Objects.isNull(principalInfluenceIndicatorInstanceIdSet)) {principalInfluenceIndicatorInstanceIdSet = new HashSet<>();}
    kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.put(principalId, principalInfluenceIndicatorInstanceIdSet);

    /* runsix:kahn校验 */
    rsUtilBiz.algorithmKahn(null, kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap);

    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.forEach((indicatorInstanceId, influencedIndicatorInstanceIdSet) -> {
      IndicatorExpressionInfluenceEntity indicatorExpressionInfluenceEntity = kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap.get(indicatorInstanceId);
      Set<String> influenceIndicatorInstanceIdSet1 = kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.get(indicatorInstanceId);
      String newInfluenceIndicatorInstanceId = rsUtilBiz.getCommaList(influenceIndicatorInstanceIdSet1);
      Set<String> influencedIndicatorInstanceIdSet1 = kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(indicatorInstanceId);
      String newInfluencedIndicatorInstanceId = rsUtilBiz.getCommaList(influencedIndicatorInstanceIdSet1);
      if (Objects.isNull(indicatorExpressionInfluenceEntity)) {
        indicatorExpressionInfluenceEntity = IndicatorExpressionInfluenceEntity
            .builder()
            .indicatorExpressionInfluenceId(idGenerator.nextIdStr())
            .appId(appId)
            .indicatorInstanceId(indicatorInstanceId)
            .influenceIndicatorInstanceIdList(newInfluenceIndicatorInstanceId)
            .influencedIndicatorInstanceIdList(newInfluencedIndicatorInstanceId)
            .build();
      } else {
        indicatorExpressionInfluenceEntity.setInfluenceIndicatorInstanceIdList(newInfluenceIndicatorInstanceId);
        indicatorExpressionInfluenceEntity.setInfluencedIndicatorInstanceIdList(newInfluencedIndicatorInstanceId);
      }
      indicatorExpressionInfluenceEntityList.add(indicatorExpressionInfluenceEntity);
    });
  }

  private void populateAllInfluenceSet(
      String appId,
      Map<String, IndicatorExpressionInfluenceEntity> kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap,
      Map<String, Set<String>> kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
      Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap) {
    if (StringUtils.isBlank(appId)
        || Objects.isNull(kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap)
        || Objects.isNull(kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap)
    ) {return;}
    indicatorExpressionInfluenceService.lambdaQuery()
        .eq(IndicatorExpressionInfluenceEntity::getAppId, appId)
        .list()
        .forEach(indicatorExpressionInfluenceEntity -> {
          String indicatorInstanceId = indicatorExpressionInfluenceEntity.getIndicatorInstanceId();
          kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap.put(indicatorInstanceId, indicatorExpressionInfluenceEntity);

          Set<String> influenceIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.get(indicatorInstanceId);
          if (Objects.isNull(influenceIndicatorInstanceIdSet)) {influenceIndicatorInstanceIdSet = new HashSet<>();}
          String influenceIndicatorInstanceIdList = indicatorExpressionInfluenceEntity.getInfluenceIndicatorInstanceIdList();
          influenceIndicatorInstanceIdSet.addAll(rsUtilBiz.getSplitList(influenceIndicatorInstanceIdList));
          kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.put(indicatorInstanceId, influenceIndicatorInstanceIdSet);

          Set<String> influencedIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(indicatorInstanceId);
          if (Objects.isNull(influencedIndicatorInstanceIdSet)) {influencedIndicatorInstanceIdSet = new HashSet<>();}
          String influencedIndicatorInstanceIdList = indicatorExpressionInfluenceEntity.getInfluencedIndicatorInstanceIdList();
          influencedIndicatorInstanceIdSet.addAll(rsUtilBiz.getSplitList(influencedIndicatorInstanceIdList));
          kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(indicatorInstanceId, influencedIndicatorInstanceIdSet);
        });
  }

  /* runsix:TODO DELETE  */
  public void oldCheckCircleDependencyAndPopulateIndicatorExpressionInfluenceEntity(
      String appId,
      List<IndicatorExpressionInfluenceEntity> indicatorExpressionInfluenceEntityList,
      Integer source,
      String principalId,
      List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList,
      CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs,
      CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(indicatorExpressionInfluenceEntityList)
        || Objects.isNull(source) || !EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource().equals(source)
        || Objects.isNull(principalId)
    ) {return;}
    Map<String, IndicatorExpressionInfluenceEntity> kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap = new HashMap<>();
    Set<String> indicatorInstanceIdSet = new HashSet<>();
    indicatorInstanceIdSet.add(principalId);
    Set<String> paramInfluencedIndicatorInstanceIdSet = new HashSet<>();
    List<String> conditionValListList = new ArrayList<>();
    List<String> resultValListList = new ArrayList<>();
    this.populateCreateOrUpdateIndicatorExpressionItemRequestRsList(
        conditionValListList,
        resultValListList,
        createOrUpdateIndicatorExpressionItemRequestRsList,
        minCreateOrUpdateIndicatorExpressionItemRequestRs,
        maxCreateOrUpdateIndicatorExpressionItemRequestRs
    );
    indicatorInstanceIdSet.addAll(conditionValListList);
    indicatorInstanceIdSet.addAll(resultValListList);
    CompletableFuture<Void> cfPopulateKIndicatorInstanceIdVIndicatorExpressionInfluenceMap = CompletableFuture.runAsync(() -> {
      this.populateKIndicatorInstanceIdVIndicatorExpressionInfluenceMap(kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap, indicatorInstanceIdSet);
    });
    cfPopulateKIndicatorInstanceIdVIndicatorExpressionInfluenceMap.get();

    IndicatorExpressionInfluenceEntity indicatorExpressionInfluenceEntity = kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap.get(principalId);

    CompletableFuture<Void> cfPopulateInfluencedIndicatorInstanceIdSet = CompletableFuture.runAsync(() -> {
      this.populateInfluencedIndicatorInstanceIdSet(principalId, paramInfluencedIndicatorInstanceIdSet, conditionValListList, resultValListList);
    });
    cfPopulateInfluencedIndicatorInstanceIdSet.get();

    if (Objects.nonNull(indicatorExpressionInfluenceEntity)) {
      String dbInfluenceIndicatorInstanceIdList = indicatorExpressionInfluenceEntity.getInfluenceIndicatorInstanceIdList();
      if (StringUtils.isNotBlank(dbInfluenceIndicatorInstanceIdList)
          && paramInfluencedIndicatorInstanceIdSet.stream().anyMatch(dbInfluenceIndicatorInstanceIdList::contains)
      ) {
        log.error("IndicatorExpressionBiz.v2CreateOrUpdate circle dependency dbInfluenceIndicatorInstanceIdList:{}, paramInfluencedIndicatorInstanceIdSet:{}", dbInfluenceIndicatorInstanceIdList, paramInfluencedIndicatorInstanceIdSet);
        throw new IndicatorExpressionException(EnumESC.INDICATOR_EXPRESSION_CIRCLE_DEPENDENCY);
      }


      indicatorExpressionInfluenceEntity.setInfluencedIndicatorInstanceIdList(String.join(EnumString.COMMA.getStr(), paramInfluencedIndicatorInstanceIdSet));
    } else {
      indicatorExpressionInfluenceEntity = IndicatorExpressionInfluenceEntity
          .builder()
          .indicatorExpressionInfluenceId(idGenerator.nextIdStr())
          .appId(appId)
          .indicatorInstanceId(principalId)
          .influencedIndicatorInstanceIdList(String.join(EnumString.COMMA.getStr(), paramInfluencedIndicatorInstanceIdSet))
          .build();
    }
    indicatorExpressionInfluenceEntityList.add(indicatorExpressionInfluenceEntity);
  }

  public void populateCreateOrUpdateIndicatorExpressionItemRequestRsList(
      List<String> conditionValListList,
      List<String> resultValListList,
      List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList,
      CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs,
      CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs
  ) {
    List<CreateOrUpdateIndicatorExpressionItemRequestRs> allCreateOrUpdateIndicatorExpressionItemRequestRsList = new ArrayList<>();
    if (Objects.nonNull(createOrUpdateIndicatorExpressionItemRequestRsList) && !createOrUpdateIndicatorExpressionItemRequestRsList.isEmpty()) {
      allCreateOrUpdateIndicatorExpressionItemRequestRsList.addAll(createOrUpdateIndicatorExpressionItemRequestRsList);
    }
    if (Objects.nonNull(minCreateOrUpdateIndicatorExpressionItemRequestRs)) {allCreateOrUpdateIndicatorExpressionItemRequestRsList.add(minCreateOrUpdateIndicatorExpressionItemRequestRs);}
    if (Objects.nonNull(maxCreateOrUpdateIndicatorExpressionItemRequestRs)) {allCreateOrUpdateIndicatorExpressionItemRequestRsList.add(maxCreateOrUpdateIndicatorExpressionItemRequestRs);}
    allCreateOrUpdateIndicatorExpressionItemRequestRsList.forEach(createOrUpdateIndicatorExpressionItemRequestRs -> {
      String conditionValList = createOrUpdateIndicatorExpressionItemRequestRs.getConditionValList();
      if (StringUtils.isNotBlank(conditionValList)) {
        conditionValListList.addAll(rsUtilBiz.getConditionValSplitList(conditionValList));
      }
      String resultValList = createOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
      if (StringUtils.isNotBlank(resultValList)) {
        resultValListList.addAll(rsUtilBiz.getResultValSplitList(resultValList));
      }
    });
  }

  /**
   * runsix method process
   * 1.最小最大是不跟指标公式关联的，是指标公式找它们
   * 2.最小最大值只能是数字
  */
  private void populateMinOrMaxIndicatorExpressionItem(
      boolean typeChange,
      AtomicReference<IndicatorExpressionItemEntity> indicatorExpressionItemEntityAtomicReference,
      CreateOrUpdateIndicatorExpressionItemRequestRs createOrUpdateIndicatorExpressionItemRequestRs
  ) {
    if (Objects.isNull(indicatorExpressionItemEntityAtomicReference)
        || Objects.isNull(createOrUpdateIndicatorExpressionItemRequestRs)
    ) {return;}
    String resultRaw = createOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
    String resultExpression = createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
    /* runsix: */
    if (StringUtils.isNoneBlank(resultRaw) && !NumberUtils.isCreatable(resultRaw)) {
      log.warn("RsIndicatorExpressionBiz.populateMinOrMaxIndicatorExpressionItem resultRaw:{}, is not blank and is not a digit", resultRaw);
      throw new RsIndicatorExpressionBizException(EnumESC.INDICATOR_EXPRESSION_MIN_MAX_MUST_BE_DIGIT);
    }
    if (StringUtils.isNoneBlank(resultExpression) && !NumberUtils.isCreatable(resultExpression)) {
      log.warn("RsIndicatorExpressionBiz.populateMinOrMaxIndicatorExpressionItem resultExpression:{} is not blank and is not a digit", resultExpression);
      throw new RsIndicatorExpressionBizException(EnumESC.INDICATOR_EXPRESSION_MIN_MAX_MUST_BE_DIGIT);
    }
    /* runsix:类型变化就是创建新的最小最大 */
    IndicatorExpressionItemEntity indicatorExpressionItemEntity = indicatorExpressionItemEntityAtomicReference.get();
    /* runsix:指标公式类型变化，需要创建新的最小最大值 */
    if (typeChange || Objects.isNull(indicatorExpressionItemEntity)) {
      indicatorExpressionItemEntity = IndicatorExpressionItemEntity
          .builder()
          .indicatorExpressionItemId(idGenerator.nextIdStr())
          .appId(createOrUpdateIndicatorExpressionItemRequestRs.getAppId())
          .resultRaw(createOrUpdateIndicatorExpressionItemRequestRs.getResultRaw())
          .resultExpression(createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression())
          .build();
    } else {
      indicatorExpressionItemEntity.setResultRaw(createOrUpdateIndicatorExpressionItemRequestRs.getResultRaw());
      indicatorExpressionItemEntity.setResultExpression(createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression());
    }
    indicatorExpressionItemEntityAtomicReference.set(indicatorExpressionItemEntity);
  }

  public void populateMinAndMaxIndicatorExpressionItem(
      boolean typeChange,
      AtomicReference<IndicatorExpressionItemEntity> minIndicatorExpressionItemEntityAtomicReference,
      CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs,
      AtomicReference<IndicatorExpressionItemEntity> maxIndicatorExpressionItemEntityAtomicReference,
      CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs
  ) {
    String minResultRaw = null;
    String minResultExpression = null;
    String maxResultRaw = null;
    String maxResultExpression = null;
    if (Objects.nonNull(minCreateOrUpdateIndicatorExpressionItemRequestRs)) {
      minResultRaw = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
      minResultExpression = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      populateMinOrMaxIndicatorExpressionItem(typeChange, minIndicatorExpressionItemEntityAtomicReference, minCreateOrUpdateIndicatorExpressionItemRequestRs);
    }
    if (Objects.nonNull(maxCreateOrUpdateIndicatorExpressionItemRequestRs)) {
      maxResultRaw = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
      maxResultExpression = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      populateMinOrMaxIndicatorExpressionItem(typeChange, maxIndicatorExpressionItemEntityAtomicReference, maxCreateOrUpdateIndicatorExpressionItemRequestRs);
    }
    /* runsix:最大最小都不为空并且都是数字，则需要保证最大大于或等于最小 */
    if (StringUtils.isNoneBlank(minResultRaw, minResultExpression, maxResultRaw, maxResultExpression)
        && NumberUtils.isCreatable(minResultRaw) && NumberUtils.isCreatable(minResultExpression)
        && NumberUtils.isCreatable(maxResultRaw) && NumberUtils.isCreatable(maxResultExpression)
        && Double.parseDouble(maxResultRaw) < Double.parseDouble(minResultRaw)
    ) {
      log.warn("RsIndicatorExpressionBiz.populateMinAndMaxIndicatorExpressionItem maxResultRaw:{} lt minResultRaw:{}", maxResultRaw, minResultRaw);
      throw new RsIndicatorExpressionBizException(EnumESC.INDICATOR_EXPRESSION_MAX_MUST_GE_MIN);
    }
  }

  public void populateByItemIdSetKIndicatorExpressionItemIdVIndicatorExpressionItemMap(
      Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemMap,
      Set<String> indicatorExpressionItemIdSet
  ) {
    if (Objects.isNull(kIndicatorExpressionItemIdVIndicatorExpressionItemMap)
        || Objects.isNull(indicatorExpressionItemIdSet) || indicatorExpressionItemIdSet.isEmpty()
    ) {return;}
    indicatorExpressionItemService.lambdaQuery()
        .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, indicatorExpressionItemIdSet)
        .list()
        .forEach(indicatorExpressionItemEntity -> {
          kIndicatorExpressionItemIdVIndicatorExpressionItemMap.put(indicatorExpressionItemEntity.getIndicatorExpressionItemId(), indicatorExpressionItemEntity);
        });
  }

  public void populateByIdKIndicatorExpressionItemIdVIndicatorExpressionItemMap(
      Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemMap,
      String indicatorExpressionId
  ) {
    if (Objects.isNull(kIndicatorExpressionItemIdVIndicatorExpressionItemMap)
        || StringUtils.isBlank(indicatorExpressionId)
    ) {return;}
    indicatorExpressionItemService.lambdaQuery()
        .in(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionId)
        .list()
        .forEach(indicatorExpressionItemEntity -> {
          kIndicatorExpressionItemIdVIndicatorExpressionItemMap.put(indicatorExpressionItemEntity.getIndicatorExpressionItemId(), indicatorExpressionItemEntity);
        });
  }

  public void populateByDbAndParamIndicatorExpressionItemEntityList(
      String indicatorExpressionId,
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList,
      Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemMap,
      List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList
  ) {
    if (Objects.isNull(indicatorExpressionItemEntityList)
        || Objects.isNull(kIndicatorExpressionItemIdVIndicatorExpressionItemMap)
        || Objects.isNull(createOrUpdateIndicatorExpressionItemRequestRsList) || createOrUpdateIndicatorExpressionItemRequestRsList.isEmpty()
    ) {return;}
    AtomicInteger seqAtomicInteger = new AtomicInteger(1);
    createOrUpdateIndicatorExpressionItemRequestRsList.forEach(createOrUpdateIndicatorExpressionItemRequestRs -> {
      String indicatorExpressionItemId = createOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
      String resultExpression = createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      resultExpression = rsUtilBiz.handleResultExpression(resultExpression);
      IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
      if (StringUtils.isBlank(indicatorExpressionItemId)) {
        Integer seq = null;
        if (StringUtils.isBlank(createOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw())) {
          seq = Integer.MAX_VALUE;
        } else {
          seq = seqAtomicInteger.getAndIncrement();
        }
        indicatorExpressionItemEntity = IndicatorExpressionItemEntity
          .builder()
          .indicatorExpressionItemId(idGenerator.nextIdStr())
          .appId(createOrUpdateIndicatorExpressionItemRequestRs.getAppId())
          .indicatorExpressionId(indicatorExpressionId)
          .conditionRaw(createOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw())
          .conditionExpression(createOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression())
          .conditionNameList(createOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList())
          .conditionValList(createOrUpdateIndicatorExpressionItemRequestRs.getConditionValList())
          .resultRaw(createOrUpdateIndicatorExpressionItemRequestRs.getResultRaw())
          .resultExpression(resultExpression)
          .resultNameList(createOrUpdateIndicatorExpressionItemRequestRs.getResultNameList())
          .resultValList(createOrUpdateIndicatorExpressionItemRequestRs.getResultValList())
          .seq(seq)
          .build();
      } else {
        indicatorExpressionItemEntity = kIndicatorExpressionItemIdVIndicatorExpressionItemMap.get(indicatorExpressionItemId);
        if (Objects.isNull(indicatorExpressionItemEntity)) {
          log.warn("RsIndicatorExpressionBiz.populateByDbAndParamIndicatorExpressionItemEntityList indicatorExpressionItemId:{} is illegal", indicatorExpressionItemId);
          throw new RsIndicatorExpressionBizException(EnumESC.INDICATOR_EXPRESSION_ITEM_ID_IS_ILLEGAL);
        }
        indicatorExpressionItemEntity.setConditionRaw(createOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw());
        indicatorExpressionItemEntity.setConditionExpression(createOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression());
        indicatorExpressionItemEntity.setConditionNameList(createOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList());
        indicatorExpressionItemEntity.setConditionValList(createOrUpdateIndicatorExpressionItemRequestRs.getConditionValList());
        indicatorExpressionItemEntity.setResultRaw(createOrUpdateIndicatorExpressionItemRequestRs.getResultRaw());
        indicatorExpressionItemEntity.setResultExpression(resultExpression);
        indicatorExpressionItemEntity.setResultNameList(createOrUpdateIndicatorExpressionItemRequestRs.getResultNameList());
        indicatorExpressionItemEntity.setResultValList(createOrUpdateIndicatorExpressionItemRequestRs.getResultValList());
        indicatorExpressionItemEntity.setSeq(seqAtomicInteger.getAndIncrement());
      }
      indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
    });
  }

  public void populateKIndicatorInstanceIdVValMap(
      Map<String, String> kIndicatorInstanceIdVValMap,
      Set<String> indicatorInstanceIdSet
  ) {
    if (Objects.isNull(kIndicatorInstanceIdVValMap)
        || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()
    ) {return;}
    indicatorRuleService.lambdaQuery()
        .in(IndicatorRuleEntity::getVariableId, indicatorInstanceIdSet)
        .list()
        .forEach(indicatorRuleEntity -> {
          kIndicatorInstanceIdVValMap.put(indicatorRuleEntity.getVariableId(), indicatorRuleEntity.getDef());
        });
  }

  public void populateParseParam(
      Set<String> reasonIdSet,
      Map<String, List<IndicatorExpressionEntity>> kReasonIdVIndicatorExpressionEntityListMap,
      Map<String, List<IndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap,
      Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()
        || Objects.isNull(kReasonIdVIndicatorExpressionEntityListMap)
        || Objects.isNull(kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap)
        || Objects.isNull(kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap)
    ) {return;}

    CompletableFuture<Void> cfPopulateKReasonIdVIndicatorExpressionEntityListMap = CompletableFuture.runAsync(() -> {
      try {
        populateKReasonIdVIndicatorExpressionEntityListMap(kReasonIdVIndicatorExpressionEntityListMap, reasonIdSet);
      } catch (Exception e) {
        log.error("RsCaseIndicatorExpressionBiz.populateParseParam error", e);
        throw new RsIndicatorExpressionException("填充解析公式参数出错，请及时与管理员联系");
      }
    });
    cfPopulateKReasonIdVIndicatorExpressionEntityListMap.get();

    Set<String> indicatorExpressionIdSet = new HashSet<>();
    kReasonIdVIndicatorExpressionEntityListMap.forEach((reasonId, indicatorExpressionRsEntityList) -> {
      indicatorExpressionRsEntityList.forEach(indicatorExpressionEntity -> {
        indicatorExpressionIdSet.add(indicatorExpressionEntity.getIndicatorExpressionId());
      });
    });
    if (indicatorExpressionIdSet.isEmpty()) {return;}

    Map<String, IndicatorExpressionEntity> kIndicatorExpressionIdVIndicatorExpressionEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKIndicatorExpressionIdVIndicatorExpressionEntityMap = CompletableFuture.runAsync(() -> {
      populateKIndicatorExpressionIdVIndicatorExpressionEntityMap(kIndicatorExpressionIdVIndicatorExpressionEntityMap, indicatorExpressionIdSet);
    });
    cfPopulateKIndicatorExpressionIdVIndicatorExpressionEntityMap.get();

    CompletableFuture<Void> cfPopulateKIndicatorExpressionIdVIndicatorExpressionItemListMap = CompletableFuture.runAsync(() -> {
      populateKIndicatorExpressionIdVIndicatorExpressionItemListMap(
          kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap, indicatorExpressionIdSet);
    });
    cfPopulateKIndicatorExpressionIdVIndicatorExpressionItemListMap.get();

    /* runsix:需要注意把最大最小值加进来 */
    Set<String> indicatorExpressionItemIdSet = new HashSet<>();
    kIndicatorExpressionIdVIndicatorExpressionEntityMap.values().forEach(indicatorExpressionEntity -> {
      String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
        indicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
      }
      String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
        indicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
      }
    });
    kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.forEach((indicatorExpressionId, indicatorExpressionItemEntityList) -> {
      indicatorExpressionItemEntityList.forEach(indicatorExpressionItemEntity -> {
        indicatorExpressionItemIdSet.add(indicatorExpressionItemEntity.getIndicatorExpressionItemId());
      });
    });
    if (indicatorExpressionItemIdSet.isEmpty()) {return;}

    CompletableFuture<Void> cfPopulateKIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap = CompletableFuture.runAsync(() -> {
      populateKIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap(
          kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap, indicatorExpressionItemIdSet);
    });
    cfPopulateKIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.get();
  }

  public void populateKReasonIdVIndicatorExpressionEntityListMap(
      Map<String, List<IndicatorExpressionEntity>> kReasonIdVIndicatorExpressionEntityListMap,
      Set<String> reasonIdSet
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(kReasonIdVIndicatorExpressionEntityListMap)
        || Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()
    ) {return;}
    /* runsix:init kReasonIdVIndicatorExpressionEntityListMap for lambda */
    reasonIdSet.forEach(reasonId -> {
      kReasonIdVIndicatorExpressionEntityListMap.put(reasonId, new ArrayList<>());
    });

    Map<String, List<IndicatorExpressionRefEntity>> kReasonIdVIndicatorExpressionRefListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKReasonIdVIndicatorExpressionRefListMap = CompletableFuture.runAsync(() -> {
      populateKReasonIdVIndicatorExpressionRefListMap(kReasonIdVIndicatorExpressionRefListMap, reasonIdSet);
    });
    cfPopulateKReasonIdVIndicatorExpressionRefListMap.get();

    Set<String> indicatorExpressionIdSet = new HashSet<>();
    kReasonIdVIndicatorExpressionRefListMap.forEach((reasonId, indicatorExpressionRefList) -> {
      indicatorExpressionRefList.forEach(indicatorExpressionRefRsEntity -> {
        indicatorExpressionIdSet.add(indicatorExpressionRefRsEntity.getIndicatorExpressionId());
      });
    });
    if (indicatorExpressionIdSet.isEmpty()) {return;}

    Map<String, IndicatorExpressionEntity> kIndicatorExpressionIdVIndicatorExpressionEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKIndicatorExpressionIdVIndicatorExpressionEntityMap = CompletableFuture.runAsync(() -> {
      populateKIndicatorExpressionIdVIndicatorExpressionEntityMap(kIndicatorExpressionIdVIndicatorExpressionEntityMap, indicatorExpressionIdSet);
    });
    cfPopulateKIndicatorExpressionIdVIndicatorExpressionEntityMap.get();

    kReasonIdVIndicatorExpressionRefListMap.forEach((reasonId, indicatorExpressionRefList) -> {
      List<IndicatorExpressionEntity> indicatorExpressionEntityList = kReasonIdVIndicatorExpressionEntityListMap.get(reasonId);
      indicatorExpressionRefList.forEach(indicatorExpressionRefEntity -> {
        String indicatorExpressionId = indicatorExpressionRefEntity.getIndicatorExpressionId();
        IndicatorExpressionEntity indicatorExpressionEntity = kIndicatorExpressionIdVIndicatorExpressionEntityMap.get(indicatorExpressionId);
        if (Objects.nonNull(indicatorExpressionEntity)) {
          indicatorExpressionEntityList.add(indicatorExpressionEntity);
        }
      });
      kReasonIdVIndicatorExpressionEntityListMap.put(reasonId, indicatorExpressionEntityList);
    });
  }

  public void populateKReasonIdVIndicatorExpressionRefListMap(
      Map<String, List<IndicatorExpressionRefEntity>> kReasonIdVIndicatorExpressionRefListMap,
      Set<String> reasonIdSet
  ) {
    if (Objects.isNull(kReasonIdVIndicatorExpressionRefListMap) || Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()) {return;}
    indicatorExpressionRefService.lambdaQuery()
        .in(IndicatorExpressionRefEntity::getReasonId, reasonIdSet)
        .list()
        .forEach(indicatorExpressionRefEntity -> {
          String reasonId = indicatorExpressionRefEntity.getReasonId();
          List<IndicatorExpressionRefEntity> indicatorExpressionRefEntityList = kReasonIdVIndicatorExpressionRefListMap.get(reasonId);
          if (Objects.isNull(indicatorExpressionRefEntityList)) {
            indicatorExpressionRefEntityList = new ArrayList<>();
          }
          indicatorExpressionRefEntityList.add(indicatorExpressionRefEntity);
          kReasonIdVIndicatorExpressionRefListMap.put(reasonId, indicatorExpressionRefEntityList);
        });
  }

  public void populateKIndicatorExpressionIdVIndicatorExpressionEntityMap(
      Map<String, IndicatorExpressionEntity> kIndicatorExpressionIdVIndicatorExpressionEntityMap,
      Set<String> indicatorExpressionIdSet
  ) {
    if (Objects.isNull(kIndicatorExpressionIdVIndicatorExpressionEntityMap)
        || Objects.isNull(indicatorExpressionIdSet) || indicatorExpressionIdSet.isEmpty()
    ) {return;}
    indicatorExpressionService.lambdaQuery()
        .in(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
        .list()
        .forEach(indicatorExpressionEntity -> {
          kIndicatorExpressionIdVIndicatorExpressionEntityMap.put(indicatorExpressionEntity.getIndicatorExpressionId(), indicatorExpressionEntity);
        });
  }

  public void populateKIndicatorExpressionIdVIndicatorExpressionItemListMap(
      Map<String, List<IndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemListMap,
      Set<String> indicatorExpressionIdSet) {
    if (Objects.isNull(kIndicatorExpressionIdVIndicatorExpressionItemListMap)) {return;}
    if (Objects.isNull(indicatorExpressionIdSet) || indicatorExpressionIdSet.isEmpty()) {return;}
    indicatorExpressionItemService.lambdaQuery()
        .in(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
        .list()
        .forEach(indicatorExpressionItemEntity -> {
          String indicatorExpressionId = indicatorExpressionItemEntity.getIndicatorExpressionId();
          List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemListMap.get(indicatorExpressionId);
          if (Objects.isNull(indicatorExpressionItemEntityList)) {
            indicatorExpressionItemEntityList = new ArrayList<>();
          }
          indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
          kIndicatorExpressionIdVIndicatorExpressionItemListMap.put(indicatorExpressionId, indicatorExpressionItemEntityList);
        });
  }

  public void populateKIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap(
      Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap,
      Set<String> indicatorExpressionItemIdSet
  ) {
    if (Objects.isNull(kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap)
        || Objects.isNull(indicatorExpressionItemIdSet) || indicatorExpressionItemIdSet.isEmpty()
    ) {return;}
    indicatorExpressionItemService.lambdaQuery()
        .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, indicatorExpressionItemIdSet)
        .list()
        .forEach(indicatorExpressionItemEntity -> {
          kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.put(indicatorExpressionItemEntity.getIndicatorExpressionItemId(), indicatorExpressionItemEntity);
        });
  }

  public void parseIndicatorExpression(
      Integer field, Integer source, Integer scene,
      AtomicReference<String> resultAtomicReference,
      DatabaseCalIndicatorExpressionRequest databaseCalIndicatorExpressionRequest
  ) {
    rsUtilBiz.checkField(field);
    rsUtilBiz.checkScene(scene);
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = rsUtilBiz.checkSource(source);
    Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap = databaseCalIndicatorExpressionRequest.getKIndicatorInstanceIdVIndicatorRuleEntityMap();
    IndicatorExpressionEntity indicatorExpressionEntity = databaseCalIndicatorExpressionRequest.getIndicatorExpressionEntity();
    List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = databaseCalIndicatorExpressionRequest.getIndicatorExpressionItemEntityList();
    IndicatorExpressionItemEntity minIndicatorExpressionItemEntity = databaseCalIndicatorExpressionRequest.getMinIndicatorExpressionItemEntity();
    IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity = databaseCalIndicatorExpressionRequest.getMaxIndicatorExpressionItemEntity();
    switch (enumIndicatorExpressionSource) {
      case INDICATOR_MANAGEMENT -> dPIEIndicatorManagement(
          scene,
          resultAtomicReference,
          kIndicatorInstanceIdVIndicatorRuleEntityMap,
          indicatorExpressionEntity,
          indicatorExpressionItemEntityList,
          minIndicatorExpressionItemEntity,
          maxIndicatorExpressionItemEntity
      );
      case CROWDS -> dPIECrowds(resultAtomicReference, indicatorExpressionItemEntityList, kIndicatorInstanceIdVIndicatorRuleEntityMap);
      case RISK_MODEL -> dPIERiskModel(scene,
          resultAtomicReference,
          kIndicatorInstanceIdVIndicatorRuleEntityMap,
          indicatorExpressionEntity,
          indicatorExpressionItemEntityList,
          minIndicatorExpressionItemEntity,
          maxIndicatorExpressionItemEntity);
      default -> {
        log.error("RsIndicatorExpressionBiz.parseDatabaseIndicatorExpression source:{} is illegal", source);
        throw new RsIndicatorExpressionException("公式来源不合法");
      }
    }
  }

  private void dPIERiskModel(Integer scene, AtomicReference<String> resultAtomicReference, Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap, IndicatorExpressionEntity indicatorExpressionEntity, List<IndicatorExpressionItemEntity> IndicatorExpressionItemEntityList, IndicatorExpressionItemEntity minIndicatorExpressionItemEntity, IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity) {
    dPIEIndicatorManagement(
        scene,
        resultAtomicReference,
        kIndicatorInstanceIdVIndicatorRuleEntityMap,
        indicatorExpressionEntity,
        IndicatorExpressionItemEntityList,
        minIndicatorExpressionItemEntity,
        maxIndicatorExpressionItemEntity
    );
  }

  private void dPIEIndicatorManagement(
      Integer scene, AtomicReference<String> resultAtomicReference,
      Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap,
      IndicatorExpressionEntity indicatorExpressionRsEntity,
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList,
      IndicatorExpressionItemEntity minIndicatorExpressionItemEntity,
      IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity
  ) {
    dPIEResultUsingIndicatorInstanceIdCombineWithHandle(
        scene, resultAtomicReference,
        kIndicatorInstanceIdVIndicatorRuleEntityMap,
        indicatorExpressionRsEntity,
        indicatorExpressionItemEntityList,
        minIndicatorExpressionItemEntity,
        maxIndicatorExpressionItemEntity
    );
  }

  private void dPIECrowds(
      AtomicReference<String> resultAtomicReference,
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList,
      Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap
  ) {
    if (Objects.isNull(indicatorExpressionItemEntityList) || indicatorExpressionItemEntityList.isEmpty()) {return;}
    /* runsix:人群类型只能有一个公式，并且公式只有一个条件 */
    boolean result = dPIEConditionUsingIndicatorInstanceId(
        indicatorExpressionItemEntityList.get(0),
        kIndicatorInstanceIdVIndicatorRuleEntityMap
    );
    resultAtomicReference.set(String.valueOf(result));
  }

  public void dPIEResultUsingIndicatorInstanceIdCombineWithHandle(
      Integer scene, AtomicReference<String> resultAtomicReference,
      Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap,
      IndicatorExpressionEntity indicatorExpressionRsEntity,
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList,
      IndicatorExpressionItemEntity minIndicatorExpressionItemEntity,
      IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity
  ) {
    /* runsix:1.按顺序解析每一个公式 */
    indicatorExpressionItemEntityList.sort(Comparator.comparingInt(IndicatorExpressionItemEntity::getSeq));
    for (int i = 0; i <= indicatorExpressionItemEntityList.size()-1; i++) {
      IndicatorExpressionItemEntity indicatorExpressionItemRsEntity = indicatorExpressionItemEntityList.get(i);
      boolean hasResult = dPIEResultUsingIndicatorInstanceIdCombineWithoutHandle(
          resultAtomicReference, kIndicatorInstanceIdVIndicatorRuleEntityMap, indicatorExpressionItemRsEntity
      );
      if (hasResult) {
        /* runsix:2.处理解析后的结果 */
        handleParsedResult(
            resultAtomicReference, scene, indicatorExpressionRsEntity, minIndicatorExpressionItemEntity, maxIndicatorExpressionItemEntity
        );
        break;
      }
    }
  }

  private boolean dPIEConditionUsingIndicatorInstanceId(
      IndicatorExpressionItemEntity indicatorExpressionItemEntity,
      Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap
  ) {
    try {
      String conditionExpression = indicatorExpressionItemEntity.getConditionExpression();
      String conditionNameList = indicatorExpressionItemEntity.getConditionNameList();
      List<String> conditionNameSplitList = rsUtilBiz.getConditionNameSplitList(conditionNameList);
      String conditionValList = indicatorExpressionItemEntity.getConditionValList();
      List<String> conditionValSplitList = rsUtilBiz.getConditionValSplitList(conditionValList);
      if (StringUtils.isBlank(conditionExpression)) {
        return true;
      }
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(conditionExpression);
      for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
        String indicatorInstanceId = conditionValSplitList.get(i);
        IndicatorRuleEntity indicatorRuleEntity = kIndicatorInstanceIdVIndicatorRuleEntityMap.get(indicatorInstanceId);
        if (Objects.isNull(indicatorRuleEntity) || StringUtils.isBlank(indicatorRuleEntity.getDef())) {
          return false;
        }
        String currentVal = indicatorRuleEntity.getDef();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(conditionNameSplitList.get(i), currentVal);
        }
      }
      return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    } catch(Exception e) {
      log.error("RsCaseIndicatorExpressionBiz.dPIEConditionUsingExperimentIndicatorInstanceId", e);
      return false;
    }
  }

  private boolean dPIEResultUsingIndicatorInstanceIdCombineWithoutHandle(
      AtomicReference<String> resultAtomicReference,
      Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap,
      IndicatorExpressionItemEntity indicatorExpressionItemEntity
  ) {
    boolean parsedCondition = dPIEConditionUsingIndicatorInstanceId(indicatorExpressionItemEntity, kIndicatorInstanceIdVIndicatorRuleEntityMap);
    /* runsix:2.如果条件不满足，不解析结果，继续下一个 */
    if (!parsedCondition) {
      return false;
    }

    /* runsix:3.如果一个公式有结果就跳出 */
    String parsedResult = dPIEResultUsingCaseIndicatorInstanceId(indicatorExpressionItemEntity, kIndicatorInstanceIdVIndicatorRuleEntityMap);
    if (RsUtilBiz.RESULT_DROP.equals(parsedResult)) {
      return false;
    }
    resultAtomicReference.set(parsedResult);
    return true;
  }

  private void handleParsedResult(
      AtomicReference<String> resultAtomicReference,
      Integer scene,
      IndicatorExpressionEntity indicatorExpressionEntity,
      IndicatorExpressionItemEntity minIndicatorExpressionItemEntity,
      IndicatorExpressionItemEntity maxIndicatorExpressionItemRsEntity
  ) {
    EnumIndicatorExpressionScene enumIndicatorExpressionScene = EnumIndicatorExpressionScene.getByScene(scene);
    /* runsix:如果公式使用场景不明确，不做特殊处理，直接返回 */
    if (Objects.isNull(enumIndicatorExpressionScene)) {
      return;
    }
  }

  private String dPIEResultUsingCaseIndicatorInstanceId(
      IndicatorExpressionItemEntity indicatorExpressionItemEntity,
      Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap
  ) {
    try {
      String resultExpression = indicatorExpressionItemEntity.getResultExpression();
      String resultNameList = indicatorExpressionItemEntity.getResultNameList();
      List<String> resultNameSplitList = rsUtilBiz.getResultNameSplitList(resultNameList);
      String resultValList = indicatorExpressionItemEntity.getResultValList();
      List<String> resultValSplitList = rsUtilBiz.getResultValSplitList(resultValList);
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      if (StringUtils.isBlank(resultExpression)) {
        return RsUtilBiz.RESULT_DROP;
      }
      for (int i = 0; i <= resultNameSplitList.size() - 1; i++) {
        String indicatorInstanceId = resultValSplitList.get(i);
        IndicatorRuleEntity indicatorRuleEntity = kIndicatorInstanceIdVIndicatorRuleEntityMap.get(indicatorInstanceId);
        if (Objects.isNull(indicatorRuleEntity) || StringUtils.isBlank(indicatorRuleEntity.getDef())) {
          return RsUtilBiz.RESULT_DROP;
        }
        String currentVal = indicatorRuleEntity.getDef();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(resultNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(resultNameSplitList.get(i), currentVal);
        }
      }
      return expression.getValue(context, String.class);
    } catch(Exception e) {
      log.error("RsCaseIndicatorExpressionBiz.dPIEResultUsingExperimentIndicatorInstanceId", e);
      return RsUtilBiz.RESULT_DROP;
    }
  }
}
