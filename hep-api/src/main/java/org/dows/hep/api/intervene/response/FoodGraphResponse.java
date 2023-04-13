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
@Schema(name = "FoodGraph 对象", title = "能量占比、膳食宝塔")
public class FoodGraphResponse{
    @Schema(title = "能量占比json")
    private String statEnergy;

    @Schema(title = "膳食结构json")
    private String statCateg;


}
