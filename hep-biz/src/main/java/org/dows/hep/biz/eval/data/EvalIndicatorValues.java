package org.dows.hep.biz.eval.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/9/5 14:54
 */
@Data
@Accessors(chain = true)
public class EvalIndicatorValues {

    @Schema(title = "指标id")
    private String indicatorId;

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "计算批次")
    private Integer evalNo;
    @Schema(title = "当前值")
    private String currentVal;

    @Schema(title = "之前值")
    private String lastVal;
    @Schema(title = "增量值")
    private String changeVal;
    @Schema(title = "是否已结算增量值")
    private boolean changedFlag;
}
