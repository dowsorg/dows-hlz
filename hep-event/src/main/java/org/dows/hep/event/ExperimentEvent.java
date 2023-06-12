package org.dows.hep.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public abstract class ExperimentEvent extends ApplicationEvent {
    @Setter
    @Getter
    protected EventName eventName;

    public ExperimentEvent(Object source) {
        super(source);
    }

    public ExperimentEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
