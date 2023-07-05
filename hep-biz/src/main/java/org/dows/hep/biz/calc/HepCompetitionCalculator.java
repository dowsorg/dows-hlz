package org.dows.hep.biz.calc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.springframework.stereotype.Component;

/**
 * 健康指数得分OR竞赛得分
 */
@Slf4j
@RequiredArgsConstructor
@Component("hepHealthIndexCalculator")
public class HepCompetitionCalculator implements Calculatable {


    @Override
    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {

    }

    @Override
    public void calc(String experimentInstanceId, String experimentGroupId, Integer period) {

    }
}
