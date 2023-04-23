package org.dows.hep.api.user.experiment.response;

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
@Schema(name = "OrgJudgedItems 对象", title = "已选判断指标列表")
public class OrgJudgedItemsResponse{
    @Schema(title = "指标id")
    private String indicatorId;

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "类别id")
    private String categId;

    @Schema(title = "类别名称")
    private String categName;


}
