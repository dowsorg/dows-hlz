package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionItemRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionItemResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.IndicatorExpressionException;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorExpressionRefEntity;
import org.dows.hep.service.IndicatorExpressionItemService;
import org.dows.hep.service.IndicatorExpressionRefService;
import org.dows.hep.service.IndicatorExpressionService;
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
public class IndicatorExpressionBiz{
  private final IdGenerator idGenerator;
  private final IndicatorExpressionService indicatorExpressionService;
  private final IndicatorExpressionItemService indicatorExpressionItemService;
  private final IndicatorExpressionRefService indicatorExpressionRefService;

  public static IndicatorExpressionResponseRs indicatorExpression2ResponseRs(
      IndicatorExpressionEntity indicatorExpressionEntity,
      List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList,
      IndicatorExpressionItemResponseRs maxIndicatorExpressionItemResponseRs,
      IndicatorExpressionItemResponseRs minIndicatorExpressionItemResponseRs
      ) {
    if (Objects.isNull(indicatorExpressionEntity)) {
      return null;
    }
    if (Objects.isNull(indicatorExpressionItemResponseRsList)) {
      indicatorExpressionItemResponseRsList = new ArrayList<>();
    }
    return IndicatorExpressionResponseRs
        .builder()
        .id(indicatorExpressionEntity.getId())
        .indicatorExpressionId(indicatorExpressionEntity.getIndicatorExpressionId())
        .appId(indicatorExpressionEntity.getAppId())
        .type(indicatorExpressionEntity.getType())
        .deleted(indicatorExpressionEntity.getDeleted())
        .dt(indicatorExpressionEntity.getDt())
        .indicatorExpressionItemResponseRsList(indicatorExpressionItemResponseRsList)
        .maxIndicatorExpressionItemResponseRs(maxIndicatorExpressionItemResponseRs)
        .minIndicatorExpressionItemResponseRs(minIndicatorExpressionItemResponseRs)
        .build();
  }

