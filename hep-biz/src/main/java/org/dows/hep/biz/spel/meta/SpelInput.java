package org.dows.hep.biz.spel.meta;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.util.ShareUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/21 10:50
 */

@Data
@Accessors(chain = true)
public class SpelInput {

    public SpelInput(){

    }
    public SpelInput(EnumIndicatorExpressionSource source){
        this.source=source;
    }
    public SpelInput(Integer source){
        this.source=EnumIndicatorExpressionSource.of(source);
    }
    private EnumIndicatorExpressionSource source;

    private String reasonId;

    //公式id
    private String expressionId;

    //指标id
    private String indicatorId;

    //是否随机
    private boolean random;

    //公式下限
    private BigDecimal min;

    //公式上限
    private BigDecimal max;

    //指标下限
    private BigDecimal indicatorMin;

    //指标上限
    private BigDecimal indicatorMax;

    //用量
    private BigDecimal factor;
    private List<SpelExpressionItem> expressions;

    public boolean hasFactor(){
        if(ShareUtil.XObject.anyEmpty(factor,source))
            return false;
        return source==EnumIndicatorExpressionSource.INDICATOR_OPERATOR_NO_REPORT_TWO_LEVEL
                ||source==EnumIndicatorExpressionSource.INDICATOR_OPERATOR_HAS_REPORT_FOUR_LEVEL;

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SpelInput{");
        sb.append("reasonId='").append(reasonId).append('\'');
        sb.append(", expressionId='").append(expressionId).append('\'');
        sb.append(", indicatorId='").append(indicatorId).append('\'');
        sb.append(", random=").append(random);
        sb.append(", min=").append(min);
        sb.append(", max=").append(max);
        sb.append(", factor=").append(factor);
        sb.append('}');
        return sb.toString();
    }

    @Data
    @Accessors(chain = true)
    @Builder
    public static class SpelExpressionItem {

        private String conditionExpression;

        private String resultExpression;
    }
}
