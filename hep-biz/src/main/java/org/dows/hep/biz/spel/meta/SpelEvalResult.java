package org.dows.hep.biz.spel.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;

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

    private Object curVal;

    private Object val;

    private BigDecimal valNumber;

    private BigDecimal min;

    private BigDecimal max;

    public String getValString(){
        return getString(val);
    }

    String getString(Object obj){
        if(ShareUtil.XObject.isEmpty(obj)){
            return null;
        }
        if(obj instanceof BigDecimal){
            return BigDecimalUtil.formatRoundDecimal((BigDecimal) obj, 2);
        }
        return obj.toString();
    }

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
