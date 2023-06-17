package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

public class ExptSchemeSyncEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.exptSchemeSyncEvent;


    public ExptSchemeSyncEvent(Object source) {
        super(source);
    }

    public ExptSchemeSyncEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
