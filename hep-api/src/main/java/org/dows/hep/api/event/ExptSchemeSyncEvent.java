package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * @author fhb
 * @version 1.0
 * @description 实验方案设计同步事件，每个人保存方案设计的答案的时候触发，以通知组内成员拉取最新的答案
 * @date 2023/6/20 0:17
 **/
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
