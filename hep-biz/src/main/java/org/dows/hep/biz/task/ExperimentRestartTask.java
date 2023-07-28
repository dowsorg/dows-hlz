package org.dows.hep.biz.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentTask;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.biz.calc.ExperimentScoreCalculator;
import org.dows.hep.biz.noticer.PeriodEndNoticer;
import org.dows.hep.biz.noticer.PeriodStartNoticer;
import org.dows.hep.biz.request.ExperimentTaskParamsRequest;
import org.dows.hep.biz.schedule.TaskScheduler;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentTaskScheduleService;
import org.dows.hep.service.ExperimentTimerService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实验重启任务
 * 重新初始化调度器
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentRestartTask implements Runnable {
    private final ExperimentTaskScheduleService experimentTaskScheduleService;

    private final ExperimentInstanceService experimentInstanceService;

    private final ExperimentParticipatorService experimentParticipatorService;

    private final ExperimentTimerService experimentTimerService;

    private final ApplicationEventPublisher applicationEventPublisher;

    // 应用ID(需要根据应用ID查询该应用下的实验)
    private final String appId;

    private final TaskScheduler taskScheduler;

    private final ExperimentTimerBiz experimentTimerBiz;

    private final ExperimentScoreCalculator experimentScoreCalculator;

    private final PeriodStartNoticer periodStartNoticer;

    private final PeriodEndNoticer periodEndNoticer;

    private final ExperimentSchemeBiz experimentSchemeBiz;


    /**
     * todo
     * 1.获取实验计时器，
     * 2.获取experiment_task_schedule
     * 3.判断experiment_task_schedule.restartTime > experiment_task_schedule.execute_time && executed == '未执行' do 执行原来的任务
     * 4.判断experiment_task_schedule.restartTime <= experiment_task_schedule.execute_time && executed == '未执行' do 重新拉起定时任务
     */
    @Override
    public void run() {
        // 1、获取所有未执行的实验
        List<ExperimentTaskScheduleEntity> scheduleEntityList = experimentTaskScheduleService.lambdaQuery()
                .eq(ExperimentTaskScheduleEntity::getDeleted, false)
                .eq(ExperimentTaskScheduleEntity::getExecuted, false)
                .eq(ExperimentTaskScheduleEntity::getAppId, appId)
                .list();
        // 2、有些数据部分信息可能会被误删，要过滤掉这些数据
        scheduleEntityList.stream().filter(schedule -> experimentTimerBiz.getPeriodsTimerList(schedule.getExperimentInstanceId()) == null ||
                experimentTimerBiz.getPeriodsTimerList(schedule.getExperimentInstanceId()).size() == 0)
                .forEach(schedule -> experimentTaskScheduleService.lambdaUpdate().set(ExperimentTaskScheduleEntity::getDeleted, true)
                        .eq(ExperimentTaskScheduleEntity::getId, schedule.getId())
                        .update()
                );
        scheduleEntityList = scheduleEntityList.stream().filter(schedule -> experimentTimerBiz.getPeriodsTimerList(schedule.getExperimentInstanceId()) != null &&
                experimentTimerBiz.getPeriodsTimerList(schedule.getExperimentInstanceId()).size() > 0)
                .collect(Collectors.toList());
        // 3、判断是在重启开始前应该执行的还是之后执行的任务，如果本来应该是重启之前执行的任务，重启的时候就执行，否则的话就拉起任务
        if (scheduleEntityList != null && scheduleEntityList.size() > 0) {
            scheduleEntityList.forEach(scheduleEntity -> {
                if (scheduleEntity.getRestartTime() != null && scheduleEntity.getExecuteTime().after(scheduleEntity.getRestartTime())) {
                    ExperimentTaskParamsRequest json = JSON.parseObject(scheduleEntity.getTaskParams(),ExperimentTaskParamsRequest.class);
                    //3.1、直接重新拉取，后期重新执行
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentBeginTask.getDesc())) {
                        // 3.2、执行定时任务
                        ExperimentBeginTask experimentBeginTask = new ExperimentBeginTask(
                                experimentInstanceService, experimentParticipatorService, experimentTimerService, applicationEventPublisher,
                                experimentTaskScheduleService, json.getExperimentInstanceId());
                        taskScheduler.schedule(experimentBeginTask, scheduleEntity.getExecuteTime());
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentCalcTask.getDesc())) {
                        // 3.3、执行定时任务
                        ExperimentCalcTask experimentCalcTask = new ExperimentCalcTask(
                                experimentTimerBiz,
                                experimentScoreCalculator,
                                experimentTaskScheduleService,
                                json.getExperimentInstanceId(),
                                json.getExperimentGroupId(),
                                json.getPeriod());

                        taskScheduler.schedule(experimentCalcTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentFinishTask.getDesc())) {
                        // 3.4、执行定时任务
                        ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask(experimentInstanceService,
                                experimentParticipatorService, experimentTimerService, experimentTaskScheduleService, experimentScoreCalculator,
                                json.getExperimentInstanceId(), json.getPeriod());

                        taskScheduler.schedule(experimentFinishTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentPeriodStartNoticeTask.getDesc())) {
                        // 3.5、执行定时任务
                        ExperimentNoticeTask experimentPeriodStartNoticeTask = new ExperimentNoticeTask(
                                json.getExperimentInstanceId(),
                                json.getExperimentGroupId(),
                                json.getPeriod(),
                                periodStartNoticer,
                                JSON.parseObject(json.getNoticeParams(), NoticeContent.class),
                                experimentTaskScheduleService
                        );
                        taskScheduler.schedule(experimentPeriodStartNoticeTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentPeriodEndNoticeTask.getDesc())) {
                        // 3.6、执行定时任务
                        ExperimentNoticeTask experimentPeriodEndNoticeTask = new ExperimentNoticeTask(
                                json.getExperimentInstanceId(),
                                json.getExperimentGroupId(),
                                json.getPeriod(),
                                periodStartNoticer,
                                JSON.parseObject(json.getNoticeParams(), NoticeContent.class),
                                experimentTaskScheduleService
                        );
                        taskScheduler.schedule(experimentPeriodEndNoticeTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.exptSchemeExpireTask.getDesc())) {
                        // 3.7、执行定时任务
                        ExptSchemeExpireTask exptSchemeExpireTask = new ExptSchemeExpireTask(
                                experimentTaskScheduleService,
                                experimentSchemeBiz,
                                json.getExperimentInstanceId()
                        );
                        taskScheduler.schedule(exptSchemeExpireTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.exptSchemeFinishTask.getDesc())) {
                        // 3.8、执行定时任务
                        ExptSchemeFinishTask exptSchemeFinishTask = new ExptSchemeFinishTask(
                                experimentTaskScheduleService,
                                experimentSchemeBiz,
                                json.getExperimentInstanceId(),
                                json.getExperimentGroupId()
                        );
                        taskScheduler.schedule(exptSchemeFinishTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                    }
                }
                // 3.9、之前的任务，因为宕机没有按时执行，直接全部一次性执行了
                if (scheduleEntity.getExecuteTime().before(new Date())) {
                    ExperimentTaskParamsRequest json = JSON.parseObject(scheduleEntity.getTaskParams(),ExperimentTaskParamsRequest.class);
                    //3.10、直接重新拉取，后期重新执行
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentBeginTask.getDesc())) {
                        // 3.11、执行定时任务
                        ExperimentBeginTask experimentBeginTask = new ExperimentBeginTask(
                                experimentInstanceService, experimentParticipatorService, experimentTimerService, applicationEventPublisher,
                                experimentTaskScheduleService, json.getExperimentInstanceId());
                        experimentBeginTask.run();
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentCalcTask.getDesc())) {
                        // 3.12、执行定时任务
                        ExperimentCalcTask experimentCalcTask = new ExperimentCalcTask(
                                experimentTimerBiz,
                                experimentScoreCalculator,
                                experimentTaskScheduleService,
                                json.getExperimentInstanceId(),
                                json.getExperimentGroupId(),
                                json.getPeriod());
                        experimentCalcTask.run();
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentFinishTask.getDesc())) {
                        // 3.13、执行定时任务
                        ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask(experimentInstanceService,
                                experimentParticipatorService, experimentTimerService, experimentTaskScheduleService, experimentScoreCalculator,
                                json.getExperimentInstanceId(), json.getPeriod());

                        experimentFinishTask.run();
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentPeriodStartNoticeTask.getDesc())) {
                        // 3.14、执行定时任务
                        ExperimentNoticeTask experimentPeriodStartNoticeTask = new ExperimentNoticeTask(
                                json.getExperimentInstanceId(),
                                json.getExperimentGroupId(),
                                json.getPeriod(),
                                periodStartNoticer,
                                JSON.parseObject(json.getNoticeParams(), NoticeContent.class),
                                experimentTaskScheduleService
                        );
                        experimentPeriodStartNoticeTask.run();
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentPeriodEndNoticeTask.getDesc())) {
                        // 3.15、执行定时任务
                        ExperimentNoticeTask experimentPeriodEndNoticeTask = new ExperimentNoticeTask(
                                json.getExperimentInstanceId(),
                                json.getExperimentGroupId(),
                                json.getPeriod(),
                                periodStartNoticer,
                                JSON.parseObject(json.getNoticeParams(), NoticeContent.class),
                                experimentTaskScheduleService
                        );
                        experimentPeriodEndNoticeTask.run();
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.exptSchemeExpireTask.getDesc())) {
                        // 3.16、执行定时任务
                        ExptSchemeExpireTask exptSchemeExpireTask = new ExptSchemeExpireTask(
                                experimentTaskScheduleService,
                                experimentSchemeBiz,
                                json.getExperimentInstanceId()
                        );
                        exptSchemeExpireTask.run();
                    }
                    if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.exptSchemeFinishTask.getDesc())) {
                        // 3.17、执行定时任务
                        ExptSchemeFinishTask exptSchemeFinishTask = new ExptSchemeFinishTask(
                                experimentTaskScheduleService,
                                experimentSchemeBiz,
                                json.getExperimentInstanceId(),
                                json.getExperimentGroupId()
                        );
                        exptSchemeFinishTask.run();
                    }
                }
            });
        }
    }
}
