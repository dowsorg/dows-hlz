package org.dows.hep.biz.calc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.util.ShareUtil;

import java.math.BigDecimal;

/**
 * @author : wuzl
 * @date : 2023/8/17 19:18
 */
@Data
@Accessors(chain = true)
@Schema(name = "RiskFactorScoreVO 对象", title = "危险因素分值")
public class RiskFactorScoreVO {
    public RiskFactorScoreVO(String riskFactorId,String riskExpressionId,  BigDecimal score,BigDecimal minScore,BigDecimal maxScore){
        this.riskFactorId=riskFactorId;
        this.riskExpressionId=riskExpressionId;
        this.score=score;
        this.minScore= ShareUtil.XObject.defaultIfNull(minScore, score);
        this.maxScore= ShareUtil.XObject.defaultIfNull(maxScore, score);
    }

    @Schema(title = "危险因素id")
    private String riskFactorId;

    @Schema(title = "危险表达式id")
    private String riskExpressionId;
    @Schema(title = "当前分值")
    private BigDecimal score;

    @Schema(title = "分值下限")
    private BigDecimal minScore;

    @Schema(title = "分值上限")
    private BigDecimal maxScore;
}
