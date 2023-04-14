package org.dows.hep.api.base.indicator.response;

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
@Schema(name = "IndicatorJudgeDiseaseProblem 对象", title = "判断指标疾病问题")
public class IndicatorJudgeDiseaseProblemResponse {
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "判断指标疾病问题分布式ID")
    private String IndicatorJudgeDiseaseProblemId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

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
