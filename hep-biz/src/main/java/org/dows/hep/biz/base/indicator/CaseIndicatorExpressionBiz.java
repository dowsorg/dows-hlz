package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseIndicatorExpressionBiz {
  private final IdGenerator idGenerator;
  private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
  private final CaseIndicatorInstanceService caseIndicatorInstanceService;
  private final CaseIndicatorCategoryService caseIndicatorCategoryService;

  public CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemResponseRs2Case(
      String caseIndicatorExpressionItemId,
      String indicatorExpressionItemId,
      String appId,
      String caseIndicatorExpressionId,
      String conditionRaw,
      String conditionExpression,
      String conditionNameList,
      String conditionValList,
      String resultRaw,
      String resultExpression,
      String resultNameList,
      String resultValList,
      Integer seq
  ) {
    return CaseIndicatorExpressionItemEntity
        .builder()
        .caseIndicatorExpressionItemId(caseIndicatorExpressionItemId)
        .indicatorExpressionItemId(indicatorExpressionItemId)
        .appId(appId)
        .indicatorExpressionId(caseIndicatorExpressionId)
        .conditionRaw(conditionRaw)
        .conditionExpression(conditionExpression)
        .conditionNameList(conditionNameList)
        .conditionValList(conditionValList)
        .resultRaw(resultRaw)
        .resultExpression(resultExpression)
        .resultNameList(resultNameList)
        .resultValList(resultValList)
        .seq(seq)
        .build();
  }

  public static CaseIndicatorExpressionResponseRs caseIndicatorExpression2ResponseRs(
      CaseIndicatorExpressionEntity caseIndicatorExpressionEntity,
      List<CaseIndicatorExpressionItemResponseRs> caseIndicatorExpressionItemResponseRsList,
      CaseIndicatorExpressionItemResponseRs caseMaxIndicatorExpressionItemResponseRs,
      CaseIndicatorExpressionItemResponseRs caseMinIndicatorExpressionItemResponseRs,
      CaseIndicatorCategoryResponse caseIndicatorCategoryResponse,
      String caseIndicatorExpressionRefId
  ) {
    if (Objects.isNull(caseIndicatorExpressionEntity)) {
      return null;
    }
    if (Objects.isNull(caseIndicatorExpressionItemResponseRsList)) {
      caseIndicatorExpressionItemResponseRsList = new ArrayList<>();
    }
    return CaseIndicatorExpressionResponseRs
        .builder()
        .id(caseIndicatorExpressionEntity.getId())
        .indicatorExpressionRefId(caseIndicatorExpressionRefId)
        .indicatorExpressionId(caseIndicatorExpressionEntity.getIndicatorExpressionId())
        .appId(caseIndicatorExpressionEntity.getAppId())
        .principalId(caseIndicatorExpressionEntity.getPrincipalId())
        .caseIndicatorCategoryResponse(caseIndicatorCategoryResponse)
        .type(caseIndicatorExpressionEntity.getType())
        .source(caseIndicatorExpressionEntity.getSource())
        .deleted(caseIndicatorExpressionEntity.getDeleted())
        .dt(caseIndicatorExpressionEntity.getDt())
        .caseIndicatorExpressionItemResponseRsList(caseIndicatorExpressionItemResponseRsList)
        .caseMaxIndicatorExpressionItemResponseRs(caseMaxIndicatorExpressionItemResponseRs)
        .caseMinIndicatorExpressionItemResponseRs(caseMinIndicatorExpressionItemResponseRs)
        .build();
  }

  public void populateKCaseReasonIdVCaseIndicatorExpressionResponseRsListMap(String appId, Set<String> reasonIdSet, Map<String, List<CaseIndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap) {
    if (Objects.isNull(kReasonIdVIndicatorExpressionResponseRsListMap)) {
      log.warn("method IndicatorExpressionBiz.populateKIndicatorExpressionIdVIndicatorExpressionEntityMap param kIndicatorInstanceIdVIndicatorExpressionResponseRsMap is null");
      return;
    }
    if (Objects.isNull(reasonIdSet) || reasonIdSet.isEmpty()) {
      return;
    }
    Map<String, List<String>> kReasonIdVIndicatorExpressionIdListMap = new HashMap<>();
    Map<String, String> kIndicatorExpressionIdVIndicatorExpressionRefIdMap = new HashMap<>();
    Set<String> indicatorExpressionIdSet = new HashSet<>();
    caseIndicatorExpressionRefService.lambdaQuery()
        .eq(CaseIndicatorExpressionRefEntity::getAppId, appId)
        .in(CaseIndicatorExpressionRefEntity::getReasonId, reasonIdSet)
        .list()
        .forEach(indicatorExpressionRefEntity -> {
          String indicatorExpressionId = indicatorExpressionRefEntity.getIndicatorExpressionId();
          indicatorExpressionIdSet.add(indicatorExpressionId);
          String reasonId = indicatorExpressionRefEntity.getReasonId();
          List<String> indicatorExpressionIdList = kReasonIdVIndicatorExpressionIdListMap.get(reasonId);
          if (Objects.isNull(indicatorExpressionIdList)) {
            indicatorExpressionIdList = new ArrayList<>();
          }
          indicatorExpressionIdList.add(indicatorExpressionId);
          kReasonIdVIndicatorExpressionIdListMap.put(reasonId, indicatorExpressionIdList);
          kIndicatorExpressionIdVIndicatorExpressionRefIdMap.put(indicatorExpressionId, indicatorExpressionRefEntity.getIndicatorExpressionRefId());
        });
    Map<String, CaseIndicatorExpressionEntity> kIndicatorExpressionIdVIndicatorExpressionEntityMap = new HashMap<>();
    Map<String, List<CaseIndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap = new HashMap<>();
    Set<String> maxAndMinIndicatorExpressionItemIdSet = new HashSet<>();
    Map<String, CaseIndicatorExpressionItemResponseRs> kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap = new HashMap<>();
    Set<String> principalIdSet = new HashSet<>();
    if (!indicatorExpressionIdSet.isEmpty()) {
      caseIndicatorExpressionService.lambdaQuery()
          .eq(CaseIndicatorExpressionEntity::getAppId, appId)
          .in(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, indicatorExpressionIdSet)
          .list()
          .forEach(indicatorExpressionEntity -> {
            if (Objects.nonNull(indicatorExpressionEntity.getPrincipalId())) {
              principalIdSet.add(indicatorExpressionEntity.getPrincipalId());
            }
            kIndicatorExpressionIdVIndicatorExpressionEntityMap.put(
                indicatorExpressionEntity.getCaseIndicatorExpressionId(), indicatorExpressionEntity);
            String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
            String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
            if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
              maxAndMinIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
            }
            if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
              maxAndMinIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
            }
          });
      caseIndicatorExpressionItemService.lambdaQuery()
          .eq(CaseIndicatorExpressionItemEntity::getAppId, appId)
          .in(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
          .list()
          .forEach(indicatorExpressionItemEntity -> {
            String indicatorExpressionId = indicatorExpressionItemEntity.getIndicatorExpressionId();
            List<CaseIndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
            if (Objects.isNull(indicatorExpressionItemEntityList)) {
              indicatorExpressionItemEntityList = new ArrayList<>();
            }
            indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
            kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.put(indicatorExpressionId, indicatorExpressionItemEntityList);
          });
    }
    if (!maxAndMinIndicatorExpressionItemIdSet.isEmpty()) {
      caseIndicatorExpressionItemService.lambdaQuery()
          .eq(CaseIndicatorExpressionItemEntity::getAppId, appId)
          .in(CaseIndicatorExpressionItemEntity::getIndicatorExpressionItemId, maxAndMinIndicatorExpressionItemIdSet)
          .list()
          .forEach(indicatorExpressionItemEntity -> kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.put(
              indicatorExpressionItemEntity.getIndicatorExpressionItemId(), CaseIndicatorExpressionItemBiz.caseIndicatorExpressionItem2ResponseRs(indicatorExpressionItemEntity)
          ));
    }
    Map<String, String> kIndicatorInstanceIdVIndicatorCategoryIdMap = new HashMap<>();
    Map<String, CaseIndicatorCategoryResponse> kPrincipalIdVIndicatorCategoryRsMap = new HashMap<>();
    Map<String, CaseIndicatorCategoryResponse> kIndicatorCategoryIdVIndicatorCategoryRsMap = new HashMap<>();
    Set<String> indicatorCategoryIdSet = new HashSet<>();
    if (!principalIdSet.isEmpty()) {
      caseIndicatorInstanceService.lambdaQuery()
          .eq(CaseIndicatorInstanceEntity::getAppId, appId)
          .in(CaseIndicatorInstanceEntity::getIndicatorInstanceId, principalIdSet)
          .list()
          .forEach(indicatorInstanceEntity -> {
            String indicatorCategoryId = indicatorInstanceEntity.getIndicatorCategoryId();
            indicatorCategoryIdSet.add(indicatorCategoryId);
            kIndicatorInstanceIdVIndicatorCategoryIdMap.put(indicatorInstanceEntity.getIndicatorInstanceId(), indicatorInstanceEntity.getIndicatorCategoryId());
          });
      if (!indicatorCategoryIdSet.isEmpty()) {
        caseIndicatorCategoryService.lambdaQuery()
            .eq(CaseIndicatorCategoryEntity::getAppId, appId)
            .in(CaseIndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
            .list()
            .forEach(indicatorCategoryEntity -> {
              kIndicatorCategoryIdVIndicatorCategoryRsMap.put(
                  indicatorCategoryEntity.getIndicatorCategoryId(),
                  CaseIndicatorCategoryBiz.caseIndicatorCategoryEntity2Response(indicatorCategoryEntity));
            });
      }
    }
    kIndicatorInstanceIdVIndicatorCategoryIdMap.forEach((indicatorInstanceId, indicatorCategoryId) -> {
      CaseIndicatorCategoryResponse indicatorCategoryResponse = kIndicatorCategoryIdVIndicatorCategoryRsMap.get(indicatorCategoryId);
      kPrincipalIdVIndicatorCategoryRsMap.put(indicatorInstanceId, indicatorCategoryResponse);
    });
    kReasonIdVIndicatorExpressionIdListMap.forEach((reasonId, indicatorExpressionIdList) -> {
      indicatorExpressionIdList.forEach(indicatorExpressionId -> {
        CaseIndicatorExpressionEntity indicatorExpressionEntity = kIndicatorExpressionIdVIndicatorExpressionEntityMap.get(indicatorExpressionId);
        if (Objects.isNull(indicatorExpressionEntity)) {
          return;
        }
        String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
        String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
        List<CaseIndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
        if (Objects.isNull(indicatorExpressionItemEntityList)) {
          indicatorExpressionItemEntityList = new ArrayList<>();
        }
        List<CaseIndicatorExpressionItemResponseRs> finalIndicatorExpressionItemResponseRsList = new ArrayList<>();
        List<CaseIndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList = indicatorExpressionItemEntityList.stream().map(CaseIndicatorExpressionItemBiz::caseIndicatorExpressionItem2ResponseRs)
            .sorted(Comparator.comparingInt(CaseIndicatorExpressionItemResponseRs::getSeq)).collect(Collectors.toList());
        /* runsix:TODO 弥补孙福聪那边实现不了，他必须要返回2个 */
        if (indicatorExpressionItemResponseRsList.size() == 0) {
          CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItemResponseRs0 = new CaseIndicatorExpressionItemResponseRs();
          caseIndicatorExpressionItemResponseRs0.setSeq(-2);
          finalIndicatorExpressionItemResponseRsList.add(caseIndicatorExpressionItemResponseRs0);
          CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItemResponseRs1 = new CaseIndicatorExpressionItemResponseRs();
          caseIndicatorExpressionItemResponseRs1.setSeq(-1);
          finalIndicatorExpressionItemResponseRsList.add(caseIndicatorExpressionItemResponseRs1);
        } else if (indicatorExpressionItemResponseRsList.size() == 1) {
          CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItemResponseRs0 = new CaseIndicatorExpressionItemResponseRs();
          caseIndicatorExpressionItemResponseRs0.setSeq(-1);
          finalIndicatorExpressionItemResponseRsList.add(caseIndicatorExpressionItemResponseRs0);
          finalIndicatorExpressionItemResponseRsList.addAll(indicatorExpressionItemResponseRsList);
        } else {
          finalIndicatorExpressionItemResponseRsList.addAll(indicatorExpressionItemResponseRsList);
        }
        CaseIndicatorExpressionResponseRs caseIndicatorExpressionResponseRs = CaseIndicatorExpressionBiz.caseIndicatorExpression2ResponseRs(
            indicatorExpressionEntity,
            finalIndicatorExpressionItemResponseRsList,
            kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.get(maxIndicatorExpressionItemId),
            kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.get(minIndicatorExpressionItemId),
            kPrincipalIdVIndicatorCategoryRsMap.get(indicatorExpressionEntity.getPrincipalId()),
            kIndicatorExpressionIdVIndicatorExpressionRefIdMap.get(indicatorExpressionId)
        );
        List<CaseIndicatorExpressionResponseRs> caseIndicatorExpressionResponseRsList = kReasonIdVIndicatorExpressionResponseRsListMap.get(reasonId);
        if (Objects.isNull(caseIndicatorExpressionResponseRsList)) {
          caseIndicatorExpressionResponseRsList = new ArrayList<>();
        }
        caseIndicatorExpressionResponseRsList.add(caseIndicatorExpressionResponseRs);
        kReasonIdVIndicatorExpressionResponseRsListMap.put(reasonId, caseIndicatorExpressionResponseRsList);
      });
    });
  }
}
