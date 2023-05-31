package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.user.experiment.vo.ExptTreatPlanItemVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "ExptTreatPlan 对象", title = "学生治疗方案")
public class ExptTreatPlanResponse {

    @Schema(title = "治疗项目列表json")
    private List<ExptTreatPlanItemVO> treatItems;

}
