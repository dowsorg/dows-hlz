//package org.dows.hep.job;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.hep.api.ExperimentContext;
//import org.dows.hep.api.enums.ExperimentStateEnum;
//import org.dows.hep.entity.ExperimentInstanceEntity;
//import org.dows.hep.entity.ExperimentParticipatorEntity;
//import org.dows.hep.entity.ExperimentTimerEntity;
//import org.dows.hep.service.ExperimentInstanceService;
//import org.dows.hep.service.ExperimentParticipatorService;
//import org.dows.hep.service.ExperimentTimerService;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * 实验定时器job
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ExperimentTimerJob{
//
//    private final ExperimentInstanceService experimentInstanceService;
//
//    private final ExperimentParticipatorService experimentParticipatorService;
//
//    private final ExperimentTimerService experimentTimerService;
//
//    @Scheduled(cron = "*/30 * * * * ?")
//    public void execute() {
//        //1、判断实验是否到时间，到时间则更新状态
//        List<ExperimentContext> instanceEntities = ExperimentContext.getMap();
//        instanceEntities.forEach(entity -> {
//            if (entity.getState() == ExperimentStateEnum.UNBEGIN) {
//                experimentParticipatorService.lambdaUpdate()
//                        .eq(ExperimentParticipatorEntity::getExperimentInstanceId, entity.getExperimentId())
//                        .eq(ExperimentParticipatorEntity::getDeleted, false)
//                        .set(ExperimentParticipatorEntity::getState,ExperimentStateEnum.PREPARE.getState()).update();
//                experimentInstanceService.lambdaUpdate()
//                        .eq(ExperimentInstanceEntity::getExperimentInstanceId, entity.getExperimentId())
//                        .eq(ExperimentInstanceEntity::getDeleted, false)
//                        .set(ExperimentInstanceEntity::getState,ExperimentStateEnum.PREPARE.getState()).update();
//                experimentTimerService.lambdaUpdate()
//                        .eq(ExperimentTimerEntity::getExperimentInstanceId, entity.getExperimentId())
//                        .eq(ExperimentTimerEntity::getDeleted, false)
//                        .set(ExperimentTimerEntity::getState,ExperimentStateEnum.PREPARE.getState()).update();
//                //1、更改缓存
//                ExperimentContext experimentContext = ExperimentContext.getExperimentContext(entity.getExperimentId());
//                experimentContext.setState(ExperimentStateEnum.PREPARE);
//            }
//        });
//    }
//}
