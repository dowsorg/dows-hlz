package org.dows.hep.biz.spel.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author : wuzl
 * @date : 2023/7/20 23:26
 */

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpelEvalResult {
    private String reasonId;

    private String expressionId;

    private String indicatorId;

    private Object val;

    private BigDecimal valNumber;

    private BigDecimal min;

    private BigDecimal max;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SpelEvalResult{");
        sb.append("reasonId='").append(reasonId).append('\'');
        sb.append(", expressionId='").append(expressionId).append('\'');
        sb.append(", indicatorId='").append(indicatorId).append('\'');
        sb.append(", val=").append(val);
        sb.append(", valNumber=").append(valNumber);
        sb.append(", min=").append(min);
        sb.append(", max=").append(max);
        sb.append('}');
        return sb.toString();
    }
}
