package org.dows.hep.api.intervene.request;

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
@Schema(name = "SaveFoodMaterial 对象", title = "食材信息")
public class SaveFoodMaterialRequest{
    @Schema(title = "食材id,新增时为空")
    private String foodMaterialId;

    @Schema(title = "食材名称")
    private String foodMaterialName;

    @Schema(title = "图片")
    private String pic;

    @Schema(title = "分类id")
    private String interveneCategId;

    @Schema(title = "关联指标列表json")
    private String indicators;

    @Schema(title = "营养成分列表json")
    private String nutrients;


}
