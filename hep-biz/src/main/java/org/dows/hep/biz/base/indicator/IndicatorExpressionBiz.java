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
      indicatorExpressionItemResponseRsList = Collections.emptyList();
    }
    return IndicatorExpressionResponseRs
        .builder()
        .id(indicatorExpressionEntity.getId())
        .indicatorExpressionId(indicatorExpressionEntity.getIndicatorExpressionId())
        .appId(indicatorExpressionEntity.getAppId())
        .deleted(indicatorExpressionEntity.getDeleted())
        .dt(indicatorExpressionEntity.getDt())
        .indicatorExpressionItemResponseRsList(indicatorExpressionItemResponseRsList)
        .maxIndicatorExpressionItemResponseRs(maxIndicatorExpressionItemResponseRs)
        .minIndicatorExpressionItemResponseRs(minIndicatorExpressionItemResponseRs)
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public void createOrUpdate(CreateOrUpdateIndicatorExpressionRequestRs createOrUpdateIndicatorExpressionRequestRs) {
    String indicatorExpressionId = createOrUpdateIndicatorExpressionRequestRs.getIndicatorExpressionId();
    String principalId = createOrUpdateIndicatorExpressionRequestRs.getPrincipalId();
    String indicatorExpressionRefId = createOrUpdateIndicatorExpressionRequestRs.getIndicatorExpressionRefId();
    String appId = createOrUpdateIndicatorExpressionRequestRs.getAppId();
    Integer type = createOrUpdateIndicatorExpressionRequestRs.getType();
    IndicatorExpressionEntity indicatorExpressionEntity = null;
    List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = new ArrayList<>();
    List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList = createOrUpdateIndicatorExpressionRequestRs.getCreateOrUpdateIndicatorExpressionItemRequestRsList();
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
      indicatorExpressionRefService.lambdaQuery()
          .eq(IndicatorExpressionRefEntity::getIndicatorExpressionRefId, indicatorExpressionRefId)
          .eq(IndicatorExpressionRefEntity::getIndicatorExpressionId, indicatorExpressionId)
          .eq(IndicatorExpressionRefEntity::getPrincipalId, principalId)
          .oneOpt()
          .orElseThrow(() -> {
            log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs indicatorExpressionRefId:{}, indicatorExpressionId:{}, principalId:{} is illegal", indicatorExpressionRefId, finalIndicatorExpressionId, principalId);
            throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
          });
      if (!createOrUpdateIndicatorExpressionItemRequestRsList.isEmpty()) {
        Set<String> paramIndicatorExpressionItemIdSet = createOrUpdateIndicatorExpressionItemRequestRsList
            .stream()
            .map(CreateOrUpdateIndicatorExpressionItemRequestRs::getIndicatorExpressionItemId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Set<String> dbIndicatorExpressionItemIdSet = new HashSet<>();
        Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap = indicatorExpressionItemService.lambdaQuery()
            .eq(IndicatorExpressionItemEntity::getAppId, appId)
            .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, paramIndicatorExpressionItemIdSet)
            .list()
            .stream()
            .peek(indicatorExpressionItemEntity -> dbIndicatorExpressionItemIdSet.add(indicatorExpressionItemEntity.getIndicatorExpressionItemId()))
            .collect(Collectors.toMap(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, a->a));
        if (
            paramIndicatorExpressionItemIdSet.stream().anyMatch(indicatorExpressionItemId -> !dbIndicatorExpressionItemIdSet.contains(indicatorExpressionItemId))
        ) {
          log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs paramIndicatorExpressionItemIdSet:{} is illegal", paramIndicatorExpressionItemIdSet);
          throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
        }
        createOrUpdateIndicatorExpressionItemRequestRsList.forEach(createOrUpdateIndicatorExpressionItemRequestRs -> {
          String indicatorExpressionItemId = createOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
          String condition = createOrUpdateIndicatorExpressionItemRequestRs.getConditionExpression();
          String conditionNameList = createOrUpdateIndicatorExpressionItemRequestRs.getConditionNameList();
          String conditionValList = createOrUpdateIndicatorExpressionItemRequestRs.getConditionValList();
          String result = createOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
          Integer seq = createOrUpdateIndicatorExpressionItemRequestRs.getSeq();
          IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
          if (StringUtils.isBlank(indicatorExpressionItemId)) {
            indicatorExpressionItemId = idGenerator.nextIdStr();
            indicatorExpressionItemEntity = IndicatorExpressionItemEntity
                .builder()
                .indicatorExpressionItemId(indicatorExpressionItemId)
                .conditionExpression(condition)
                .conditionNameList(conditionNameList)
                .conditionValList(conditionValList)
                .resultExpression(result)
                .seq(seq)
                .build();
          } else {
            indicatorExpressionItemEntity = kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.get(indicatorExpressionItemId);
            if (Objects.isNull(indicatorExpressionItemEntity)) {
              log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs paramIndicatorExpressionItemIdSet:{} is illegal", paramIndicatorExpressionItemIdSet);
              throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
            }
            indicatorExpressionItemEntity.setConditionExpression(condition);
            indicatorExpressionItemEntity.setConditionNameList(conditionNameList);
            indicatorExpressionItemEntity.setConditionValList(conditionValList);
            indicatorExpressionItemEntity.setResultExpression(result);
            indicatorExpressionItemEntity.setSeq(seq);
          }
          indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
        });
      }
    }
    if (Objects.nonNull(maxCreateOrUpdateIndicatorExpressionItemRequestRs)) {
      IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
      String indicatorExpressionItemId = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
      String result = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      String resultNameList = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList();
      String resultValList = maxCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
      if (StringUtils.isBlank(indicatorExpressionItemId)) {
        indicatorExpressionItemId = idGenerator.nextIdStr();
        indicatorExpressionItemEntity = IndicatorExpressionItemEntity
            .builder()
            .indicatorExpressionItemId(indicatorExpressionItemId)
            .appId(appId)
            .indicatorExpressionId(indicatorExpressionId)
            .resultExpression(result)
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
        indicatorExpressionItemEntity.setResultExpression(result);
        indicatorExpressionItemEntity.setResultNameList(resultNameList);
        indicatorExpressionItemEntity.setResultValList(resultValList);
      }
      indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
    }
    if (Objects.nonNull(minCreateOrUpdateIndicatorExpressionItemRequestRs)) {
      IndicatorExpressionItemEntity indicatorExpressionItemEntity = null;
      String indicatorExpressionItemId = minCreateOrUpdateIndicatorExpressionItemRequestRs.getIndicatorExpressionItemId();
      String result = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultExpression();
      String resultNameList = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultNameList();
      String resultValList = minCreateOrUpdateIndicatorExpressionItemRequestRs.getResultValList();
      if (StringUtils.isBlank(indicatorExpressionItemId)) {
        indicatorExpressionItemId = idGenerator.nextIdStr();
        indicatorExpressionItemEntity = IndicatorExpressionItemEntity
            .builder()
            .indicatorExpressionItemId(indicatorExpressionItemId)
            .appId(appId)
            .indicatorExpressionId(indicatorExpressionId)
            .resultExpression(result)
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
        indicatorExpressionItemEntity.setResultExpression(result);
        indicatorExpressionItemEntity.setResultNameList(resultNameList);
        indicatorExpressionItemEntity.setResultValList(resultValList);
      }
      indicatorExpressionItemEntityList.add(indicatorExpressionItemEntity);
    }
    indicatorExpressionService.saveOrUpdate(indicatorExpressionEntity);
    indicatorExpressionItemService.saveOrUpdateBatch(indicatorExpressionItemEntityList);
  }
}
