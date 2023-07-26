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

    private Object val;

    private BigDecimal valNumber;

    public Double getValdDouble(){
        if(ShareUtil.XObject.notNumber(val)){
            return null;
        }
        return Double.valueOf(val.toString());
    }
    public String getValString(){
        if(ShareUtil.XObject.isEmpty(val)){
            return null;
        }
        if(val instanceof BigDecimal){
            return ((BigDecimal)val).toString();
        }
        return val.toString();
    }
}
