package org.dows.hep.api.risk.response;

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
@Schema(name = "RiskCategory 对象", title = "风险类别")
public class RiskCategoryResponse{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "分类名称")
    private String riskCategoryName;


}
