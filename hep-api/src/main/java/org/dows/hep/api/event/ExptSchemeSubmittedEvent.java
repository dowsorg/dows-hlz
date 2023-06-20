package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * @author fhb
 * @version 1.0
 * @description 实验方案设计提交事件，组长提交方案设计的时候触发，以通知组内成员作答已结束
 * @date 2023/6/20 0:17
 **/
public class ExptSchemeSubmittedEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.exptSchemeSubmittedEvent;

    public ExptSchemeSubmittedEvent(Object source) {
        super(source);
    }

    public ExptSchemeSubmittedEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
