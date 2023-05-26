package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "IndicatorJudgeHealthProblem 对象", title = "判断指标健康问题")
public class IndicatorJudgeHealthProblemResponse implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String indicatorJudgeHealthProblemId;

    @Schema(title = "健康问题名称")
    private String name;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;
}
