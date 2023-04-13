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
@Schema(name = "CreateIndicatorJudgeHealthProblem 对象", title = "创建判断指标健康问题")
public class CreateIndicatorJudgeHealthProblemRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "健康问题名称")
    private String name;

    @Schema(title = "健康问题类别")
    private String type;

    @Schema(title = "分数")
    private java.math.BigDecimal point;

    @Schema(title = "判断规则")
    private String expression;

    @Schema(title = "结果说明")
    private String resultExplain;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
