package org.dows.hep.api.base.intervene.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/5/15 16:22
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EventIndicatorVO 对象", title = "事件关联指标")

public class EventIndicatorVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id,新增时为空")
    private Long id;

    @Schema(title = "关联分布式id，删除使用")
    private String refId;

    @Schema(title = "事件选项id")
    private String eventActionId;

    @JsonIgnore
    @Schema(title = "指标标记，0-事件影响指标 1-措施影响指标")
    private Boolean initFlag;

    @Schema(title = "指标id")
    @ApiModelProperty(required = true)
    private String indicatorInstanceId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

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

}
