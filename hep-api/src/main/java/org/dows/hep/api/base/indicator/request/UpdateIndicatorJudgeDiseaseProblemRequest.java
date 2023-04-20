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
@Schema(name = "UpdateIndicatorJudgeDiseaseProblem 对象", title = "判断指标疾病问题")
public class UpdateIndicatorJudgeDiseaseProblemRequest{
    @Schema(title = "判断指标疾病问题分布式ID")
    private String indicatorJudgeDiseaseProblemId;

    @Schema(title = "疾病问题名称")
    private String name;

    @Schema(title = "疾病问题类别")
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
