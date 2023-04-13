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
@Schema(name = "TreatItemInfo 对象", title = "治疗项目信息")
public class TreatItemInfoResponse{
    @Schema(title = "分布式id")
    private String treatItemId;

    @Schema(title = "治疗名称")
    private String treatItemName;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "当前分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "费用")
    private String fee;

    @Schema(title = "关联指标json对象")
    private String indicators;


}
