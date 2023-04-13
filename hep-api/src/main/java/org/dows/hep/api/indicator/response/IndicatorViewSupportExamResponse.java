package org.dows.hep.api.indicator.response;

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
@Schema(name = "IndicatorViewSupportExam 对象", title = "查看指标辅助检查类")
public class IndicatorViewSupportExamResponse{
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "查看指标辅助检查类分布式ID")
    private String IndicatorViewSupportExamId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

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


}
