package org.dows.hep.biz.eval.codec;

import org.dows.hep.biz.eval.data.EvalRiskValues;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/9/6 16:27
 */
@Component
public class EvalRiskValuesCodec implements IRDStringCodec<EvalRiskValues> {
    private static volatile EvalRiskValuesCodec s_instance;

    public static EvalRiskValuesCodec Instance(){
        return s_instance;
    }

    private EvalRiskValuesCodec(){
        s_instance=this;
    }
    final static String SPLITChar="__";

    @Override
    public EvalRiskValues fromRDString(String str) {
        if (ShareUtil.XObject.isEmpty(str)) {
            return null;
        }
        EvalRiskValues rst = new EvalRiskValues();
        String[] vals = str.split(SPLITChar);
        if (ShareUtil.XObject.isEmpty(vals))
            return rst;
        return rst.setCrowdId(toString(vals, 0))
                .setRiskId(toString(vals, 1))
                .setRiskName(toString(vals, 2))
                ;
    }

    @Override
    public void appendRDString(StringBuilder sb,EvalRiskValues obj) {
        if (ShareUtil.XObject.isEmpty(obj)) {
            return;
        }
        sb.append(toString(obj.getCrowdId())).append(SPLITChar)
                .append(toString(obj.getRiskId())).append(SPLITChar)
                .append(toString(obj.getRiskName()))
                ;

    }
}
