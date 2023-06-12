package org.dows.hep.event;

import lombok.Data;

import java.io.Serializable;
import java.time.Clock;

/**
 * todo
 * 实验开始事件
 * 1.websocket 通知客户端，接触客户端操作限制
 * 2.服务端该实验记录ExperimentTimer
 * 3.恢复相关的任务，事件等的定时器
 */
public class StartEvent extends ExperimentEvent implements Serializable {
    public StartEvent(Object source) {
        super(source);
    }

    public StartEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
