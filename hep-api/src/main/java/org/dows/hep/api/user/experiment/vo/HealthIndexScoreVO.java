package org.dows.hep.api.user.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author : wuzl
 * @date : 2023/9/26 15:47
 */
@Data
@Accessors(chain = true)
@Schema(name = "HealthIndexScoreVO 对象", title = "竞争性得分计算数据")
public class HealthIndexScoreVO {

    @Schema(title = "案例人名")
    private String personName;
    @Schema(title = "案例id")
    private String casePersonId;

    @Schema(title = "结果分")
    private BigDecimal rstScore;

    @Schema(title = "当前健康指数")
    private BigDecimal curHP;
    @Schema(title = "最小分")
    private BigDecimal minScore;

    @Schema(title = "最大分")
    private BigDecimal maxScore;
}
