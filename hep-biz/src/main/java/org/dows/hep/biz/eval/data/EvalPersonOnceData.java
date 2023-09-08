package org.dows.hep.biz.eval.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.biz.calc.RiskModelHealthIndexVO;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Schema(title = "指标列表")
    private final ConcurrentMap<String,EvalIndicatorValues> mapIndicators=new ConcurrentHashMap<>();

    public boolean isValued(){
        if(ShareUtil.XObject.anyEmpty(header,mapIndicators)){
            return false;
        }
        return ShareUtil.XObject.allNotEmpty(header.evalNo,header.syncState);
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

    public EvalPersonOnceData flip(int evalNo,boolean isPeriodInit){
        EvalPersonOnceData rst=new EvalPersonOnceData();
        rst.setHeader(new Header()
                .setEvalNo(evalNo)
                .setSyncState(EnumEvalSyncState.NEW)
                .setPeriods(isPeriodInit?header.getPeriods()+1: header.periods)
                .setLastEvalDay(header.getEvalDay())
                .setLastHealthIndex(header.getHealthIndex())
                .setLastMoney(header.getMoney())
        );
        rst.setRisks(ShareUtil.XCollection.map(risks,i->
                CopyWrapper.create(EvalRiskValues::new).endFrom(i)));
        getMapIndicators().forEach((k,v)->rst.getMapIndicators().put(k, v.flip(isPeriodInit)));
        return rst;
    }


    @Data
    @Accessors(chain = true)
    public static class Header {
        @Schema(title = "计算批次")
        private Integer evalNo;

        @Schema(title = "同步状态")
        private EnumEvalSyncState syncState;

        @Schema(title = "功能点类型")
        private EnumEvalFuncType funcType;

        @Schema(title = "期数")
        private Integer periods;

        @Schema(title = "计算天数")
        private Integer evalDay;

        @Schema(title = "计算时间")
        private Date evalTime;

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
                return 0;
            }
            return Math.max(0,evalDay-Optional.ofNullable(lastEvalDay)
                            .orElse(0));
        }
    }


}
