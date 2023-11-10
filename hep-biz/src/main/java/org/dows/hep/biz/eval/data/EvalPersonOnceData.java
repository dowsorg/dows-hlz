package org.dows.hep.biz.eval.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.biz.calc.RiskModelHealthIndexVO;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:15
 */
@Data
@Accessors(chain = true)
public class EvalPersonOnceData {

    @Schema(title = "header信息")
    private Header header;

    @Schema(title = "危险因素列表")
    private List<EvalRiskValues> risks;

    @Schema(title = "健康指数计算过程")
    private List<RiskModelHealthIndexVO> evalRisks;

    @Schema(title = "每期资金")
    private final ConcurrentMap<Integer, EvalIndicatorValues> mapPeriodMoney=new ConcurrentHashMap<>();

    @Schema(title = "指标列表")
    private final ConcurrentMap<String,EvalIndicatorValues> mapIndicators=new ConcurrentHashMap<>();

    @Schema(title = "判断操作")
    private final ConcurrentMap<String,Map<String, BigDecimal>> mapJudgeItems=new ConcurrentHashMap<>();

    @Schema(title = "兼容旧版指标列表")
    private final ConcurrentMap<String, ExperimentIndicatorValRsEntity> oldMapIndicators=new ConcurrentHashMap<>();

    public boolean isValued(){
        if(ShareUtil.XObject.anyEmpty(header,mapIndicators)){
            return false;
        }
        return ShareUtil.XObject.noneEmpty(header.evalNo,header.syncState);
    }
    public boolean isSynced(){
        if(null==header){
            return false;
        }
        return header.syncState==EnumEvalSyncState.SYNCED2DB;
    }
    public boolean isSyncing() {
        if (null == header || null == header.syncState) {
            return false;
        }
        return header.syncState == EnumEvalSyncState.SYNCING || header.syncState == EnumEvalSyncState.SYNCED2RD;
    }
    public boolean equalsEvalNo(Integer evalNo){
        return Optional.ofNullable(header)
                .map(Header::getEvalNo)
                .orElse(0)
                .equals(evalNo);
    }

    public boolean setSyncState(EnumEvalSyncState syncState){
        if(isSynced()){
            return false;
        }
        header.syncState=syncState;
        return true;
    }

    public boolean syncMoney(EvalIndicatorValues moneyVals){
        mapPeriodMoney.put(header.getPeriods(), moneyVals);
        return true;
    }
    public Map<String,BigDecimal> getJudgeItems(ExptOrgFuncRequest req){
        return mapJudgeItems.get(getJudgeItemsKey(req));
    }
    public boolean putJudgeItems(ExptOrgFuncRequest req, Map<String,BigDecimal> judgeItems){
        mapJudgeItems.put(getJudgeItemsKey(req),judgeItems);
        return true;
    }
    private String getJudgeItemsKey(ExptOrgFuncRequest req) {
        return String.format("%s-%s-%s-%s",req.getExperimentGroupId() , req.getExperimentPersonId(),
                req.getExperimentOrgId(), req.getIndicatorFuncId());
    }

    public EvalPersonOnceData flip(int evalNo,EnumEvalFuncType funcType){
        EvalPersonOnceData rst=new EvalPersonOnceData();
        rst.setHeader(new Header()
                .setEvalNo(evalNo)
                .setSyncState(EnumEvalSyncState.NEW)
                .setPeriods(funcType==EnumEvalFuncType.PERIODEnd?header.getPeriods()+1: header.periods)
                .setLastEvalDay(header.getEvalDay())
                .setHealthIndex(header.getHealthIndex())
                .setLastHealthIndex(header.getHealthIndex())
                .setMoney(header.getMoney())
                .setLastMoney(header.getMoney())
                .setEvalDay(header.getEvalDay())

        );
        rst.getMapPeriodMoney().putAll(this.getMapPeriodMoney());
        rst.getMapJudgeItems().putAll(this.getMapJudgeItems());
        rst.setRisks(ShareUtil.XCollection.map(risks,i->
                CopyWrapper.create(EvalRiskValues::new).endFrom(i)));
        getMapIndicators().forEach((k,v)->rst.getMapIndicators().put(k, v.flip(funcType)));
        rst. getOldMap(true);
        return rst;
    }

