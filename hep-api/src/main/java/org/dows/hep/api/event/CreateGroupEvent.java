package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;

/**
 * 教师端创建小组事件
 */
public class CreateGroupEvent extends ExperimentEvent implements Serializable {


    @Getter
    private EventName eventName = EventName.createGroupEvent;

    public CreateGroupEvent(Object source) {
        super(source);
    }


}
