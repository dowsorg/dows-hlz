package org.dows.hep.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.task.handler.ExperimentRestartTaskHandler;

/**
 * 实验重启任务
 * 重新初始化调度器
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentRestartTask implements Runnable {
//    private final ExperimentTaskScheduleService experimentTaskScheduleService;
//
//    private final ExperimentInstanceService experimentInstanceService;
//
//    private final ExperimentParticipatorService experimentParticipatorService;
//
//    private final ExperimentTimerService experimentTimerService;
//
//    private final ApplicationEventPublisher applicationEventPublisher;
//
//    private final PeriodEndNoticer periodEndNoticer;



//    private final TaskScheduler taskScheduler;
//
//    private final ExperimentTimerBiz experimentTimerBiz;
//
//    private final CalculatorDispatcher calculatorDispatcher;
//
//    private final PeriodStartNoticer periodStartNoticer;
//
//    private final ExperimentSettingBiz experimentSettingBiz;
//
//    private final ExperimentSchemeBiz experimentSchemeBiz;
//
//    private final ExperimentBeginHandler experimentBeginHandler;
//
//    private final ExperimentFinishHandler experimentFinishHandler;

    // 应用ID(需要根据应用ID查询该应用下的实验)
    private final String appId;
    private final ExperimentRestartTaskHandler experimentRestartTaskHandler;
    /**
     * todo
     * 1.获取实验计时器，
     * 2.获取experiment_task_schedule
     * 3.判断experiment_task_schedule.restartTime > experiment_task_schedule.execute_time && executed == '未执行' do 执行原来的任务
     * 4.判断experiment_task_schedule.restartTime <= experiment_task_schedule.execute_time && executed == '未执行' do 重新拉起定时任务
     */
    @Override
    public void run() {

        experimentRestartTaskHandler.handler(appId);

//        // 1、获取所有未执行的实验
//        List<ExperimentTaskScheduleEntity> scheduleEntityList = experimentTaskScheduleService.lambdaQuery()
//                .eq(ExperimentTaskScheduleEntity::getDeleted, false)
//                .eq(ExperimentTaskScheduleEntity::getExecuted, false)
//                .eq(ExperimentTaskScheduleEntity::getAppId, appId)
//                .list();
//        if (CollUtil.isEmpty(scheduleEntityList)) {
//            return;
//        }
//
//        // 数据分为方案设计与沙盘
//        List<String> exptInstanceIds = scheduleEntityList.stream()
//                .map(ExperimentTaskScheduleEntity::getExperimentInstanceId)
//                .toList();
//        Map<String, ExptSettingModeEnum> exptIdMapSettingMode = experimentSettingBiz.listExptSettingMode(exptInstanceIds);
//        HashMap<ExptSettingModeEnum, Set<String>> settingModeMapExptId = new HashMap<>();
//        settingModeMapExptId.put(ExptSettingModeEnum.SAND, new HashSet<>());
//        settingModeMapExptId.put(ExptSettingModeEnum.SCHEME, new HashSet<>());
//        exptIdMapSettingMode.forEach((k, v) -> {
//            Set<String> set = settingModeMapExptId.get(v);
//            if (set != null) {
//                set.add(k);
//            }
//        });
//
//
//        // 重置方案设计相关
//        try {
//            doResetScheme(settingModeMapExptId.get(ExptSettingModeEnum.SCHEME), scheduleEntityList);
//        } catch (Exception e) {
//            log.error("服务重启时，重置方案设计相关数据时，发生如下异常：" + e.getMessage());
//        }
//
//        // 重置沙盘相关
//        try {
//            doResetSand(settingModeMapExptId.get(ExptSettingModeEnum.SAND), scheduleEntityList);
//        } catch (Exception e) {
//            log.error("服务重启时，重置沙盘相关数据时，发生如下异常：" + e.getMessage());
//        }
//
//        try {
//            // 重置实验相关
//            deExpt(scheduleEntityList);
//        } catch (Exception e) {
//            log.error("服务重启，重置实验相关数据时，发生如下异常： " + e.getMessage());
//        }
    }
