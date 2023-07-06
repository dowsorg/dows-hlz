package org.dows.hep.biz.task;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.biz.calc.ExperimentScoreCalculator;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;

import java.util.List;

/**
 * ranking 排行/分数计算任务
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentPeroidTimerTask implements Runnable {

    // 计时器
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentScoreCalculator experimentScoreCalculator;
    // 实验ID
    private final String experimentInstanceId;
    private final String experimentGroupId;
    // 期数
    private final Integer period;


    @Override
    public void run() {
        List<EnumCalcCode> calcCodes = List.of(EnumCalcCode.hepHealthIndexCalculator, EnumCalcCode.hepKnowledgeCalculator,
                EnumCalcCode.hepTreatmentPercentCalculator, EnumCalcCode.hepOperateRightCalculator);
        experimentScoreCalculator.calc(experimentInstanceId, experimentGroupId, period, calcCodes);
    }
}
