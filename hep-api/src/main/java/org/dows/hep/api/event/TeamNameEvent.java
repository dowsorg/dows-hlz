package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * @author jx
 * @date 2023/6/19 14:26
 */
public class TeamNameEvent extends ExperimentEvent implements Serializable {

    @Getter
    private EventName eventName = EventName.teamName;
    public TeamNameEvent(Object source) {
        super(source);
    }

    public TeamNameEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
