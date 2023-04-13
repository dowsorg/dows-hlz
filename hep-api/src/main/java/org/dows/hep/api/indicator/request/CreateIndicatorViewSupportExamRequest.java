package org.dows.hep.api.indicator.request;

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
@Schema(name = "CreateIndicatorViewSupportExam 对象", title = "创建查看指标辅助检查类")
public class CreateIndicatorViewSupportExamRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "辅助检查名称")
    private String name;

    @Schema(title = "辅助检查类别")
    private String type;

    @Schema(title = "费用")
    private java.math.BigDecimal fee;

    @Schema(title = "结果解析")
    private String resultAnalysis;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "")
    private  创建查看指标辅助检查关联指标列表;


}
