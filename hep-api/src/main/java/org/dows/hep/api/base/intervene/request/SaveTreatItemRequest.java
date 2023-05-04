package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;

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

    @Schema(title = "数据库id")
    private Long id;
    @Schema(title = "分布式id")
    private String treatItemId;


    @Schema(title = "治疗名称")
    private String treatItemName;

    @Schema(title = "功能点id")
    private String indicatorFuncId;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "费用")
    private String fee;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "关联指标json对象")
    private List<InterveneIndicatorVO> indicators;


}
