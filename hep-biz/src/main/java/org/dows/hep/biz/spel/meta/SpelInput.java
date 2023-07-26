package org.dows.hep.biz.spel.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/21 10:50
 */

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpelInput {
    private String reasonId;

    //公式id
    private String expressionId;

    //指标id
    private String indicatorId;

    //是否随机
    private boolean random;

    //下限
    private BigDecimal min;

    //上限
    private BigDecimal max;

    //用量
    private BigDecimal factor;
    private List<SpelExpressionItem> expressions;

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
