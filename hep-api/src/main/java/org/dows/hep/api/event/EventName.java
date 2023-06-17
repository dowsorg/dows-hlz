package org.dows.hep.api.event;

import lombok.Getter;

public enum EventName {
    startEvent("startHandler"),
    finishEvent("finishHandler"),
    suspendEvent("suspendHandler"),
    allotEvent("allotHandler"),
    groupEvent("groupEvent"),
    exptSchemeSyncEvent("exptSchemeSyncEvent"),

    ;


    @Getter
    String handler;

    EventName(String handler) {
        this.handler = handler;
    }
}
