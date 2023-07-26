package org.dows.hep.api.event;


import lombok.Getter;

import java.io.Serializable;

/**
 * 实验间隔事件
 */
public class IntervalEvent extends ExperimentEvent implements Serializable {

    @Getter
    private EventName eventName = EventName.experimentIntervalEvent;

    public IntervalEvent(Object source) {
        super(source);
    }

}
