package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.CaseCreateCopyToPersonRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionItemResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceCategoryResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponseRs;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumIndicatorRuleType;
import org.dows.hep.api.enums.EnumString;
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
public class CaseIndicatorInstanceBiz {
  private final IdGenerator idGenerator;
  private final IndicatorInstanceBiz indicatorInstanceBiz;
  private final CaseIndicatorCategoryService caseIndicatorCategoryService;
  private final CaseIndicatorCategoryRefService caseIndicatorCategoryRefService;
  private final CaseIndicatorRuleService caseIndicatorRuleService;
  private final CaseIndicatorInstanceService caseIndicatorInstanceService;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
  private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
  private final CaseIndicatorExpressionInfluenceService caseIndicatorExpressionInfluenceService;
  private final CaseIndicatorExpressionBiz caseIndicatorExpressionBiz;
  private final CaseIndicatorPrincipalRefService caseIndicatorPrincipalRefService;

  public static String convertConditionValList2Case(
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      String conditionValList) {
    if (StringUtils.isBlank(conditionValList)) {
      return null;
    }
    if (Objects.isNull(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap)) {
      return null;
    }
    List<String> caseIndicatorInstanceIdList = Arrays.stream(conditionValList.split(EnumString.COMMA.getStr()))
        .map(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return String.join(EnumString.COMMA.getStr(), caseIndicatorInstanceIdList);
  }

  public static String convertResultValList2Case(
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      String resultValList) {
    if (StringUtils.isBlank(resultValList)) {
      return null;
    }
    if (Objects.isNull(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap)) {
      return null;
    }
    List<String> caseIndicatorInstanceIdList = Arrays.stream(resultValList.split(EnumString.COMMA.getStr()))
        .map(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return String.join(EnumString.COMMA.getStr(), caseIndicatorInstanceIdList);
  }

  public void populateInfluenceMap(
      Map<String, Set<String>> kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
      Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
      String indicatorInstanceId, String conditionValList, String resultValList
  ) {
    Set<String> influenceIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.get(indicatorInstanceId);
    if (Objects.isNull(influenceIndicatorInstanceIdSet)) {
      influenceIndicatorInstanceIdSet = new HashSet<>();
    }
    Set<String> influencedIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(indicatorInstanceId);
    if (Objects.isNull(influencedIndicatorInstanceIdSet)) {
      influencedIndicatorInstanceIdSet = new HashSet<>();
    }
    influencedIndicatorInstanceIdSet.addAll(Arrays.stream(conditionValList.split(EnumString.COMMA.getStr())).collect(Collectors.toSet()));
    influenceIndicatorInstanceIdSet.addAll(Arrays.stream(resultValList.split(EnumString.COMMA.getStr())).collect(Collectors.toSet()));
    kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.put(indicatorInstanceId, influenceIndicatorInstanceIdSet);
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(indicatorInstanceId, influencedIndicatorInstanceIdSet);
  }

  @Transactional(rollbackFor = Exception.class)
  public void copyToPerson(CaseCreateCopyToPersonRequestRs caseCreateCopyToPersonRequestRs) {
    String appId = caseCreateCopyToPersonRequestRs.getAppId();
    String casePersonId = caseCreateCopyToPersonRequestRs.getCasePersonId();
    List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList = indicatorInstanceBiz.getByAppId(appId);
    List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList = new ArrayList<>();
    List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityList = new ArrayList<>();
    List<CaseIndicatorRuleEntity> caseIndicatorRuleEntityList = new ArrayList<>();
    List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = new ArrayList<>();
    List<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityList = new ArrayList<>();
    List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = new ArrayList<>();
    List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList = new ArrayList<>();
    List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityList = new ArrayList<>();
    List<CaseIndicatorPrincipalRefEntity> caseIndicatorPrincipalRefEntityList = new ArrayList<>();
    Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap = new HashMap<>();
    Map<String, Set<String>> kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap = new HashMap<>();
    Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
    indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
      String indicatorCategoryId = indicatorInstanceCategoryResponseRs.getIndicatorCategoryId();
      String caseIndicatorCategoryId = idGenerator.nextIdStr();
      String categoryName = indicatorInstanceCategoryResponseRs.getCategoryName();
      Integer seq = indicatorInstanceCategoryResponseRs.getSeq();
      List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
      if (Objects.isNull(indicatorInstanceResponseRsList)) {
        return;
      }
      indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
        String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
        String caseIndicatorInstanceId = idGenerator.nextIdStr();
        kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.put(indicatorInstanceId, caseIndicatorInstanceId);
        String indicatorName = indicatorInstanceResponseRs.getIndicatorName();
        Integer displayByPercent = indicatorInstanceResponseRs.getDisplayByPercent();
        String unit = indicatorInstanceResponseRs.getUnit();
        Integer core = indicatorInstanceResponseRs.getCore();
        Integer food = indicatorInstanceResponseRs.getFood();
        String descr = indicatorInstanceResponseRs.getDescr();
        String def = indicatorInstanceResponseRs.getDef();
        String min = indicatorInstanceResponseRs.getMin();
        String max = indicatorInstanceResponseRs.getMax();
        Integer seqIndicatorInstance = indicatorInstanceResponseRs.getSeq();
        caseIndicatorInstanceEntityList.add(CaseIndicatorInstanceEntity
            .builder()
            .caseIndicatorInstanceId(caseIndicatorInstanceId)
            .indicatorInstanceId(indicatorInstanceId)
            .appId(appId)
            .indicatorName(indicatorName)
            .displayByPercent(displayByPercent)
            .unit(unit)
            .core(core)
            .food(food)
            .descr(descr)
            .build());
        caseIndicatorRuleEntityList.add(CaseIndicatorRuleEntity
            .builder()
            .caseIndicatorRuleId(idGenerator.nextIdStr())
            .appId(appId)
            .variableId(caseIndicatorInstanceId)
            .ruleType(EnumIndicatorRuleType.INDICATOR.getCode())
            .min(min)
            .max(max)
            .def(def)
            .build());
        caseIndicatorCategoryRefEntityList.add(CaseIndicatorCategoryRefEntity
            .builder()
            .caseIndicatorCategoryRefId(idGenerator.nextIdStr())
            .indicatorCategoryId(indicatorCategoryId)
            .appId(appId)
            .indicatorCategoryId(caseIndicatorCategoryId)
            .indicatorInstanceId(caseIndicatorInstanceId)
            .seq(seqIndicatorInstance)
            .build());
      });
      caseIndicatorCategoryEntityList.add(CaseIndicatorCategoryEntity
          .builder()
          .caseIndicatorCategoryId(caseIndicatorCategoryId)
          .indicatorCategoryId(indicatorCategoryId)
          .appId(appId)
          .pid(EnumIndicatorCategory.INDICATOR_MANAGEMENT.getCode())
          .categoryName(categoryName)
          .seq(seq)
          .build());
    });
    indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
      List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
      if (Objects.isNull(indicatorInstanceResponseRsList)) {
        return;
      }
      indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
        List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = indicatorInstanceResponseRs.getIndicatorExpressionResponseRsList();
        if (Objects.isNull(indicatorExpressionResponseRsList)) {
          return;
        }
        String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
        String caseIndicatorInstanceId = kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get(indicatorInstanceId);
        indicatorExpressionResponseRsList.forEach(indicatorExpressionResponseRs -> {
          String indicatorExpressionId = indicatorExpressionResponseRs.getIndicatorExpressionId();
          String caseIndicatorExpressionId = idGenerator.nextIdStr();
          String indicatorExpressionRefId = indicatorExpressionResponseRs.getIndicatorExpressionRefId();
          String caseIndicatorExpressionRefId = idGenerator.nextIdStr();
          String principalId = indicatorExpressionResponseRs.getPrincipalId();
          Integer type = indicatorExpressionResponseRs.getType();
          Integer source = indicatorExpressionResponseRs.getSource();
          List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList = indicatorExpressionResponseRs.getIndicatorExpressionItemResponseRsList();
          if (Objects.isNull(indicatorExpressionItemResponseRsList) || indicatorExpressionItemResponseRsList.isEmpty()) {
            return;
          }
          indicatorExpressionItemResponseRsList.forEach(indicatorExpressionItemResponseRs -> {
            String indicatorExpressionItemId = indicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
            String caseIndicatorExpressionItemId = idGenerator.nextIdStr();
            String conditionRaw = indicatorExpressionItemResponseRs.getConditionRaw();
            String conditionExpression = indicatorExpressionItemResponseRs.getConditionExpression();
            String conditionNameList = indicatorExpressionItemResponseRs.getConditionNameList();
            String conditionValList = convertConditionValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, indicatorExpressionItemResponseRs.getConditionValList());
            String resultRaw = indicatorExpressionItemResponseRs.getResultRaw();
            String resultExpression = indicatorExpressionItemResponseRs.getResultExpression();
            String resultNameList = indicatorExpressionItemResponseRs.getResultNameList();
            String resultValList = convertResultValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, indicatorExpressionItemResponseRs.getResultValList());
            Integer seqIndicatorExpressionItem = indicatorExpressionItemResponseRs.getSeq();
            caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionBiz.indicatorExpressionItemResponseRs2Case(
                caseIndicatorExpressionItemId, indicatorExpressionItemId, appId, caseIndicatorExpressionId, conditionRaw,
                conditionExpression, conditionNameList, conditionValList, resultRaw, resultExpression, resultNameList, resultValList, seqIndicatorExpressionItem));
            populateInfluenceMap(
                kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
                kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
                caseIndicatorInstanceId,
                conditionValList,
                resultValList
                );
          });
          IndicatorExpressionItemResponseRs minIndicatorExpressionItemResponseRs = indicatorExpressionResponseRs.getMinIndicatorExpressionItemResponseRs();
          if (Objects.isNull(minIndicatorExpressionItemResponseRs)) {
            return;
          }
          String minIndicatorExpressionItemId = minIndicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
          String minCaseIndicatorExpressionItemId = idGenerator.nextIdStr();
          String minConditionRaw = minIndicatorExpressionItemResponseRs.getConditionRaw();
          String minConditionExpression = minIndicatorExpressionItemResponseRs.getConditionExpression();
          String minConditionNameList = minIndicatorExpressionItemResponseRs.getConditionNameList();
          String minConditionValList = convertConditionValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, minIndicatorExpressionItemResponseRs.getConditionValList());
          String minResultRaw = minIndicatorExpressionItemResponseRs.getResultRaw();
          String minResultExpression = minIndicatorExpressionItemResponseRs.getResultExpression();
          String minResultNameList = minIndicatorExpressionItemResponseRs.getResultNameList();
          String minResultValList = convertResultValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, minIndicatorExpressionItemResponseRs.getResultValList());
          Integer minSeqIndicatorExpressionItem = minIndicatorExpressionItemResponseRs.getSeq();
          caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionBiz.indicatorExpressionItemResponseRs2Case(
              minCaseIndicatorExpressionItemId, minIndicatorExpressionItemId, appId, caseIndicatorExpressionId, minConditionRaw,
              minConditionExpression, minConditionNameList, minConditionValList, minResultRaw, minResultExpression, minResultNameList, minResultValList, minSeqIndicatorExpressionItem));
          populateInfluenceMap(
              kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
              kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
              caseIndicatorInstanceId,
              minConditionValList,
              minResultValList
          );
          IndicatorExpressionItemResponseRs maxIndicatorExpressionItemResponseRs = indicatorExpressionResponseRs.getMaxIndicatorExpressionItemResponseRs();
          if (Objects.isNull(maxIndicatorExpressionItemResponseRs)) {
            return;
          }
          String maxIndicatorExpressionItemId = maxIndicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
          String maxCaseIndicatorExpressionItemId = idGenerator.nextIdStr();
          String maxConditionRaw = maxIndicatorExpressionItemResponseRs.getConditionRaw();
          String maxConditionExpression = maxIndicatorExpressionItemResponseRs.getConditionExpression();
          String maxConditionNameList = maxIndicatorExpressionItemResponseRs.getConditionNameList();
          String maxConditionValList = convertConditionValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, maxIndicatorExpressionItemResponseRs.getConditionValList());
          String maxResultRaw = maxIndicatorExpressionItemResponseRs.getResultRaw();
          String maxResultExpression = maxIndicatorExpressionItemResponseRs.getResultExpression();
          String maxResultNameList = maxIndicatorExpressionItemResponseRs.getResultNameList();
          String maxResultValList = convertResultValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, maxIndicatorExpressionItemResponseRs.getResultValList());
          Integer maxSeqIndicatorExpressionItem = maxIndicatorExpressionItemResponseRs.getSeq();
          caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionBiz.indicatorExpressionItemResponseRs2Case(
              maxCaseIndicatorExpressionItemId, maxIndicatorExpressionItemId, appId, caseIndicatorExpressionId, maxConditionRaw,
              maxConditionExpression, maxConditionNameList, maxConditionValList, maxResultRaw, maxResultExpression, maxResultNameList, maxResultValList, maxSeqIndicatorExpressionItem));
          populateInfluenceMap(
              kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
              kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
              caseIndicatorInstanceId,
              maxConditionValList,
              maxResultValList
          );
          caseIndicatorExpressionEntityList.add(CaseIndicatorExpressionEntity
              .builder()
              .caseIndicatorExpressionId(caseIndicatorExpressionId)
              .indicatorExpressionId(indicatorExpressionId)
              .appId(appId)
              .casePrincipalId(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get(principalId))
              .principalId(principalId)
              .maxIndicatorExpressionItemId(maxIndicatorExpressionItemId)
              .minIndicatorExpressionItemId(minIndicatorExpressionItemId)
              .type(type)
              .source(source)
              .build());
          caseIndicatorExpressionRefEntityList.add(CaseIndicatorExpressionRefEntity
              .builder()
                  .caseIndicatorExpressionRefId(caseIndicatorExpressionRefId)
                  .indicatorExpressionRefId(indicatorExpressionRefId)
                  .appId(appId)
                  .indicatorExpressionId(caseIndicatorExpressionId)
                  .reasonId(caseIndicatorInstanceId)
              .build());
        });
      });
    });
    kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.values().forEach(caseIndicatorInstanceId -> {
      caseIndicatorPrincipalRefEntityList.add(CaseIndicatorPrincipalRefEntity
          .builder()
          .caseIndicatorPrincipalRefId(idGenerator.nextIdStr())
          .principalId(casePersonId)
          .indicatorInstanceId(caseIndicatorInstanceId)
          .appId(appId)
          .build());
    });
    caseIndicatorCategoryService.saveOrUpdateBatch(caseIndicatorCategoryEntityList);
    caseIndicatorCategoryRefService.saveOrUpdateBatch(caseIndicatorCategoryRefEntityList);
    caseIndicatorRuleService.saveOrUpdateBatch(caseIndicatorRuleEntityList);
    caseIndicatorInstanceService.saveOrUpdateBatch(caseIndicatorInstanceEntityList);
    caseIndicatorExpressionService.saveOrUpdateBatch(caseIndicatorExpressionEntityList);
    caseIndicatorExpressionItemService.saveOrUpdateBatch(caseIndicatorExpressionItemEntityList);
    caseIndicatorExpressionRefService.saveOrUpdateBatch(caseIndicatorExpressionRefEntityList);
    caseIndicatorExpressionInfluenceService.saveOrUpdateBatch(caseIndicatorExpressionInfluenceEntityList);
    caseIndicatorPrincipalRefService.saveOrUpdateBatch(caseIndicatorPrincipalRefEntityList);
  }
}
