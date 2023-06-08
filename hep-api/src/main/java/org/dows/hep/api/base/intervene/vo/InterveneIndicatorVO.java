package org.dows.hep.api.base.intervene.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;

/**
 * @author : wuzl
 * @date : 2023/4/23 19:05
 */

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "InterveneIndicatorVO 对象", title = "干预关联指标")
public class InterveneIndicatorVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id,新增时为空")
    private Long id;

    @Schema(title = "关联分布式id，删除使用")
    private String refId;

    @Schema(title = "指标id")
    @ApiModelProperty(required = true)
    private String indicatorInstanceId;

    @Schema(title = "指标分类ID")
    @ApiModelProperty(required = true)
    private String indicatorCategoryId;

    @Schema(title = "指标公式ID")
    @ApiModelProperty(required = true)
    private String indicatorExpressionId;

    @Schema(title = "表达式")
    @ApiModelProperty(required = true)
    private String expression;

    @Schema(title = "表达式描述")
    @ApiModelProperty(required = true)
    private String expressionDescr;

    @Schema(title = "参数id列表")
    private String expressionVars;

    @Schema(title = "参数名列表")
    private String expressionNames;

    @Schema(title = "排序号")
    private Integer seq;

    @Schema(title = "表达式详情")
    private IndicatorExpressionResponseRs expressionInfo;

}
