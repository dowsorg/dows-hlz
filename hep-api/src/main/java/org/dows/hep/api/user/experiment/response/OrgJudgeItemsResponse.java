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
@Schema(name = "OrgJudgeItems 对象", title = "判断指标列表")
public class OrgJudgeItemsResponse{
    @Schema(title = "指标id")
    private String indicatorId;

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "类别id")
    private String categId;

    @Schema(title = "类别名称")
    private String categName;

    @Schema(title = "类别id路径")
    private String categIdPath;

    @Schema(title = "类别名称路径")
    private String categNamePath;

    @Schema(title = "是否选中 0-否 1-是")
    private Integer checked;


}
