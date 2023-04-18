package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CreateIndicatorJudgeRiskFactor 对象", title = "创建危险因素")
public class CreateIndicatorJudgeRiskFactorRequest{
    @Schema(title = "应用ID")
    private String appId;

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
