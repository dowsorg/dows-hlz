package org.dows.calc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.Calculatable;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.springframework.stereotype.Component;

/**
 * 医疗占比计算器
 */
@Slf4j
@RequiredArgsConstructor
@Component("hepTreatmentPercentCalculator")
public class HepTreatmentPercentCalculator implements Calculatable {


    //private final OperateCostBiz operateCostBiz;
    @Override
    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {

//        CostRequest costRequest = CostRequest.builder().build();
//        operateCostBiz.calcGroupTreatmentPercent(costRequest);

    }


    @Override
    public void calc(String experimentInstanceId, String experimentGroupId, Integer period) {

    }
}
