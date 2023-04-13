package org.dows.hep.api.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SportItem 对象", title = "运动项目列表")
public class SportItemResponse{
    @Schema(title = "运动项目id")
    private String sportItemId;

    @Schema(title = "运动项目名称")
    private String sportItemName;

    @Schema(title = "图片")
    private String pic;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "运动强度(MET)")
    private String strengthMet;

    @Schema(title = "运动强度类别")
    private String strengthType;


}
