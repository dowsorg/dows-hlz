//package org.dows.hep.event.handler;
//
//import cn.hutool.json.JSONUtil;
//import io.netty.channel.Channel;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.framework.api.uim.AccountInfo;
//import org.dows.hep.entity.ExperimentTimerEntity;
//import org.dows.hep.websocket.HepClientManager;
//import org.dows.hep.websocket.proto.MessageCode;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentMap;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class AllotHandler extends AbstractEventHandler implements EventHandler<List<ExperimentTimerEntity>> {
//
//
//    @Override
//    public void exec(List<ExperimentTimerEntity> experimentTimerEntities) {
//
//
//        // todo 启动定时器  创建开始定时通知
//
//
//
//        //todo 定时器
//        log.info("分配实验事件：{}", JSONUtil.toJsonStr(experimentTimerEntities));
//        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
//
//        Set<Channel> channels = userInfos.keySet();
//
//        for (Channel channel : channels) {
//            HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, experimentTimerEntities);
//        }
//
//
//    }
//}
