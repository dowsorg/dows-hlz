package org.dows.hep.biz.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorRuleEntity;

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
public class DatabaseCalIndicatorExpressionRequest implements Serializable {
  private Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap;
  private IndicatorExpressionEntity indicatorExpressionEntity;
  private List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList;
  private IndicatorExpressionItemEntity minIndicatorExpressionItemEntity;
  private IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity;
}
