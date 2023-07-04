package org.dows.hep.biz.calc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("hepOperateRightCalculator")
public class HepOperateRightCalcultor implements Calculatable {
    @Override
    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {

    }

    @Override
    public void calc(String experimentInstanceId, String experimentGroupId, Integer period) {

    }
}
