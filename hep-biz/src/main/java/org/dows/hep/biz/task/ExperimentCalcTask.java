package org.dows.hep.biz.task;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.biz.calc.ExperimentScoreCalculator;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.ExperimentTaskScheduleService;

import java.util.List;

/**
 * 实验计算任务
 * ranking 排行/分数计算任务
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentCalcTask implements Runnable {

    // 计时器
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentScoreCalculator experimentScoreCalculator;
    private final ExperimentTaskScheduleService experimentTaskScheduleService;
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

        //更改计算任务状态
        ExperimentTaskScheduleEntity calcTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentCalcTask.getDesc())
                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentTaskScheduleEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentTaskScheduleEntity::getPeriods,period)
                .one();
        if(calcTaskScheduleEntity == null || ReflectUtil.isObjectNull(calcTaskScheduleEntity)){
            throw new ExperimentException("该计算任务不存在");
        }
        experimentTaskScheduleService.lambdaUpdate()
                .eq(ExperimentTaskScheduleEntity::getId,calcTaskScheduleEntity.getId())
                .set(ExperimentTaskScheduleEntity::getExecuted,true)
                .update();
    }
}
