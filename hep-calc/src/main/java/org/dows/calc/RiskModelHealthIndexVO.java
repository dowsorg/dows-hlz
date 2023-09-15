package org.dows.calc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/8/18 14:57
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "RiskModelHealthIndexVO 对象", title = "死亡原因健康指数")
public class RiskModelHealthIndexVO {
    @Schema(title = "人群id")
    private String crowdsId;
    @Schema(title = "风险模型id")
    private String riskModelId;

    @Schema(title = "风险模型名称")
    private String riskModelName;
    @Schema(title = "人群总死亡概率")
    private Integer crowdsDeathRate;

    @Schema(title = "风险模型死亡概率")
    private Integer riskModelDeathRate;

    @Schema(title = "健康指数")
    private BigDecimal healthIndex;

    @Schema(title = "当前分值")
    private BigDecimal score;

    @Schema(title = "分值下限")
    private BigDecimal minScore;

    @Schema(title = "分值上限")
    private BigDecimal maxScore;

    @Schema(title = "风险因素列表")
    private List<RiskFactorScoreVO> riskFactors;

    /**
     * 获取死亡概率权重
     * @return
     */
//    public BigDecimal getDeathRateWeight(){
//        return BigDecimalUtil.div(BigDecimal.valueOf(this.getRiskModelDeathRate()),
//                BigDecimal.valueOf(this.getCrowdsDeathRate()), 2, RoundingMode.DOWN);
//    }
//
//    public BigDecimal getDeathRateWeight(BigDecimal total){
//        return BigDecimalUtil.div(BigDecimal.valueOf(this.getRiskModelDeathRate()),
//                total, 2, RoundingMode.DOWN);
//    }
//
//    public BigDecimal getExistsDeathScore(){
//        return BigDecimalUtil.mul(BigDecimal.valueOf(this.getRiskModelDeathRate()),
//                this.getScore(),2, RoundingMode.DOWN);
//    }


}
