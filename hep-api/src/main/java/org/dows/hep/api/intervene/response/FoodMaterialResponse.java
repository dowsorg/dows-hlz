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
@Schema(name = "FoodMaterial 对象", title = "食材列表")
public class FoodMaterialResponse{
    @Schema(title = "食材id")
    private String foodMaterialId;

    @Schema(title = "食材名称")
    private String foodMaterialName;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "蛋白质每100g")
    private String protein;

    @Schema(title = "碳水每100g")
    private String cho;

    @Schema(title = "脂肪每100g")
    private String fat;

    @Schema(title = "总能量每100g")
    private String energy;


}