  public void populateKIndicatorExpressionIdVIndicatorExpressionEntityMap(String appId, Set<String> principalIdSet, Map<String, IndicatorExpressionResponseRs> kIndicatorInstanceIdVIndicatorExpressionResponseRsMap) {
    if (Objects.isNull(kIndicatorInstanceIdVIndicatorExpressionResponseRsMap)) {
      log.warn("method IndicatorInstanceBiz.populateKIndicatorExpressionIdVIndicatorExpressionEntityMap param kIndicatorInstanceIdVIndicatorExpressionResponseRsMap is null");
      return;
    }
    if (Objects.isNull(principalIdSet) || principalIdSet.isEmpty()) {
      return;
    }
    Map<String, String> kIndicatorInstanceIdVIndicatorExpressionIdMap = new HashMap<>();
    Set<String> indicatorExpressionIdSet = new HashSet<>();
    Map<String, IndicatorExpressionEntity> kIndicatorExpressionIdVIndicatorExpressionEntityMap = new HashMap<>();
    Map<String, List<IndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap = new HashMap<>();
    Set<String> maxAndMinIndicatorExpressionItemIdSet = new HashSet<>();
    Map<String, IndicatorExpressionItemResponseRs> kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap = new HashMap<>();
    indicatorExpressionRefService.lambdaQuery()
        .eq(IndicatorExpressionRefEntity::getAppId, appId)
        .in(IndicatorExpressionRefEntity::getPrincipalId, principalIdSet)
        .list()
        .forEach(indicatorExpressionRefEntity -> {
          indicatorExpressionIdSet.add(indicatorExpressionRefEntity.getIndicatorExpressionId());
          kIndicatorInstanceIdVIndicatorExpressionIdMap.put(indicatorExpressionRefEntity.getPrincipalId(), indicatorExpressionRefEntity.getIndicatorExpressionId());
        });
    if (!indicatorExpressionIdSet.isEmpty()) {
      indicatorExpressionService.lambdaQuery()
          .eq(IndicatorExpressionEntity::getAppId, appId)
          .in(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
          .list()
          .forEach(indicatorExpressionEntity -> {
            kIndicatorExpressionIdVIndicatorExpressionEntityMap.put(
                indicatorExpressionEntity.getIndicatorExpressionId(), indicatorExpressionEntity);
            String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
            String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
            if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
              maxAndMinIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
            }
            if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
              maxAndMinIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
            }
          });
      indicatorExpressionItemService.lambdaQuery()
          .eq(IndicatorExpressionItemEntity::getAppId, appId)
          .in(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionIdSet)
          .list()
          .forEach(indicatorExpressionItemEntity -> {
            String indicatorExpressionId = indicatorExpressionItemEntity.getIndicatorExpressionId();
            List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
            if (Objects.isNull(indicatorExpressionItemEntityList)) {
              indicatorExpressionItemEntityList = new ArrayList<>();
            }
            indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
            kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.put(indicatorExpressionId, indicatorExpressionItemEntityList);
          });
    }
    if (!maxAndMinIndicatorExpressionItemIdSet.isEmpty()) {
      indicatorExpressionItemService.lambdaQuery()
          .eq(IndicatorExpressionItemEntity::getAppId, appId)
          .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, maxAndMinIndicatorExpressionItemIdSet)
          .list()
          .forEach(indicatorExpressionItemEntity -> kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.put(
              indicatorExpressionItemEntity.getIndicatorExpressionItemId(), IndicatorExpressionItemBiz.indicatorExpressionItem2ResponseRs(indicatorExpressionItemEntity)
          ));
    }
    kIndicatorInstanceIdVIndicatorExpressionIdMap.forEach((indicatorInstanceId, indicatorExpressionId) -> {
      IndicatorExpressionEntity indicatorExpressionEntity = kIndicatorExpressionIdVIndicatorExpressionEntityMap.get(indicatorExpressionId);
      if (Objects.isNull(indicatorExpressionEntity)) {
        return;
      }
      String maxIndicatorExpressionItemId = indicatorExpressionEntity.getMaxIndicatorExpressionItemId();
      String minIndicatorExpressionItemId = indicatorExpressionEntity.getMinIndicatorExpressionItemId();
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
      if (Objects.isNull(indicatorExpressionItemEntityList)) {
        indicatorExpressionItemEntityList = new ArrayList<>();
      }
      List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList = indicatorExpressionItemEntityList.stream().map(IndicatorExpressionItemBiz::indicatorExpressionItem2ResponseRs)
          .sorted(Comparator.comparingInt(IndicatorExpressionItemResponseRs::getSeq)).collect(Collectors.toList());
      IndicatorExpressionResponseRs indicatorExpressionResponseRs = IndicatorExpressionBiz.indicatorExpression2ResponseRs(
          indicatorExpressionEntity,
          indicatorExpressionItemResponseRsList,
          kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.get(maxIndicatorExpressionItemId),
          kIndicatorExpressionItemIdVIndicatorExpressionItemResponseRsMap.get(minIndicatorExpressionItemId)
      );
      kIndicatorInstanceIdVIndicatorExpressionResponseRsMap.put(indicatorInstanceId, indicatorExpressionResponseRs);
    });
  }


  @Transactional(rollbackFor = Exception.class)
  public void createOrUpdate(CreateOrUpdateIndicatorExpressionRequestRs createOrUpdateIndicatorExpressionRequestRs) {
    String indicatorExpressionId = createOrUpdateIndicatorExpressionRequestRs.getIndicatorExpressionId();
    String principalId = createOrUpdateIndicatorExpressionRequestRs.getPrincipalId();
    String appId = createOrUpdateIndicatorExpressionRequestRs.getAppId();
    Integer type = createOrUpdateIndicatorExpressionRequestRs.getType();
    IndicatorExpressionEntity indicatorExpressionEntity = null;
    List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = new ArrayList<>();
    List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList = createOrUpdateIndicatorExpressionRequestRs.getCreateOrUpdateIndicatorExpressionItemRequestRsList();
    Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap = new HashMap<>();
    Set<String> paramIndicatorExpressionItemIdSet = new HashSet<>();
    Set<String> dbIndicatorExpressionItemIdSet = new HashSet<>();
    CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs = createOrUpdateIndicatorExpressionRequestRs.getMaxCreateOrUpdateIndicatorExpressionItemRequestRs();
    CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs = createOrUpdateIndicatorExpressionRequestRs.getMinCreateOrUpdateIndicatorExpressionItemRequestRs();
    if (StringUtils.isBlank(indicatorExpressionId)) {
      indicatorExpressionId = idGenerator.nextIdStr();
      indicatorExpressionEntity = IndicatorExpressionEntity
          .builder()
          .indicatorExpressionId(indicatorExpressionId)
          .appId(appId)
          .type(type)
          .build();
      indicatorExpressionRefService.save(IndicatorExpressionRefEntity
          .builder()
              .indicatorExpressionRefId(idGenerator.nextIdStr())
              .appId(appId)
              .indicatorExpressionId(indicatorExpressionId)
              .principalId(principalId)
          .build()
      );
    } else {
      String finalIndicatorExpressionId = indicatorExpressionId;
      if (!createOrUpdateIndicatorExpressionItemRequestRsList.isEmpty()) {
        createOrUpdateIndicatorExpressionItemRequestRsList.forEach(createOrUpdateIndicatorExpressionItemRequestRs -> paramIndicatorExpressionItemIdSet.add(createOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId()));
        indicatorExpressionItemService.lambdaQuery()
            .eq(IndicatorExpressionItemEntity::getAppId, appId)
            .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, paramIndicatorExpressionItemIdSet)
            .list()
            .forEach(indicatorExpressionItemEntity -> {
              String indicatorExpressionItemId = indicatorExpressionItemEntity.getIndicatorExpressionItemId();
              dbIndicatorExpressionItemIdSet.add(indicatorExpressionItemId);
              kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.put(indicatorExpressionItemId, indicatorExpressionItemEntity);
            });
        if (
            paramIndicatorExpressionItemIdSet.stream().anyMatch(indicatorExpressionItemId -> !dbIndicatorExpressionItemIdSet.contains(indicatorExpressionItemId))
        ) {
          log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs paramIndicatorExpressionItemIdSet:{} is illegal", paramIndicatorExpressionItemIdSet);
          throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
        }
      }
    }
    String finalIndicatorExpressionId1 = indicatorExpressionId;
    createOrUpdateIndicatorExpressionItemRequestRsList.forEach(createOrUpdateIndicatorExpressionItemRequestRs -> {
      String indicatorExpressionItemId = createOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
      String conditionRaw = createOrUpdateIndicatorExpressionItemRequestRs.getConditionRaw();
      String conditionExpression = createOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression();
      String conditionNameList = createOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList();
      String conditionValList = createOrUpdateIndicatorExpressionItemRequestRs.getConditionValList();
      String resultRaw = createOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
      String resultExpression = createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      String resultNameList = createOrUpdateIndicatorExpressionItemRequestRs.getResultNameList();
      String resultValList = createOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
      Integer seq = createOrUpdateIndicatorExpressionItemRequestRs.getSeq();
      IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
      if (StringUtils.isBlank(indicatorExpressionItemId)) {
        indicatorExpressionItemId = idGenerator.nextIdStr();
        indicatorExpressionItemEntity = IndicatorExpressionItemEntity
            .builder()
            .indicatorExpressionItemId(indicatorExpressionItemId)
            .indicatorExpressionId(finalIndicatorExpressionId1)
            .appId(appId)
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
      } else {
        indicatorExpressionItemEntity = kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.get(indicatorExpressionItemId);
        if (Objects.isNull(indicatorExpressionItemEntity)) {
          log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs paramIndicatorExpressionItemIdSet:{} is illegal", paramIndicatorExpressionItemIdSet);
          throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
        }
        indicatorExpressionItemEntity.setConditionRaw(conditionRaw);
        indicatorExpressionItemEntity.setConditionExpression(conditionExpression);
        indicatorExpressionItemEntity.setConditionNameList(conditionNameList);
        indicatorExpressionItemEntity.setConditionValList(conditionValList);
        indicatorExpressionItemEntity.setResultRaw(resultRaw);
        indicatorExpressionItemEntity.setResultExpression(resultExpression);
        indicatorExpressionItemEntity.setResultNameList(resultNameList);
        indicatorExpressionItemEntity.setResultValList(resultValList);
        indicatorExpressionItemEntity.setSeq(seq);
      }
      indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
    });
    if (Objects.nonNull(maxCreateOrUpdateIndicatorExpressionItemRequestRs)) {
      IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
      String indicatorExpressionItemId = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
      String resultRaw = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
      String resultExpression = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      String resultNameList = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList();
      String resultValList = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
      if (StringUtils.isBlank(indicatorExpressionItemId)) {
        indicatorExpressionItemId = idGenerator.nextIdStr();
        indicatorExpressionItemEntity = IndicatorExpressionItemEntity
            .builder()
            .indicatorExpressionItemId(indicatorExpressionItemId)
            .appId(appId)
            .resultRaw(resultRaw)
            .resultExpression(resultExpression)
            .resultNameList(resultNameList)
            .resultValList(resultValList)
            .build();
      } else {
        String finalIndicatorExpressionItemId = indicatorExpressionItemId;
        indicatorExpressionItemEntity = indicatorExpressionItemService.lambdaQuery()
            .eq(IndicatorExpressionItemEntity::getAppId, appId)
            .eq(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, indicatorExpressionItemId)
            .oneOpt()
            .orElseThrow(() -> {
              log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs indicatorExpressionItemId:{} is illegal", finalIndicatorExpressionItemId);
              throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
            });
        indicatorExpressionItemEntity.setResultRaw(resultRaw);
        indicatorExpressionItemEntity.setResultExpression(resultExpression);
        indicatorExpressionItemEntity.setResultNameList(resultNameList);
        indicatorExpressionItemEntity.setResultValList(resultValList);
      }
      indicatorExpressionEntity.setMaxIndicatorExpressionItemId(indicatorExpressionItemId);
      indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
    }
    if (Objects.nonNull(minCreateOrUpdateIndicatorExpressionItemRequestRs)) {
      IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
      String indicatorExpressionItemId = minCreateOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
      String resultRaw = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultRaw();
      String resultExpression = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      String resultNameList = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList();
      String resultValList = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
      if (StringUtils.isBlank(indicatorExpressionItemId)) {
        indicatorExpressionItemId = idGenerator.nextIdStr();
        indicatorExpressionItemEntity = IndicatorExpressionItemEntity
            .builder()
            .indicatorExpressionItemId(indicatorExpressionItemId)
            .appId(appId)
            .resultRaw(resultRaw)
            .resultExpression(resultExpression)
            .resultNameList(resultNameList)
            .resultValList(resultValList)
            .build();
      } else {
        String finalIndicatorExpressionItemId = indicatorExpressionItemId;
        indicatorExpressionItemEntity = indicatorExpressionItemService.lambdaQuery()
            .eq(IndicatorExpressionItemEntity::getAppId, appId)
            .eq(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, indicatorExpressionItemId)
            .oneOpt()
            .orElseThrow(() -> {
              log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs indicatorExpressionItemId:{} is illegal", finalIndicatorExpressionItemId);
              throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
            });
        indicatorExpressionItemEntity.setResultRaw(resultRaw);
        indicatorExpressionItemEntity.setResultExpression(resultExpression);
        indicatorExpressionItemEntity.setResultNameList(resultNameList);
        indicatorExpressionItemEntity.setResultValList(resultValList);
      }
      indicatorExpressionEntity.setMinIndicatorExpressionItemId(indicatorExpressionItemId);
      indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
    }
    indicatorExpressionService.saveOrUpdate(indicatorExpressionEntity);
    indicatorExpressionItemService.saveOrUpdateBatch(indicatorExpressionItemEntityList);
  }
}
