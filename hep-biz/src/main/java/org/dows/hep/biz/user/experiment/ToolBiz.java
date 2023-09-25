package org.dows.hep.biz.user.experiment;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.base.indicator.request.RsCalculatePeriodsRequest;
import org.dows.hep.api.base.indicator.request.RsCalculatePersonRequestRs;
import org.dows.hep.api.base.indicator.request.RsExperimentCalculateFuncRequest;
import org.dows.hep.biz.eval.EvalHealthIndexBiz;
import org.dows.hep.biz.eval.EvalPersonBiz;
import org.dows.hep.biz.eval.EvalPersonIndicatorBiz;
import org.dows.hep.biz.event.PersonBasedEventTask;
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

    public String ping(){

       return "0925-b";
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

    public void raiseevent(RsCalculatePersonRequestRs req)  {
        PersonBasedEventTask.runPersonBasedEventAsync(req.getAppId(), req.getExperimentId(),
                Optional.ofNullable(req.getPersonIdSet()).map(i->i.toArray((String[])null)).orElse(null));
    }
}
