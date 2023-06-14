package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * todo
 * 实验暂停事件，
 * 1.websocket 通知客户端，禁止操作，同时根据实验ID,服务端拦截器中禁用该实验ID的客户端请求
 * 2.服务端记录/更新实验暂停时间，ExperimentTimer
 * 3.停止相关的任务和事件的计时器
 */
public class SuspendEvent extends ExperimentEvent implements Serializable {

    @Getter
    private EventName eventName = EventName.suspendEvent;
    public SuspendEvent(Object source) {
        super(source);
    }

    public SuspendEvent(Object source, Clock clock) {
        super(source, clock);
    }
}