package org.dows.hep.biz.task;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.event.SuspendEvent;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.biz.event.ExperimentTimerCache;
import org.dows.hep.biz.task.handler.ExperimentBeginTaskHandler;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.Date;

/**
 * 实验开始任务
 * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
 * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
 */
@Slf4j
@RequiredArgsConstructor
//@Component
public class ExperimentBeginTask implements Runnable {
//    // 实验实例
//    private final ExperimentInstanceService experimentInstanceService;
//    // 实验参与者
//    private final ExperimentParticipatorService experimentParticipatorService;
//    // 实验计时器
//    private final ExperimentTimerService experimentTimerService;
//
//    private final ApplicationEventPublisher applicationEventPublisher;
//
//    private final ExperimentTaskScheduleService experimentTaskScheduleService;

    private final String experimentInstanceId;

    private final ExperimentBeginTaskHandler experimentBeginTaskHandler;

    @Override
    public void run() {
        /**
         * todo
         * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
         * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
         */
        experimentBeginTaskHandler.handle(experimentInstanceId);
//        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
//                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
//                //.eq(ExperimentInstanceEntity::getAppId, experimentGroupSettingRequest.getAppId())
//                //.ge(ExperimentInstanceEntity::getStartTime, LocalDateTime.now())
//                .oneOpt()
//                .orElse(null);
//        if (experimentInstanceEntity == null) {
//            throw new ExperimentException("不存在该实验!");
//        }
//        log.info("执行开始任务,查询到实验实例:{}", JSONUtil.toJsonStr(experimentInstanceEntity));
//        //1、判断实验是否到时间，到时间则更新状态
//        if (experimentInstanceEntity.getState() == EnumExperimentState.UNBEGIN.getState()) {
//
//            experimentInstanceService.lambdaUpdate()
//                    .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
//                    .eq(ExperimentInstanceEntity::getDeleted, false)
//                    .set(ExperimentInstanceEntity::getState, EnumExperimentState.PREPARE.getState())
//                    .update();
//            experimentInstanceEntity.setState(EnumExperimentState.PREPARE.getState());
//
//            experimentParticipatorService.lambdaUpdate()
//                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
//                    .eq(ExperimentParticipatorEntity::getDeleted, false)
//                    .set(ExperimentParticipatorEntity::getState, EnumExperimentState.PREPARE.getState())
//                    .update();
//            experimentTimerService.lambdaUpdate()
//                    .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
//                    .eq(ExperimentTimerEntity::getDeleted, false)
//                    .set(ExperimentTimerEntity::getState, EnumExperimentState.PREPARE.getState())
//                    .update();
//            ExperimentTimerCache.Instance().remove(experimentInstanceEntity.getAppId(), experimentInstanceEntity.getExperimentInstanceId());
//            /**
//             * todo 优化更改缓存
//             * ExperimentContext experimentContext = ExperimentContext.getExperimentContext(experimentInstanceEntity.getExperimentInstanceId());
//             * experimentContext.setState(ExperimentStateEnum.PREPARE);
//             */
//        }
//
//        /**
//         * 当前实验状态为准备中时则发布实验暂停事件
//         */
//        EnumExperimentState enumExperimentState = Arrays.stream(EnumExperimentState.values())
//                .filter(e -> e.getState() == experimentInstanceEntity.getState())
//                .findFirst()
//                .orElse(null);
//        if (enumExperimentState == EnumExperimentState.PREPARE) {
//            ExperimentRestartRequest experimentRestartRequest = new ExperimentRestartRequest();
//            experimentRestartRequest.setExperimentInstanceId(experimentInstanceId);
//            experimentRestartRequest.setPaused(true);
//            //experimentRestartRequest.setPeriods(experimentInstanceEntity);
//            experimentRestartRequest.setModel(experimentInstanceEntity.getModel());
//            //experimentRestartRequest.setAppId(experimentGroupSettingRequest.getAppId());
//            experimentRestartRequest.setCurrentTime(new Date());
//            applicationEventPublisher.publishEvent(new SuspendEvent(experimentRestartRequest));
//        }
//
//        //更改实验任务状态
//        ExperimentTaskScheduleEntity beginTaskScheduleEntity = experimentTaskScheduleService.lambdaQuery()
//                .eq(ExperimentTaskScheduleEntity::getTaskBeanCode, EnumExperimentTask.experimentBeginTask.getDesc())
//                .eq(ExperimentTaskScheduleEntity::getExperimentInstanceId, experimentInstanceId)
//                .isNull(ExperimentTaskScheduleEntity::getPeriods)
//                .one();
//        if(beginTaskScheduleEntity == null || ReflectUtil.isObjectNull(beginTaskScheduleEntity)){
//            throw new ExperimentException("该实验任务不存在");
//        }
//        experimentTaskScheduleService.lambdaUpdate()
//                .eq(ExperimentTaskScheduleEntity::getId,beginTaskScheduleEntity.getId())
//                .set(ExperimentTaskScheduleEntity::getExecuted,true)
//                .update();
    }
}
