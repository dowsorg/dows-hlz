package org.dows.hep.biz.calc;

import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.springframework.stereotype.Component;


@Component("hepKnowledgeCalculator")
public class HepKnowledgeCalculator implements Calculatable {
    @Override
    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {

    }

    @Override
    public void calc(String experimentInstanceId, String experimentGroupId, Integer period) {

    }
}
