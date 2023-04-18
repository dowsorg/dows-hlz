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
@Schema(name = "CreateIndicatorViewSupportExam 对象", title = "创建查看指标辅助检查类")
public class CreateIndicatorViewSupportExamRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "辅助检查名称")
    private String name;

    @Schema(title = "辅助检查类别")
    private String type;

    @Schema(title = "费用")
    private BigDecimal fee;

    @Schema(title = "结果解析")
    private String resultAnalysis;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "创建查看指标辅助检查关联指标列表")
    private String listCreateIndicatorViewSupportExamRef;


}
