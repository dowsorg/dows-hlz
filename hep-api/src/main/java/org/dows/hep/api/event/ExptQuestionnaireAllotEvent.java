package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * @author fhb
 * @version 1.0
 * @description 实验知识答题分配试卷事件，沙盘机构分组的时候触发该事件
 * @date 2023/6/20 0:16
 **/
public class ExptQuestionnaireAllotEvent extends ExperimentEvent implements Serializable  {
    @Getter
    private EventName eventName = EventName.exptQuestionnaireAllotEvent;

    public ExptQuestionnaireAllotEvent(Object source) {
        super(source);
    }

    public ExptQuestionnaireAllotEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
