package org.dows.hep.api.base.risk.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RiskModel 对象", title = "筛选风险模型")
public class RiskModelResponse{

    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "风险模型ID")
    private String riskModelId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "模型名称")
    private String name;

    @Schema(title = "死亡概率")
    private Integer riskDeathProbability;

    @Schema(title = "人群类别ID")
    private String crowdsCategoryId;

    @Schema(title = "人群类别名称")
    private String crowdsCategoryName;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "指标公式")
    private List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList;
}
