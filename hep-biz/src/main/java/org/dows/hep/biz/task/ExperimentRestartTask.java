package org.dows.hep.biz.task;

/**
 * 实验重启任务
 * 重新初始化调度器
 */
public class ExperimentRestartTask implements Runnable {


    /**
     * todo
     * 1.获取实验计时器，
     * 2.获取experiment_task_schedule
     * 3.判断experiment_task_schedule.restartTime > experiment_task_schedule.execute_time && executed == '未执行' do 执行原来的任务
     * 4.判断experiment_task_schedule.restartTime <= experiment_task_schedule.execute_time && executed == '未执行' do 重新拉起定时任务
     */
    @Override
    public void run() {

    }


}
