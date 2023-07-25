package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.CaseCreateOrUpdateIndicatorExpressionItemRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.CaseIndicatorExpressionException;
import org.dows.hep.api.exception.RsCaseIndicatorExpressionBizException;
import org.dows.hep.api.exception.RsIndicatorExpressionException;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
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
public class RsCaseIndicatorExpressionBiz {
  private final IdGenerator idGenerator;
  private final CaseIndicatorCategoryService caseIndicatorCategoryService;
  private final CaseIndicatorInstanceService caseIndicatorInstanceService;
  private final CaseIndicatorRuleService caseIndicatorRuleService;
  private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
  private final CaseIndicatorExpressionInfluenceService caseIndicatorExpressionInfluenceService;
  private final RsUtilBiz rsUtilBiz;
  private final CasePersonService casePersonService;
  public void populateCaseIndicatorExpressionEntity(
      AtomicReference<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityAR,
      String caseIndicatorExpressionId) {
    if (Objects.isNull(caseIndicatorExpressionEntityAR) || StringUtils.isBlank(caseIndicatorExpressionId)) {return;}
    caseIndicatorExpressionService.lambdaQuery()
        .eq(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, caseIndicatorExpressionId)
        .oneOpt()
        .ifPresent(caseIndicatorExpressionEntityAR::set);
  }

  public void populateByCaseItemIdSetKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap(
      Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap,
      Set<String> caseIndicatorExpressionItemIdSet
  ) {
    if (Objects.isNull(kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap)
        || Objects.isNull(caseIndicatorExpressionItemIdSet) || caseIndicatorExpressionItemIdSet.isEmpty()
    ) {return;}
    caseIndicatorExpressionItemService.lambdaQuery()
        .in(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, caseIndicatorExpressionItemIdSet)
        .list()
        .forEach(caseIndicatorExpressionItemEntity -> {
          kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.put(caseIndicatorExpressionItemEntity.getIndicatorExpressionItemId(), caseIndicatorExpressionItemEntity);
        });
  }

  /**
   * runsix method process
   * 1.最小最大是不跟指标公式关联的，是指标公式找它们
   * 2.最小最大值只能是数字
   */
  private void populateMinOrMaxCaseIndicatorExpressionItem(
      boolean typeChange,
      AtomicReference<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityAtomicReference,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseCreateOrUpdateIndicatorExpressionItemRequestRs
  ) {
    if (Objects.isNull(caseIndicatorExpressionItemEntityAtomicReference)
        || Objects.isNull(caseCreateOrUpdateIndicatorExpressionItemRequestRs)
    ) {return;}
    String resultRaw = caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
    String resultExpression = caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
    /* runsix: */
    if (StringUtils.isNoneBlank(resultRaw) && !NumberUtils.isCreatable(resultRaw)) {
      log.warn("RsCaseIndicatorExpressionBiz.populateMinOrMaxCaseIndicatorExpressionItem resultRaw:{}, is not blank and is not a digit", resultRaw);
      throw new RsCaseIndicatorExpressionBizException(EnumESC.CASE_INDICATOR_EXPRESSION_MIN_MAX_MUST_BE_DIGIT);
    }
    if (StringUtils.isNoneBlank(resultExpression) && !NumberUtils.isCreatable(resultExpression)) {
      log.warn("RsCaseIndicatorExpressionBiz.populateMinOrMaxCaseIndicatorExpressionItem resultExpression:{} is not blank and is not a digit", resultExpression);
      throw new RsCaseIndicatorExpressionBizException(EnumESC.CASE_INDICATOR_EXPRESSION_MIN_MAX_MUST_BE_DIGIT);
    }
    /* runsix:类型变化就是创建新的最小最大 */
    CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity = caseIndicatorExpressionItemEntityAtomicReference.get();
    /* runsix:指标公式类型变化，需要创建新的最小最大值 */
    if (typeChange || Objects.isNull(caseIndicatorExpressionItemEntity)) {
      caseIndicatorExpressionItemEntity = CaseIndicatorExpressionItemEntity
          .builder()
          .caseIndicatorExpressionItemId(idGenerator.nextIdStr())
          .appId(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getAppId())
          .resultRaw(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw())
          .resultExpression(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression())
          .build();
    } else {
      caseIndicatorExpressionItemEntity.setResultRaw(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw());
      caseIndicatorExpressionItemEntity.setResultExpression(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression());
    }
    caseIndicatorExpressionItemEntityAtomicReference.set(caseIndicatorExpressionItemEntity);
  }

