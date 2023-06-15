package org.dows.hep.api.event;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public abstract class ExperimentEvent extends ApplicationEvent {

    public abstract EventName getEventName();

    public ExperimentEvent(Object source) {
        super(source);
    }

    public ExperimentEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
