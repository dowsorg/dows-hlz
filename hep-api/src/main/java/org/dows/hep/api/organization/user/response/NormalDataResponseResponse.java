package org.dows.hep.api.organization.user.response;

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
@Schema(name = "NormalDataResponse 对象", title = "分类实例")
public class NormalDataResponseResponse{
    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "所占百分比")
    private BigDecimal per;


}
