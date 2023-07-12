package org.dows.hep.api.tenant.casus.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "UpdateIndicatorValueRequest 对象", title = "更新主体[人物|NPC]指标所对应的值[描述|说明等]")
public class UpdateIndicatorValueRequest {
    @Schema(title = "案例与指标的关联ID")
    private String caseIndicatorInstanceId;
    @Schema(title = "指标实例ID")
    private String indicatorInstanceId;
    @Schema(title = "主体ID[实验人物|NPC等]")
    private String principal;
    @Schema(title = "指标类目ID")
    private String indicatorCategoryId;
    @Schema(title = "指标值")
    private String indicatorName;
    @Schema(title = "指标code[目前缺失]")
    private String indicatorCode;
    @Schema(title = "指标值")
    private String indicatorValue;

}
