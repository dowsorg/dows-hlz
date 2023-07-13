package org.dows.hep.biz.event;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.dows.hep.biz.cache.BaseManulCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.util.ShareBiz;

import java.util.concurrent.*;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:34
 */
public class EventScheduler {
    static final int DFTCoreSize=4;
    private static final EventScheduler s_instance=new EventScheduler(DFTCoreSize);
    public static EventScheduler Instance(){
        return s_instance;
    }
    private EventScheduler(int coreSize){
        ScheduledThreadPoolExecutor executor=new ScheduledThreadPoolExecutor(coreSize,
                new ThreadFactoryBuilder().setNameFormat("eventScheduler-%d").build(),
                new ThreadPoolAbortPolicy());
        executor.setRemoveOnCancelPolicy(true);
        scheduledExecutor=executor;

    }
    private final ScheduledThreadPoolExecutor scheduledExecutor;

    private final ExperimentFutureCache futureCache=new ExperimentFutureCache();

    //region schedule
    /**
     * 按时间触发事件定时
     * @param appId
     * @param experimentId
     * @param delaySeconds
     * @return
     */
    public ScheduledFuture<?> scheduleTimeBasedEvent(String appId,String experimentId, long delaySeconds) {
        appId= ShareBiz.checkAppId(appId,experimentId);
        final ExperimentCacheKey cacheKey = new ExperimentCacheKey(appId, experimentId);
        final String exclusiveKey = String.format("timeevent:%s", cacheKey);
        final Runnable task = new TimeBasedEventTask(cacheKey);
        return scheduleExclusive(exclusiveKey, task, delaySeconds);
    }

    public ScheduledFuture<?> scheduleExclusive(String exclusiveKey, Runnable  cmd, long delaySeconds) {
        Future<?>[] buffer = futureCache.caffineCache().get(exclusiveKey, key -> new Future<?>[2]);
        ScheduledFuture<?> rst=null;
        final long additiveSeconds=5;
        synchronized (buffer){
            clearExclusiveTaskBuffer(buffer);
            buffer[0]=rst=schedule(decoratedExclusiveTask(exclusiveKey,cmd),delaySeconds,TimeUnit.SECONDS);
            buffer[1]=schedule(decoratedExclusiveTask(exclusiveKey,cmd),delaySeconds+additiveSeconds,TimeUnit.SECONDS);
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
    private Future<?>[] clearExclusiveTaskBuffer(String exclusiveKey){
        return clearExclusiveTaskBuffer(futureCache.caffineCache().getIfPresent(exclusiveKey));
    }
    private Future<?>[] clearExclusiveTaskBuffer(Future<?>[] buffer){
        if(null==buffer){
            return null;
        }
        final BlockingQueue queue= scheduledExecutor.getQueue();
        synchronized (buffer){
            for(int i=0;i<buffer.length;i++){
                Future future=buffer[i];
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

    public static class ExperimentFutureCache extends BaseManulCache<String,Future<?>[]> {

        private ExperimentFutureCache() {
            super(10, 20, 60*60*12, 0);
        }
    }
}
