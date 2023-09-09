package org.dows.hep.biz.eval.codec;

import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/9/6 15:20
 */
@Component
public class EvalIndicatorValuesCodec implements IRDStringCodec<EvalIndicatorValues>{

    private static volatile EvalIndicatorValuesCodec s_instance;

    public static EvalIndicatorValuesCodec Instance(){
        return s_instance;
    }

    private EvalIndicatorValuesCodec(){
        s_instance=this;
    }

    final static String SPLITChar="__";
    @Override
    public EvalIndicatorValues fromRDString(String str) {
        if (ShareUtil.XObject.isEmpty(str)) {
            return null;
        }
        EvalIndicatorValues rst = new EvalIndicatorValues();
        String[] vals = str.split(SPLITChar);
        if (ShareUtil.XObject.isEmpty(vals))
            return rst;
        return rst.setIndicatorId(toString(vals, 0))
                .setIndicatorName(toString(vals, 1))
                .setEvalNo(toInteger(vals, 2))
                .setCurVal(toString(vals, 3))
                .setLastVal(toString(vals, 4))
                .setPeriodInitVal(toString(vals, 5))
                .setChangingVal(toBigDecimal(vals, 6))
                .setChangedVal(toBigDecimal(vals, 7));

    }

    @Override
    public void appendRDString(StringBuilder sb,EvalIndicatorValues obj) {
        if (ShareUtil.XObject.isEmpty(obj)) {
            return ;
        }
        sb.append(toString(obj.getIndicatorId())).append(SPLITChar)
                .append(toString(obj.getIndicatorName())).append(SPLITChar)
                .append(toString(obj.getEvalNo())).append(SPLITChar)
                .append(toString(obj.getCurVal())).append(SPLITChar)
                .append(toString(obj.getLastVal())).append(SPLITChar)
                .append(toString(obj.getPeriodInitVal())).append(SPLITChar)
                .append(toString(obj.getChangingVal())).append(SPLITChar)
                .append(toString(obj.getChangedVal()));
    }
}
