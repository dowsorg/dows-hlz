package org.dows.hep.api.base.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodMaterialVO;
import org.dows.hep.api.base.intervene.vo.FoodStatVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "FoodDishesInfo 对象", title = "菜肴信息")
public class FoodDishesInfoResponse{
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "菜肴id")
    private String foodDishesId;

    @Schema(title = "菜肴名称")
    private String foodDishesName;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "当前分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "食材列表json")
    private List<FoodMaterialVO> materials;

    @Schema(title = "能量占比json")
    private List<FoodStatVO> statEnergy;

    @Schema(title = "膳食结构json")
    private List<FoodStatVO> statCateg;


}
