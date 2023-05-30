package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.ExptOrgFuncRequest;
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
@Schema(name = "SaveExptTreatRequest 对象", title = "保存学生治疗方案")
public class SaveExptTreatRequest extends ExptOrgFuncRequest {
    @Schema(title = "治疗项目列表json")
    private List<ExptTreatPlanItemVO> treatItems;


}
