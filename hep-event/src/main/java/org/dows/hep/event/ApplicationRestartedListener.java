package org.dows.hep.event;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.calc.ExperimentScoreCalculator;
import org.dows.hep.biz.noticer.PeriodEndNoticer;
import org.dows.hep.biz.noticer.PeriodStartNoticer;
import org.dows.hep.biz.schedule.TaskScheduler;
import org.dows.hep.biz.task.ExperimentRestartTask;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * 应用重启事件，拉起任务及执行任务
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ApplicationRestartedListener implements ApplicationListener<ApplicationStartedEvent> {
    private final ExperimentTaskScheduleService experimentTaskScheduleService;

    private final ExperimentInstanceService experimentInstanceService;

    private final ExperimentParticipatorService experimentParticipatorService;

    private final ExperimentTimerService experimentTimerService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final TaskScheduler taskScheduler;

    private final ExperimentTimerBiz experimentTimerBiz;

    private final ExperimentScoreCalculator experimentScoreCalculator;

    private final PeriodStartNoticer periodStartNoticer;

    private final PeriodEndNoticer periodEndNoticer;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("执行任务重启......");
        String appId = "3";
        Date now = DateUtil.date();
        // 更新重启时间(当前应用下大于当前时间且未执行的任务)
        experimentTaskScheduleService.lambdaUpdate()
                .set(ExperimentTaskScheduleEntity::getRestartTime, now)
                .eq(ExperimentTaskScheduleEntity::getExecuted,false)
                .eq(ExperimentTaskScheduleEntity::getAppId,appId)
                .gt(ExperimentTaskScheduleEntity::getExecuteTime,now)
                .update();
        ExperimentRestartTask experimentRestartTask = new ExperimentRestartTask(experimentTaskScheduleService,experimentInstanceService,
                experimentParticipatorService,experimentTimerService,applicationEventPublisher,
                appId,taskScheduler,experimentTimerBiz,experimentScoreCalculator,periodStartNoticer,periodEndNoticer);
        experimentRestartTask.run();
    }
}
