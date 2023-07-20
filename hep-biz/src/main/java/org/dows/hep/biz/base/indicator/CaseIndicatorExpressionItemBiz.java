package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.CaseIndicatorExpressionItemResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionItemResponseRs;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.service.CaseIndicatorExpressionItemService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
public class CaseIndicatorExpressionItemBiz {
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
}
