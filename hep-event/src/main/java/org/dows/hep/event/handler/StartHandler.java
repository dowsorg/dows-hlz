package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * todo
 * 实验开始事件
 * 1.websocket 通知客户端，解除客户端操作限制
 * 2.服务端该实验记录ExperimentTimer
 * 3.恢复相关的任务，事件等的定时器
 */
@RequiredArgsConstructor
@Component
public class StartHandler extends AbstractEventHandler implements EventHandler{
    

}
