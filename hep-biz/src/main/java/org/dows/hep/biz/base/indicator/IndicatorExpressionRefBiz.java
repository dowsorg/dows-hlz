package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.IndicatorExpressionItemRefException;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorExpressionRefEntity;
import org.dows.hep.service.IndicatorExpressionItemService;
import org.dows.hep.service.IndicatorExpressionRefService;
import org.dows.hep.service.IndicatorExpressionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional(rollbackFor = Exception.class)
  public void delete(String indicatorExpressionRefId) {
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
    String indicatorExpressionId = indicatorExpressionRefEntity.getIndicatorExpressionId();
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
