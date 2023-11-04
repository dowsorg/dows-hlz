package org.dows.hep.biz.user.experiment;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.base.indicator.request.RsCalculatePeriodsRequest;
import org.dows.hep.api.base.indicator.request.RsCalculatePersonRequestRs;
import org.dows.hep.api.base.indicator.request.RsExperimentCalculateFuncRequest;
import org.dows.hep.biz.eval.*;
import org.dows.hep.biz.event.PersonBasedEventTask;
import org.dows.hep.biz.spel.SpelInvoker;
import org.dows.hep.biz.spel.SpelPersonContext;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.websocket.HepClientManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : wuzl
 * @date : 2023/8/31 12:18
 */
@RequiredArgsConstructor
@Service
public class ToolBiz {

    private final EvalHealthIndexBiz evalHealthIndexBiz;

    private final EvalPersonIndicatorBiz evalPersonIndicatorBiz;

    private final EvalPersonBiz evalPersonBiz;

    private final EvalScoreRankBiz evalScoreRankBiz;

    private final EvalJudgeScoreBiz evalJudgeScoreBiz;

    private final EvalPersonMoneyBiz evalPersonMoneyBiz;

    public String ping(){
        String exptId="396527169049858048";
       /* ExperimentCacheKey exptKey=ExperimentCacheKey.create("3", exptId);
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(exptKey,true);
        LocalDateTime ldtNow=LocalDateTime.of(2023, 11, 2, 20, 31,47);
        ExperimentTimePoint timePoint= ExperimentSettingCache.getTimePointByRealTimeSilence(exptColl, exptKey, ldtNow, true);

        LocalDateTime nextTime= calcTriggeringTime(timePoint,exptColl,139);
        nextTime= calcTriggeringTime(timePoint,exptColl,139);
        int todoDay=139;
        Integer nextDay=calcNextTodoDay(timePoint,exptColl,30,109, 139);
        nextDay=calcNextTodoDay(timePoint,exptColl,30,109, 139);
        nextDay=calcNextTodoDay(timePoint,exptColl,30,109, 139);*/

        SpelPersonContext spelContext = new SpelPersonContext().setVariables("396527224976707584", 1);
        String eventId="368233096589479940";
        boolean triggered=SpelInvoker.Instance().checkEventCondition(exptId, "396527224976707584",
                eventId, spelContext);
        triggered=SpelInvoker.Instance().checkEventCondition(exptId, "396527224976707584",
                eventId, spelContext);


        return "1102-c";
    }

   /* LocalDateTime calcTriggeringTime(ExperimentTimePoint timePoint, ExperimentSettingCollection exptColl, Integer todoDays){
        if(ShareUtil.XObject.anyEmpty(timePoint,todoDays)){
            return null;
        }
        if(exptColl.getTotalDays()< todoDays){
            return null;
        }
        if(todoDays<= timePoint.getGameDay()){
            return timePoint.getRealTime();
        }
        Integer rawSeconds=  exptColl.getRawSecondsByGameDay(todoDays);
        return exptColl.getSandStartTime().plusSeconds(rawSeconds+timePoint.getCntPauseSeconds());

    }
    Integer calcNextTodoDay(ExperimentTimePoint timePoint, ExperimentSettingCollection exptColl, int dueDays,int setAtDay,int todoDay){
        int curDay=Math.max(timePoint.getGameDay(), todoDay);
        final int curTimes=Math.max(0,(curDay-setAtDay)/dueDays);
        int nextTodoDay=0;
        for(int i=1;i<10;i++){
            nextTodoDay=setAtDay+(curTimes+i)*dueDays;
            if(curDay<nextTodoDay){
                break;
            }
        }
        if(todoDay<= timePoint.getGameDay()){
            nextTodoDay+=dueDays;
        }
        if(nextTodoDay<=exptColl.getTotalDays()){
            return nextTodoDay;
        }
        return null;
    }*/

    public String getWebSocketState(String exptId){
        Map<Channel, AccountInfo> map=null;
        if(ShareUtil.XObject.notEmpty(exptId)){
            map=new HashMap<>();
            map.putAll(HepClientManager.getUserInfosByExperimentId(exptId));
        }else {
            map=HepClientManager.getUserInfos();
        }
        int cntUser=HepClientManager.getAuthUserCount();
        int cntMsg=HepClientManager.getMsgCount();
        StringBuilder sb=new StringBuilder();
        sb.append(" cntUser:").append(cntUser)
                .append(" cntMsg:").append(cntMsg)
                .append( "users:");

        if(ShareUtil.XCollection.isEmpty(map)){
            return sb.append("[]").toString() ;
        }
        sb.append("[");
        AtomicReference<String> vExptId=new AtomicReference<>("");
        map.forEach((k,v)->{
            sb.append("{");
            if(!vExptId.equals(v.getTenantName())&&ShareUtil.XObject.notEmpty(v.getTenantName()) ){
                sb.append("expt:").append(v.getTenantName());
                vExptId.set(v.getTenantName());
            }
            sb.append(" channel:").append(k.hashCode());
            sb.append(" user:").append(v.getAccountName());
            sb.append("},");
        });
        sb.append("]");
        return sb.toString();
    }

    public void evalOrgFunc(RsExperimentCalculateFuncRequest req) {

        evalPersonBiz.evalOrgFunc(req);

    }

    public void evalPeriodEnd(RsCalculatePeriodsRequest req)  {

        evalPersonBiz.evalPeriodEnd(req);
    }

    public void evalPeriodEndScore(RsCalculatePeriodsRequest req){
        evalScoreRankBiz.saveOrUpd(req.getExperimentId(),req.getPeriods());
    }

    public void evalPeriodJudgeScore(RsCalculatePeriodsRequest req){
        evalJudgeScoreBiz.evalJudgeScore4Period(req.getExperimentId(),req.getPeriods());
    }
    public void evalPeriodMoneyScore(RsCalculatePeriodsRequest req){

        evalPersonMoneyBiz.evalMoneyScore4Period(req.getExperimentId(),req.getPeriods());

    }

    public void raiseevent(RsCalculatePersonRequestRs req)  {
        PersonBasedEventTask.runPersonBasedEventAsync(req.getAppId(), req.getExperimentId(),
                Optional.ofNullable(req.getPersonIdSet()).map(i->i.toArray((String[])null)).orElse(null));
    }
}
