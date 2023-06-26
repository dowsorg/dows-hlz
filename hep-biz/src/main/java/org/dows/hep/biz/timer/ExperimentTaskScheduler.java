package org.dows.hep.biz.timer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
@Component
public class ExperimentTaskScheduler {

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public void schedule(Runnable task, Date date) {
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(task, date);
    /*    try {
            Object o = schedule.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }*/
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
        threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
    }

    /**
     * shutdown and init
     */
    public void reset() {
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