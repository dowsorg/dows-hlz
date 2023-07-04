package org.dows.hep.biz.calc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("hepKnowledgeCalculator")
public class HepKnowledgeCalculator implements Calculatable {

    private final ExperimentScoringBiz experimentScoringBiz;
    @Override
    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {

    }

    @Override
    public void calc(String experimentInstanceId, String experimentGroupId, Integer period) {

    }
}
