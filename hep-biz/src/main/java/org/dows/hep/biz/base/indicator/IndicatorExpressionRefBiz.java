package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.IndicatorExpressionItemRefException;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionInfluenceEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorExpressionRefEntity;
import org.dows.hep.service.IndicatorExpressionInfluenceService;
import org.dows.hep.service.IndicatorExpressionItemService;
import org.dows.hep.service.IndicatorExpressionRefService;
import org.dows.hep.service.IndicatorExpressionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorExpressionRefBiz {
  private final IndicatorExpressionRefService indicatorExpressionRefService;
  private final IndicatorExpressionService indicatorExpressionService;
  private final IndicatorExpressionItemService indicatorExpressionItemService;
  private final IndicatorExpressionInfluenceService indicatorExpressionInfluenceService;
  private final RsUtilBiz rsUtilBiz;


  @Transactional(rollbackFor = Exception.class)
  public void oldDelete(String indicatorExpressionRefId) {
    IndicatorExpressionRefEntity indicatorExpressionRefEntity = indicatorExpressionRefService.lambdaQuery()
        .eq(IndicatorExpressionRefEntity::getIndicatorExpressionRefId, indicatorExpressionRefId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method IndicatorExpressionRefBiz.delete indicatorExpressionRefId:{} is illegal", indicatorExpressionRefId);
          throw new IndicatorExpressionItemRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    /* runsix: */
    boolean isRemove = indicatorExpressionRefService.remove(
        new LambdaQueryWrapper<IndicatorExpressionRefEntity>()
            .eq(IndicatorExpressionRefEntity::getIndicatorExpressionRefId, indicatorExpressionRefId)
    );
    if (!isRemove) {
      log.warn("method IndicatorExpressionRefBiz.delete indicatorExpressionRefId:{} is illegal", indicatorExpressionRefId);
      throw new IndicatorExpressionItemRefException(EnumESC.VALIDATE_EXCEPTION);
    }
    String indicatorExpressionId = indicatorExpressionRefEntity.getIndicatorExpressionId();
    AtomicReference<String> indicatorInstanceIdAR = new AtomicReference<>();
    indicatorExpressionService.lambdaQuery()
      .eq(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionId)
      .oneOpt()
      .ifPresent(indicatorExpressionEntity -> {
          if (EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource().equals(indicatorExpressionEntity.getSource())) {
            indicatorInstanceIdAR.set(indicatorExpressionEntity.getPrincipalId());
          }
      });
    if (StringUtils.isNotBlank(indicatorInstanceIdAR.get())) {
      indicatorExpressionInfluenceService.lambdaUpdate()
          .eq(IndicatorExpressionInfluenceEntity::getIndicatorInstanceId, indicatorInstanceIdAR.get())
          .set(IndicatorExpressionInfluenceEntity::getInfluenceIndicatorInstanceIdList, null)
          .set(IndicatorExpressionInfluenceEntity::getInfluencedIndicatorInstanceIdList, null)
          .update();
    }
    indicatorExpressionService.remove(
        new LambdaQueryWrapper<IndicatorExpressionEntity>()
            .eq(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionId)
    );
    indicatorExpressionItemService.remove(
        new LambdaQueryWrapper<IndicatorExpressionItemEntity>()
            .eq(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionId)
    );
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String indicatorExpressionRefId) {
    /* runsix:delete IndicatorExpressionRefEntity */
    IndicatorExpressionRefEntity indicatorExpressionRefEntity = indicatorExpressionRefService.lambdaQuery()
        .eq(IndicatorExpressionRefEntity::getIndicatorExpressionRefId, indicatorExpressionRefId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method IndicatorExpressionRefBiz.delete indicatorExpressionRefId:{} is illegal", indicatorExpressionRefId);
          throw new IndicatorExpressionItemRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    boolean isRemove = indicatorExpressionRefService.remove(
        new LambdaQueryWrapper<IndicatorExpressionRefEntity>()
            .eq(IndicatorExpressionRefEntity::getIndicatorExpressionRefId, indicatorExpressionRefId)
    );
    if (!isRemove) {
      log.warn("method IndicatorExpressionRefBiz.delete indicatorExpressionRefId:{} is illegal", indicatorExpressionRefId);
      throw new IndicatorExpressionItemRefException(EnumESC.VALIDATE_EXCEPTION);
    }

    /* runsix:delete IndicatorExpressionEntity */
    String indicatorExpressionId = indicatorExpressionRefEntity.getIndicatorExpressionId();
    IndicatorExpressionEntity indicatorExpressionEntity = indicatorExpressionService.lambdaQuery()
        .eq(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionId)
        .one();
    if (Objects.isNull(indicatorExpressionEntity)) {return;}
    Integer source = indicatorExpressionEntity.getSource();
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = EnumIndicatorExpressionSource.getBySource(source);
    /* runsix:如果是指标管理中指标产生的公式，需要对影响进行处理 */
    if (enumIndicatorExpressionSource == EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT) {
      /* runsix:指标本体 */
      String principalId = indicatorExpressionEntity.getPrincipalId();
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = indicatorExpressionItemService.lambdaQuery()
          .eq(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionId)
          .list();
      /* runsix:影响这个指标的指标id */
      Set<String> influencedIndicatorInstanceIdSet = new HashSet<>();
      indicatorExpressionItemEntityList.forEach(indicatorExpressionItemEntity -> {
        String conditionValList = indicatorExpressionItemEntity.getConditionValList();
        String resultValList = indicatorExpressionItemEntity.getResultValList();
        influencedIndicatorInstanceIdSet.addAll(rsUtilBiz.getConditionValSplitList(conditionValList));
        influencedIndicatorInstanceIdSet.addAll(rsUtilBiz.getResultValSplitList(resultValList));
      });
      /* runsix:移除它自己 */
      influencedIndicatorInstanceIdSet.remove(principalId);
      /* runsix:所有需要改的，包括它自己和影响它的 */
      List<IndicatorExpressionInfluenceEntity> allIndicatorExpressionInfluenceEntityList = new ArrayList<>();
      IndicatorExpressionInfluenceEntity principalIndicatorExpressionInfluenceEntity = indicatorExpressionInfluenceService.lambdaQuery()
          .eq(IndicatorExpressionInfluenceEntity::getIndicatorInstanceId, principalId)
          .one();
      principalIndicatorExpressionInfluenceEntity.setInfluencedIndicatorInstanceIdList(null);
      allIndicatorExpressionInfluenceEntityList.add(principalIndicatorExpressionInfluenceEntity);

      List<IndicatorExpressionInfluenceEntity> indicatorExpressionInfluenceEntityList = indicatorExpressionInfluenceService.lambdaQuery()
          .in(IndicatorExpressionInfluenceEntity::getIndicatorInstanceId, influencedIndicatorInstanceIdSet)
          .list();
      indicatorExpressionInfluenceEntityList.forEach(indicatorExpressionInfluenceEntity -> {
        String influenceIndicatorInstanceIdList = indicatorExpressionInfluenceEntity.getInfluenceIndicatorInstanceIdList();
        List<String> originList = rsUtilBiz.getSplitList(influenceIndicatorInstanceIdList);
        /* runsix:减去此次删除的指标id */
        originList.remove(principalId);
        indicatorExpressionInfluenceEntity.setInfluenceIndicatorInstanceIdList(rsUtilBiz.getCommaList(originList));
        allIndicatorExpressionInfluenceEntityList.add(indicatorExpressionInfluenceEntity);
      });
      indicatorExpressionInfluenceService.saveOrUpdateBatch(allIndicatorExpressionInfluenceEntityList);
    }
    indicatorExpressionService.remove(
        new LambdaQueryWrapper<IndicatorExpressionEntity>()
            .eq(IndicatorExpressionEntity::getIndicatorExpressionId, indicatorExpressionId)
    );
    indicatorExpressionItemService.remove(
        new LambdaQueryWrapper<IndicatorExpressionItemEntity>()
            .eq(IndicatorExpressionItemEntity::getIndicatorExpressionId, indicatorExpressionId)
    );
  }
}
