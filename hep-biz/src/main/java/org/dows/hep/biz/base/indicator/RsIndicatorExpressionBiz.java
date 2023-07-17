package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionItemRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.IndicatorExpressionException;
import org.dows.hep.api.exception.RsIndicatorExpressionBizException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

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
  private final IndicatorExpressionService indicatorExpressionService;
  private final IndicatorExpressionItemService indicatorExpressionItemService;
  private final IndicatorExpressionInfluenceService indicatorExpressionInfluenceService;
  private final IdGenerator idGenerator;


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
      AtomicReference<IndicatorExpressionInfluenceEntity> indicatorExpressionInfluenceEntityAtomicReference,
      Integer source,
      String principalId,
      List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList,
      CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs,
      CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs

  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(indicatorExpressionInfluenceEntityAtomicReference)
        || Objects.isNull(source) || !EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource().equals(source)
        || Objects.isNull(principalId)
    ) {return;}
    Map<String, IndicatorExpressionInfluenceEntity> kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap = new HashMap<>();
    Set<String> indicatorInstanceIdSet = new HashSet<>();
    indicatorInstanceIdSet.add(principalId);
    CompletableFuture<Void> cfPopulateKIndicatorInstanceIdVIndicatorExpressionInfluenceMap = CompletableFuture.runAsync(() -> {
      this.populateKIndicatorInstanceIdVIndicatorExpressionInfluenceMap(kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap, indicatorInstanceIdSet);
    });
    cfPopulateKIndicatorInstanceIdVIndicatorExpressionInfluenceMap.get();

    IndicatorExpressionInfluenceEntity indicatorExpressionInfluenceEntity = kIndicatorInstanceIdVIndicatorExpressionInfluenceEntityMap.get(principalId);
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
      indicatorExpressionInfluenceEntityAtomicReference.set(indicatorExpressionInfluenceEntity);
    }
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
        conditionValListList.add(conditionValList);
      }
      String resultValList = createOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
      if (StringUtils.isNotBlank(resultValList)) {
        resultValListList.add(resultValList);
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
    String minResultRaw = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
    String minResultExpression = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
    String maxResultRaw = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
    String maxResultExpression = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
    /* runsix:最大最小都不为空并且都是数字，则需要保证最大大于或等于最小 */
    if (StringUtils.isNoneBlank(minResultRaw, minResultExpression, maxResultRaw, maxResultExpression)
        && NumberUtils.isCreatable(minResultRaw) && NumberUtils.isCreatable(minResultExpression)
        && NumberUtils.isCreatable(maxResultRaw) && NumberUtils.isCreatable(maxResultExpression)
        && Double.parseDouble(maxResultRaw) < Double.parseDouble(minResultRaw)
    ) {
      log.warn("RsIndicatorExpressionBiz.populateMinAndMaxIndicatorExpressionItem maxResultRaw:{} lt minResultRaw:{}", maxResultRaw, minResultRaw);
      throw new RsIndicatorExpressionBizException(EnumESC.INDICATOR_EXPRESSION_MAX_MUST_GE_MIN);
    }
    populateMinOrMaxIndicatorExpressionItem(typeChange, minIndicatorExpressionItemEntityAtomicReference, minCreateOrUpdateIndicatorExpressionItemRequestRs);
    populateMinOrMaxIndicatorExpressionItem(typeChange, maxIndicatorExpressionItemEntityAtomicReference, maxCreateOrUpdateIndicatorExpressionItemRequestRs);
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
      /* runsix:TODO 做校验 */
      String indicatorExpressionItemId = createOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
      IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
      if (StringUtils.isBlank(indicatorExpressionItemId)) {
        indicatorExpressionItemEntity = IndicatorExpressionItemEntity
            .builder()
            .indicatorExpressionItemId(idGenerator.nextIdStr())
            .appId(createOrUpdateIndicatorExpressionItemRequestRs.getAppId())
            .indicatorExpressionId(createOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionId())
            .conditionRaw(createOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw())
            .conditionExpression(createOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression())
            .conditionNameList(createOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList())
            .conditionValList(createOrUpdateIndicatorExpressionItemRequestRs.getConditionValList())
            .resultRaw(createOrUpdateIndicatorExpressionItemRequestRs.getResultRaw())
            .resultExpression(createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression())
            .resultNameList(createOrUpdateIndicatorExpressionItemRequestRs.getResultNameList())
            .resultValList(createOrUpdateIndicatorExpressionItemRequestRs.getResultValList())
            .seq(seqAtomicInteger.getAndIncrement())
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
        indicatorExpressionItemEntity.setResultExpression(createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression());
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
}
