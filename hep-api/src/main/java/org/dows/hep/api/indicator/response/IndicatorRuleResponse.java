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
@Schema(name = "IndicatorRule 对象", title = "指标规则")
public class IndicatorRuleResponse{
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String indicatorRuleId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标或变量ID")
    private String variableId;

    @Schema(title = "变量类型[0:指标，1:变量]")
    private Integer ruleType;

    @Schema(title = "最小值")
    private String min;

    @Schema(title = "最大值")
    private String max;

    @Schema(title = "默认值")
    private String def;

    @Schema(title = "描述")
    private String descr;


}
