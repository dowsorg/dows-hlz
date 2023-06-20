package org.dows.hep.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.event.source.ExptQuestionnaireSubmittedEventSource;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @version 1.0
 * @description 每期完成的时候 `publish` 该事件，以完成 `知识答题` 的提交
 * @date 2023/6/19 23:18
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class ExptQuestionnaireSubmittedHandler extends AbstractEventHandler implements EventHandler<ExptQuestionnaireSubmittedEventSource> {

    private final ExperimentQuestionnaireBiz experimentQuestionnaireBiz;

    @Override
    public void exec(ExptQuestionnaireSubmittedEventSource obj) {
        String experimentQuestionnaireId = obj.getExperimentQuestionnaireId();
        String accountId = obj.getAccountId();
        experimentQuestionnaireBiz.submitQuestionnaire(experimentQuestionnaireId, accountId);
    }
}
