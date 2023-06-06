package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.SportPlanItemVO;

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
@Schema(name = "ExptSportPlan 对象", title = "学生运动方案")
public class ExptSportPlanResponse {

    @Schema(title = "运动项目列表json")
    private List<SportPlanItemVO> sportItems;

}
