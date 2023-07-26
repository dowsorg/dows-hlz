package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;

/**
 * 随访事件
 */
public class FollowupEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.experimentFollowupEvent;

    public FollowupEvent(Object source) {
        super(source);
    }
}
