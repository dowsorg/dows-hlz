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
@Schema(name = "TreatItem 对象", title = "治疗项目列表")
public class TreatItemResponse{
    @Schema(title = "分布式id")
    private String treatItemId;

    @Schema(title = "治疗名称")
    private String treatItemName;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "费用")
    private String fee;


}
