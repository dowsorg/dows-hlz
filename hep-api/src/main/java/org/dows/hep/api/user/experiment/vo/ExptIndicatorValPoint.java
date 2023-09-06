package org.dows.hep.api.user.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author : wuzl
 * @date : 2023/9/6 9:49
 */
@Data
@Accessors(chain = true)
@Schema(title = "健康档案指标节点")
public class ExptIndicatorValPoint {
    @Schema(title = "游戏内天数")
    private Integer gameDay;

    @Schema(title = "字符串型指标值")
    private String indicatorValStr;

    @Schema(title = "数值型指标值")
    private BigDecimal indicatorVal;
}
