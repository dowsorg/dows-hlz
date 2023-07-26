package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * 实验就绪事件
 */
public class ReadyEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.experimentReadyEvent;

    public ReadyEvent(Object source) {
        super(source);
    }

    public ReadyEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
