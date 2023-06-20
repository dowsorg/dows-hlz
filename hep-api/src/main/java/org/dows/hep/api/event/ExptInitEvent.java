package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * @author fhb
 * @version 1.0
 * @description 实验初始化事件，创建实验并分组的时候触发该事件
 * @date 2023/6/20 0:16
 **/
public class ExptInitEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.exptInitEvent;

    public ExptInitEvent(Object source) {
        super(source);
    }

    public ExptInitEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
