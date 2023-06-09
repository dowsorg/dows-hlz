package org.dows.hep.biz.timer;

//import lombok.RequiredArgsConstructor;
//import org.dows.hep.entity.OperateFollowupTimerEntity;
//import org.dows.hep.service.OperateFollowupTimerService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author jx
// * @date 2023/6/9 14:21
// */
//@Component
//@RequiredArgsConstructor
//public class FixedMonitorTask {
//    Logger logger = LoggerFactory.getLogger(getClass());
//    private final OperateFollowupTimerService operateFollowupTimerService;
//
//    @Scheduled(cron = "*/15 * * * * ?")
//    public void execute() {
//        operateFollowupTimerService.lambdaQuery()
//                .eq(OperateFollowupTimerEntity::getDeleted,false)
//                .list();
//    }
//}
