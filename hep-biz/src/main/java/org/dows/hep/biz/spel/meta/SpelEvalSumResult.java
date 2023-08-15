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
 * @date : 2023/7/20 23:28
 */

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpelEvalSumResult {
    private String experimentIndicatorId;

    private Object curVal;

    private Object val;

    private BigDecimal valNumber;

    private BigDecimal min;

    private BigDecimal max;

    public Double getValdDouble(){
        if(ShareUtil.XObject.notNumber(val)){
            return 0d;
        }
        return Double.valueOf(val.toString());
    }
    public String getValString(){
        return getString(val);
    }
    public String getCurValString(){
        return getString(curVal);
    }

    public String getNewValString(){
        if(ShareUtil.XObject.notNumber(val)||ShareUtil.XObject.notNumber(curVal)){
            return getValString();
        }
        BigDecimal newVal=BigDecimalUtil.valueOf(val).add(BigDecimalUtil.valueOf(curVal));
        if (ShareUtil.XObject.allEmpty(this.getMin(), this.getMax())){
            return getString(newVal);
        }
        if (ShareUtil.XObject.notEmpty(this.getMin()) && newVal.compareTo(this.getMin()) < 0) {
            newVal = this.getMin();
        }
        if (ShareUtil.XObject.notEmpty(this.getMax()) && this.getMax().compareTo(newVal) < 0) {
            newVal = this.getMax();
        }
        return getString(newVal);
    }

    String getString(Object obj){
        if(ShareUtil.XObject.isEmpty(obj)){
            return null;
        }
        if(obj instanceof BigDecimal){
            return ((BigDecimal)obj).toPlainString();
        }
        return obj.toString();
    }
}
