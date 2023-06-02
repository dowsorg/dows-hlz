package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.SportPlanItemVO;
import org.dows.hep.api.core.ExptOrgFuncRequest;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveExptSportRequest 对象", title = "保存学生运动方案")
public class SaveExptSportRequest extends ExptOrgFuncRequest {

    @Schema(title = "运动项目列表json")
    private List<SportPlanItemVO> sportItems;


}
