package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.CaseIndicatorExpressionRefBizException;
import org.dows.hep.api.exception.IndicatorExpressionItemRefException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseIndicatorExpressionRefBiz {
  private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;

  @Transactional(rollbackFor = Exception.class)
  public void delete(String caseIndicatorExpressionRefId) {
    CaseIndicatorExpressionRefEntity caseIndicatorExpressionRefEntity = caseIndicatorExpressionRefService.lambdaQuery()
        .eq(CaseIndicatorExpressionRefEntity::getCaseIndicatorExpressionRefId, caseIndicatorExpressionRefId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method CaseIndicatorExpressionRefBiz.delete caseIndicatorExpressionRefId:{} is illegal", caseIndicatorExpressionRefId);
          throw new CaseIndicatorExpressionRefBizException(EnumESC.CASE_INDICATOR_EXPRESSION_REF_ID_IS_ILLEGAL);
        });
    boolean isRemove = caseIndicatorExpressionRefService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionRefEntity>()
            .eq(CaseIndicatorExpressionRefEntity::getCaseIndicatorExpressionRefId, caseIndicatorExpressionRefId)
    );
    if (!isRemove) {
      log.warn("method CaseIndicatorExpressionRefBiz.delete caseIndicatorExpressionRefId:{} is illegal", caseIndicatorExpressionRefId);
      throw new IndicatorExpressionItemRefException(EnumESC.CASE_INDICATOR_EXPRESSION_REF_ID_IS_ILLEGAL);
    }
    String caseIndicatorExpressionId = caseIndicatorExpressionRefEntity.getIndicatorExpressionId();
    caseIndicatorExpressionService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionEntity>()
            .eq(CaseIndicatorExpressionEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
    );
    caseIndicatorExpressionItemService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionItemEntity>()
            .eq(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
    );
  }
}
