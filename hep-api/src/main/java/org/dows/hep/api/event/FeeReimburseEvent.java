package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * @author jx
 * @date 2023/7/25 19:15
 */
public class FeeReimburseEvent extends ExperimentEvent implements Serializable {

    @Getter
    private EventName eventName = EventName.feeReimburseEvent;

    public FeeReimburseEvent(Object source) {
        super(source);
    }

    public FeeReimburseEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
