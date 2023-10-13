package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.CaseIndicatorExpressionItemResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.IndicatorExpressionItemException;
import org.dows.hep.entity.CaseIndicatorExpressionEntity;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.service.CaseIndicatorExpressionItemService;
import org.dows.hep.service.CaseIndicatorExpressionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseIndicatorExpressionItemBiz {
  private final RsCaseIndicatorExpressionBiz rsCaseIndicatorExpressionBiz;
  private final CaseIndicatorExpressionService caseIndicatorExpressionService;
  private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
  public static CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItem2ResponseRs(CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity) {
    if (Objects.isNull(caseIndicatorExpressionItemEntity)) {
      return null;
    }
    return CaseIndicatorExpressionItemResponseRs
        .builder()
        .id(caseIndicatorExpressionItemEntity.getId())
        .appId(caseIndicatorExpressionItemEntity.getAppId())
        .indicatorExpressionItemId(caseIndicatorExpressionItemEntity.getCaseIndicatorExpressionItemId())
        .indicatorExpressionId(caseIndicatorExpressionItemEntity.getIndicatorExpressionId())
        .conditionRaw(caseIndicatorExpressionItemEntity.getConditionRaw())
        .conditionExpression(caseIndicatorExpressionItemEntity.getConditionExpression())
        .conditionNameList(caseIndicatorExpressionItemEntity.getConditionNameList())
        .conditionValList(caseIndicatorExpressionItemEntity.getConditionValList())
        .resultRaw(caseIndicatorExpressionItemEntity.getResultRaw())
        .resultExpression(caseIndicatorExpressionItemEntity.getResultExpression())
        .resultNameList(caseIndicatorExpressionItemEntity.getResultNameList())
        .resultValList(caseIndicatorExpressionItemEntity.getResultValList())
        .seq(caseIndicatorExpressionItemEntity.getSeq())
        .deleted(caseIndicatorExpressionItemEntity.getDeleted())
        .dt(caseIndicatorExpressionItemEntity.getDt())
        .build();
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String caseIndicatorExpressionItemId) {
    CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity = caseIndicatorExpressionItemService.lambdaQuery()
        .eq(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, caseIndicatorExpressionItemId)
        .one();
    if (Objects.isNull(caseIndicatorExpressionItemEntity)) {return;}

    String caseIndicatorExpressionId = caseIndicatorExpressionItemEntity.getIndicatorExpressionId();
    CaseIndicatorExpressionEntity caseindicatorExpressionEntity = caseIndicatorExpressionService.lambdaQuery()
        .eq(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, caseIndicatorExpressionId)
        .one();
    if (Objects.isNull(caseindicatorExpressionEntity)) {return;}
    rsCaseIndicatorExpressionBiz.modifyInfluenced(caseindicatorExpressionEntity);
    boolean isRemove = caseIndicatorExpressionItemService.remove(
        new LambdaQueryWrapper<CaseIndicatorExpressionItemEntity>()
            .eq(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, caseIndicatorExpressionItemId)
    );
    if (!isRemove) {
      log.warn("method CaseIndicatorExpressionItemBiz.delete caseIndicatorExpressionItemId:{} is illegal", caseIndicatorExpressionItemId);
      throw new IndicatorExpressionItemException(EnumESC.VALIDATE_EXCEPTION);
    }
  }
}
