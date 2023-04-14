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
@Schema(name = "IndicatorJudgeHealthManagementGoal 对象", title = "判断指标健管目标")
public class IndicatorJudgeHealthManagementGoalResponse{
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "判断指标健管目标分布式ID")
    private String indicatorJudgeHealthManagementGoalId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

    @Schema(title = "分数")
    private BigDecimal point;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "逻辑删除")
    private Integer deleted;

    @Schema(title = "时间戳")
    private java.time.LocalDateTime dt;


}