    public Map<String,ExperimentIndicatorValRsEntity> getOldMap(){
        return oldMapIndicators;
    }
    public Map<String,ExperimentIndicatorValRsEntity> getOldMap(boolean getNewly){
        if(getNewly) {
            mapIndicators.values().forEach(item -> {
                oldMapIndicators.computeIfAbsent(item.getIndicatorId(), k -> new ExperimentIndicatorValRsEntity().setIndicatorInstanceId(item.getIndicatorId()))
                        .setCurrentVal(item.getCurVal())
                        .setInitVal(item.getPeriodInitVal());
            });
        }
        return oldMapIndicators;
    }

    public Map<String,ExperimentIndicatorValRsEntity> getOldMap(Set<String> indicatorIds){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return getOldMap();
        }
        indicatorIds.forEach(i->{
            Optional.ofNullable(mapIndicators.get(i))
                    .ifPresent(v->oldMapIndicators.computeIfAbsent(v.getIndicatorId(), k -> new ExperimentIndicatorValRsEntity().setIndicatorInstanceId(v.getIndicatorId()))
                            .setCurrentVal(v.getCurVal())
                            .setInitVal(v.getPeriodInitVal()));
        });
        return oldMapIndicators;
    }
    public Map<String,String> fillCurVal(Map<String,String> mapCurVal,Set<String> indicatorIds){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return mapCurVal;
        }

        indicatorIds.forEach(i->{
            Optional.ofNullable(mapIndicators.get(i))
                    .ifPresent(v->mapCurVal.put(v.getIndicatorId(),v.getCurVal()));
        });
        return mapCurVal;

    }




    @Data
    @Accessors(chain = true)
    public static class Header {
        private final String appId="3";
        @Schema(title = "计算批次")
        private Integer evalNo;

        @Schema(title = "同步状态")
        private EnumEvalSyncState syncState;

        @Schema(title = "功能点类型")
        private EnumEvalFuncType funcType=EnumEvalFuncType.INIT;

        @Schema(title = "期数")
        private Integer periods;

        @Schema(title = "计算天数")
        private Integer evalDay;

        @Schema(title = "计算开始时间")
        private Date evalingTime;

        @Schema(title = "计算结束时间")
        private Date evaledTime;

        @Schema(title = "上次计算天数")
        private Integer lastEvalDay;

        @Schema(title = "当前健康指数")
        private String healthIndex;

        @Schema(title = "上次健康指数")
        private String lastHealthIndex;

        @Schema(title = "本次资金")
        private String money;

        @Schema(title = "上次资金")
        private String lastMoney;
        @Schema(title = "医疗占比")
        private String moneyScore;


        public int getLastDays(){
            if(ShareUtil.XObject.isEmpty(evalDay)){
                return 1;
            }
            return Math.max(1,evalDay-Optional.ofNullable(lastEvalDay)
                            .orElse(1));
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("appId='").append(appId).append('\'');
            sb.append(", evalNo=").append(evalNo);
            sb.append(", syncState=").append(syncState);
            sb.append(", funcType=").append(funcType);
            sb.append(", periods=").append(periods);
            sb.append(", evalDay=").append(evalDay);
            sb.append(", evalingTime=").append(evalingTime);
            sb.append(", evaledTime=").append(evaledTime);
            sb.append(", lastEvalDay=").append(lastEvalDay);
            sb.append(", healthIndex='").append(healthIndex);
            sb.append(", lastHealthIndex='").append(lastHealthIndex);
            sb.append(", money='").append(money);
            sb.append(", lastMoney='").append(lastMoney);
            sb.append(", moneyScore='").append(moneyScore);
            sb.append('}');
            return sb.toString();
        }
    }


}
