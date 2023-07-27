package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.CaseIndicatorExpressionItemRefException;
import org.dows.hep.api.exception.CaseIndicatorExpressionRefBizException;
import org.dows.hep.api.exception.IndicatorExpressionItemRefException;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
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
public class CaseIndicatorExpressionRefBiz {
  private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
  private final CaseIndicatorExpressionInfluenceService caseIndicatorExpressionInfluenceService;
  private final RsUtilBiz rsUtilBiz;
  private final RsCaseIndicatorExpressionBiz rsCaseIndicatorExpressionBiz;

  @Transactional(rollbackFor = Exception.class)
  public void oldDelete(String caseIndicatorExpressionRefId) {
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
    AtomicReference<String> caseIndicatorInstanceIdAR = new AtomicReference<>();
    caseIndicatorExpressionService.lambdaQuery()
        .eq(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, caseIndicatorExpressionId)
        .oneOpt()
        .ifPresent(caseIndicatorExpressionEntity -> {
          if (EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource().equals(caseIndicatorExpressionEntity.getSource())) {
            caseIndicatorInstanceIdAR.set(caseIndicatorExpressionEntity.getPrincipalId());
          }
        });
    if (StringUtils.isNotBlank(caseIndicatorInstanceIdAR.get())) {
      caseIndicatorExpressionInfluenceService.lambdaUpdate()
          .eq(CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId, caseIndicatorInstanceIdAR.get())
          .set(CaseIndicatorExpressionInfluenceEntity::getInfluenceIndicatorInstanceIdList, null)
          .set(CaseIndicatorExpressionInfluenceEntity::getInfluencedIndicatorInstanceIdList, null)
          .update();
    }
    caseIndicatorExpressionService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionEntity>()
            .eq(CaseIndicatorExpressionEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
    );
    caseIndicatorExpressionItemService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionItemEntity>()
            .eq(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
    );
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String caseIndicatorExpressionRefId) {
    /* runsix:delete IndicatorExpressionRefEntity */
    CaseIndicatorExpressionRefEntity caseIndicatorExpressionRefEntity = caseIndicatorExpressionRefService.lambdaQuery()
        .eq(CaseIndicatorExpressionRefEntity::getCaseIndicatorExpressionRefId, caseIndicatorExpressionRefId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method CaseIndicatorExpressionRefBiz.delete caseIndicatorExpressionRefId:{} is illegal", caseIndicatorExpressionRefId);
          throw new CaseIndicatorExpressionItemRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    boolean isRemove = caseIndicatorExpressionRefService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionRefEntity>()
            .eq(CaseIndicatorExpressionRefEntity::getCaseIndicatorExpressionRefId, caseIndicatorExpressionRefId)
    );
    if (!isRemove) {
      log.warn("method CaseIndicatorExpressionRefBiz.delete caseIndicatorExpressionRefId:{} is illegal", caseIndicatorExpressionRefId);
      throw new IndicatorExpressionItemRefException(EnumESC.VALIDATE_EXCEPTION);
    }

    /* runsix:delete IndicatorExpressionEntity */
    String caseIndicatorExpressionId = caseIndicatorExpressionRefEntity.getIndicatorExpressionId();
    CaseIndicatorExpressionEntity caseIndicatorExpressionEntity = caseIndicatorExpressionService.lambdaQuery()
        .eq(CaseIndicatorExpressionEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
        .one();
    rsCaseIndicatorExpressionBiz.modifyInfluenced(caseIndicatorExpressionEntity);
    caseIndicatorExpressionService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionEntity>()
            .eq(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, caseIndicatorExpressionId)
    );
    caseIndicatorExpressionItemService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionItemEntity>()
            .eq(CaseIndicatorExpressionItemEntity::getIndicatorExpressionId, caseIndicatorExpressionId)
    );
  }
}
