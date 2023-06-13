package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionItemResponseRs;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseIndicatorExpressionBiz {
  private final IdGenerator idGenerator;

  public CaseIndicatorExpressionItemEntity indicatorExpressionItemResponseRs2Case(
      String caseIndicatorExpressionItemId,
      String indicatorExpressionItemId,
      String appId,
      String caseIndicatorExpressionId,
      String conditionRaw,
      String conditionExpression,
      String conditionNameList,
      String conditionValList,
      String resultRaw,
      String resultExpression,
      String resultNameList,
      String resultValList,
      Integer seq
  ) {
    return CaseIndicatorExpressionItemEntity
        .builder()
        .caseIndicatorExpressionItemId(caseIndicatorExpressionItemId)
        .indicatorExpressionItemId(indicatorExpressionItemId)
        .appId(appId)
        .indicatorExpressionId(caseIndicatorExpressionId)
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
  }
}
