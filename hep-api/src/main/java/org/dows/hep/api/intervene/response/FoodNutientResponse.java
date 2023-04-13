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
@Schema(name = "FoodNutient 对象", title = "营养指标列表")
public class FoodNutientResponse{
    @Schema(title = "营养指标")
    private String indicatorInstanceId;

    @Schema(title = "营养成分名称")
    private String nutrientName;

    @Schema(title = "成分单位")
    private String unit;

    @Schema(title = "初始值")
    private String amt;


}
