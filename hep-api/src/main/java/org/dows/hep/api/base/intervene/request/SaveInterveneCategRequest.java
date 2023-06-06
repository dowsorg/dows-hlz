package org.dows.hep.api.base.intervene.request;

import io.swagger.annotations.ApiModelProperty;
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
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "数据库id，新增时为空")
    private Long id;

    @Schema(title = "类别分布式id，新增时为空")
    private String categId;

    @Schema(title = "类别名称")
    @ApiModelProperty(required = true)
    private String categName;

    @Schema(title = "父类别分布式id")
    private String categPid;

    @Schema(title = "根类别标识，food.material-食材类别；sport.item-运动项目类别  treat.item:指标功能点id -自定义治疗项目...")
    @ApiModelProperty(required = true)
    private String family;

    @Schema(title = "标记 1-膳食主要分类")
    private Integer mark;

    @Schema(title = "扩展属性，饮食推荐量")
    private FoodCategExtendVO extend;


}
