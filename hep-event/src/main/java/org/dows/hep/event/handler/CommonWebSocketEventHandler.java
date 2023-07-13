package org.dows.hep.event.handler;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.WsMessageResponse;
import org.dows.hep.api.event.CommonWebSocketEventSource;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wuzl
 * @date : 2023/7/11 11:54
 */
@Component
@Slf4j
public class CommonWebSocketEventHandler<T> extends AbstractEventHandler implements EventHandler<CommonWebSocketEventSource<T>> {

    @Override
    public void exec(CommonWebSocketEventSource<T> obj) {
        sendWebSocketData(obj);
    }

    public static <T> int sendWebSocketData(CommonWebSocketEventSource<T> src){
        if(ShareUtil.XObject.isEmpty(src)){
            return -1;
        }
        if(ShareUtil.XObject.anyEmpty(src.getExperimentInstanceId(),src.getClientIds())){
            return -1;
        }
        ConcurrentMap<Channel, AccountInfo> clients= HepClientManager.getUserInfosByExperimentId(src.getExperimentInstanceId());
        if(ShareUtil.XObject.isEmpty(clients)){
            return -1;
        }
        WsMessageResponse wsMsg= WsMessageResponse.builder()
                .type(src.getSocketType())
                .data(src.getData())
                .build();
        Response<WsMessageResponse> wsPack=Response.ok(wsMsg);
        final Set<String> clientIds=src.getClientIds();
        Set<String> sended=new HashSet<>();
        for(Map.Entry<Channel, AccountInfo> item : clients.entrySet()){
            if(!clientIds.contains(item.getValue().getAccountName())){
                continue;
            }
            sended.add(item.getValue().getAccountName());
            HepClientManager.sendInfo(item.getKey(), MessageCode.MESS_CODE, wsPack);

        }
        log.info(String.format("CommonWebSocketEventHandler.sendWebSocketData total:%s sended:%s sendedIds:%",
                clientIds.size(),sended.size(),String.join(",", sended)));
        return sended.size();

    }
}
