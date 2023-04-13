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
@Schema(name = "SetFoodCookbookState 对象", title = "启用、禁用菜谱")
public class SetFoodCookbookStateRequest{
    @Schema(title = "菜肴id")
    private String foodCookbookId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;


}
