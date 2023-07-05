package org.dows.hep.biz.calc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.exception.ExperimentException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;


    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {
        List<EnumCalcCode> enumCalcCodes = experimentScoreCalcRequest.getEnumCalcCodes();
        if (enumCalcCodes == null || enumCalcCodes.size() == 0) {
            throw new ExperimentException("实验分数计算器集合为空");
        }
        extracted(experimentScoreCalcRequest.getExperimentInstanceId(),
                experimentScoreCalcRequest.getExperimentGroupId(),
                experimentScoreCalcRequest.getPeriod(), enumCalcCodes);
    }


    public void calc(String expeirmentInstanceId, String experimentGroupId, Integer peroid, List<EnumCalcCode> calcCodes) {
        extracted(expeirmentInstanceId, experimentGroupId, peroid, calcCodes);
    }


    private void extracted(String expeirmentInstanceId, String experimentGroupId, Integer peroid,
                           List<EnumCalcCode> enumCalcCodes) {
        for (EnumCalcCode enumCalcCode : enumCalcCodes) {
            Calculatable calculatable = calculatableMap.get(enumCalcCode);
            if (calculatable != null) {
                // 交由线程执行
                threadPoolTaskExecutor.execute(() -> {
                    calculatable.calc(expeirmentInstanceId, experimentGroupId, peroid);
                });
            }
        }
    }

}