  public void populateByCaseIdKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap(
      Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap,
      String caseIndicatorExpressionId
  ) {
    if (Objects.isNull(kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap)
        || StringUtils.isBlank(caseIndicatorExpressionId)
    ) {return;}
    caseIndicatorExpressionItemService.lambdaQuery()
        .in(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
        .list()
        .forEach(caseIndicatorExpressionItemEntity -> {
          kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.put(caseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId(), caseIndicatorExpressionItemEntity);
        });
  }

  public void populateByDbAndParamCaseIndicatorExpressionItemEntityList(
      String caseIndicatorExpressionId,
      List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList,
      Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap,
      List<CaseCreateOrUpdateIndicatorExpressionItemRequestRs> caseCreateOrUpdateIndicatorExpressionItemRequestRsList
  ) {
    if (Objects.isNull(caseIndicatorExpressionItemEntityList)
        || Objects.isNull(kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap)
        || Objects.isNull(caseCreateOrUpdateIndicatorExpressionItemRequestRsList) || caseCreateOrUpdateIndicatorExpressionItemRequestRsList.isEmpty()
    ) {return;}
    AtomicInteger seqAtomicInteger = new AtomicInteger(1);
    caseCreateOrUpdateIndicatorExpressionItemRequestRsList.forEach(caseCreateOrUpdateIndicatorExpressionItemRequestRs -> {
      String caseIndicatorExpressionItemId = caseCreateOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
      String resultExpression = caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      resultExpression = rsUtilBiz.handleResultExpression(resultExpression);
      CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity = null;
      if (StringUtils.isBlank(caseIndicatorExpressionItemId)) {
        Integer seq = null;
        if (StringUtils.isBlank(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw())) {
          seq = Integer.MAX_VALUE;
        } else {
          seq = seqAtomicInteger.getAndIncrement();
        }
        caseIndicatorExpressionItemEntity = CaseIndicatorExpressionItemEntity
            .builder()
            .caseIndicatorExpressionItemId(idGenerator.nextIdStr())
            .appId(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getAppId())
            .indicatorExpressionId(caseIndicatorExpressionId)
            .conditionRaw(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw())
            .conditionExpression(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression())
            .conditionNameList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList())
            .conditionValList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionValList())
            .resultRaw(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw())
            .resultExpression(resultExpression)
            .resultNameList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList())
            .resultValList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList())
            .seq(seq)
            .build();
      } else {
        caseIndicatorExpressionItemEntity = kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get(caseIndicatorExpressionItemId);
        if (Objects.isNull(caseIndicatorExpressionItemEntity)) {
          log.warn("RsCaseIndicatorExpressionBiz.populateByDbAndParamCaseIndicatorExpressionItemEntityList caseIndicatorExpressionItemId:{} is illegal", caseIndicatorExpressionItemId);
          throw new RsCaseIndicatorExpressionBizException(EnumESC.CASE_INDICATOR_EXPRESSION_ITEM_ID_IS_ILLEGAL);
        }
        caseIndicatorExpressionItemEntity.setConditionRaw(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw());
        caseIndicatorExpressionItemEntity.setConditionExpression(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression());
        caseIndicatorExpressionItemEntity.setConditionNameList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList());
        caseIndicatorExpressionItemEntity.setConditionValList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionValList());
        caseIndicatorExpressionItemEntity.setResultRaw(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw());
        caseIndicatorExpressionItemEntity.setResultExpression(resultExpression);
        caseIndicatorExpressionItemEntity.setResultNameList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList());
        caseIndicatorExpressionItemEntity.setResultValList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList());
        caseIndicatorExpressionItemEntity.setSeq(seqAtomicInteger.getAndIncrement());
      }
      caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionItemEntity);
    });
  }

  public void populateKCaseIndicatorInstanceIdVValMap(
      Map<String, String> kCaseIndicatorInstanceIdVValMap,
      Set<String> caseIndicatorInstanceIdSet
  ) {
    if (Objects.isNull(kCaseIndicatorInstanceIdVValMap)
        || Objects.isNull(caseIndicatorInstanceIdSet) || caseIndicatorInstanceIdSet.isEmpty()
    ) {return;}
    caseIndicatorRuleService.lambdaQuery()
        .in(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceIdSet)
        .list()
        .forEach(caseIndicatorRuleEntity -> {
          kCaseIndicatorInstanceIdVValMap.put(caseIndicatorRuleEntity.getVariableId(), caseIndicatorRuleEntity.getDef());
        });
  }

  public void populateMinAndMaxCaseIndicatorExpressionItem(
      boolean typeChange,
      AtomicReference<CaseIndicatorExpressionItemEntity> caseMinIndicatorExpressionItemEntityAtomicReference,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
      AtomicReference<CaseIndicatorExpressionItemEntity> caseMaxIndicatorExpressionItemEntityAtomicReference,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs
  ) {
    String minResultRaw = caseMinCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
    String minResultExpression = caseMinCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
    String maxResultRaw = caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
    String maxResultExpression = caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
    /* runsix:最大最小都不为空并且都是数字，则需要保证最大大于或等于最小 */
    if (StringUtils.isNoneBlank(minResultRaw, minResultExpression, maxResultRaw, maxResultExpression)
        && NumberUtils.isCreatable(minResultRaw) && NumberUtils.isCreatable(minResultExpression)
        && NumberUtils.isCreatable(maxResultRaw) && NumberUtils.isCreatable(maxResultExpression)
        && Double.parseDouble(maxResultRaw) < Double.parseDouble(minResultRaw)
    ) {
      log.warn("RsCaseIndicatorExpressionBiz.populateMinAndMaxCaseIndicatorExpressionItem maxResultRaw:{} lt minResultRaw:{}", maxResultRaw, minResultRaw);
      throw new RsCaseIndicatorExpressionBizException(EnumESC.CASE_INDICATOR_EXPRESSION_MAX_MUST_GE_MIN);
    }
    populateMinOrMaxCaseIndicatorExpressionItem(typeChange, caseMinIndicatorExpressionItemEntityAtomicReference, caseMinCreateOrUpdateIndicatorExpressionItemRequestRs);
    populateMinOrMaxCaseIndicatorExpressionItem(typeChange, caseMaxIndicatorExpressionItemEntityAtomicReference, caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs);
  }

  public void populateKCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceMap(
      Map<String, CaseIndicatorExpressionInfluenceEntity> kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap,
      Set<String> caseIndicatorInstanceIdSet
  ) {
    if (Objects.isNull(kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap)
        || Objects.isNull(caseIndicatorInstanceIdSet) || caseIndicatorInstanceIdSet.isEmpty()
    ) {return;}
    caseIndicatorExpressionInfluenceService.lambdaQuery()
        .in(CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId, caseIndicatorInstanceIdSet)
        .list()
        .forEach(caseIndicatorExpressionInfluenceEntity -> {
          kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap.put(caseIndicatorExpressionInfluenceEntity.getIndicatorInstanceId(), caseIndicatorExpressionInfluenceEntity);
        });
  }

  public void populateCaseCreateOrUpdateIndicatorExpressionItemRequestRsList(
      List<String> conditionValListList,
      List<String> resultValListList,
      List<CaseCreateOrUpdateIndicatorExpressionItemRequestRs> caseCreateOrUpdateIndicatorExpressionItemRequestRsList,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs
  ) {
    List<CaseCreateOrUpdateIndicatorExpressionItemRequestRs> allCaseCreateOrUpdateIndicatorExpressionItemRequestRsList = new ArrayList<>();
    if (Objects.nonNull(caseCreateOrUpdateIndicatorExpressionItemRequestRsList) && !caseCreateOrUpdateIndicatorExpressionItemRequestRsList.isEmpty()) {
      allCaseCreateOrUpdateIndicatorExpressionItemRequestRsList.addAll(caseCreateOrUpdateIndicatorExpressionItemRequestRsList);
    }
    if (Objects.nonNull(caseMinCreateOrUpdateIndicatorExpressionItemRequestRs)) {allCaseCreateOrUpdateIndicatorExpressionItemRequestRsList.add(caseMinCreateOrUpdateIndicatorExpressionItemRequestRs);}
    if (Objects.nonNull(caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs)) {allCaseCreateOrUpdateIndicatorExpressionItemRequestRsList.add(caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs);}
    allCaseCreateOrUpdateIndicatorExpressionItemRequestRsList.forEach(createOrUpdateIndicatorExpressionItemRequestRs -> {
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

  /* runsix:自己不算，去掉自己 */
  public void populateCaseInfluencedIndicatorInstanceIdSet(
      String principalId,
      Set<String> paramCaseInfluencedIndicatorInstanceIdSet,
      List<String> conditionValListList,
      List<String> resultValListList
  ) {
    if (Objects.isNull(paramCaseInfluencedIndicatorInstanceIdSet)
        || Objects.isNull(conditionValListList)
        || Objects.isNull(resultValListList)
    ) {return;}
    conditionValListList.forEach(conditionValList -> {
      if (StringUtils.isBlank(conditionValList)) {return;}
      paramCaseInfluencedIndicatorInstanceIdSet.addAll(Arrays.stream(conditionValList.split(EnumString.COMMA.getStr())).filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
    });
    resultValListList.forEach(resultValList -> {
      if (StringUtils.isBlank(resultValList)) {return;}
      paramCaseInfluencedIndicatorInstanceIdSet.addAll(Arrays.stream(resultValList.split(EnumString.COMMA.getStr())).filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
    });
    /* runsix:自己不算，去掉自己 */
    paramCaseInfluencedIndicatorInstanceIdSet.remove(principalId);
  }

  private void populateAccountAllInfluenceSet(
      String accountId,
      String appId,
      Map<String, CaseIndicatorExpressionInfluenceEntity> kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap,
      Map<String, Set<String>> kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap,
      Map<String, Set<String>> kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap) {
    if (StringUtils.isBlank(appId)
        || Objects.isNull(kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap)
        || Objects.isNull(kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap)
    ) {return;}
    Set<String> caseIndicatorInstanceIdSet = caseIndicatorInstanceService.lambdaQuery()
        .eq(CaseIndicatorInstanceEntity::getAppId, appId)
        .eq(CaseIndicatorInstanceEntity::getPrincipalId, accountId)
        .list()
        .stream()
        .map(CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId)
        .collect(Collectors.toSet());
    if (caseIndicatorInstanceIdSet.isEmpty()) {return;}
    caseIndicatorExpressionInfluenceService.lambdaQuery()
        .in(CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId, caseIndicatorInstanceIdSet)
        .list()
        .forEach(caseIndicatorExpressionInfluenceEntity -> {
          String caseIndicatorInstanceId = caseIndicatorExpressionInfluenceEntity.getIndicatorInstanceId();
          kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap.put(caseIndicatorInstanceId, caseIndicatorExpressionInfluenceEntity);

          Set<String> caseInfluenceIndicatorInstanceIdSet = kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.get(caseIndicatorInstanceId);
          if (Objects.isNull(caseInfluenceIndicatorInstanceIdSet)) {caseInfluenceIndicatorInstanceIdSet = new HashSet<>();}
          String caseInfluenceIndicatorInstanceIdList = caseIndicatorExpressionInfluenceEntity.getInfluenceIndicatorInstanceIdList();
          caseInfluenceIndicatorInstanceIdSet.addAll(rsUtilBiz.getSplitList(caseInfluenceIndicatorInstanceIdList));
          kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.put(caseIndicatorInstanceId, caseInfluenceIndicatorInstanceIdSet);

          Set<String> caseInfluencedIndicatorInstanceIdSet = kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap.get(caseIndicatorInstanceId);
          if (Objects.isNull(caseInfluencedIndicatorInstanceIdSet)) {caseInfluencedIndicatorInstanceIdSet = new HashSet<>();}
          String caseInfluencedIndicatorInstanceIdList = caseIndicatorExpressionInfluenceEntity.getInfluencedIndicatorInstanceIdList();
          caseInfluencedIndicatorInstanceIdSet.addAll(rsUtilBiz.getSplitList(caseInfluencedIndicatorInstanceIdList));
          kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap.put(caseIndicatorInstanceId, caseInfluencedIndicatorInstanceIdSet);
        });
  }

  public void checkCircleDependencyAndPopulateIndicatorExpressionInfluenceEntity(
      String accountId,
      String appId,
      List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityList,
      Integer source,
      String principalId,
      List<CaseCreateOrUpdateIndicatorExpressionItemRequestRs> caseCreateOrUpdateIndicatorExpressionItemRequestRsList,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(caseIndicatorExpressionInfluenceEntityList)
        || Objects.isNull(source) || !EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource().equals(source)
        || Objects.isNull(principalId)
    ) {return;}
    Map<String, CaseIndicatorExpressionInfluenceEntity> kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap = new HashMap<>();
    Map<String, Set<String>> kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
    Map<String, Set<String>> kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateAllInfluenceSet = CompletableFuture.runAsync(() -> {
      this.populateAccountAllInfluenceSet(accountId, appId, kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap, kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap, kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap);
    });
    cfPopulateAllInfluenceSet.get();

    List<String> conditionValListList = new ArrayList<>();
    List<String> resultValListList = new ArrayList<>();
    this.populateCaseCreateOrUpdateIndicatorExpressionItemRequestRsList(
        conditionValListList,
        resultValListList,
        caseCreateOrUpdateIndicatorExpressionItemRequestRsList,
        caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
        caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs
    );

    Set<String> oldCaseInfluencedIndicatorInstanceIdSet = kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap.get(principalId);
    /* runsix:如果有影响关系，说明是已经存在的指标。先删除 */
    if (Objects.nonNull(oldCaseInfluencedIndicatorInstanceIdSet)) {
      /* runsix:维护其它指标影响它 */
      oldCaseInfluencedIndicatorInstanceIdSet.forEach(oldCaseInfluencedIndicatorInstanceId -> {
        Set<String> caseInfluenceIndicatorInstanceIdSet = kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.get(oldCaseInfluencedIndicatorInstanceId);
        if (Objects.nonNull(caseInfluenceIndicatorInstanceIdSet) && !caseInfluenceIndicatorInstanceIdSet.isEmpty()) {
          caseInfluenceIndicatorInstanceIdSet.remove(principalId);
          kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.put(oldCaseInfluencedIndicatorInstanceId, caseInfluenceIndicatorInstanceIdSet);
        }
      });
    }

    /* runsix:后新增 new */
    Set<String> newCaseInfluencedIndicatorInstanceIdSet = new HashSet<>();
    newCaseInfluencedIndicatorInstanceIdSet.addAll(conditionValListList);
    newCaseInfluencedIndicatorInstanceIdSet.addAll(resultValListList);
    newCaseInfluencedIndicatorInstanceIdSet.remove(principalId);

    /* runsix:维护新的影响它的指标 */
    newCaseInfluencedIndicatorInstanceIdSet.forEach(newInfluencedIndicatorInstanceId -> {
      Set<String> caseInfluenceIndicatorInstanceIdSet = kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.get(newInfluencedIndicatorInstanceId);
      if (Objects.isNull(caseInfluenceIndicatorInstanceIdSet)) {caseInfluenceIndicatorInstanceIdSet = new HashSet<>();}
      caseInfluenceIndicatorInstanceIdSet.add(principalId);
      kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.put(newInfluencedIndicatorInstanceId, caseInfluenceIndicatorInstanceIdSet);
    });

    /* runsix:旧的被影响指标都删掉，下面新增 */
    kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap.put(principalId, newCaseInfluencedIndicatorInstanceIdSet);
    /* runsix:它影响谁 */
    Set<String> casePrincipalInfluenceIndicatorInstanceIdSet = kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.get(principalId);
    if (Objects.isNull(casePrincipalInfluenceIndicatorInstanceIdSet)) {casePrincipalInfluenceIndicatorInstanceIdSet = new HashSet<>();}
    kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.put(principalId, casePrincipalInfluenceIndicatorInstanceIdSet);

    /* runsix:kahn校验 */
    rsUtilBiz.algorithmKahn(null, kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap);

    kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap.forEach((caseIndicatorInstanceId, caseInfluencedIndicatorInstanceIdSet) -> {
      CaseIndicatorExpressionInfluenceEntity caseIndicatorExpressionInfluenceEntity = kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap.get(caseIndicatorInstanceId);
      Set<String> caseInfluenceIndicatorInstanceIdSet1 = kCaseIndicatorInstanceIdVCaseInfluenceIndicatorInstanceIdSetMap.get(caseIndicatorInstanceId);
      String newCaseInfluenceIndicatorInstanceId = rsUtilBiz.getCommaList(caseInfluenceIndicatorInstanceIdSet1);
      Set<String> caseInfluencedIndicatorInstanceIdSet1 = kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap.get(caseIndicatorInstanceId);
      String newInfluencedIndicatorInstanceId = rsUtilBiz.getCommaList(caseInfluencedIndicatorInstanceIdSet1);
      if (Objects.isNull(caseIndicatorExpressionInfluenceEntity)) {
        caseIndicatorExpressionInfluenceEntity = CaseIndicatorExpressionInfluenceEntity
            .builder()
            .caseIndicatorExpressionInfluenceId(idGenerator.nextIdStr())
            .appId(appId)
            .indicatorInstanceId(caseIndicatorInstanceId)
            .influenceIndicatorInstanceIdList(newCaseInfluenceIndicatorInstanceId)
            .influencedIndicatorInstanceIdList(newInfluencedIndicatorInstanceId)
            .build();
      } else {
        caseIndicatorExpressionInfluenceEntity.setInfluenceIndicatorInstanceIdList(newCaseInfluenceIndicatorInstanceId);
        caseIndicatorExpressionInfluenceEntity.setInfluencedIndicatorInstanceIdList(newInfluencedIndicatorInstanceId);
      }
      caseIndicatorExpressionInfluenceEntityList.add(caseIndicatorExpressionInfluenceEntity);
    });
  }

  public void populateCaseIndicatorExpressionItemEntityList(
      List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList,
      String caseIndicatorExpressionId) {
    if (Objects.isNull(caseIndicatorExpressionItemEntityList) || StringUtils.isBlank(caseIndicatorExpressionId)) {return;}
    caseIndicatorExpressionItemEntityList.addAll(caseIndicatorExpressionItemService.lambdaQuery()
        .eq(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
        .list());
  }


  public void populateCaseIndicatorExpressionItemEntity(
      AtomicReference<CaseIndicatorExpressionItemEntity> minCaseIndicatorExpressionItemEntityAR,
      String minIndicatorExpressionItemId) {
    if (Objects.isNull(minCaseIndicatorExpressionItemEntityAR) || StringUtils.isBlank(minIndicatorExpressionItemId)) {return;}
    caseIndicatorExpressionItemService.lambdaQuery()
        .eq(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, minIndicatorExpressionItemId)
        .oneOpt()
        .ifPresent(minCaseIndicatorExpressionItemEntityAR::set);
  }

  public void populateCaseIndicatorInstanceEntity(
      AtomicReference<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityAR,
      String casePrincipalId) {
    if (Objects.isNull(caseIndicatorInstanceEntityAR) || StringUtils.isBlank(casePrincipalId)) {return;}
    caseIndicatorInstanceService.lambdaQuery()
        .eq(CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId, casePrincipalId)
        .oneOpt()
        .ifPresent(caseIndicatorInstanceEntityAR::set);
  }

  public void populateCaseIndicatorCategoryEntity(
      AtomicReference<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityAR,
      String indicatorCategoryId) {
    if (Objects.isNull(caseIndicatorCategoryEntityAR) || StringUtils.isBlank(indicatorCategoryId)) {return;}
    caseIndicatorCategoryService.lambdaQuery()
        .eq(CaseIndicatorCategoryEntity::getCaseIndicatorCategoryId, indicatorCategoryId)
        .oneOpt()
        .ifPresent(caseIndicatorCategoryEntityAR::set);
  }

  public void populateCaseIndicatorExpressionRefEntity(
      AtomicReference<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityAR,
      String caseIndicatorExpressionId) {
    if (Objects.isNull(caseIndicatorExpressionRefEntityAR) || StringUtils.isBlank(caseIndicatorExpressionId)) {return;}
    caseIndicatorExpressionRefService.lambdaQuery()
        .eq(CaseIndicatorExpressionRefEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
        .oneOpt()
        .ifPresent(caseIndicatorExpressionRefEntityAR::set);
  }

  public void parseCaseIndicatorExpression(
      Integer field, Integer source, Integer scene,
      AtomicReference<String> resultAtomicReference,
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      DatabaseCalIndicatorExpressionRequest databaseCalIndicatorExpressionRequest,
      CaseCalIndicatorExpressionRequest caseCalIndicatorExpressionRequest
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
    switch (enumIndicatorExpressionSource) {
      case INDICATOR_MANAGEMENT -> cPIEIndicatorManagement(
          scene,
          resultAtomicReference,
          kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
          caseIndicatorExpressionEntity,
          caseIndicatorExpressionItemEntityList,
          minCaseIndicatorExpressionItemEntity,
          maxCaseIndicatorExpressionItemEntity
      );
      case CROWDS -> cPIECrowds(
          resultAtomicReference,
          kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
          indicatorExpressionItemEntityList,
          kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap);
      case RISK_MODEL -> cPIERiskModel(scene,
          resultAtomicReference,
          kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
          kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
          indicatorExpressionEntity,
          indicatorExpressionItemEntityList,
          minIndicatorExpressionItemEntity,
          maxIndicatorExpressionItemEntity);
      default -> {
        log.error("RsIndicatorExpressionBiz.experimentParseIndicatorExpression source:{} is illegal", source);
        throw new RsIndicatorExpressionException("公式来源不合法");
      }
    }
  }

  private void cPIERiskModel(Integer scene, AtomicReference<String> resultAtomicReference, Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, Map<String, CaseIndicatorRuleEntity> kIndicatorInstanceIdVCaseIndicatorRuleEntityMap, IndicatorExpressionEntity indicatorExpressionEntity, List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList, IndicatorExpressionItemEntity minIndicatorExpressionItemEntity, IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity) {
    cPIEResultUsingIndicatorInstanceIdCombineWithHandle(
        scene, resultAtomicReference, kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, kIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
        indicatorExpressionEntity, indicatorExpressionItemEntityList, minIndicatorExpressionItemEntity, maxIndicatorExpressionItemEntity
    );
  }
  private void cPIEIndicatorManagement(
      Integer scene, AtomicReference<String> resultAtomicReference,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
      CaseIndicatorExpressionEntity caseIndicatorExpressionRsEntity,
      List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList,
      CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity,
      CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity
  ) {
    cPIEResultUsingCaseIndicatorInstanceIdCombineWithHandle(
        scene, resultAtomicReference,
        kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
        caseIndicatorExpressionRsEntity,
        caseIndicatorExpressionItemEntityList,
        minCaseIndicatorExpressionItemEntity,
        maxCaseIndicatorExpressionItemEntity
    );
  }

  private void cPIECrowds(
      AtomicReference<String> resultAtomicReference,
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap
  ) {
    if (Objects.isNull(indicatorExpressionItemEntityList) || indicatorExpressionItemEntityList.isEmpty()
        || Objects.isNull(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap) || kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.isEmpty()
    ) {return;}
    /* runsix:人群类型只能有一个公式，并且公式只有一个条件 */
    boolean result = cPIEConditionUsingIndicatorInstanceId(
        kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
        indicatorExpressionItemEntityList.get(0),
        kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap
    );
    resultAtomicReference.set(String.valueOf(result));
  }

  public void cPIEResultUsingCaseIndicatorInstanceIdCombineWithHandle(
      Integer scene, AtomicReference<String> resultAtomicReference,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
      CaseIndicatorExpressionEntity caseIndicatorExpressionRsEntity,
      List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList,
      CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity,
      CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity
  ) {
    /* runsix:1.按顺序解析每一个公式 */
    if (Objects.isNull(caseIndicatorExpressionItemEntityList)) {return;}
    caseIndicatorExpressionItemEntityList.sort(Comparator.comparingInt(CaseIndicatorExpressionItemEntity::getSeq));
    for (int i = 0; i <= caseIndicatorExpressionItemEntityList.size()-1; i++) {
      CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemRsEntity = caseIndicatorExpressionItemEntityList.get(i);
      boolean hasResult = cPIEResultUsingCaseIndicatorInstanceIdCombineWithoutHandle(
          resultAtomicReference, kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap, caseIndicatorExpressionItemRsEntity
      );
      if (hasResult) {
        /* runsix:2.处理解析后的结果 */
        handleParsedResultUsingCaseIndicatorInstanceId(
            resultAtomicReference, scene, caseIndicatorExpressionRsEntity, minCaseIndicatorExpressionItemEntity, maxCaseIndicatorExpressionItemEntity
        );
        break;
      }
    }
  }

  public void cPIEResultUsingIndicatorInstanceIdCombineWithHandle(
      Integer scene, AtomicReference<String> resultAtomicReference,
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
      IndicatorExpressionEntity indicatorExpressionEntity,
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList,
      IndicatorExpressionItemEntity minIndicatorExpressionItemEntity,
      IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity
  ) {
    /* runsix:1.按顺序解析每一个公式 */
    indicatorExpressionItemEntityList.sort(Comparator.comparingInt(IndicatorExpressionItemEntity::getSeq));
    for (int i = 0; i <= indicatorExpressionItemEntityList.size()-1; i++) {
      IndicatorExpressionItemEntity indicatorExpressionItemRsEntity = indicatorExpressionItemEntityList.get(i);
      boolean hasResult = cPIEResultUsingIndicatorInstanceIdCombineWithoutHandle(
          resultAtomicReference, kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap, indicatorExpressionItemRsEntity
      );
      if (hasResult) {
        /* runsix:2.处理解析后的结果 */
        handleParsedResultUsingIndicatorInstanceId(
            resultAtomicReference, scene, indicatorExpressionEntity, minIndicatorExpressionItemEntity, maxIndicatorExpressionItemEntity
        );
        break;
      }
    }
  }

  private void handleParsedResultUsingCaseIndicatorInstanceId(
      AtomicReference<String> resultAtomicReference,
      Integer scene,
      CaseIndicatorExpressionEntity caseIndicatorExpressionEntity,
      CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity,
      CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemRsEntity
  ) {
    EnumIndicatorExpressionScene enumIndicatorExpressionScene = EnumIndicatorExpressionScene.getByScene(scene);
    /* runsix:如果公式使用场景不明确，不做特殊处理，直接返回 */
    if (Objects.isNull(enumIndicatorExpressionScene)) {
      return;
    }
  }

  private void handleParsedResultUsingIndicatorInstanceId(
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

  private boolean cPIEResultUsingCaseIndicatorInstanceIdCombineWithoutHandle(
      AtomicReference<String> resultAtomicReference,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
      CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity
  ) {
    boolean parsedCondition = cPIEConditionUsingCaseIndicatorInstanceId(caseIndicatorExpressionItemEntity, kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap);
    /* runsix:2.如果条件不满足，不解析结果，继续下一个 */
    if (!parsedCondition) {
      return false;
    }

    /* runsix:3.如果一个公式有结果就跳出 */
    String parsedResult = cPIEResultUsingCaseIndicatorInstanceId(caseIndicatorExpressionItemEntity, kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap);
    if (RsUtilBiz.RESULT_DROP.equals(parsedResult)) {
      return false;
    }
    resultAtomicReference.set(parsedResult);
    return true;
  }

  private boolean cPIEResultUsingIndicatorInstanceIdCombineWithoutHandle(
      AtomicReference<String> resultAtomicReference,
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
      IndicatorExpressionItemEntity indicatorExpressionItemEntity
  ) {
    boolean parsedCondition = cPIEConditionUsingIndicatorInstanceId(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, indicatorExpressionItemEntity, kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap);
    /* runsix:2.如果条件不满足，不解析结果，继续下一个 */
    if (!parsedCondition) {
      return false;
    }

    /* runsix:3.如果一个公式有结果就跳出 */
    String parsedResult = cPIEResultUsingIndicatorInstanceId(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, indicatorExpressionItemEntity, kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap);
    if (RsUtilBiz.RESULT_DROP.equals(parsedResult)) {
      return false;
    }
    resultAtomicReference.set(parsedResult);
    return true;
  }

  private boolean cPIEConditionUsingIndicatorInstanceId(
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      IndicatorExpressionItemEntity indicatorExpressionItemEntity,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap
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
        String caseIndicatorInstanceId = kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get(indicatorInstanceId);
        if (StringUtils.isBlank(caseIndicatorInstanceId)) {continue;}
        CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(caseIndicatorInstanceId);
        if (Objects.isNull(caseIndicatorRuleEntity) || StringUtils.isBlank(caseIndicatorRuleEntity.getDef())) {
          return false;
        }
        String currentVal = caseIndicatorRuleEntity.getDef();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(conditionNameSplitList.get(i), currentVal);
        }
      }
      return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    } catch(Exception e) {
      log.error("RsCaseIndicatorExpressionBiz.cPIEConditionUsingIndicatorInstanceId", e);
      return false;
    }
  }

  private boolean cPIEConditionUsingCaseIndicatorInstanceId(
      CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap
  ) {
    try {
      String conditionExpression = caseIndicatorExpressionItemEntity.getConditionExpression();
      String conditionNameList = caseIndicatorExpressionItemEntity.getConditionNameList();
      List<String> conditionNameSplitList = rsUtilBiz.getConditionNameSplitList(conditionNameList);
      String conditionValList = caseIndicatorExpressionItemEntity.getConditionValList();
      List<String> conditionValSplitList = rsUtilBiz.getConditionValSplitList(conditionValList);
      if (StringUtils.isBlank(conditionExpression)) {
        return true;
      }
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(conditionExpression);
      for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
        String caseIndicatorInstanceId = conditionValSplitList.get(i);
        if (StringUtils.isBlank(caseIndicatorInstanceId)) {continue;}
        CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(caseIndicatorInstanceId);
        if (Objects.isNull(caseIndicatorRuleEntity) || StringUtils.isBlank(caseIndicatorRuleEntity.getDef())) {
          return false;
        }
        String currentVal = caseIndicatorRuleEntity.getDef();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(conditionNameSplitList.get(i), currentVal);
        }
      }
      return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    } catch(Exception e) {
      log.error("RsCaseIndicatorExpressionBiz.cPIEConditionUsingCaseIndicatorInstanceId", e);
      return false;
    }
  }

  private String cPIEResultUsingCaseIndicatorInstanceId(
      CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap
  ) {
    try {
      String resultExpression = caseIndicatorExpressionItemEntity.getResultExpression();
      String resultNameList = caseIndicatorExpressionItemEntity.getResultNameList();
      List<String> resultNameSplitList = rsUtilBiz.getResultNameSplitList(resultNameList);
      String resultValList = caseIndicatorExpressionItemEntity.getResultValList();
      List<String> resultValSplitList = rsUtilBiz.getResultValSplitList(resultValList);
      if (StringUtils.isBlank(resultExpression)) {
        return RsUtilBiz.RESULT_DROP;
      }
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      for (int i = 0; i <= resultNameSplitList.size() - 1; i++) {
        String caseIndicatorInstanceId = resultValSplitList.get(i);
        if (StringUtils.isBlank(caseIndicatorInstanceId)) {continue;}
        CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(caseIndicatorInstanceId);
        if (Objects.isNull(caseIndicatorRuleEntity) || StringUtils.isBlank(caseIndicatorRuleEntity.getDef())) {
          return RsUtilBiz.RESULT_DROP;
        }
        String currentVal = caseIndicatorRuleEntity.getDef();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(resultNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(resultNameSplitList.get(i), currentVal);
        }
      }
      return expression.getValue(context, String.class);
    } catch(Exception e) {
      log.error("RsCaseIndicatorExpressionBiz.cPIEResultUsingExperimentIndicatorInstanceId", e);
      return RsUtilBiz.RESULT_DROP;
    }
  }

  private String cPIEResultUsingIndicatorInstanceId(
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      IndicatorExpressionItemEntity indicatorExpressionItemEntity,
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap
  ) {
    try {
      String resultExpression = indicatorExpressionItemEntity.getResultExpression();
      String resultNameList = indicatorExpressionItemEntity.getResultNameList();
      List<String> resultNameSplitList = rsUtilBiz.getResultNameSplitList(resultNameList);
      String resultValList = indicatorExpressionItemEntity.getResultValList();
      List<String> resultValSplitList = rsUtilBiz.getResultValSplitList(resultValList);
      if (StringUtils.isBlank(resultExpression)) {
        return RsUtilBiz.RESULT_DROP;
      }
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      for (int i = 0; i <= resultNameSplitList.size() - 1; i++) {
        String indicatorInstanceId = resultValSplitList.get(i);
        String caseIndicatorInstanceId = kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get(indicatorInstanceId);
        if (StringUtils.isBlank(caseIndicatorInstanceId)) {continue;}
        CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(caseIndicatorInstanceId);
        if (Objects.isNull(caseIndicatorRuleEntity) || StringUtils.isBlank(caseIndicatorRuleEntity.getDef())) {
          return RsUtilBiz.RESULT_DROP;
        }
        String currentVal = caseIndicatorRuleEntity.getDef();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(resultNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(resultNameSplitList.get(i), currentVal);
        }
      }
      return expression.getValue(context, String.class);
    } catch(Exception e) {
      log.error("RsCaseIndicatorExpressionBiz.cPIEResultUsingExperimentIndicatorInstanceId", e);
      return RsUtilBiz.RESULT_DROP;
    }
  }

  public void populateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap(
      Map<String, Set<String>> kCaseIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
      Set<String> caseIndicatorInstanceIdSet) {
    if (Objects.isNull(kCaseIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap)
        || Objects.isNull(caseIndicatorInstanceIdSet) || caseIndicatorInstanceIdSet.isEmpty()
    ) {return;}
    caseIndicatorExpressionInfluenceService.lambdaQuery()
        .in(CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId, caseIndicatorInstanceIdSet)
        .list()
        .forEach(caseIndicatorExpressionInfluenceEntity -> {
          String caseIndicatorInstanceId = caseIndicatorExpressionInfluenceEntity.getIndicatorInstanceId();
          Set<String> influencedIndicatorInstanceIdSet = kCaseIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(caseIndicatorInstanceId);
          if (Objects.isNull(influencedIndicatorInstanceIdSet)) {influencedIndicatorInstanceIdSet = new HashSet<>();}
          String influencedIndicatorInstanceIdList = caseIndicatorExpressionInfluenceEntity.getInfluencedIndicatorInstanceIdList();
          List<String> splitList = rsUtilBiz.getSplitList(influencedIndicatorInstanceIdList);
          influencedIndicatorInstanceIdSet.addAll(splitList);
          kCaseIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(caseIndicatorInstanceId, influencedIndicatorInstanceIdSet);
        });
  }

  public void populateKCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap(
      Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap,
      Set<String> caseIndicatorInstanceIdSet) {
    if (Objects.isNull(kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap)
        || Objects.isNull(caseIndicatorInstanceIdSet) || caseIndicatorInstanceIdSet.isEmpty()
    ) {return;}
    caseIndicatorRuleService.lambdaQuery()
        .in(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceIdSet)
        .list()
        .forEach(caseIndicatorRuleEntity -> {
          kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.put(caseIndicatorRuleEntity.getVariableId(), caseIndicatorRuleEntity);
        });
  }

  public void populateKReasonIdVCaseIndicatorExpressionRefEntityListMap(
      Map<String, List<CaseIndicatorExpressionRefEntity>> kReasonIdVCaseIndicatorExpressionRefEntityListMap,
      Set<String> reasonIdSet
  ){
    if (Objects.isNull(kReasonIdVCaseIndicatorExpressionRefEntityListMap)
        || Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()
    ) {return;}
    caseIndicatorExpressionRefService.lambdaQuery()
        .in(CaseIndicatorExpressionRefEntity::getReasonId, reasonIdSet)
        .list()
        .forEach(caseIndicatorExpressionRefEntity -> {
          String reasonId = caseIndicatorExpressionRefEntity.getReasonId();
          List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList = kReasonIdVCaseIndicatorExpressionRefEntityListMap.get(reasonId);
          if (Objects.isNull(caseIndicatorExpressionRefEntityList)) {caseIndicatorExpressionRefEntityList = new ArrayList<>();}
          caseIndicatorExpressionRefEntityList.add(caseIndicatorExpressionRefEntity);
          kReasonIdVCaseIndicatorExpressionRefEntityListMap.put(reasonId, caseIndicatorExpressionRefEntityList);
        });
  }

  public void populateKCaseIndicatorExpressionIdCaseIndicatorExpressionEntityMap(
      Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap,
      Set<String> indicatorExpressionIdSet
  ) {
    if (Objects.isNull(kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap)
        || Objects.isNull(indicatorExpressionIdSet) || indicatorExpressionIdSet.isEmpty()
    ) {return;}
    caseIndicatorExpressionService.lambdaQuery()
        .in(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, indicatorExpressionIdSet)
        .list()
        .forEach(caseIndicatorExpressionEntity -> {
          kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.put(caseIndicatorExpressionEntity.getCaseIndicatorExpressionId(), caseIndicatorExpressionEntity);
        });
  }

  public void populateKCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap(
      Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap,
      Set<String> caseIndicatorInstanceIdSet) {
    if (Objects.isNull(kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap)
        || Objects.isNull(caseIndicatorInstanceIdSet) || caseIndicatorInstanceIdSet.isEmpty()
    ) {return;}
    Map<String, List<CaseIndicatorExpressionRefEntity>> kReasonIdVCaseIndicatorExpressionRefEntityListMap = new HashMap<>();
    this.populateKReasonIdVCaseIndicatorExpressionRefEntityListMap(kReasonIdVCaseIndicatorExpressionRefEntityListMap, caseIndicatorInstanceIdSet);
    Map<String, Set<String>> kReasonIdVCaseIndicatorExpressionIdSetMap = new HashMap<>();
    Set<String> caseIndicatorExpressionIdSet = new HashSet<>();
    kReasonIdVCaseIndicatorExpressionRefEntityListMap.forEach((reasonId, caseIndicatorExpressionRefEntityList) -> {
      Set<String> caseIndicatorExpressionIdSet2 = caseIndicatorExpressionRefEntityList.stream().map(CaseIndicatorExpressionRefEntity::getIndicatorExpressionId).collect(Collectors.toSet());
      caseIndicatorExpressionIdSet.addAll(caseIndicatorExpressionIdSet2);
      Set<String> caseIndicatorExpressionIdSet1 = kReasonIdVCaseIndicatorExpressionIdSetMap.get(reasonId);
      if (Objects.isNull(caseIndicatorExpressionIdSet1)) {
        caseIndicatorExpressionIdSet1 = new HashSet<>();
      }
      caseIndicatorExpressionIdSet1.addAll(caseIndicatorExpressionIdSet2);
      kReasonIdVCaseIndicatorExpressionIdSetMap.put(reasonId, caseIndicatorExpressionIdSet1);
    });
    Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap = new HashMap<>();
    this.populateKCaseIndicatorExpressionIdCaseIndicatorExpressionEntityMap(kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap, caseIndicatorExpressionIdSet);
    kReasonIdVCaseIndicatorExpressionIdSetMap.forEach((reasonId, caseIndicatorExpressionIdSet2) -> {
      caseIndicatorExpressionIdSet2.forEach(caseIndicatorExpressionId -> {
        CaseIndicatorExpressionEntity caseIndicatorExpressionEntity = kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.get(caseIndicatorExpressionId);
        if (Objects.isNull(caseIndicatorExpressionEntity)) {return;}
        Integer source = caseIndicatorExpressionEntity.getSource();
        if (EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource().equals(source)) {
          kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap.put(reasonId, caseIndicatorExpressionEntity);
        }
      });
    });
  }

  public void populateKCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap(
      Map<String, List<CaseIndicatorExpressionItemEntity>> kCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap,
      Set<String> caseIndicatorInstanceIdSet) {
    if (Objects.isNull(kCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap)
        || Objects.isNull(caseIndicatorInstanceIdSet) || caseIndicatorInstanceIdSet.isEmpty()
    ) {return;}
    Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap = new HashMap<>();
    this.populateKCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap, caseIndicatorInstanceIdSet);
    if (kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap.isEmpty()) {return;}
    Set<String> caseIndicatorExpressionIdSet = kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap.values().stream().map(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId).collect(Collectors.toSet());
    Map<String, List<CaseIndicatorExpressionItemEntity>> kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap = new HashMap<>();
    caseIndicatorExpressionItemService.lambdaQuery()
        .in(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionIdSet)
        .list()
        .forEach(caseIndicatorExpressionItemEntity -> {
          String indicatorExpressionId = caseIndicatorExpressionItemEntity.getIndicatorExpressionId();
          List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
          if (Objects.isNull(caseIndicatorExpressionItemEntityList)) {
            caseIndicatorExpressionItemEntityList = new ArrayList<>();
          }
          caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionItemEntity);
          kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap.put(indicatorExpressionId, caseIndicatorExpressionItemEntityList);
        });
    kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap.forEach((caseIndicatorInstanceId, caseIndicatorExpressionEntity) -> {
      String indicatorExpressionId = caseIndicatorExpressionEntity.getCaseIndicatorExpressionId();
      List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
      if (Objects.isNull(caseIndicatorExpressionItemEntityList)) {return;}
      kCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap.put(caseIndicatorInstanceId, caseIndicatorExpressionItemEntityList);
    });
  }

  public void populateKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap(
      Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap,
      Set<String> caseIndicatorExpressionItemIdSet
  ) {
    if (Objects.isNull(kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap)
        || Objects.isNull(caseIndicatorExpressionItemIdSet) || caseIndicatorExpressionItemIdSet.isEmpty()
    ) {return;}
    caseIndicatorExpressionItemService.lambdaQuery()
        .in(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, caseIndicatorExpressionItemIdSet)
        .list()
        .forEach(caseIndicatorExpressionItemEntity -> {
          kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.put(caseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId(), caseIndicatorExpressionItemEntity);
        });
  }

  public void populateCaseParseParam(
      Set<String> caseReasonIdSet,
      Map<String, List<CaseIndicatorExpressionEntity>> kReasonIdVCaseIndicatorExpressionEntityListMap,
      Map<String, List<CaseIndicatorExpressionItemEntity>> kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap,
      Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(caseReasonIdSet) || caseReasonIdSet.isEmpty()
        || Objects.isNull(kReasonIdVCaseIndicatorExpressionEntityListMap)
        || Objects.isNull(kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap)
        || Objects.isNull(kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap)
    ) {return;}

    CompletableFuture<Void> cfPopulateKCaseReasonIdVCaseIndicatorExpressionRsEntityListMap = CompletableFuture.runAsync(() -> {
      try {
        populateKCaseReasonIdVCaseIndicatorExpressionEntityListMap(kReasonIdVCaseIndicatorExpressionEntityListMap, caseReasonIdSet);
      } catch (Exception e) {
        log.error("RsCaseIndicatorExpressionBiz.populateParseParam error", e);
        throw new RsIndicatorExpressionException("填充解析公式参数出错，请及时与管理员联系");
      }
    });
    cfPopulateKCaseReasonIdVCaseIndicatorExpressionRsEntityListMap.get();

    Set<String> caseIndicatorExpressionIdSet = new HashSet<>();
    kReasonIdVCaseIndicatorExpressionEntityListMap.forEach((reasonId, caseIndicatorExpressionRsEntityList) -> {
      caseIndicatorExpressionRsEntityList.forEach(caseIndicatorExpressionEntity -> {
        caseIndicatorExpressionIdSet.add(caseIndicatorExpressionEntity.getCaseIndicatorExpressionId());
      });
    });
    if (caseIndicatorExpressionIdSet.isEmpty()) {return;}

    Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap = CompletableFuture.runAsync(() -> {
      populateKCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap(kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap, caseIndicatorExpressionIdSet);
    });
    cfPopulateKCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.get();

    CompletableFuture<Void> cfPopulateKCaseIndicatorExpressionIdVCaseIndicatorExpressionItemListMap = CompletableFuture.runAsync(() -> {
      populateKCaseIndicatorExpressionIdVCaseIndicatorExpressionItemListMap(
          kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap, caseIndicatorExpressionIdSet);
    });
    cfPopulateKCaseIndicatorExpressionIdVCaseIndicatorExpressionItemListMap.get();

    /* runsix:需要注意把最大最小值加进来 */
    Set<String> caseIndicatorExpressionItemIdSet = new HashSet<>();
    kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.values().forEach(caseIndicatorExpressionEntity -> {
      String minIndicatorExpressionItemId = caseIndicatorExpressionEntity.getMinIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
        caseIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
      }
      String maxIndicatorExpressionItemId = caseIndicatorExpressionEntity.getMaxIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
        caseIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
      }
    });
    kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap.forEach((caseIndicatorExpressionId, caseIndicatorExpressionItemEntityList) -> {
      caseIndicatorExpressionItemEntityList.forEach(caseIndicatorExpressionItemEntity -> {
        caseIndicatorExpressionItemIdSet.add(caseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId());
      });
    });
    if (caseIndicatorExpressionItemIdSet.isEmpty()) {return;}

    CompletableFuture<Void> cfPopulateKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap = CompletableFuture.runAsync(() -> {
      populateKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap(
          kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap, caseIndicatorExpressionItemIdSet);
    });
    cfPopulateKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap.get();
  }

  public void populateKCaseReasonIdVCaseIndicatorExpressionEntityListMap(
      Map<String, List<CaseIndicatorExpressionEntity>> kReasonIdVCaseIndicatorExpressionEntityListMap,
      Set<String> reasonIdSet
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(kReasonIdVCaseIndicatorExpressionEntityListMap)
        || Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()
    ) {return;}
    /* runsix:init kReasonIdVCaseIndicatorExpressionEntityListMap for lambda */
    reasonIdSet.forEach(reasonId -> {
      kReasonIdVCaseIndicatorExpressionEntityListMap.put(reasonId, new ArrayList<>());
    });

    Map<String, List<CaseIndicatorExpressionRefEntity>> kReasonIdVCaseIndicatorExpressionRefListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKReasonIdVCaseIndicatorExpressionRefListMap = CompletableFuture.runAsync(() -> {
      populateKReasonIdVCaseIndicatorExpressionRefListMap(kReasonIdVCaseIndicatorExpressionRefListMap, reasonIdSet);
    });
    cfPopulateKReasonIdVCaseIndicatorExpressionRefListMap.get();

    Set<String> caseIndicatorExpressionIdSet = new HashSet<>();
    kReasonIdVCaseIndicatorExpressionRefListMap.forEach((reasonId, caseIndicatorExpressionRefList) -> {
      caseIndicatorExpressionRefList.forEach(caseIndicatorExpressionRefRsEntity -> {
        caseIndicatorExpressionIdSet.add(caseIndicatorExpressionRefRsEntity.getIndicatorExpressionId());
      });
    });
    if (caseIndicatorExpressionIdSet.isEmpty()) {return;}

    Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap = CompletableFuture.runAsync(() -> {
      populateKCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap(kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap, caseIndicatorExpressionIdSet);
    });
    cfPopulateKCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.get();

    kReasonIdVCaseIndicatorExpressionRefListMap.forEach((reasonId, caseIndicatorExpressionRefList) -> {
      List<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityList = kReasonIdVCaseIndicatorExpressionEntityListMap.get(reasonId);
      caseIndicatorExpressionRefList.forEach(caseIndicatorExpressionRefEntity -> {
        String caseIndicatorExpressionId = caseIndicatorExpressionRefEntity.getIndicatorExpressionId();
        CaseIndicatorExpressionEntity caseIndicatorExpressionEntity = kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.get(caseIndicatorExpressionId);
        if (Objects.nonNull(caseIndicatorExpressionEntity)) {
          caseIndicatorExpressionEntityList.add(caseIndicatorExpressionEntity);
        }
      });
      kReasonIdVCaseIndicatorExpressionEntityListMap.put(reasonId, caseIndicatorExpressionEntityList);
    });
  }

  public void populateKReasonIdVCaseIndicatorExpressionRefListMap(
      Map<String, List<CaseIndicatorExpressionRefEntity>> kReasonIdVCaseIndicatorExpressionRefListMap,
      Set<String> reasonIdSet
  ) {
    if (Objects.isNull(kReasonIdVCaseIndicatorExpressionRefListMap) || Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()) {
      return;
    }
    caseIndicatorExpressionRefService.lambdaQuery()
        .in(CaseIndicatorExpressionRefEntity::getReasonId, reasonIdSet)
        .list()
        .forEach(caseIndicatorExpressionRefEntity -> {
          String reasonId = caseIndicatorExpressionRefEntity.getReasonId();
          List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList = kReasonIdVCaseIndicatorExpressionRefListMap.get(reasonId);
          if (Objects.isNull(caseIndicatorExpressionRefEntityList)) {
            caseIndicatorExpressionRefEntityList = new ArrayList<>();
          }
          caseIndicatorExpressionRefEntityList.add(caseIndicatorExpressionRefEntity);
          kReasonIdVCaseIndicatorExpressionRefListMap.put(reasonId, caseIndicatorExpressionRefEntityList);
        });
  }

  public void populateKCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap(
      Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap,
      Set<String> caseIndicatorExpressionIdSet
  ) {
    if (Objects.isNull(kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap)
        || Objects.isNull(caseIndicatorExpressionIdSet) || caseIndicatorExpressionIdSet.isEmpty()
    ) {
      return;
    }
    caseIndicatorExpressionService.lambdaQuery()
        .in(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, caseIndicatorExpressionIdSet)
        .list()
        .forEach(caseIndicatorExpressionEntity -> {
          kCaseIndicatorExpressionIdVCaseIndicatorExpressionEntityMap.put(caseIndicatorExpressionEntity.getCaseIndicatorExpressionId(), caseIndicatorExpressionEntity);
        });
  }

  public void populateKCaseIndicatorExpressionIdVCaseIndicatorExpressionItemListMap(
      Map<String, List<CaseIndicatorExpressionItemEntity>> kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemListMap,
      Set<String> caseIndicatorExpressionIdSet) {
    if (Objects.isNull(kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemListMap)) {
      return;
    }
    if (Objects.isNull(caseIndicatorExpressionIdSet) || caseIndicatorExpressionIdSet.isEmpty()) {
      return;
    }
    caseIndicatorExpressionItemService.lambdaQuery()
        .in(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionIdSet)
        .list()
        .forEach(caseIndicatorExpressionItemEntity -> {
          String caseIndicatorExpressionId = caseIndicatorExpressionItemEntity.getIndicatorExpressionId();
          List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemListMap.get(caseIndicatorExpressionId);
          if (Objects.isNull(caseIndicatorExpressionItemEntityList)) {
            caseIndicatorExpressionItemEntityList = new ArrayList<>();
          }
          caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionItemEntity);
          kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemListMap.put(caseIndicatorExpressionId, caseIndicatorExpressionItemEntityList);
        });
  }

  public void populateKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap(
      Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap,
      Set<String> caseIndicatorExpressionItemIdSet
  ) {
    if (Objects.isNull(kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap)
        || Objects.isNull(caseIndicatorExpressionItemIdSet) || caseIndicatorExpressionItemIdSet.isEmpty()
    ) {
      return;
    }
    caseIndicatorExpressionItemService.lambdaQuery()
        .in(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, caseIndicatorExpressionItemIdSet)
        .list()
        .forEach(caseIndicatorExpressionItemEntity -> {
          kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap.put(
              caseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId(), caseIndicatorExpressionItemEntity
          );
        });
  }

  public void populateAllKCaseIndicatorInstanceIdVSeqMap(String caseInstanceId, Map<String, Integer> kCaseIndicatorInstanceIdVSeqMap) {
    if (Objects.isNull(kCaseIndicatorInstanceIdVSeqMap) || StringUtils.isBlank(caseInstanceId)) {return;}
    Map<String, Set<String>> kPrincipalIdVCaseIndicatorInstanceIdSetMap = new HashMap<>();
    Set<String> accountIdSet = casePersonService.lambdaQuery()
        .eq(CasePersonEntity::getCaseInstanceId, caseInstanceId)
        .list()
        .stream().map(CasePersonEntity::getAccountId)
        .collect(Collectors.toSet());
    if (accountIdSet.isEmpty()) {return;}
    caseIndicatorInstanceService.lambdaQuery()
        .in(CaseIndicatorInstanceEntity::getPrincipalId, accountIdSet)
        .list()
        .forEach(caseIndicatorInstanceEntity -> {
          String principalId = caseIndicatorInstanceEntity.getPrincipalId();
          Set<String> caseIndicatorInstanceIdSet = kPrincipalIdVCaseIndicatorInstanceIdSetMap.get(principalId);
          if (Objects.isNull(caseIndicatorInstanceIdSet)) {caseIndicatorInstanceIdSet = new HashSet<>();}
          caseIndicatorInstanceIdSet.add(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId());
          kPrincipalIdVCaseIndicatorInstanceIdSetMap.put(principalId, caseIndicatorInstanceIdSet);
        });
    kPrincipalIdVCaseIndicatorInstanceIdSetMap.values().forEach(caseIndicatorInstanceIdSet -> {
      List<String> seqList = new ArrayList<>();
      Map<String, Set<String>> kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
      CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap = CompletableFuture.runAsync(() -> {
        this.populateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap(kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap, caseIndicatorInstanceIdSet);
      });
      try {
        cfPopulateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap.get();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      }
      rsUtilBiz.algorithmKahn(seqList, kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap);
      for (int i = 0; i <= seqList.size()-1; i++) {
        String caseIndicatorInstanceId = seqList.get(i);
        kCaseIndicatorInstanceIdVSeqMap.put(caseIndicatorInstanceId, i+1);
      }
    });
  }
}
