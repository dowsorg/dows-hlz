package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.CaseCreateOrUpdateIndicatorExpressionItemRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionItemRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.CaseIndicatorExpressionException;
import org.dows.hep.api.exception.RsCaseIndicatorExpressionBizException;
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
public class RsCaseIndicatorExpressionBiz {
  private final IdGenerator idGenerator;
  private final CaseIndicatorCategoryService caseIndicatorCategoryService;
  private final CaseIndicatorInstanceService caseIndicatorInstanceService;
  private final CaseIndicatorRuleService caseIndicatorRuleService;
  private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
  private final CaseIndicatorExpressionInfluenceService caseIndicatorExpressionInfluenceService;
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
          kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.put(caseIndicatorExpressionItemEntity.getIndicatorExpressionItemId(), caseIndicatorExpressionItemEntity);
        });
  }

  public void populateByDbAndParamCaseIndicatorExpressionItemEntityList(
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
      /* runsix:TODO 做校验 */
      String caseIndicatorExpressionItemId = caseCreateOrUpdateIndicatorExpressionItemRequestRs.getCaseIndicatorExpressionItemId();
      CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity = null;
      if (StringUtils.isBlank(caseIndicatorExpressionItemId)) {
        caseIndicatorExpressionItemEntity = CaseIndicatorExpressionItemEntity
            .builder()
            .caseIndicatorExpressionItemId(idGenerator.nextIdStr())
            .appId(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getAppId())
            .indicatorExpressionId(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionId())
            .conditionRaw(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw())
            .conditionExpression(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression())
            .conditionNameList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList())
            .conditionValList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getConditionValList())
            .resultRaw(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw())
            .resultExpression(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression())
            .resultNameList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList())
            .resultValList(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList())
            .seq(seqAtomicInteger.getAndIncrement())
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
        caseIndicatorExpressionItemEntity.setResultExpression(caseCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression());
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
        conditionValListList.add(conditionValList);
      }
      String resultValList = createOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
      if (StringUtils.isNotBlank(resultValList)) {
        resultValListList.add(resultValList);
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

  public void checkCircleDependencyAndPopulateCaseIndicatorExpressionInfluenceEntity(
      AtomicReference<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityAtomicReference,
      Integer source,
      String principalId,
      List<CaseCreateOrUpdateIndicatorExpressionItemRequestRs> caseCreateOrUpdateIndicatorExpressionItemRequestRsList,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
      CaseCreateOrUpdateIndicatorExpressionItemRequestRs caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs

  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(caseIndicatorExpressionInfluenceEntityAtomicReference)
        || Objects.isNull(source) || !EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource().equals(source)
        || Objects.isNull(principalId)
    ) {return;}
    Map<String, CaseIndicatorExpressionInfluenceEntity> kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap = new HashMap<>();
    Set<String> caseIndicatorInstanceIdSet = new HashSet<>();
    caseIndicatorInstanceIdSet.add(principalId);
    CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceMap = CompletableFuture.runAsync(() -> {
      this.populateKCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceMap(kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap, caseIndicatorInstanceIdSet);
    });
    cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceMap.get();

    CaseIndicatorExpressionInfluenceEntity caseIndicatorExpressionInfluenceEntity = kCaseIndicatorInstanceIdVCaseIndicatorExpressionInfluenceEntityMap.get(principalId);
    Set<String> paramCaseInfluencedIndicatorInstanceIdSet = new HashSet<>();
    List<String> conditionValListList = new ArrayList<>();
    List<String> resultValListList = new ArrayList<>();
    this.populateCaseCreateOrUpdateIndicatorExpressionItemRequestRsList(
        conditionValListList,
        resultValListList,
        caseCreateOrUpdateIndicatorExpressionItemRequestRsList,
        caseMinCreateOrUpdateIndicatorExpressionItemRequestRs,
        caseMaxCreateOrUpdateIndicatorExpressionItemRequestRs
    );
    CompletableFuture<Void> cfPopulateCaseInfluencedIndicatorInstanceIdSet = CompletableFuture.runAsync(() -> {
      this.populateCaseInfluencedIndicatorInstanceIdSet(principalId, paramCaseInfluencedIndicatorInstanceIdSet, conditionValListList, resultValListList);
    });
    cfPopulateCaseInfluencedIndicatorInstanceIdSet.get();

    if (Objects.nonNull(caseIndicatorExpressionInfluenceEntity)) {
      String dbCaseInfluenceIndicatorInstanceIdList = caseIndicatorExpressionInfluenceEntity.getInfluenceIndicatorInstanceIdList();
      if (StringUtils.isNotBlank(dbCaseInfluenceIndicatorInstanceIdList)
          && paramCaseInfluencedIndicatorInstanceIdSet.stream().anyMatch(dbCaseInfluenceIndicatorInstanceIdList::contains)
      ) {
        log.error("CaseIndicatorExpressionBiz.v2CreateOrUpdate circle dependency dbCaseInfluenceIndicatorInstanceIdList:{}, paramCaseInfluencedIndicatorInstanceIdSet:{}", dbCaseInfluenceIndicatorInstanceIdList, paramCaseInfluencedIndicatorInstanceIdSet);
        throw new CaseIndicatorExpressionException(EnumESC.CASE_INDICATOR_EXPRESSION_CIRCLE_DEPENDENCY);
      }
      caseIndicatorExpressionInfluenceEntity.setInfluencedIndicatorInstanceIdList(String.join(EnumString.COMMA.getStr(), paramCaseInfluencedIndicatorInstanceIdSet));
      caseIndicatorExpressionInfluenceEntityAtomicReference.set(caseIndicatorExpressionInfluenceEntity);
    }
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
}