/*
    private void doResetSand(Set<String> exptIdSet, List<ExperimentTaskScheduleEntity> scheduleEntityList) {
        if (CollUtil.isEmpty(exptIdSet) || CollUtil.isEmpty(scheduleEntityList)) {
            return;
        }

        // 过滤出只有沙盘的的
        List<ExperimentTaskScheduleEntity> schemeExptTaskScheduleList = scheduleEntityList.stream()
                .filter(item -> {
                    String experimentInstanceId = item.getExperimentInstanceId();
                    return exptIdSet.contains(experimentInstanceId);
                })
                .toList();
        if (CollUtil.isEmpty(schemeExptTaskScheduleList)) {
            return;
        }

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
        scheduleEntityList.forEach(scheduleEntity -> {
            if (scheduleEntity.getRestartTime() != null && scheduleEntity.getExecuteTime().after(scheduleEntity.getRestartTime())) {
                JSONObject json = JSONObject.parseObject(scheduleEntity.getTaskParams());

                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentCalcTask.getDesc())) {
                    // 3.3、执行定时任务
                    ExperimentCalcTask experimentCalcTask = new ExperimentCalcTask(
                            experimentTimerBiz,
                            calculatorDispatcher,
                            experimentTaskScheduleService,
                            (String) json.get("experimentInstanceId"),
                            (String) json.get("experimentGroupId"),
                            (Integer) json.get("period"));

                    taskScheduler.schedule(experimentCalcTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                }
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentPeriodStartNoticeTask.getDesc())) {
                    // 3.5、执行定时任务
                    ExperimentNoticeTask experimentPeriodStartNoticeTask = new ExperimentNoticeTask(
                            (String) json.get("experimentInstanceId"),
                            (String) json.get("experimentGroupId"),
                            (Integer) json.get("period"),
                            periodStartNoticer,
                            JSON.parseObject(json.get("noticeParams").toString(), NoticeContent.class),
                            experimentTaskScheduleService
                    );
                    taskScheduler.schedule(experimentPeriodStartNoticeTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                }
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentPeriodEndNoticeTask.getDesc())) {
                    // 3.6、执行定时任务
                    ExperimentNoticeTask experimentPeriodEndNoticeTask = new ExperimentNoticeTask(
                            (String) json.get("experimentInstanceId"),
                            (String) json.get("experimentGroupId"),
                            (Integer) json.get("period"),
                            periodStartNoticer,
                            JSON.parseObject(json.get("noticeParams").toString(), NoticeContent.class),
                            experimentTaskScheduleService
                    );
                    taskScheduler.schedule(experimentPeriodEndNoticeTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                }
            }
            // 3.7、之前的任务，因为宕机没有按时执行，直接全部一次性执行了
            if (scheduleEntity.getExecuteTime().before(new Date())) {
                JSONObject json = JSONObject.parseObject(scheduleEntity.getTaskParams());
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentCalcTask.getDesc())) {
                    // 3.9、执行定时任务
                    ExperimentCalcTask experimentCalcTask = new ExperimentCalcTask(
                            experimentTimerBiz,
                            calculatorDispatcher,
                            experimentTaskScheduleService,
                            (String) json.get("experimentInstanceId"),
                            (String) json.get("experimentGroupId"),
                            (Integer) json.get("period"));
                    experimentCalcTask.run();
                }
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentPeriodStartNoticeTask.getDesc())) {
                    // 3.11、执行定时任务
                    ExperimentNoticeTask experimentPeriodStartNoticeTask = new ExperimentNoticeTask(
                            (String) json.get("experimentInstanceId"),
                            (String) json.get("experimentGroupId"),
                            (Integer) json.get("period"),
                            periodStartNoticer,
                            JSON.parseObject(json.get("noticeParams").toString(), NoticeContent.class),
                            experimentTaskScheduleService
                    );
                    experimentPeriodStartNoticeTask.run();
                }
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentPeriodEndNoticeTask.getDesc())) {
                    // 3.12、执行定时任务
                    ExperimentNoticeTask experimentPeriodEndNoticeTask = new ExperimentNoticeTask(
                            (String) json.get("experimentInstanceId"),
                            (String) json.get("experimentGroupId"),
                            (Integer) json.get("period"),
                            periodStartNoticer,
                            JSON.parseObject(json.get("noticeParams").toString(), NoticeContent.class),
                            experimentTaskScheduleService
                    );
                    experimentPeriodEndNoticeTask.run();
                }
            }
        });
    }

    private void doResetScheme(Set<String> exptIdSet, List<ExperimentTaskScheduleEntity> scheduleEntityList) {
        if (CollUtil.isEmpty(exptIdSet) || CollUtil.isEmpty(scheduleEntityList)) {
            return;
        }

        // 过滤出只有方案设计的
        List<ExperimentTaskScheduleEntity> schemeExptTaskScheduleList = scheduleEntityList.stream()
                .filter(item -> {
                    String experimentInstanceId = item.getExperimentInstanceId();
                    return exptIdSet.contains(experimentInstanceId);
                })
                .toList();
        if (CollUtil.isEmpty(schemeExptTaskScheduleList)) {
            return;
        }

        // 执行任务
        scheduleEntityList.forEach(scheduleEntity -> {
            // 任务执行时间在重启时间之后
            JSONObject json = JSONObject.parseObject(scheduleEntity.getTaskParams());
            if (scheduleEntity.getRestartTime() != null && scheduleEntity.getExecuteTime().after(new Date())) {
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.exptSchemeExpireTask.getDesc())) {
                    ExptSchemeExpireTask exptSchemeExpireTask = new ExptSchemeExpireTask(
                            experimentTaskScheduleService,
                            experimentSchemeBiz,
                            (String) json.get("experimentInstanceId"));
                    taskScheduler.schedule(exptSchemeExpireTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                }
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.exptSchemeFinishTask.getDesc())) {
                    ExptSchemeFinishTask exptSchemeFinishTask = new ExptSchemeFinishTask(
                            experimentTaskScheduleService,
                            experimentSchemeBiz,
                            (String) json.get("experimentInstanceId"),
                            (String) json.get("experimentGroupId")
                    );
                    taskScheduler.schedule(exptSchemeFinishTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                }
            }
            if (scheduleEntity.getExecuteTime().before(new Date())) {
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.exptSchemeExpireTask.getDesc())) {
                    ExptSchemeExpireTask exptSchemeExpireTask = new ExptSchemeExpireTask(
                            experimentTaskScheduleService,
                            experimentSchemeBiz,
                            (String) json.get("experimentInstanceId"));
                    exptSchemeExpireTask.run();
                }
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.exptSchemeFinishTask.getDesc())) {
                    ExptSchemeFinishTask exptSchemeFinishTask = new ExptSchemeFinishTask(
                            experimentTaskScheduleService,
                            experimentSchemeBiz,
                            (String) json.get("experimentInstanceId"),
                            (String) json.get("experimentGroupId")
                    );
                    exptSchemeFinishTask.run();
                }
            }
        });
    }

    private void deExpt(List<ExperimentTaskScheduleEntity> scheduleEntityList) {
        if (CollUtil.isEmpty(scheduleEntityList)) {
            return;
        }

        // 执行任务
        scheduleEntityList.forEach(scheduleEntity -> {
            // 任务执行时间在重启时间之后
            JSONObject json = JSONObject.parseObject(scheduleEntity.getTaskParams());
            if (scheduleEntity.getRestartTime() != null && scheduleEntity.getExecuteTime().after(new Date())) {
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentBeginTask.getDesc())) {
                    ExperimentBeginTask experimentBeginTask = new ExperimentBeginTask((String) json.get("experimentInstanceId"), experimentBeginHandler);
                    *//*ExperimentBeginTask experimentBeginTask = new ExperimentBeginTask(
                            experimentInstanceService, experimentParticipatorService, experimentTimerService, applicationEventPublisher,
                            experimentTaskScheduleService, (String) json.get("experimentInstanceId"));*//*
                    taskScheduler.schedule(experimentBeginTask, scheduleEntity.getExecuteTime());
                }
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentFinishTask.getDesc())) {

                   *//* ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask(experimentInstanceService,
                            experimentParticipatorService, experimentTimerService, experimentTaskScheduleService, calculatorDispatcher,
                            (String) json.get("experimentInstanceId"), (Integer) json.get("period"));*//*

                    ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask((String) json.get("experimentInstanceId"),
                            (Integer) json.get("period"), experimentFinishHandler);
                    taskScheduler.schedule(experimentFinishTask, DateUtil.date(scheduleEntity.getExecuteTime()));
                }
            }
            if (scheduleEntity.getExecuteTime().before(new Date())) {
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentBeginTask.getDesc())) {
                    *//*ExperimentBeginTask experimentBeginTask = new ExperimentBeginTask(
                            experimentInstanceService, experimentParticipatorService, experimentTimerService, applicationEventPublisher,
                            experimentTaskScheduleService, (String) json.get("experimentInstanceId"));*//*
                    ExperimentBeginTask experimentBeginTask = new ExperimentBeginTask((String) json.get("experimentInstanceId"), experimentBeginHandler);
                    experimentBeginTask.run();
                }
                if (scheduleEntity.getTaskBeanCode().equals(EnumExperimentTask.experimentFinishTask.getDesc())) {
                    *//*ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask(experimentInstanceService,
                            experimentParticipatorService, experimentTimerService, experimentTaskScheduleService, calculatorDispatcher,
                            (String) json.get("experimentInstanceId"), (Integer) json.get("period"));*//*
                    ExperimentFinishTask experimentFinishTask = new ExperimentFinishTask((String) json.get("experimentInstanceId"),
                            (Integer) json.get("period"), experimentFinishHandler);
                    experimentFinishTask.run();
                }
            }
        });
    }*/
}
