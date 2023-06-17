package org.dows.hep.api.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 小组成员分配机构处理器
 */
@Slf4j
//@Component
public class GroupMemberAllotEvent extends ExperimentEvent implements Serializable {


    public GroupMemberAllotEvent(Object source) {
        super(source);
    }

    @Override
    public EventName getEventName() {
        return null;
    }
}
