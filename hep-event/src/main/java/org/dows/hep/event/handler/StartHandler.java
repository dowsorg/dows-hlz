package org.dows.hep.event.handler;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.event.EventName;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * todo
 * 实验开始事件
 * 1.websocket 通知客户端，解除客户端操作限制
 * 2.服务端该实验记录ExperimentTimer
 * 3.恢复相关的任务，事件等的定时器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StartHandler extends AbstractEventHandler implements EventHandler{


    @Override
    public void exec(Object obj) {
        //todo 定时器
        log.info("开启定时....");
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();

        Set<Channel> channels = userInfos.keySet();

        for (Channel channel : channels) {
            HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, obj);
        }
    }


}
