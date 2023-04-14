package org.dows.hep.api.base.indicator.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "IndicatorViewPhysicalExam 对象", title = "")
public class IndicatorViewPhysicalExamResponse {
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "查看指标体格检查类分布式ID")
    private String IndicatorViewPhysicalExamId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

    @Schema(title = "体格检查名称")
    private String name;

    @Schema(title = "体格检查类别")
    private String type;

    @Schema(title = "费用")
    private BigDecimal fee;

    @Schema(title = "结果解析")
    private String resultAnalysis;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
