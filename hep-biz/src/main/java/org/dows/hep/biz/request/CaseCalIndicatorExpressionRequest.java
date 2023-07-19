package org.dows.hep.biz.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.entity.CaseIndicatorExpressionEntity;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.CaseIndicatorRuleEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseCalIndicatorExpressionRequest implements Serializable {
  private Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap;
  private CaseIndicatorExpressionEntity caseIndicatorExpressionEntity;
  private List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList;
  private CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity;
  private CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity;
}
