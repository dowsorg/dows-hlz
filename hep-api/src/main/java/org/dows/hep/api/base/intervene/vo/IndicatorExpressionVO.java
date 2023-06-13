package org.dows.hep.api.base.intervene.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/6/12 12:08
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "IndicatorExpressionVO 对象", title = "指标表达式")

public class IndicatorExpressionVO {
    @Schema(title = "指标ID")
    @ApiModelProperty(required = true)
    private String indicatorInstanceId;

    @Schema(title = "表达式ID")
    private String indicatorExpressionId;

}
