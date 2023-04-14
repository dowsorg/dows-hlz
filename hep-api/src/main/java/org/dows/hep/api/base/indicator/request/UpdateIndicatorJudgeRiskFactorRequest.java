package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "UpdateIndicatorJudgeRiskFactor 对象", title = "判断指标危险因素")
public class UpdateIndicatorJudgeRiskFactorRequest {
    @Schema(title = "判断指标危险因素分布式Id")
    private String IndicatorJudgeRiskFactorId;

    @Schema(title = "危险因素名称")
    private String name;

    @Schema(title = "危险因素类别")
    private String type;

    @Schema(title = "分数")
    private BigDecimal point;

    @Schema(title = "判断规则")
    private String expression;

    @Schema(title = "结果说明")
    private String resultExplain;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
