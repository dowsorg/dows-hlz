package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;

public class ExperimentFollowupEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.experimentFollowupEvent;

    public ExperimentFollowupEvent(Object source) {
        super(source);
    }
}
