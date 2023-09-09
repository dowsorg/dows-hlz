package org.dows.hep.biz.eval.codec;

import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.biz.eval.data.EnumEvalSyncState;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.eval.data.EvalPersonOnceData;
import org.dows.hep.biz.eval.data.EvalRiskValues;
import org.dows.hep.biz.util.ShareUtil;
import org.redisson.api.RMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/9/6 15:21
 */
@Component
public class EvalPersonOnceDataCodec implements IRDMapCodec<EvalPersonOnceData>{
    private static volatile EvalPersonOnceDataCodec s_instance;

    public static EvalPersonOnceDataCodec Instance(){
        return s_instance;
    }

    public static HeaderCodec headerCodec() {
        return HeaderCodec.instance;
    }

    private EvalPersonOnceDataCodec(){
        s_instance=this;
    }
    public final static String HASHKey4Header="__header";
    public final static String HASHkey4Risks="__risks";

    final static String SPLITRisks="~~";

    @Override
    public EvalPersonOnceData fromRDMap(RMap<String, String> map) {
        if(null==map){
            return null;
        }
        EvalPersonOnceData rst=new EvalPersonOnceData();
        rst.getMapIndicators().clear();
        map.forEach((k,v)->{
            if(HASHKey4Header.equals(k)){
                rst.setHeader(HeaderCodec.instance.fromRDString(v));
                return;
            }
            if(HASHkey4Risks.equals(k)){
                rst.setRisks(toRisks(v));
                return;
            }
            EvalIndicatorValues obj=EvalIndicatorValuesCodec.Instance().fromRDString(v);
            if(null==obj){
                return;
            }
            rst.getMapIndicators().put(obj.getIndicatorId(),obj);
        });
        return rst;
    }

    @Override
    public Map<String, String> toRDMap(EvalPersonOnceData obj) {
        if (null == obj) {
            return null;
        }
        Map<String, String> rst = new HashMap<>();
        if (null != obj.getHeader()) {
            rst.put(HASHKey4Header, HeaderCodec.instance.toRDString(obj.getHeader()));
        }
        if (null != obj.getRisks()) {
            rst.put(HASHkey4Risks, fromRisks(obj.getRisks()));
        }
        if (null != obj.getMapIndicators()) {
            obj.getMapIndicators().values().forEach(item -> rst.put(item.getIndicatorId(),
                    EvalIndicatorValuesCodec.Instance().toRDString(item)));
        }
        return rst;
    }
    public List<EvalRiskValues> toRisks(String str){
        if(null==str){
            return null;
        }
        List<EvalRiskValues> rst=new ArrayList<>();
        String[] vals= str.split(SPLITRisks);
        if(ShareUtil.XObject.isEmpty(vals)){
            return rst;
        }
        for(String item:vals){
            EvalRiskValues obj=EvalRiskValuesCodec.Instance().fromRDString(item);
            if(null==obj){
                continue;
            }
            rst.add(obj);
        }
        return rst;
    }
    public String fromRisks(List<EvalRiskValues> list){
        if(ShareUtil.XObject.isEmpty(list)){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        list.forEach(item->{
            if(sb.length()>0){
                sb.append(SPLITRisks);
            }
            EvalRiskValuesCodec.Instance().appendRDString(sb,item);

        });
        String rst = sb.toString();
        sb.delete(0, sb.length());
        return rst;
    }



    public static class HeaderCodec implements IRDStringCodec<EvalPersonOnceData.Header> {

        private final static HeaderCodec instance=new HeaderCodec();

        final static String SPLITChar="__";
        @Override
        public EvalPersonOnceData.Header fromRDString(String str) {
            if (ShareUtil.XObject.isEmpty(str)) {
                return null;
            }
            EvalPersonOnceData.Header rst = new EvalPersonOnceData.Header();
            String[] vals = str.split(SPLITChar);
            if (ShareUtil.XObject.isEmpty(vals))
                return rst;
            return rst.setEvalNo(toInteger(vals, 0))
                    .setSyncState(EnumEvalSyncState.of(toInteger(vals, 1)))
                    .setFuncType(EnumEvalFuncType.of(toInteger(vals, 2)))
                    .setPeriods(toInteger(vals, 3))
                    .setEvalDay(toInteger(vals, 4))
                    .setEvalingTime(toDate(vals, 5))
                    .setEvaledTime(toDate(vals, 6))
                    .setLastEvalDay(toInteger(vals, 7))
                    .setHealthIndex(toString(vals, 8))
                    .setLastHealthIndex(toString(vals, 9))
                    .setMoney(toString(vals, 10))
                    .setLastMoney(toString(vals, 11))
                    .setMoneyScore(toString(vals, 12))
                    ;

        }


        @Override
        public void appendRDString(StringBuilder sb, EvalPersonOnceData.Header obj) {
            if (ShareUtil.XObject.isEmpty(obj)) {
                return;
            }
            sb.append(toString(obj.getEvalNo())).append(SPLITChar)
                    .append(toString(obj.getSyncState().getCode())).append(SPLITChar)
                    .append(toString(obj.getFuncType().getCode())).append(SPLITChar)
                    .append(toString(obj.getPeriods())).append(SPLITChar)
                    .append(toString(obj.getEvalDay())).append(SPLITChar)
                    .append(toString(obj.getEvalingTime())).append(SPLITChar)
                    .append(toString(obj.getEvaledTime())).append(SPLITChar)
                    .append(toString(obj.getLastEvalDay())).append(SPLITChar)
                    .append(toString(obj.getHealthIndex())).append(SPLITChar)
                    .append(toString(obj.getLastHealthIndex())).append(SPLITChar)
                    .append(toString(obj.getMoney())).append(SPLITChar)
                    .append(toString(obj.getLastMoney())).append(SPLITChar)
                    .append(toString(obj.getMoneyScore()))
            ;
        }
    }
}
