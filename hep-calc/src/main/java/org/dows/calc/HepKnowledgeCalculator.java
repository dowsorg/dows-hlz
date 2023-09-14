package org.dows.calc;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.calc.Calculatable;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.springframework.stereotype.Component;


@Component("hepKnowledgeCalculator")
@RequiredArgsConstructor
public class HepKnowledgeCalculator implements Calculatable {
//    private final ExperimentQuestionnaireScoreBiz experimentQuestionnaireScoreBiz;
    @Override
    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {
       // experimentQuestionnaireScoreBiz.calculateExptQuestionnaireScore(experimentScoreCalcRequest.getExperimentInstanceId(), experimentScoreCalcRequest.getPeriod());
    }

    @Override
    public void calc(String experimentInstanceId, String experimentGroupId, Integer period) {
        //experimentQuestionnaireScoreBiz.calculateExptQuestionnaireScore(experimentInstanceId, period);
    }
}
