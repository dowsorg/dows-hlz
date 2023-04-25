package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.FoodCategExtendVO;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveInterveneCateg 对象", title = "类别信息")
public class SaveInterveneCategRequest{
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "类别id")
    private String categId;

    @Schema(title = "类别名称")
    private String categName;

    @Schema(title = "父类别id")
    private String categPid;

    @Schema(title = "根类别标识，food.material-食材类别...")
    private String family;

    @Schema(title = "标记 1-膳食主要分类")
    private Boolean mark;

    @Schema(title = "扩展属性，饮食推荐量")
    private FoodCategExtendVO extend;


}
