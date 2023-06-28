package org.dows.hep.api.event;

import lombok.Getter;

public enum EventName {
    startEvent("startHandler"),
    finishEvent("finishHandler"),
    suspendEvent("suspendHandler"),
    allotEvent("allotHandler"),
    exptInitEvent("exptInitHandler"),
    exptQuestionnaireAllotEvent("exptQuestionnaireAllotHandler"),
    exptQuestionnaireSubmittedEvent("exptQuestionnaireSubmittedHandler"),
    exptSchemeStartEvent("exptSchemeStartHandler"),
    exptSchemeSubmittedEvent("exptSchemeSubmittedHandler"),
    exptSchemeSyncEvent("exptSchemeSyncHandler"),
    createGroupEvent("createGroupHandler"),
    groupMemberAllotEvent("groupMemberAllotHandler"),
    teamName("teamNameHandler"),
    copyExperimentPersonAndOrgEvent("copyExperimentPersonAndOrgHandler"),

    experimentReadyEvent("experimentReadyHandler"),
    ;


    @Getter
    String handler;

    EventName(String handler) {
        this.handler = handler;
    }
}
