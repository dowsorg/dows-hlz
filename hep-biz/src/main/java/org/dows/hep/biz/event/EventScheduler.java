package org.dows.hep.biz.event;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.biz.cache.BaseManulCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.sysevent.SysEventTask;
import org.dows.hep.biz.util.ShareBiz;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:34
 */
@Slf4j
@Component
public class EventScheduler implements ApplicationListener<ContextClosedEvent> {
    static final int DFTCoreSize=4;

    static final long additiveSeconds4UserEvent = 10;
    static final long additiveSeconds4FollowupPlan = 3;

    static final long additiveSeconds4SysEvent = 3;
    private static volatile EventScheduler s_instance;
    public static EventScheduler Instance(){
        return s_instance;
    }
    private EventScheduler(){
        s_instance=this;
        ScheduledThreadPoolExecutor executor=new ScheduledThreadPoolExecutor(DFTCoreSize,
                new ThreadFactoryBuilder().setNameFormat("eventScheduler-%d").build(),
                new ThreadPoolAbortPolicy());
        executor.setRemoveOnCancelPolicy(true);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        scheduledExecutor=executor;

    }
    private final ScheduledThreadPoolExecutor scheduledExecutor;

    private final ExperimentFutureCache futureCache=new ExperimentFutureCache();

    //region schedule

    /**
     * 随访计划定时
     * @param appId
     * @param experimentId
     * @param delaySeconds
     * @return
     */

    public ScheduledFuture<?> scheduleFollowUpPlan(String appId,String experimentId, long delaySeconds) {
        appId = ShareBiz.checkAppId(appId, experimentId);
        final ExperimentCacheKey experimentKey = new ExperimentCacheKey(appId, experimentId);
        log.info("EventScheduler scheduleFollowUpPlan. {} {}", scheduledExecutor, experimentKey);
        return scheduleFollowUpPlan(experimentKey, LocalDateTime.now().plusSeconds(delaySeconds));
    }
    public ScheduledFuture<?> scheduleFollowUpPlan(ExperimentCacheKey experimentKey, LocalDateTime nextTime){
        return null;
    /*    final String exclusiveKey = String.format("followup:%s", experimentKey.getKeyString());
        return scheduleExclusive(exclusiveKey, new FollowupPlanTask(experimentKey), nextTime,additiveSeconds4FollowupPlan);
 */   }
    /**
     * 系统事件定时
     * @param appId
     * @param experimentId
     * @param delaySeconds
     * @return
     */

    public ScheduledFuture<?> scheduleSysEvent(String appId,String experimentId, long delaySeconds) {
        if(ConfigExperimentFlow.SWITCH2TaskSchedule){
            return null;
        }
        appId = ShareBiz.checkAppId(appId, experimentId);
        final ExperimentCacheKey experimentKey = new ExperimentCacheKey(appId, experimentId);
        log.info("EventScheduler scheduleSysEvent. {} {}", scheduledExecutor, experimentKey);
        return scheduleSysEvent(experimentKey, LocalDateTime.now().plusSeconds(delaySeconds));
    }
    public ScheduledFuture<?> scheduleSysEvent(ExperimentCacheKey experimentKey, LocalDateTime nextTime){
        if(ConfigExperimentFlow.SWITCH2TaskSchedule){
            return null;
        }
        final String exclusiveKey = String.format("sysevent:%s", experimentKey.getKeyString());
        return scheduleExclusive(exclusiveKey, new SysEventTask(experimentKey), nextTime,additiveSeconds4SysEvent);
    }
    /**
     * 按时间触发事件定时
     * @param appId
     * @param experimentId
     * @param delaySeconds
     * @return
     */
    public ScheduledFuture<?> scheduleTimeBasedEvent(String appId,String experimentId, long delaySeconds) {
        appId = ShareBiz.checkAppId(appId, experimentId);
        final ExperimentCacheKey experimentKey = new ExperimentCacheKey(appId, experimentId);
        log.info("EventScheduler scheduleTimeBasedEvent. {} {}", scheduledExecutor, experimentKey);
        return scheduleTimeBasedEvent(experimentKey, LocalDateTime.now().plusSeconds(delaySeconds));
    }
    public ScheduledFuture<?> scheduleTimeBasedEvent(ExperimentCacheKey experimentKey, LocalDateTime nextTime){
        final String exclusiveKey = String.format("timeevent:%s", experimentKey.getKeyString());
        return scheduleExclusive(exclusiveKey, new TimeBasedEventTask(experimentKey), nextTime,additiveSeconds4UserEvent);
    }

    public ScheduledFuture<?> scheduleExclusive(String exclusiveKey, Runnable  cmd, LocalDateTime nextTime,long additiveSeconds) {
        ScheduledFuture<?>[] buffer = futureCache.caffineCache().get(exclusiveKey, key -> new ScheduledFuture<?>[2]);
        ScheduledFuture<?> rst = null;

        synchronized (buffer) {
            clearExclusiveTaskBuffer(buffer);
            final long delaySeconds = Math.max(1, Duration.between(LocalDateTime.now(), nextTime).toSeconds());
            buffer[0] = rst = schedule(decoratedExclusiveTask(exclusiveKey, cmd), delaySeconds, TimeUnit.SECONDS);
            buffer[1] = schedule(decoratedExclusiveTask(exclusiveKey, cmd), delaySeconds + additiveSeconds, TimeUnit.SECONDS);
        }
        return rst;
    }
    public ScheduledFuture<?> schedule(Runnable  cmd, long delay, TimeUnit unit) {
        return scheduledExecutor.schedule(cmd, delay, unit);
    }
    //endregion

    private Runnable decoratedExclusiveTask(String exclusiveKey, Runnable runnable){
        return ()->{
            clearExclusiveTaskBuffer(exclusiveKey);
            runnable.run();
        };
    }
    private ScheduledFuture<?>[] clearExclusiveTaskBuffer(String exclusiveKey){
        return clearExclusiveTaskBuffer(futureCache.caffineCache().getIfPresent(exclusiveKey));
    }
    private ScheduledFuture<?>[] clearExclusiveTaskBuffer(ScheduledFuture<?>[] buffer){
        if(null==buffer){
            return null;
        }
        final BlockingQueue queue= scheduledExecutor.getQueue();
        synchronized (buffer){
            for(int i=0;i<buffer.length;i++){
                ScheduledFuture future=buffer[i];
                if(null==future){
                    continue;
                }
                if(queue.contains(future)) {
                    future.cancel(false);
                    buffer[i]=null;
                }
            }
        }
        return buffer;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        this.shutDown();
        EventExecutor.Instance().shutDown();
    }

    public void shutDown(){
        try{
            log.info("EventScheduler closing. {}",scheduledExecutor);
            scheduledExecutor.shutdownNow();
            log.info("EventScheduler closed. {}",scheduledExecutor);
        }catch (Exception ex){
            log.error(String.format("EventScheduler failClose. %s",scheduledExecutor) ,ex);
        }
    }

    public static class ExperimentFutureCache extends BaseManulCache<String,ScheduledFuture<?>[]> {

        private ExperimentFutureCache() {
            super(10, 120, 60*60*12, 0);
        }
    }


}
