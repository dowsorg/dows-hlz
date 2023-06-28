package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * 实验就绪事件
 */
public class ExperimentReadyEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.experimentReadyEvent;

    public ExperimentReadyEvent(Object source) {
        super(source);
    }

    public ExperimentReadyEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
