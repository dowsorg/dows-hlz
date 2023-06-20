package org.dows.hep.api.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;

/**
 * @author fhb
 * @version 1.0
 * @description 实验知识答题提交事件，每一期结束的时候触发该事件，通过该事件触发试卷的提交
 * @date 2023/6/20 0:16
 **/
public class ExptQuestionnaireSubmittedEvent extends ExperimentEvent implements Serializable {
    @Getter
    private EventName eventName = EventName.exptQuestionnaireSubmittedEvent;

    public ExptQuestionnaireSubmittedEvent(Object source) {
        super(source);
    }

    public ExptQuestionnaireSubmittedEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
