package org.dows.hep.api.event;

import lombok.Getter;

public enum EventName {
    startEvent("experimentStartHandler"),
    finishEvent("experimentFinishHandler"),
    suspendEvent("experimentSuspendHandler"),
//    allotEvent("allotHandler"),
    experimentInitEvent("experimentInitHandler"),
    exptQuestionnaireAllotEvent("exptQuestionnaireAllotHandler"),
    exptQuestionnaireSubmittedEvent("exptQuestionnaireSubmittedHandler"),
    exptSchemeStartEvent("exptSchemeStartHandler"),
    exptSchemeSubmittedEvent("exptSchemeSubmittedHandler"),
    exptSchemeSyncEvent("exptSchemeSyncHandler"),
    createGroupEvent("createGroupHandler"),
    groupMemberAllotEvent("groupMemberAllotHandler"),
    teamName("teamNameHandler"),
    copyExperimentPersonAndOrgEvent("copyExperimentPersonAndOrgHandler"),
    feeReimburseEvent("feeReimburseHandler"),
    experimentReadyEvent("experimentReadyHandler"),
    exptEventTriggeredHandler("commonWebSocketEventHandler"),
    experimentFollowupEvent("experimentFollowupHandler"),

    experimentIntervalEvent("experimentIntervalHandler")
    ;


    @Getter
    String handler;

    EventName(String handler) {
        this.handler = handler;
    }
}
