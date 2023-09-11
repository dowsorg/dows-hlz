package org.dows.hep.task.handler;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.biz.calc.CalculatorDispatcher;
import org.dows.hep.biz.event.ExperimentTimerCache;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExperimentFinishTaskHandler {
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验计时器
    private final ExperimentTimerService experimentTimerService;

    private final ExperimentTaskScheduleService experimentTaskScheduleService;
    //
    private final CalculatorDispatcher calculatorDispatcher;

    public void handle(String experimentInstanceId, Integer period) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(String.format("input:%s-%s@%s ", experimentInstanceId, period, LocalDateTime.now()));
            /**
             * todo
             * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
             * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
             */
            ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                    .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                    .oneOpt()
                    .orElse(null);
            if (experimentInstanceEntity == null) {
                throw new ExperimentException("不存在该实验!");
            }
            log.info("EXPTFLOW-FINISH 执行开始任务,查询到实验实例:{}", JSONUtil.toJsonStr(experimentInstanceEntity));

            //1、判断实验是否到时间，到时间则更新状态
            experimentInstanceService.lambdaUpdate()
                    .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                    .eq(ExperimentInstanceEntity::getDeleted, false)
                    .set(ExperimentInstanceEntity::getState, EnumExperimentState.FINISH.getState())
                    .update();

            experimentParticipatorService.lambdaUpdate()
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                    .eq(ExperimentParticipatorEntity::getDeleted, false)
                    .set(ExperimentParticipatorEntity::getState, EnumExperimentState.FINISH.getState())
                    .update();
            experimentTimerService.lambdaUpdate()
                    .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                    .eq(ExperimentTimerEntity::getDeleted, false)
                    .set(ExperimentTimerEntity::getState, EnumExperimentState.FINISH.getState())
                    .update();
            ExperimentTimerCache.Instance().remove(experimentInstanceEntity.getAppId(), experimentInstanceEntity.getExperimentInstanceId());

            sb.append(String.format(" 1-updateState@%s", LocalDateTime.now()));

            //todo 计算总排行
            ExperimentScoreCalcRequest experimentScoreCalcRequest = new ExperimentScoreCalcRequest();
            experimentScoreCalcRequest.setExperimentInstanceId(experimentInstanceId);
            experimentScoreCalcRequest.setEnumCalcCodes(List.of(EnumCalcCode.hepTotalScoreCalculator));
            //todo 根据条件计算总排行
            calculatorDispatcher.calc(experimentScoreCalcRequest);
            sb.append(String.format(" 2-calc@%s", LocalDateTime.now()));

            //更改完成任务状态
            ExperimentTaskScheduleEntity finishTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
                    .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentFinishTask.getDesc())
                    .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceId)
                    .eq(ExperimentTaskScheduleEntity::getPeriods, period)
                    .one();
            if (finishTaskScheduleEntity == null || ReflectUtil.isObjectNull(finishTaskScheduleEntity)) {
                throw new ExperimentException("该完成任务不存在");
            }
            experimentTaskScheduleService.lambdaUpdate()
                    .eq(ExperimentTaskScheduleEntity::getId, finishTaskScheduleEntity.getId())
                    .set(ExperimentTaskScheduleEntity::getExecuted, true)
                    .update();
            sb.append(String.format(" 3-succ@%s", LocalDateTime.now()));

        } catch (Exception ex) {
            log.error(String.format("EXPTFLOW-FINISH error.", ex.getMessage()));
            sb.append("error:");
            sb.append(ex.getMessage());
        } finally {
            log.info(String.format("EXPTFLOW-FINISH %s.run@%s[%s] %s", this.getClass().getName(), LocalDateTime.now(), Thread.currentThread().getName(), sb));
            sb.setLength(0);
        }
    }
}
