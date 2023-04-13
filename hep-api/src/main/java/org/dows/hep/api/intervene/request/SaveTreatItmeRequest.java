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
@Schema(name = "SaveTreatItme 对象", title = "治疗项目信息")
public class SaveTreatItmeRequest{
    @Schema(title = "分布式id")
    private String treatItemId;

    @Schema(title = "治疗类型 1-心理治疗 2-医学治疗")
    private Integer treatItemType;

    @Schema(title = "治疗名称")
    private String treatItemName;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "费用")
    private String fee;

    @Schema(title = "关联指标json对象")
    private String indicators;


}
