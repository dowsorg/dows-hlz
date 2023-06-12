package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionItemResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.IndicatorExpressionItemException;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.service.IndicatorExpressionItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorExpressionItemBiz {

  private final IndicatorExpressionItemService indicatorExpressionItemService;
  public static IndicatorExpressionItemResponseRs indicatorExpressionItem2ResponseRs(IndicatorExpressionItemEntity indicatorExpressionItemEntity) {
    if (Objects.isNull(indicatorExpressionItemEntity)) {
      return null;
    }
    return IndicatorExpressionItemResponseRs
        .builder()
        .id(indicatorExpressionItemEntity.getId())
        .appId(indicatorExpressionItemEntity.getAppId())
        .indicatorExpressionId(indicatorExpressionItemEntity.getIndicatorExpressionId())
        .indicatorExpressionItemId(indicatorExpressionItemEntity.getIndicatorExpressionItemId())
        .conditionRaw(indicatorExpressionItemEntity.getConditionRaw())
        .conditionExpression(indicatorExpressionItemEntity.getConditionExpression())
        .conditionNameList(indicatorExpressionItemEntity.getConditionNameList())
        .conditionValList(indicatorExpressionItemEntity.getConditionValList())
        .resultRaw(indicatorExpressionItemEntity.getResultRaw())
        .resultExpression(indicatorExpressionItemEntity.getResultExpression())
        .resultNameList(indicatorExpressionItemEntity.getResultNameList())
        .resultValList(indicatorExpressionItemEntity.getResultValList())
        .seq(indicatorExpressionItemEntity.getSeq())
        .deleted(indicatorExpressionItemEntity.getDeleted())
        .dt(indicatorExpressionItemEntity.getDt())
        .build();
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String indicatorExpressionItemId) {
    boolean isRemove = indicatorExpressionItemService.remove(
        new LambdaQueryWrapper<IndicatorExpressionItemEntity>()
            .eq(IndicatorExpressionItemEntity::getIndicatorExpressionItemId, indicatorExpressionItemId)
    );
    if (!isRemove) {
      log.warn("method IndicatorExpressionItemBiz.delete indicatorExpressionItemId:{} is illegal", indicatorExpressionItemId);
      throw new IndicatorExpressionItemException(EnumESC.VALIDATE_EXCEPTION);
    }
  }
}
