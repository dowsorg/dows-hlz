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
@Schema(name = "FindInterveneCateg 对象", title = "查询条件")
public class FindInterveneCategRequest{
    @Schema(title = "根类别标识，food.material-食材类别；sport.item-运动项目类别...")
    private String family;

    @Schema(title = "父类别")
    private String pid;

    @Schema(title = "是否包含子节点 0-否 1-是")
    private Integer withChild;


}
