package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.SportPlanItemVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveSportPlan 对象", title = "保存运动方案")
public class SaveSportPlanRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "数据库id,新增时为空")
    private Long id;

    @Schema(title = "运动方案id,新增时为空")
    private String sportPlanId;

    @Schema(title = "运动方案名称")
    private String sportPlanName;

    @Schema(title = "分类id")
    private String interveneCategId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "运动项目列表json")
    private List<SportPlanItemVO> sportItems;


}
