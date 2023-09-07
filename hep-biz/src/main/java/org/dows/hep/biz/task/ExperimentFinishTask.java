package org.dows.hep.biz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.calc.CalculatorDispatcher;
import org.dows.hep.biz.task.handler.ExperimentFinishTaskHandler;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.dows.hep.service.ExperimentTimerService;

/**
 * 实验结束任务
 * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为已结束
 * 计算总排行
 */
@Slf4j
@RequiredArgsConstructor
//@Component
public class ExperimentFinishTask implements Runnable {
//    // 实验实例
//    private final ExperimentInstanceService experimentInstanceService;
//    // 实验参与者
//    private final ExperimentParticipatorService experimentParticipatorService;
//    // 实验计时器
//    private final ExperimentTimerService experimentTimerService;
//
//    private final ExperimentTaskScheduleService experimentTaskScheduleService;
//    //
//    private final CalculatorDispatcher calculatorDispatcher;

    private final String experimentInstanceId;

    private final Integer period;

    private final ExperimentFinishTaskHandler experimentFinishTaskHandler;

    @Override
    public void run() {

        experimentFinishTaskHandler.handle(experimentInstanceId, period);
//        StringBuilder sb=new StringBuilder();
//        try {
//            sb.append(String.format("input:%s-%s@%s ", experimentInstanceId,period,LocalDateTime.now()));
//            /**
//             * todo
//             * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
//             * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
//             */
//            ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
//                    .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
//                    .oneOpt()
//                    .orElse(null);
//            if (experimentInstanceEntity == null) {
//                throw new ExperimentException("不存在该实验!");
//            }
//            log.info("EXPTFLOW-FINISH 执行开始任务,查询到实验实例:{}", JSONUtil.toJsonStr(experimentInstanceEntity));
//
//            //1、判断实验是否到时间，到时间则更新状态
//            experimentInstanceService.lambdaUpdate()
//                    .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
//                    .eq(ExperimentInstanceEntity::getDeleted, false)
//                    .set(ExperimentInstanceEntity::getState, EnumExperimentState.FINISH.getState())
//                    .update();
//
//            experimentParticipatorService.lambdaUpdate()
//                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
//                    .eq(ExperimentParticipatorEntity::getDeleted, false)
//                    .set(ExperimentParticipatorEntity::getState, EnumExperimentState.FINISH.getState())
//                    .update();
//            experimentTimerService.lambdaUpdate()
//                    .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
//                    .eq(ExperimentTimerEntity::getDeleted, false)
//                    .set(ExperimentTimerEntity::getState, EnumExperimentState.FINISH.getState())
//                    .update();
//            ExperimentTimerCache.Instance().remove(experimentInstanceEntity.getAppId(), experimentInstanceEntity.getExperimentInstanceId());
//
//            sb.append(String.format(" 1-updateState@%s",LocalDateTime.now()));
//
//            //todo 计算总排行
//            ExperimentScoreCalcRequest experimentScoreCalcRequest = new ExperimentScoreCalcRequest();
//            experimentScoreCalcRequest.setExperimentInstanceId(experimentInstanceId);
//            experimentScoreCalcRequest.setEnumCalcCodes(List.of(EnumCalcCode.hepTotalScoreCalculator));
//            //todo 根据条件计算总排行
//            calculatorDispatcher.calc(experimentScoreCalcRequest);
//            sb.append(String.format(" 2-calc@%s",LocalDateTime.now()));
//
//            //更改完成任务状态
//            ExperimentTaskScheduleEntity finishTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
//                    .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentFinishTask.getDesc())
//                    .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceId)
//                    .eq(ExperimentTaskScheduleEntity::getPeriods,period)
//                    .one();
//            if(finishTaskScheduleEntity == null || ReflectUtil.isObjectNull(finishTaskScheduleEntity)){
//                throw new ExperimentException("该完成任务不存在");
//            }
//            experimentTaskScheduleService.lambdaUpdate()
//                    .eq(ExperimentTaskScheduleEntity::getId,finishTaskScheduleEntity.getId())
//                    .set(ExperimentTaskScheduleEntity::getExecuted,true)
//                    .update();
//            sb.append(String.format(" 3-succ@%s",LocalDateTime.now()));
//
//        }catch (Exception ex){
//            log.error(String.format("EXPTFLOW-FINISH error.",ex.getMessage()));
//            sb.append("error:");
//            sb.append(ex.getMessage());
//        }finally {
//            log.info(String.format("EXPTFLOW-FINISH %s.run@%s[%s] %s", this.getClass().getName(), LocalDateTime.now(),Thread.currentThread().getName(),sb));
//            sb.setLength(0);
//        }


    }
}
