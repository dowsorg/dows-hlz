package org.dows.hep.vo.report;

import lombok.Builder;
import lombok.Data;

/**
 * 风险关联业务id对象
 */
@Builder
@Data
public class RiskRelevanceBo {
    private String experimentPersonId;
    private String experimentIndicatorInstanceId;
    private String experimentIndicatorExpressionId;
    private String reasonId;

}
