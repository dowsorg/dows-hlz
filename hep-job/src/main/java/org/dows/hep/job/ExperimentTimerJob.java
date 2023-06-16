package org.dows.hep.job;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.ExperimentContext;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 实验定时器job
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExperimentTimerJob{

    private final ExperimentInstanceService experimentInstanceService;

    private final ExperimentParticipatorService experimentParticipatorService;

    @Scheduled(cron = "*/30 * * * * ?")
    public void execute() {
        //1、判断实验是否到时间，到时间则更新状态
//        List<ExperimentInstanceEntity> instanceEntities = experimentInstanceService.lambdaQuery()
//                .eq(ExperimentInstanceEntity::getAppId, 3)
//                .eq(ExperimentInstanceEntity::getDeleted,false)
//                .eq(ExperimentInstanceEntity::getState, ExperimentStateEnum.UNBEGIN.getState())
//                .list();
        List<ExperimentContext> instanceEntities = ExperimentContext.getMap();
        instanceEntities.forEach(entity -> {
            if (entity.getState().equals(ExperimentStateEnum.UNBEGIN.getState())) {
                LambdaUpdateWrapper<ExperimentParticipatorEntity> participatorWrapper = new LambdaUpdateWrapper<ExperimentParticipatorEntity>()
                        .eq(ExperimentParticipatorEntity::getExperimentInstanceId, entity.getExperimentId())
                        .set(ExperimentParticipatorEntity::getState, ExperimentStateEnum.ONGOING.getState());
                experimentParticipatorService.update(participatorWrapper);
                LambdaUpdateWrapper<ExperimentInstanceEntity> instanceWrapper = new LambdaUpdateWrapper<ExperimentInstanceEntity>()
                        .eq(ExperimentInstanceEntity::getExperimentInstanceId, entity.getExperimentId())
                        .set(ExperimentInstanceEntity::getState,ExperimentStateEnum.ONGOING.getState());
                experimentInstanceService.update(instanceWrapper);
            }
        });
    }
}
