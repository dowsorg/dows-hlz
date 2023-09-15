package org.dows.calc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.Calculatable;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.springframework.stereotype.Component;
@Slf4j
@RequiredArgsConstructor
@Component("hepHealthIndexCalculator")
public class HepHealthIndexCalculator implements Calculatable {


    @Override
    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {

    }

    @Override
    public void calc(String experimentInstanceId, String experimentGroupId, Integer period) {

    }
}
