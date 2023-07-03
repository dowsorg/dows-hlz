package org.dows.hep.biz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExperimentTaskScheduler {

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    // 任务队列管理
    private ConcurrentHashMap<String, ScheduledFuture> futureMap = new ConcurrentHashMap();


    public void schedule(Runnable task, Date date) {
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(task, date);
        // 加入到队列中
        futureMap.put(String.valueOf(date.getTime()), future);
    }

    /**
     * Cron Example patterns:
     * <li>"0 0 * * * *" = the top of every hour of every day.</li>
     * <li>"0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.</li>
     * <li>"0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.</li>
     * <li>"0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays</li>
     * <li>"0 0 0 25 12 ?" = every Christmas Day at midnight</li>
     */
    public void schedule(Runnable task, String cron) {
        if (cron == null || "".equals(cron)) {
            cron = "0 * * * * *";
        }
        ScheduledFuture future = threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
        // 加入到队列中
        futureMap.put(cron, future);
    }


    /**
     * 移除定时任务
     *
     * @param cron
     * @return
     */
    public Object remove(String cron) {
        ScheduledFuture scheduledFuture = futureMap.get(cron);
        if (scheduledFuture != null) {
            // 取消定时任务
            scheduledFuture.cancel(true);
            // 如果任务取消需要消耗点时间
            boolean cancelled = scheduledFuture.isCancelled();
            while (!cancelled) {
                scheduledFuture.cancel(true);
                log.info("定时任务取消中:{}", cron);
            }
            // 最后从队列中删除
            futureMap.remove(cron);
        }
        return null;
    }

    /**
     * 重设时间定时任务
     *
     * @param task
     * @param date
     */
    public void reset(Runnable task, Date date) {
        remove(String.valueOf(date.getTime()));
        schedule(task, date);
    }

    /**
     * 重设cron表达式任务
     *
     * @param task
     * @param cron
     */
    public void reset(Runnable task, String cron) {
        remove(cron);
        schedule(task, cron);
    }

    /**
     * shutdown and init
     */
    public void resetSchedule() {
        threadPoolTaskScheduler.shutdown();
        threadPoolTaskScheduler.initialize();
    }

    /**
     * shutdown before a new schedule operation
     *
     * @param task
     * @param cron
     */
    public void resetSchedule(Runnable task, String cron) {
        shutdown();
        threadPoolTaskScheduler.initialize();
        schedule(task, cron);
    }

    /**
     * shutdown
     */
    public void shutdown() {
        threadPoolTaskScheduler.shutdown();
    }
}