package org.dows.hep.biz.spel;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRefCache;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/9/14 10:47
 */
@Slf4j
public class SnapshotRefValidator {

    public SnapshotRefValidator(String experimentId){
        this.experimentId=experimentId;
        this.appId= ShareBiz.checkAppId(null,experimentId);
    }

    private final String appId;
    private final String experimentId;

    private Optional<String> optExpressionRef;
    private Optional<String> optExpression;
    private Optional<String> optExpressionItem;

    private Optional<String> optCrowd;

    private Optional<String> optRiskModel;

    private Optional<String> optTreatItem;


    public String getExperimentId(){
        return experimentId;
    }

    //region expression

    public SnapshotRefValidator checkExpressionRef(){
        if(ShareUtil.XObject.isEmpty(getExpressionRefId())){
            logError("SnapshotRefValidator", "miss snapshot4ExpressionRef. experimentId:%s",experimentId);
        }
        return this;
    }
    public String getExpressionRefId(){
        return getExpressionRef().orElse("");
    }

    public Optional<String> getExpressionRef(){
        if(null==optExpressionRef){
            optExpressionRef=Optional.ofNullable(SnapshotRefCache.Instance().getRefExperimentId(appId, EnumSnapshotType.CASEIndicatorExpressionRef,experimentId));
        }
        return optExpressionRef;
    }

    public SnapshotRefValidator checkExpression(){
        if(ShareUtil.XObject.isEmpty(getExpressionId())){
            logError("SnapshotRefValidator", "miss snapshot4Expression. experimentId:%s",experimentId);
        }
        return this;
    }
    public String getExpressionId(){
        return getExpression().orElse("");
    }

    public Optional<String> getExpression(){
        if(null==optExpression){
            optExpression=Optional.ofNullable(SnapshotRefCache.Instance().getRefExperimentId(appId,EnumSnapshotType.CASEIndicatorExpression,experimentId));
        }
        return optExpression;
    }

    public SnapshotRefValidator checkExpressionItem(){
        if(ShareUtil.XObject.isEmpty(getExpressionItemId())){
            logError("SnapshotRefValidator", "miss snapshot4ExpressionItem. experimentId:%s",experimentId);
        }
        return this;
    }
    public String getExpressionItemId(){
        return getExpressionItem().orElse("");
    }
    public Optional<String> getExpressionItem(){
        if(null==optExpressionItem){
            optExpressionItem=Optional.ofNullable(SnapshotRefCache.Instance().getRefExperimentId(appId,EnumSnapshotType.CASEIndicatorExpressionItem,experimentId));
        }
        return optExpressionItem;
    }
    //endregion

    //region crowd
    public SnapshotRefValidator checkCrowd(){
        if(ShareUtil.XObject.isEmpty(getCrowdId())){
            logError("SnapshotRefValidator", "miss snapshot4Crowd. experimentId:%s",experimentId);
        }
        return this;
    }
    public String getCrowdId(){
        return getCrowd().orElse("");
    }
    public Optional<String> getCrowd(){
        if(null==optCrowd){
            optCrowd=Optional.ofNullable(SnapshotRefCache.Instance().getRefExperimentId(appId,EnumSnapshotType.CROWD,experimentId));
        }
        return optCrowd;
    }

    public SnapshotRefValidator checkRiskModel(){
        if(ShareUtil.XObject.isEmpty(getRiskModelId())){
            logError("SnapshotRefValidator", "miss snapshot4RiskModel. experimentId:%s",experimentId);
        }
        return this;
    }
    public String getRiskModelId(){
        return getRiskModel().orElse("");
    }
    public Optional<String> getRiskModel(){
        if(null==optRiskModel){
            optRiskModel=Optional.ofNullable(SnapshotRefCache.Instance().getRefExperimentId(appId,EnumSnapshotType.RISKModel,experimentId));
        }
        return optRiskModel;
    }
    //endregion

    //region treatItem
    public SnapshotRefValidator checkTreatItem(){
        if(ShareUtil.XObject.isEmpty(getTreatItemId())){
            logError("SnapshotRefValidator", "miss snapshot4TreatItem. experimentId:%s",experimentId);
        }
        return this;
    }
    public String getTreatItemId(){
        return getTreatItem().orElse("");
    }
    public Optional<String> getTreatItem(){
        if(null==optTreatItem){
            optTreatItem=Optional.ofNullable(SnapshotRefCache.Instance().getRefExperimentId(appId,EnumSnapshotType.TreatItem,experimentId));
        }
        return optTreatItem;
    }
    //endregion


    protected void logError(String func, String msg,Object... args){
        logError(null, func, msg, args);
    }
    protected void logError(Throwable ex, String func, String msg,Object... args){
        String str=String.format("%s.%s@%s[%s] %s", this.getClass().getName(), func, LocalDateTime.now(),this.hashCode(),
                String.format(Optional.ofNullable(msg).orElse(""), args));
        log.error(str,ex);

    }
}
