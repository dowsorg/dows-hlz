package org.dows.hep.biz.calc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.springframework.stereotype.Component;

/**
 * 医疗占比计算器
 */
@Slf4j
@RequiredArgsConstructor
@Component("hepTreatmentPercentCalculator")
public class HepTreatmentPercentCalculator implements Calculatable {


    private final OperateCostBiz operateCostBiz;
    @Override
    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {

//        CostRequest costRequest = CostRequest.builder().build();
//        operateCostBiz.calcGroupTreatmentPercent(costRequest);

    }


    @Override
    public void calc(String experimentInstanceId, String experimentGroupId, Integer period) {

    }
}
