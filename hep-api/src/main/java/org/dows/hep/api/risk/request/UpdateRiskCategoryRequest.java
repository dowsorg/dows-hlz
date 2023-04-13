package org.dows.hep.api.risk.request;

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
@Schema(name = "UpdateRiskCategory 对象", title = "更改风险类别")
public class UpdateRiskCategoryRequest{
    @Schema(title = "风险")
    private String riskCategoryId;

    @Schema(title = "风险")
    private String riskCategoryName;

    @Schema(title = "展示顺序")
    private Integer seq;


}
