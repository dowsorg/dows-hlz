package org.dows.hep.websocket;

import io.netty.channel.Channel;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@RestController
public class MsgController {

    @GetMapping("ws/send")
    public void sendMsg(String experimentId,String content){

        // 通知客户端
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
        Set<Channel> channels = userInfos.keySet();
        for (Channel channel : channels) {
            HepClientManager.sendInfoRetry(channel, MessageCode.MESS_CODE, Response.ok("ok"),null);
        }

    }
}
