package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * todo
 * 实验暂停事件，
 * 1.websocket 通知客户端，禁止操作，同时根据实验ID,服务端拦截器中禁用该实验ID的客户端请求
 * 2.服务端记录/更新实验暂停时间，ExperimentTimer
 * 3.停止相关的任务和事件的计时器
 */
@RequiredArgsConstructor
@Component
public class SuspendHandler extends AbstractEventHandler implements EventHandler {


    @Override
    public void exec(Object obj) {
        EventHandler.super.exec(obj);
    }
}