package org.dows.hep.biz.user.experiment;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.websocket.HepClientManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : wuzl
 * @date : 2023/8/31 12:18
 */
@RequiredArgsConstructor
@Service
public class ToolBiz {

    public String getWebSocketState(String exptId){
        ConcurrentMap<Channel, AccountInfo> map=null;
        if(ShareUtil.XObject.notEmpty(exptId)){
            map=HepClientManager.getUserInfosByExperimentId(exptId);
        }else {
            map=HepClientManager.getUserInfos();
        }
        if(ShareUtil.XCollection.isEmpty(map)){
            return "[]";
        }
        Map<Integer,AccountInfo> dump=new HashMap<>();
        map.forEach((k,v)->dump.put(k.hashCode(),v));
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        AtomicReference<String> vExptId=new AtomicReference<>("");
        dump.forEach((k,v)->{
            if(sb.length()>0){
                sb.append(",");
            }
            sb.append("{");
            if(!vExptId.equals(v.getTenantName())&&ShareUtil.XObject.notEmpty(v.getTenantName()) ){
                sb.append("expt:").append(v.getTenantName());
                vExptId.set(v.getTenantName());
            }
            sb.append(" channel:").append(k);
            sb.append(" user:").append(v.getAccountName());
            sb.append("}");
        });
        sb.append("]");
        dump.clear();
        return sb.toString();
    }
}
