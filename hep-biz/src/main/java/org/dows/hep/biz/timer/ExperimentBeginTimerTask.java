package org.dows.hep.biz.timer;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.event.SuspendEvent;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentRestartRequest;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
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
public class ExperimentBeginTimerTask implements Runnable {
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验计时器
    private final ExperimentTimerService experimentTimerService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final String experimentInstanceId;

    @Override
    public void run() {
        /**
         * todo
         * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
         * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
         */
        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                //.eq(ExperimentInstanceEntity::getAppId, experimentGroupSettingRequest.getAppId())
                //.ge(ExperimentInstanceEntity::getStartTime, LocalDateTime.now())
                .oneOpt()
                .orElse(null);
        if (experimentInstanceEntity == null) {
            throw new ExperimentException("不存在该实验!");
        }
        log.info("执行开始任务,查询到实验实例:{}", JSONUtil.toJsonStr(experimentInstanceEntity));
        //1、判断实验是否到时间，到时间则更新状态
        if (experimentInstanceEntity.getState() == ExperimentStateEnum.UNBEGIN.getState()) {

            experimentInstanceService.lambdaUpdate()
                    .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                    .eq(ExperimentInstanceEntity::getDeleted, false)
                    .set(ExperimentInstanceEntity::getState, ExperimentStateEnum.PREPARE.getState())
                    .update();
            experimentInstanceEntity.setState(ExperimentStateEnum.PREPARE.getState());

            experimentParticipatorService.lambdaUpdate()
                    .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                    .eq(ExperimentParticipatorEntity::getDeleted, false)
                    .set(ExperimentParticipatorEntity::getState, ExperimentStateEnum.PREPARE.getState())
                    .update();
            experimentTimerService.lambdaUpdate()
                    .eq(ExperimentTimerEntity::getExperimentInstanceId, experimentInstanceEntity.getExperimentInstanceId())
                    .eq(ExperimentTimerEntity::getDeleted, false)
                    .set(ExperimentTimerEntity::getState, ExperimentStateEnum.PREPARE.getState())
                    .update();
            //1、更改缓存
            /*ExperimentContext experimentContext = ExperimentContext.getExperimentContext(experimentInstanceEntity.getExperimentInstanceId());
            experimentContext.setState(ExperimentStateEnum.PREPARE);*/
        }

        /**
         * 当前实验状态为准备中时则发布实验暂停事件
         */
        ExperimentStateEnum experimentStateEnum = Arrays.stream(ExperimentStateEnum.values())
                .filter(e -> e.getState() == experimentInstanceEntity.getState())
                .findFirst()
                .orElse(null);
        if (experimentStateEnum == ExperimentStateEnum.PREPARE) {
            ExperimentRestartRequest experimentRestartRequest = new ExperimentRestartRequest();
            experimentRestartRequest.setExperimentInstanceId(experimentInstanceId);
            experimentRestartRequest.setPaused(true);
            //experimentRestartRequest.setPeriods(experimentInstanceEntity);
            experimentRestartRequest.setModel(experimentInstanceEntity.getModel());
            //experimentRestartRequest.setAppId(experimentGroupSettingRequest.getAppId());
            experimentRestartRequest.setCurrentTime(new Date());
            applicationEventPublisher.publishEvent(new SuspendEvent(experimentRestartRequest));
        }

    }
}
