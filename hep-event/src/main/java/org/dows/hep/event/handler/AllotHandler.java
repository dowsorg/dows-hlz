package org.dows.hep.event.handler;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllotHandler extends AbstractEventHandler implements EventHandler {


    @Override
    public void exec(Object obj) {

        //todo 定时器
        log.info("开启调度....");
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();

        Set<Channel> channels = userInfos.keySet();

        for (Channel channel : channels) {
            HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, obj);
        }


    }
}
