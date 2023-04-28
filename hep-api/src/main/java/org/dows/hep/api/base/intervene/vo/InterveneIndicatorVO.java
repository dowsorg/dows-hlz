package org.dows.hep.api.base.intervene.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "关联分布式id，删除使用")
    private String refId;

    @Schema(title = "指标id")
    private String indicatorInstanceId;

    @Schema(title = "表达式")
    private String expression;

    @Schema(title = "表达式描述")
    private String expressionDescr;

    @Schema(title = "排序号")
    private Integer seq;

}
