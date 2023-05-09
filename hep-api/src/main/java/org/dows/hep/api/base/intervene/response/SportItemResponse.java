package org.dows.hep.api.base.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "SportItem 对象", title = "运动项目列表")
public class SportItemResponse{

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "运动项目id")
    private String sportItemId;

    @Schema(title = "运动项目名称")
    private String sportItemName;


    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;


    @Schema(title = "分布式id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "运动强度(MET)")
    private String strengthMet;

    @Schema(title = "运动强度类别")
    private String strengthType;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;


}
