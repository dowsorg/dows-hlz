package org.dows.hep.biz.calc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.exception.ExperimentException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 排行榜计算
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentScoreCalculator {

    private final Map<String, Calculatable> calculatableMap;



    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {
        List<EnumCalcCode> enumCalcCodes = experimentScoreCalcRequest.getEnumCalcCodes();
        if (enumCalcCodes == null || enumCalcCodes.size() == 0) {
            throw new ExperimentException("实验分数计算器集合为空");
        }
        for (EnumCalcCode enumCalcCode : enumCalcCodes) {
            Calculatable calculatable = calculatableMap.get(enumCalcCode);
            if (calculatable != null) {
                calculatable.calc(experimentScoreCalcRequest.getExperimentInstanceId(),
                        experimentScoreCalcRequest.getExperimentGroupId(),
                        experimentScoreCalcRequest.getPeriod());
            }
        }
    }

    public void calc(String expeirmentInstanceId, Integer peroid, List<String> calcCodes) {
        //experimentScoringBiz.
    }
}
