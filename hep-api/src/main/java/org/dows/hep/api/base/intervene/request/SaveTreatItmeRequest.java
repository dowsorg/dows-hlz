package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveTreatItme 对象", title = "治疗项目信息")
public class SaveTreatItmeRequest{
    @Schema(title = "分布式id")
    private String treatItemId;

    @Schema(title = "治疗类型 1-心理治疗 2-医学治疗")
    private Integer treatItemType;

    @Schema(title = "治疗名称")
    private String treatItemName;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "费用")
    private String fee;

    @Schema(title = "关联指标json对象")
    private String indicators;


}
