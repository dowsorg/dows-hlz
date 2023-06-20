package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * @author fhb
 * @version 1.0
 * @description 实验方案设计开始事件，方案设计组长分配完目录后触发， 通过该事件通知组内成员开始作答
 * @date 2023/6/20 0:17
 **/
public class ExptSchemeStartEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.exptSchemeStartEvent;

    public ExptSchemeStartEvent(Object source) {
        super(source);
    }

    public ExptSchemeStartEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
