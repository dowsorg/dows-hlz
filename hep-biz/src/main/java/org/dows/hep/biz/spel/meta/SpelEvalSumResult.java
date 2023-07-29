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

    public Double getValdDouble(){
        if(ShareUtil.XObject.notNumber(val)){
            return null;
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
        return getString( BigDecimalUtil.valueOf(curVal).add(BigDecimalUtil.valueOf(val)));
    }

    String getString(Object obj){
        if(ShareUtil.XObject.isEmpty(obj)){
            return null;
        }
        if(obj instanceof BigDecimal){
            return ((BigDecimal)val).toPlainString();
        }
        return obj.toString();
    }
}
