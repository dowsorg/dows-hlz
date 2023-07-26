package org.dows.hep.biz.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.entity.*;

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
public class ExperimentCalIndicatorExpressionRequest implements Serializable {
  private Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap;
  private Map<String, ExperimentIndicatorValRsEntity> lastKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap;
  private ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity;
  private List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList;
  private ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity;
  private ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity;
}
