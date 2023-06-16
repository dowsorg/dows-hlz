package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

public class GroupEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.groupEvent;

    public GroupEvent(Object source) {
        super(source);
    }

    public GroupEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
