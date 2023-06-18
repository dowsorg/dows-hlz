package org.dows.hep.api.event;

import lombok.Getter;

public enum EventName {
    startEvent("startHandler"),
    finishEvent("finishHandler"),
    suspendEvent("suspendHandler"),
    allotEvent("allotHandler"),
    groupEvent("groupHandler"),
    exptSchemeSyncEvent("exptSchemeSyncHandler"),

    createGroupEvent("createGroupHandler"),

    groupMemberAllotEvent("groupMemberAllotHandler"),

    ;


    @Getter
    String handler;

    EventName(String handler) {
        this.handler = handler;
    }
}
