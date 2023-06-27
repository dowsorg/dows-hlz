package org.dows.hep.biz.timer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.event.SuspendEvent;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.ExperimentGroupSettingRequest;
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
import java.util.List;

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

    private final ExperimentGroupSettingRequest experimentGroupSettingRequest;



    @Override
    public void run() {
        /**
         * todo
         * 实验开始时更新实验相关（ExperimentInstance,ExperimentTimer,ExperimentParticitor）状态为准备中
         * 触发实验暂停事件，并根据实验模式确定新增ExperimentTimer的暂停计时器的暂停开始时间
         */

        //1、判断实验是否到时间，到时间则更新状态
        List<ExperimentContext> instanceEntities = ExperimentContext.getMap();
        instanceEntities.forEach(entity -> {
            if (entity.getState() == ExperimentStateEnum.UNBEGIN) {
                experimentParticipatorService.lambdaUpdate()
                        .eq(ExperimentParticipatorEntity::getExperimentInstanceId, entity.getExperimentId())
                        .eq(ExperimentParticipatorEntity::getDeleted, false)
                        .set(ExperimentParticipatorEntity::getState, ExperimentStateEnum.PREPARE.getState()).update();
                experimentInstanceService.lambdaUpdate()
                        .eq(ExperimentInstanceEntity::getExperimentInstanceId, entity.getExperimentId())
                        .eq(ExperimentInstanceEntity::getDeleted, false)
                        .set(ExperimentInstanceEntity::getState, ExperimentStateEnum.PREPARE.getState()).update();
                experimentTimerService.lambdaUpdate()
                        .eq(ExperimentTimerEntity::getExperimentInstanceId, entity.getExperimentId())
                        .eq(ExperimentTimerEntity::getDeleted, false)
                        .set(ExperimentTimerEntity::getState, ExperimentStateEnum.PREPARE.getState()).update();
                //1、更改缓存
                ExperimentContext experimentContext = ExperimentContext.getExperimentContext(entity.getExperimentId());
                experimentContext.setState(ExperimentStateEnum.PREPARE);
            }
        });


        ExperimentInstanceEntity experimentInstanceEntity = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentGroupSettingRequest.getExperimentInstanceId())
                .eq(ExperimentInstanceEntity::getAppId, experimentGroupSettingRequest.getAppId())
                //.ge(ExperimentInstanceEntity::getStartTime, LocalDateTime.now())
                .oneOpt()
                .orElse(null);
        if (experimentInstanceEntity == null) {
            throw new ExperimentException("不存在的该实验!");
        }

        Integer state = experimentInstanceEntity.getState();
        ExperimentStateEnum experimentStateEnum = Arrays.stream(ExperimentStateEnum.values()).filter(e -> e.getState() == state)
                .findFirst().orElse(null);

        if (experimentStateEnum == ExperimentStateEnum.PREPARE) {
            ExperimentRestartRequest experimentRestartRequest = new ExperimentRestartRequest();
            experimentRestartRequest.setExperimentInstanceId(experimentGroupSettingRequest.getExperimentInstanceId());
            experimentRestartRequest.setPaused(true);
            //experimentRestartRequest.setPeriods(experimentInstanceEntity);
            experimentRestartRequest.setModel(experimentInstanceEntity.getModel());
            experimentRestartRequest.setAppId(experimentGroupSettingRequest.getAppId());
            experimentRestartRequest.setCurrentTime(new Date());
            applicationEventPublisher.publishEvent(new SuspendEvent(experimentRestartRequest));
        }

    }
}
