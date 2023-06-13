package org.dows.hep.api.base.intervene.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.IndicatorExpressionVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveTreatItme 对象", title = "治疗项目信息")
public class SaveTreatItemRequest {
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "数据库id,新增时为空")
    private Long id;
    @Schema(title = "分布式id,新增时为空")
    private String treatItemId;


    @Schema(title = "治疗名称")
    @ApiModelProperty(required = true)
    private String treatItemName;

    @Schema(title = "指标功能点id")
    @ApiModelProperty(required = true)
    private String indicatorFuncId;

    @Schema(title = "当前分类id")
    @ApiModelProperty(required = true)
    private String interveneCategId;

    @Schema(title = "单位")
    @ApiModelProperty(required = true)
    private String unit;

    @Schema(title = "费用")
    @ApiModelProperty(required = true)
    private String fee;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "指标公式列表(一般两级公式source=5 一般四级公式source=6)")
    private List<IndicatorExpressionVO> expresssions;


}
