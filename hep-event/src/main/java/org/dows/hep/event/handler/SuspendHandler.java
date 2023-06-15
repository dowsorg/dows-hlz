package org.dows.hep.event.handler;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * todo
 * 实验暂停事件，
 * 1.websocket 通知客户端，禁止操作，同时根据实验ID,服务端拦截器中禁用该实验ID的客户端请求
 * 2.服务端记录/更新实验暂停时间，ExperimentTimer
 * 3.停止相关的任务和事件的计时器
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SuspendHandler extends AbstractEventHandler implements EventHandler<ExperimentRestartRequest> {


    @Override
    public void exec(ExperimentRestartRequest experimentRestartRequest) {
        //todo 暂停定时器
        log.info("暂停定时器....");
        // 设置当前实验上下文信息
        ExperimentContext experimentContext = new ExperimentContext();
        experimentContext.setExperimentId(experimentRestartRequest.getExperimentInstanceId());
        experimentContext.setState(ExperimentStateEnum.SUSPEND);
        ExperimentContext.set(experimentContext);

        // 通知客户端
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
        Set<Channel> channels = userInfos.keySet();
        for (Channel channel : channels) {
            HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, experimentRestartRequest);
        }


    }
}