package org.dows.hep.api.base.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "TreatItemInfo 对象", title = "治疗项目信息")
public class TreatItemInfoResponse{

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String treatItemId;

    @Schema(title = "治疗名称")
    private String treatItemName;

    @Schema(title = "指标功能点id")
    private String indicatorFuncId;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "当前分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "费用")
    private String fee;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "指标公式列表(一般两级公式source=5 一般四级公式source=6)")
    private List<IndicatorExpressionResponseRs> expresssions;




}
