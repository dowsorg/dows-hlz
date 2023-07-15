package org.dows.hep.biz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.entity.ExperimentTaskScheduleEntity;
import org.dows.hep.service.ExperimentTaskScheduleService;

import java.util.List;

/**
 * 实验重启任务
 * 重新初始化调度器
 */
@Slf4j
@RequiredArgsConstructor
public class ExperimentRestartTask implements Runnable {
    private final ExperimentTaskScheduleService experimentTaskScheduleService;
    // 应用ID(需要根据应用ID查询该应用下的实验)
    private final String appId;

    private final Integer period;

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
                .eq(ExperimentTaskScheduleEntity::getPeriods,period)
                .eq(ExperimentTaskScheduleEntity::getAppId,appId)
                .list();
        // 2、判断是在重启开始前应该执行的还是之后执行的任务，如果本来应该是重启之前执行的任务，重启的时候就执行，否则的话就拉起任务
        if (scheduleEntityList != null && scheduleEntityList.size() > 0) {
            scheduleEntityList.forEach(scheduleEntity -> {
                if (scheduleEntity.getRestartTime() != null && scheduleEntity.getRestartTime().after(scheduleEntity.getExecuteTime())) {
                    //2.1、直接执行中途断了的任务
                    if(scheduleEntity.getTaskBeanCode().equals("experimentCalcTask")){
                    //2.2、
                    }
                    if(scheduleEntity.getTaskBeanCode().equals("experimentFinishTask")){

                    }
                    if(scheduleEntity.getTaskBeanCode().equals("experimentPeriodStartNoticeTask")){

                    }
                    if(scheduleEntity.getTaskBeanCode().equals("experimentPeriodEndNoticeTask")){

                    }
                }
            });
        }
    }
}
