package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "CreateIndicatorRule 对象", title = "创建指标规则对象")
public class CreateIndicatorRuleRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标或变量ID")
    private String variableId;

    @Schema(title = "变量类型[0:指标，1:变量]")
    private Integer ruleType;

    @Schema(title = "最小值")
    private String min;

    @Schema(title = "最大值")
    private String max;

    @Schema(title = "默认值")
    private String def;

    @Schema(title = "描述")
    private String descr;


}
