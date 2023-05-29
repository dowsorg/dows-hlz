package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionItemRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionRequestRs;
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
  @Transactional(rollbackFor = Exception.class)
  public void createOrUpdate(CreateOrUpdateIndicatorExpressionRequestRs createOrUpdateIndicatorExpressionRequestRs) {
    String indicatorExpressionId = createOrUpdateIndicatorExpressionRequestRs.getIndicatorExpressionId();
    String principalId = createOrUpdateIndicatorExpressionRequestRs.getPrincipalId();
    String indicatorExpressionRefId = createOrUpdateIndicatorExpressionRequestRs.getIndicatorExpressionRefId();
    String appId = createOrUpdateIndicatorExpressionRequestRs.getAppId();
    String def = createOrUpdateIndicatorExpressionRequestRs.getDef();
    IndicatorExpressionEntity indicatorExpressionEntity = null;
    List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = new ArrayList<>();
    List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList = createOrUpdateIndicatorExpressionRequestRs.getCreateOrUpdateIndicatorExpressionItemRequestRsList();
    if (StringUtils.isBlank(indicatorExpressionId)) {
      indicatorExpressionId = idGenerator.nextIdStr();
      indicatorExpressionEntity = IndicatorExpressionEntity
          .builder()
          .indicatorExpressionId(indicatorExpressionId)
          .appId(appId)
          .def(def)
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
        Set<String> dbIndicatorExpressionItemIdSet = indicatorExpressionItemService.lambdaQuery()
            .eq(IndicatorExpressionItemEntity::getAppId, appId)
            .in(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, paramIndicatorExpressionItemIdSet)
            .list()
            .stream()
            .map(IndicatorExpressionItemEntity::getIndicatorExpressionItemId)
            .collect(Collectors.toSet());
        if (
            paramIndicatorExpressionItemIdSet.stream().anyMatch(indicatorExpressionItemId -> !dbIndicatorExpressionItemIdSet.contains(indicatorExpressionItemId))
        ) {
          log.warn("method IndicatorExpressionBiz.createOrUpdate param createOrUpdateIndicatorExpressionRequestRs paramIndicatorExpressionItemIdSet:{} is illegal", paramIndicatorExpressionItemIdSet);
          throw new IndicatorExpressionException(EnumESC.VALIDATE_EXCEPTION);
        }

      }
    }
    indicatorExpressionService.saveOrUpdate(indicatorExpressionEntity);
    indicatorExpressionItemService.saveOrUpdateBatch(indicatorExpressionItemEntityList);
  }
}
