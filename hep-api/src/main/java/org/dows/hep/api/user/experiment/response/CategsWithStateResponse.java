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
@Schema(name = "CategsWithState 对象", title = "分类列表")
public class CategsWithStateResponse{
    @Schema(title = "类别id")
    private String categId;

    @Schema(title = "类别名称")
    private String categName;

    @Schema(title = "类别id路径")
    private String categIdPath;

    @Schema(title = "类别名称路径")
    private String categNamePath;

    @Schema(title = "子类别json")
    private String childs;


}
