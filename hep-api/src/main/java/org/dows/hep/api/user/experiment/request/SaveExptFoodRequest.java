package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
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
@Schema(name = "SaveExptFood 对象", title = "保存学生食谱")
public class SaveExptFoodRequest extends ExptOrgFuncRequest {

    @Schema(title = "食材、菜肴列表json")
    private List<FoodCookbookDetailVO> details;

}
