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
@Schema(name = "CreateIndicatorInstance 对象", title = "创建指标实例")
public class CreateIndicatorInstanceRequest {
    @Schema(title = "指标分类分布式ID")
    private String indicatorCategoryId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "描述")
    private String descr;

    @Schema(title = "默认值")
    private String def;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "指标code")
    private String indicatorCode;

    @Schema(title = "0-非关键指标，1-关键指标")
    private Integer core;

    @Schema(title = "0-非饮食关键指标，1-饮食关键指标")
    private Integer food;


}
