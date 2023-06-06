package org.dows.hep.api.base.intervene.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveFoodCookbook 对象", title = "保存菜谱")
public class SaveFoodCookbookRequest{

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "数据库id，新增时为空")
    private Long id;
    @Schema(title = "食谱id，新增时为空")
    private String foodCookbookId;

    @Schema(title = "食谱名称")
    @ApiModelProperty(required = true)
    private String foodCookbookName;

    @Schema(title = "分类id")
    @ApiModelProperty(required = true)
    private String interveneCategId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "食材、菜肴列表json")
    private List<FoodCookbookDetailVO> details;


}
