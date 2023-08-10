package org.dows.hep.websocket.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.websocket.HepClientManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
@Slf4j
@Component
public class MsgScheduler implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;
    private static ThreadPoolTaskScheduler taskScheduler;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MsgScheduler.applicationContext = applicationContext;
        taskScheduler = applicationContext.getBean(ThreadPoolTaskScheduler.class);
    }


    // 任务队列管理
    private static ConcurrentHashMap<String, ScheduledFuture> futureMap = new ConcurrentHashMap();


    /**
     * Cron Example patterns:
     * <li>"0 0 * * * *" = the top of every hour of every day.</li>
     * <li>"0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.</li>
     * <li>"0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.</li>
     * <li>"0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays</li>
     * <li>"0 0 0 25 12 ?" = every Christmas Day at midnight</li>
     */
    public static void schedule(Runnable task, String cron, String msgId, Long duration) {
        if (cron == null || "".equals(cron)) {
            cron = "0 * * * * *";
        }
        ScheduledFuture future = null;
        if (duration != null && duration != 0L) {
            future = taskScheduler.scheduleAtFixedRate(task, Instant.now(), Duration.ofSeconds(duration));
        } else {
            future = taskScheduler.schedule(task, new CronTrigger(cron));
        }
        // 加入到队列中
        futureMap.put(msgId, future);
    }

    public static Object remove(String msgId) {
        log.info("移除消息：{}", msgId);
        ScheduledFuture scheduledFuture = futureMap.get(msgId);
        if (scheduledFuture != null) {
            // 取消定时任务
            scheduledFuture.cancel(true);
            // 如果任务取消需要消耗点时间，设置线程池setRemoveOnCancelPolicy为true模式
            boolean cancelled = scheduledFuture.isCancelled();
            if (!cancelled) {
                scheduledFuture.cancel(true);
                log.info("定时任务取消中:{}", msgId);
            }
            // 最后从队列中删除
            futureMap.remove(msgId);
            // 移除消息id及消息
            HepClientManager.removeMsgById(msgId);
        }
        return null;
    }
}
