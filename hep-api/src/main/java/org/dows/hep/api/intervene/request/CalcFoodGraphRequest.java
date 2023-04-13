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
@Schema(name = "CalcFoodGraph 对象", title = "计算能量占比、膳食宝塔")
public class CalcFoodGraphRequest{
    @Schema(title = "计算类型 0-默认 1-只计算能量占比 2-只计算膳食宝塔")
    private Integer calcType;

    @Schema(title = "食材,菜肴重量列表json")
    private String details;


}
