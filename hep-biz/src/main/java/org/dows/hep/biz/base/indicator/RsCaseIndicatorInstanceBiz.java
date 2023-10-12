package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionItemResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceCategoryResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorRuleType;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.RsCaseIndicatorInstanceBizException;
import org.dows.hep.entity.*;
import org.dows.hep.service.CaseIndicatorInstanceService;
import org.dows.hep.service.CaseIndicatorRuleService;
import org.dows.hep.service.IndicatorExpressionInfluenceService;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsCaseIndicatorInstanceBiz {
  private final IdGenerator idGenerator;
  private final IndicatorExpressionInfluenceService indicatorExpressionInfluenceService;
  private final CaseIndicatorInstanceService caseIndicatorInstanceService;
  private final CaseIndicatorRuleService caseIndicatorRuleService;

  private final IndicatorInstanceService indicatorInstanceService;

  public String convertConditionValList2Case(
      String conditionValList,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (StringUtils.isBlank(conditionValList)
        || Objects.isNull(kOldIdVNewIdMap)
    ) {return null;}
    List<String> caseValValList = Arrays.stream(conditionValList.split(EnumString.COMMA.getStr()))
        .map(kOldIdVNewIdMap::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return String.join(EnumString.COMMA.getStr(), caseValValList);
  }

  public String convertResultValList2Case(
      String resultValList,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (StringUtils.isBlank(resultValList)
        || Objects.isNull(kOldIdVNewIdMap)
    ) {return null;}
    List<String> caseResultListList = Arrays.stream(resultValList.split(EnumString.COMMA.getStr()))
        .map(kOldIdVNewIdMap::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return String.join(EnumString.COMMA.getStr(), caseResultListList);
  }

  public String convertIndicatorInstanceIdList2Case(
      String indicatorInstanceIdList,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (StringUtils.isBlank(indicatorInstanceIdList)
        || Objects.isNull(kOldIdVNewIdMap)
    ) {return null;}
    List<String> caseIndicatorInstanceIdList = Arrays.stream(indicatorInstanceIdList.split(EnumString.COMMA.getStr()))
        .map(kOldIdVNewIdMap::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return String.join(EnumString.COMMA.getStr(), caseIndicatorInstanceIdList);
  }

  public CaseIndicatorExpressionItemEntity convertIndicatorExpressionItemResponseRs2Case(
      IndicatorExpressionItemResponseRs indicatorExpressionItemResponseRs,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (Objects.isNull(indicatorExpressionItemResponseRs)
        || Objects.isNull(kOldIdVNewIdMap)
    ) {return null;}
    String indicatorExpressionItemId = indicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
    String indicatorExpressionId = indicatorExpressionItemResponseRs.getIndicatorExpressionId();
    String caseIndicatorExpressionId = null;
    if (Objects.nonNull(indicatorExpressionId)) {
      caseIndicatorExpressionId = kOldIdVNewIdMap.get(indicatorExpressionId);
    }
    return CaseIndicatorExpressionItemEntity
        .builder()
        .caseIndicatorExpressionItemId(kOldIdVNewIdMap.get(indicatorExpressionItemId))
        .indicatorExpressionItemId(indicatorExpressionItemId)
        .appId(indicatorExpressionItemResponseRs.getAppId())
        .indicatorExpressionId(caseIndicatorExpressionId)
        .conditionRaw(indicatorExpressionItemResponseRs.getConditionRaw())
        .conditionExpression(indicatorExpressionItemResponseRs.getConditionExpression())
        .conditionNameList(indicatorExpressionItemResponseRs.getConditionNameList())
        .conditionValList(this.convertConditionValList2Case(indicatorExpressionItemResponseRs.getConditionValList(), kOldIdVNewIdMap))
        .resultRaw(indicatorExpressionItemResponseRs.getResultRaw())
        .resultExpression(indicatorExpressionItemResponseRs.getResultExpression())
        .resultNameList(indicatorExpressionItemResponseRs.getResultNameList())
        .resultValList(this.convertResultValList2Case(indicatorExpressionItemResponseRs.getResultValList(), kOldIdVNewIdMap))
        .seq(indicatorExpressionItemResponseRs.getSeq())
        .build();
  }

  public CaseIndicatorExpressionInfluenceEntity convertIndicatorExpressionInfluenceEntity2Case(
      IndicatorExpressionInfluenceEntity indicatorExpressionInfluenceEntity,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (Objects.isNull(indicatorExpressionInfluenceEntity)
        || Objects.isNull(kOldIdVNewIdMap)
    ) {return null;}
    return CaseIndicatorExpressionInfluenceEntity
        .builder()
        .caseIndicatorExpressionInfluenceId(idGenerator.nextIdStr())
        .appId(indicatorExpressionInfluenceEntity.getAppId())
        .indicatorInstanceId(kOldIdVNewIdMap.get(indicatorExpressionInfluenceEntity.getIndicatorInstanceId()))
        .influenceIndicatorInstanceIdList(this.convertIndicatorInstanceIdList2Case(indicatorExpressionInfluenceEntity.getInfluenceIndicatorInstanceIdList(), kOldIdVNewIdMap))
        .influencedIndicatorInstanceIdList(this.convertIndicatorInstanceIdList2Case(indicatorExpressionInfluenceEntity.getInfluencedIndicatorInstanceIdList(), kOldIdVNewIdMap))
        .build();
  }

  public void populateCaseIndicatorCategoryList(
      List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList,
      List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (Objects.isNull(caseIndicatorCategoryEntityList)
        || Objects.isNull(indicatorInstanceCategoryResponseRsList) || indicatorInstanceCategoryResponseRsList.isEmpty()
        || Objects.isNull(kOldIdVNewIdMap)
    ) {return;}
    indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
      String indicatorCategoryId = indicatorInstanceCategoryResponseRs.getIndicatorCategoryId();
      caseIndicatorCategoryEntityList.add(CaseIndicatorCategoryEntity
          .builder()
          .caseIndicatorCategoryId(kOldIdVNewIdMap.get(indicatorCategoryId))
          .indicatorCategoryId(indicatorCategoryId)
          .appId(indicatorInstanceCategoryResponseRs.getAppId())
          .pid(indicatorInstanceCategoryResponseRs.getPid())
          .categoryName(indicatorInstanceCategoryResponseRs.getCategoryName())
          .seq(indicatorInstanceCategoryResponseRs.getSeq())
          .build());
    });
  }

  public void populateCaseIndicatorCategoryRefList(
      List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityList,
      List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (Objects.isNull(caseIndicatorCategoryRefEntityList)
        ||  Objects.isNull(indicatorInstanceCategoryResponseRsList) || indicatorInstanceCategoryResponseRsList.isEmpty()
        || Objects.isNull(kOldIdVNewIdMap)
    ) {return;}
    indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
      String indicatorCategoryId = indicatorInstanceCategoryResponseRs.getIndicatorCategoryId();
      List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
      if (Objects.isNull(indicatorInstanceResponseRsList) || indicatorInstanceResponseRsList.isEmpty()) {return;}
      indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
        String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
        caseIndicatorCategoryRefEntityList.add(CaseIndicatorCategoryRefEntity
            .builder()
                .caseIndicatorCategoryRefId(idGenerator.nextIdStr())
                .appId(indicatorInstanceCategoryResponseRs.getAppId())
                .indicatorCategoryId(kOldIdVNewIdMap.get(indicatorCategoryId))
                .indicatorInstanceId(kOldIdVNewIdMap.get(indicatorInstanceId))
                .seq(indicatorInstanceResponseRs.getSeq())
            .build());
      });
    });
  }

  public void populateCaseIndicatorCategoryPrincipalRefList (
      List<CaseIndicatorCategoryPrincipalRefEntity> caseIndicatorCategoryPrincipalRefEntityList,
      List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList,
      Map<String, String> kOldIdVNewIdMap,
      String principalId
  ) {
    if (Objects.isNull(caseIndicatorCategoryPrincipalRefEntityList)
        ||  Objects.isNull(indicatorInstanceCategoryResponseRsList) || indicatorInstanceCategoryResponseRsList.isEmpty()
        || Objects.isNull(kOldIdVNewIdMap)
        || StringUtils.isBlank(principalId)
    ) {return;}
    indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
      String indicatorCategoryId = indicatorInstanceCategoryResponseRs.getIndicatorCategoryId();
      caseIndicatorCategoryPrincipalRefEntityList.add(CaseIndicatorCategoryPrincipalRefEntity
          .builder()
          .caseIndicatorCategoryPrincipalRefId(idGenerator.nextIdStr())
          .appId(indicatorInstanceCategoryResponseRs.getAppId())
          .principalId(principalId)
          .indicatorCategoryId(kOldIdVNewIdMap.get(indicatorCategoryId))
          .build());
    });
  }

  public void populateCaseIndicatorRuleEntityList (
      List<CaseIndicatorRuleEntity> caseIndicatorRuleEntityList,
      List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (Objects.isNull(caseIndicatorRuleEntityList)
        ||  Objects.isNull(indicatorInstanceCategoryResponseRsList) || indicatorInstanceCategoryResponseRsList.isEmpty()
        || Objects.isNull(kOldIdVNewIdMap)
    ) {return;}
    indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
      List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
      if (Objects.isNull(indicatorInstanceResponseRsList) || indicatorInstanceResponseRsList.isEmpty()) {return;}
      indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
        String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
        caseIndicatorRuleEntityList.add(CaseIndicatorRuleEntity
            .builder()
            .caseIndicatorRuleId(idGenerator.nextIdStr())
            .appId(indicatorInstanceCategoryResponseRs.getAppId())
            .variableId(kOldIdVNewIdMap.get(indicatorInstanceId))
            .ruleType(EnumIndicatorRuleType.INDICATOR.getCode())
            .min(indicatorInstanceResponseRs.getMin())
            .max(indicatorInstanceResponseRs.getMax())
            .def(indicatorInstanceResponseRs.getDef())
            .descr(indicatorInstanceResponseRs.getDescr())
            .build());
      });
    });
  }

  public void populateCaseIndicatorInstanceEntityList (
      List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList,
      List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList,
      Map<String, String> kOldIdVNewIdMap,
      String principalId
  ) {
    if (Objects.isNull(caseIndicatorInstanceEntityList)
        ||  Objects.isNull(indicatorInstanceCategoryResponseRsList) || indicatorInstanceCategoryResponseRsList.isEmpty()
        || Objects.isNull(kOldIdVNewIdMap)
        || StringUtils.isBlank(principalId)
    ) {return;}
    indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
      List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
      if (Objects.isNull(indicatorInstanceResponseRsList) || indicatorInstanceResponseRsList.isEmpty()) {return;}
      indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
        String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
        String indicatorCategoryId = indicatorInstanceResponseRs.getIndicatorCategoryId();
        caseIndicatorInstanceEntityList.add(CaseIndicatorInstanceEntity
            .builder()
            .caseIndicatorInstanceId(kOldIdVNewIdMap.get(indicatorInstanceId))
            .indicatorInstanceId(indicatorInstanceId)
            .appId(indicatorInstanceResponseRs.getAppId())
            .principalId(principalId)
            .indicatorCategoryId(kOldIdVNewIdMap.get(indicatorCategoryId))
            .indicatorName(indicatorInstanceResponseRs.getIndicatorName())
            .displayByPercent(indicatorInstanceResponseRs.getDisplayByPercent())
            .unit(indicatorInstanceResponseRs.getUnit())
            .core(indicatorInstanceResponseRs.getCore())
            .type(indicatorInstanceResponseRs.getType())
            .descr(indicatorInstanceResponseRs.getDescr())
            .build());
      });
    });
  }

  public void populateAllCaseIndicatorExpressionEntityList (
      List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList,
      List<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityList,
      List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList,
      List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList,
      Map<String, String> kOldIdVNewIdMap
  ) {
    indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
      List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
      if (Objects.isNull(indicatorInstanceResponseRsList) || indicatorInstanceResponseRsList.isEmpty()) {return;}
      indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
        String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
        List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = indicatorInstanceResponseRs.getIndicatorExpressionResponseRsList();
        if (Objects.isNull(indicatorExpressionResponseRsList) || indicatorExpressionResponseRsList.isEmpty()) {return;}
        indicatorExpressionResponseRsList.forEach(indicatorExpressionResponseRs -> {
          String indicatorExpressionId = indicatorExpressionResponseRs.getIndicatorExpressionId();
          String indicatorExpressionRefId = indicatorExpressionResponseRs.getIndicatorExpressionRefId();
          String principalId = indicatorExpressionResponseRs.getPrincipalId();
          String caseMinIndicatorExpressionItemId = null;
          String caseMaxIndicatorExpressionItemId = null;
          IndicatorExpressionItemResponseRs minIndicatorExpressionItemResponseRs = indicatorExpressionResponseRs.getMinIndicatorExpressionItemResponseRs();
          if (Objects.nonNull(minIndicatorExpressionItemResponseRs)) {
            String minIndicatorExpressionItemId = minIndicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
            caseMinIndicatorExpressionItemId = kOldIdVNewIdMap.get(minIndicatorExpressionItemId);
            CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity = this.convertIndicatorExpressionItemResponseRs2Case(minIndicatorExpressionItemResponseRs, kOldIdVNewIdMap);
            if (Objects.nonNull(minCaseIndicatorExpressionItemEntity)) {
              caseIndicatorExpressionItemEntityList.add(minCaseIndicatorExpressionItemEntity);
            }
          }
          IndicatorExpressionItemResponseRs maxIndicatorExpressionItemResponseRs = indicatorExpressionResponseRs.getMaxIndicatorExpressionItemResponseRs();
          if (Objects.nonNull(maxIndicatorExpressionItemResponseRs)) {
            String maxIndicatorExpressionItemId = maxIndicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
            caseMaxIndicatorExpressionItemId = kOldIdVNewIdMap.get(maxIndicatorExpressionItemId);
            CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity = this.convertIndicatorExpressionItemResponseRs2Case(maxIndicatorExpressionItemResponseRs, kOldIdVNewIdMap);
            if (Objects.nonNull(maxCaseIndicatorExpressionItemEntity)) {
              caseIndicatorExpressionItemEntityList.add(maxCaseIndicatorExpressionItemEntity);
            }
          }
          caseIndicatorExpressionRefEntityList.add(CaseIndicatorExpressionRefEntity
              .builder()
              .caseIndicatorExpressionRefId(kOldIdVNewIdMap.get(indicatorExpressionRefId))
              .indicatorExpressionRefId(indicatorExpressionRefId)
              .appId(indicatorExpressionResponseRs.getAppId())
              .indicatorExpressionId(kOldIdVNewIdMap.get(indicatorExpressionId))
              .reasonId(kOldIdVNewIdMap.get(indicatorInstanceId))
              .build());
          caseIndicatorExpressionEntityList.add(CaseIndicatorExpressionEntity
              .builder()
                  .caseIndicatorExpressionId(kOldIdVNewIdMap.get(indicatorExpressionId))
                  .indicatorExpressionId(indicatorExpressionId)
                  .appId(indicatorExpressionResponseRs.getAppId())
                  .casePrincipalId(kOldIdVNewIdMap.get(principalId))
                  .principalId(principalId)
                  .minIndicatorExpressionItemId(caseMinIndicatorExpressionItemId)
                  .maxIndicatorExpressionItemId(caseMaxIndicatorExpressionItemId)
                  .type(indicatorExpressionResponseRs.getType())
                  .source(indicatorExpressionResponseRs.getSource())
              .build());
          List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList = indicatorExpressionResponseRs.getIndicatorExpressionItemResponseRsList();
          if (Objects.isNull(indicatorExpressionItemResponseRsList) || indicatorExpressionItemResponseRsList.isEmpty()) {return;}
          indicatorExpressionItemResponseRsList.forEach(indicatorExpressionItemResponseRs -> {
            CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity = this.convertIndicatorExpressionItemResponseRs2Case(indicatorExpressionItemResponseRs, kOldIdVNewIdMap);
            if (Objects.nonNull(caseIndicatorExpressionItemEntity)) {
              caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionItemEntity);
            }
          });
        });
      });
    });
  }

  public void populateIndicatorExpressionInfluenceEntityList(
      List<IndicatorExpressionInfluenceEntity> indicatorExpressionInfluenceEntityList,
      Set<String> indicatorInstanceIdSet
  ) {
    if (Objects.isNull(indicatorExpressionInfluenceEntityList)
        || Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()
    ) {return;}
    indicatorExpressionInfluenceEntityList.addAll(
        indicatorExpressionInfluenceService.lambdaQuery()
            .in(IndicatorExpressionInfluenceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
            .list()
    );
  }

  public void populateCaseIndicatorExpressionInfluenceEntityList (
      List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityList,
      Set<String> indicatorExpressionInfluenceIdSet,
      Map<String, String> kOldIdVNewIdMap
  ) {
    if (Objects.isNull(caseIndicatorExpressionInfluenceEntityList)
      || Objects.isNull(indicatorExpressionInfluenceIdSet) || indicatorExpressionInfluenceIdSet.isEmpty()
      || Objects.isNull(kOldIdVNewIdMap)
    ) {return;}
    indicatorExpressionInfluenceService.lambdaQuery()
        .in(IndicatorExpressionInfluenceEntity::getIndicatorExpressionInfluenceId, indicatorExpressionInfluenceIdSet)
        .list()
        .forEach(indicatorExpressionInfluenceEntity -> {
          CaseIndicatorExpressionInfluenceEntity caseIndicatorExpressionInfluenceEntity = this.convertIndicatorExpressionInfluenceEntity2Case(indicatorExpressionInfluenceEntity, kOldIdVNewIdMap);
          if (Objects.nonNull(caseIndicatorExpressionInfluenceEntity)) {
            caseIndicatorExpressionInfluenceEntityList.add(caseIndicatorExpressionInfluenceEntity);
          }
        });
  }

  public void checkCaseIndicatorInstanceIdInCaseIndicatorInstanceEntity(
      AtomicReference<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityAR,
      String caseIndicatorInstanceId) {
    if (Objects.isNull(caseIndicatorInstanceEntityAR) || StringUtils.isBlank(caseIndicatorInstanceId)) {
      return;
    }
    CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = caseIndicatorInstanceService.lambdaQuery()
        .eq(CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId, caseIndicatorInstanceId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("RsCaseIndicatorInstanceBiz.checkCaseIndicatorInstanceIdInCaseIndicatorInstanceEntity caseIndicatorInstanceId:{} is illegal", caseIndicatorInstanceId);
          throw new RsCaseIndicatorInstanceBizException(EnumESC.CASE_INDICATOR_INSTANCE_ID_IS_ILLEGAL);
        });
    caseIndicatorInstanceEntityAR.set(caseIndicatorInstanceEntity);
  }

  public void checkCaseIndicatorInstanceIdInCaseIndicatorRuleEntity(
      AtomicReference<CaseIndicatorRuleEntity> caseIndicatorRuleEntityAR,
      String caseIndicatorInstanceId) {
    if (Objects.isNull(caseIndicatorRuleEntityAR) || StringUtils.isBlank(caseIndicatorInstanceId)) {
      return;
    }
    CaseIndicatorRuleEntity caseIndicatorRuleEntity = caseIndicatorRuleService.lambdaQuery()
        .eq(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("RsCaseIndicatorInstanceBiz.checkCaseIndicatorInstanceIdInCaseIndicatorRuleEntity caseIndicatorInstanceId:{} is illegal", caseIndicatorInstanceId);
          throw new RsCaseIndicatorInstanceBizException(EnumESC.CASE_INDICATOR_INSTANCE_ID_IS_ILLEGAL);
        });
    caseIndicatorRuleEntityAR.set(caseIndicatorRuleEntity);
  }

  public void checkIfCanDeleteCaseIndicatorInstanceEntity(CaseIndicatorInstanceEntity caseIndicatorInstanceEntity) {
    if (Objects.isNull(caseIndicatorInstanceEntity)) {
      return;
    }
    String indicatorInstanceId = caseIndicatorInstanceEntity.getIndicatorInstanceId();
    if (StringUtils.isNotBlank(indicatorInstanceId)) {
      if (indicatorInstanceService.lambdaQuery()
              .eq(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceId)
              .select(IndicatorInstanceEntity::getIndicatorInstanceId)
              .count() > 0) {
        throw new RsCaseIndicatorInstanceBizException(EnumESC.CASE_INDICATOR_INSTANCE_COME_FROM_DATABASE_CANNOT_DELETE);
      }
    }
  }

  public void populateKCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap(
      Map<String, CaseIndicatorInstanceEntity> kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap,
      String accountId) {
    if (Objects.isNull(kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap) || StringUtils.isBlank(accountId)) {return;}
    caseIndicatorInstanceService.lambdaQuery()
        .eq(CaseIndicatorInstanceEntity::getPrincipalId, accountId)
        .list()
        .forEach(caseIndicatorInstanceEntity -> {
          kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap.put(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId(), caseIndicatorInstanceEntity);
        });
  }

  public void populateHealthPointCaseIndicatorRuleEntity(
      AtomicReference<CaseIndicatorRuleEntity> caseIndicatorRuleEntityAR,
      String accountId) {
    if (Objects.isNull(caseIndicatorRuleEntityAR)
        || StringUtils.isBlank(accountId)
    ) {return;}
    CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = caseIndicatorInstanceService.lambdaQuery()
        .eq(CaseIndicatorInstanceEntity::getPrincipalId, accountId)
        .eq(CaseIndicatorInstanceEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
        .one();
    if (Objects.isNull(caseIndicatorInstanceEntity)) {return;}
    caseIndicatorRuleService.lambdaQuery()
        .eq(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceEntity.getCaseIndicatorInstanceId())
        .oneOpt()
        .ifPresent(caseIndicatorRuleEntityAR::set);
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

  public void populateKIndicatorInstanceIdVCaseIndicatorInstanceIdMap(
      Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
      String accountId) {
    if (Objects.isNull(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap) || StringUtils.isBlank(accountId)) {return;}
    caseIndicatorInstanceService.lambdaQuery()
        .eq(CaseIndicatorInstanceEntity::getPrincipalId, accountId)
        .list()
        .forEach(caseIndicatorInstanceEntity -> {
          kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.put(caseIndicatorInstanceEntity.getIndicatorInstanceId(), caseIndicatorInstanceEntity.getCaseIndicatorInstanceId());
        });
  }
}
