package org.dows.hep.biz.user.experiment;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.base.indicator.request.RsCalculateMoneyScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculatePeriodsRequest;
import org.dows.hep.api.base.indicator.request.RsCalculatePersonRequestRs;
import org.dows.hep.api.base.indicator.request.RsExperimentCalculateFuncRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.eval.*;
import org.dows.hep.biz.event.PersonBasedEventTask;
import org.dows.hep.biz.spel.SpelEngine;
import org.dows.hep.biz.spel.SpelPersonContext;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.websocket.HepClientManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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

    public String ping(){
        String exptId="393869331617943552";
        String exptPersonId="393869335258599424";
        String caseEventId="393190502746427392";
        SpelPersonContext context = new SpelPersonContext().setVariables(exptPersonId, 1);
        //Object val= spelContext.lookupVariable("388930656673075362");

        boolean rst= SpelEngine.Instance().loadFromSnapshot()
                .withReasonId(exptId, exptPersonId, caseEventId,
                        EnumIndicatorExpressionSource.EMERGENCY_TRIGGER_CONDITION.getSource())
                .check(context);
        SpelEngine.Instance().loadFromSnapshot()
                .withReasonId(exptId, exptPersonId, List.of(caseEventId),
                        EnumIndicatorExpressionSource.EMERGENCY_TRIGGER_CONDITION.getSource())
                .check(context);

        return "1022";
    }

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
        evalScoreRankBiz.rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs
                .builder()
                .experimentId(req.getExperimentId())
                .periods(req.getPeriods())
                .build());
    }

    public void raiseevent(RsCalculatePersonRequestRs req)  {
        PersonBasedEventTask.runPersonBasedEventAsync(req.getAppId(), req.getExperimentId(),
                Optional.ofNullable(req.getPersonIdSet()).map(i->i.toArray((String[])null)).orElse(null));
    }
}
