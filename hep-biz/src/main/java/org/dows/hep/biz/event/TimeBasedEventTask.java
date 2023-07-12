package org.dows.hep.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.enums.EnumEventTriggerSpan;
import org.dows.hep.api.enums.EnumExperimentEventState;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.dao.ExperimentEventDao;
import org.dows.hep.biz.dao.ExperimentInstanceDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.data.TimeBasedEventCollection;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:31
 */
@Slf4j
public class TimeBasedEventTask implements Callable<Integer>,Runnable {
    private final ExperimentCacheKey experimentKey;
    private final int RUNCode4Fail=-1;
    private final int RUNCode4Silence=0;
    private final int RUNCode4Succ=1;

    //最小间隔
    private final int DELAYSecondsMin=3;
    //暂停轮询间隔
    private final int DELAYSeconds4Pause=5;
    //失败重试间隔
    private final int DELAYSeconds4Fail=5;
    //子任务并发数
    private final int CONCURRENTNum=4;

    public TimeBasedEventTask(ExperimentCacheKey experimentKey){
        this.experimentKey=experimentKey;
    }
    public TimeBasedEventTask(String appId,String experimentInstanceId){
        this.experimentKey=new ExperimentCacheKey().setAppId(appId).setExperimentInstanceId(experimentInstanceId);
    }


    //region run
    @Override
    public void run() {
        try {
            call();
        }catch (Exception ex){
            logError(ex, "run",ex.getMessage());
            raiseScheduler(DELAYSecondsMin);
        }
    }

    @Override
    public Integer call() throws Exception {
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(experimentKey,true);
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMapPeriod())){
            logError("call", "missSetting");
            return RUNCode4Fail;
        }
        Integer experimentState=loadExperimentState();
        if(ShareUtil.XObject.isEmpty(experimentState)){
            logError("call", "missExperiment");
            return RUNCode4Fail;
        }
        if(experimentState.equals(EnumExperimentState.FINISH.getState())){
            logInfo("call", "finishedExperiment");
            return RUNCode4Silence;
        }
        if(experimentState.equals(EnumExperimentState.SUSPEND.getState())){
            logInfo("call", "pausedExperiment");
            raiseScheduler(DELAYSeconds4Pause);
            return RUNCode4Silence;
        }
        if(null==exptColl.getStartTime()){
            logError("call", "notStartExperiment");
            raiseScheduler(DELAYSeconds4Pause);
            return RUNCode4Silence;
        }
        TimeBasedEventCollection eventColl=TimeBasedEventCache.Instance().caffineCache().get(experimentKey,k->loadEvents(k,exptColl.getPeriods()) );
        if(ShareUtil.XObject.isEmpty(eventColl.getEventGroups()) ){
            logInfo("call", "emptyEvents");
            TimeBasedEventCache.Instance().caffineCache().invalidate(experimentKey);
            return RUNCode4Silence;
        }
        LocalDateTime dtNow=LocalDateTime.now();
        ExperimentTimePoint timePoint= ExperimentSettingCache.getTimePointByRealTimeSilence(exptColl, experimentKey, dtNow, true);
        if(ShareUtil.XObject.anyEmpty(timePoint,()->timePoint.getCntPauseSeconds())) {
            logError("call", "missTimePoint");
            raiseScheduler(DELAYSeconds4Fail);
            return RUNCode4Fail;
        }
        if(timePoint.getGameState()==EnumExperimentState.FINISH){
            logInfo("call", "finishedExperiment");
            return RUNCode4Silence;
        }
        exptColl.setPauseSeconds(timePoint.getCntPauseSeconds());
        eventColl.setNextTriggerTime(calcTriggeringTime(dtNow, exptColl,eventColl));
        List<List<TimeBasedEventCollection.TimeBasedEventGroup>> groups=eventColl.splitGroups(CONCURRENTNum);
        final RunStat runStat=new RunStat(groups.size());
        groups.forEach(i->{
            CompletableFuture.runAsync(()->runEventGroup(i,timePoint,eventColl,runStat), EventExecutor.Instance().getThreadPool())
                    .exceptionally(ex-> {
                        logError(ex, "runEventGroup", "caseEventIds:%s", String.join(",",
                                ShareUtil.XCollection.map(i, TimeBasedEventCollection.TimeBasedEventGroup::getCaseEventId)));
                        return null;
                    });
        });
        return RUNCode4Succ;
    }
    void runEventGroup(List<TimeBasedEventCollection.TimeBasedEventGroup> groups, ExperimentTimePoint timePoint, TimeBasedEventCollection eventColl, RunStat runStat){
        List<ExperimentEventEntity> triggeredEvents=new ArrayList<>();
        List<TimeBasedEventCollection.TimeBasedEventGroup> triggeredGroups=new ArrayList<>();
        LocalDateTime triggerTime=timePoint.getRealTime();
        for(TimeBasedEventCollection.TimeBasedEventGroup group:groups ) {
            runStat.todoCounter.addAndGet(group.getEventItems().size());
            if (null == group.getTriggeringTime()
                    || null != group.getTriggeredTime()
                    || triggerTime.isBefore(group.getTriggeringTime())) {
                continue;
            }
            group.setTriggeredTime(triggerTime).setTriggeredPeriod(timePoint.getPeriod());
            group.getEventItems().forEach(i -> {
                i.setTriggerTime(ShareUtil.XDate.localDT2Date(triggerTime))
                        .setTriggeredPeriod(timePoint.getPeriod())
                        .setTriggerGameDay(timePoint.getGameDay())
                        .setState(EnumExperimentEventState.TRIGGERED.getCode());
            });
            triggeredEvents.addAll(group.getEventItems());
            triggeredGroups.add(group);
        }
        boolean exceptionFlag=false;
        if(ShareUtil.XCollection.notEmpty(triggeredEvents)) {
            try {
                ExperimentEventRules.Instance().saveTriggeredTimeEvent(triggeredEvents,true);
                runStat.doneCounter.addAndGet(triggeredEvents.size());
            } catch (Exception ex) {
                groups.forEach(i -> i.setTriggeredTime(null).setTriggeredPeriod(null));
                logError(ex, "runEventGroup fail.", "eventIds:%s",
                        String.join(",",ShareUtil.XCollection.map(triggeredEvents,ExperimentEventEntity::getExperimentEventId)));
                runStat.failTheadCounter.incrementAndGet();
                exceptionFlag=true;
            }
        }
        if(!exceptionFlag) {
            eventColl.removeGroups(triggeredGroups);
        }
        if(runStat.theadCounter.decrementAndGet()>0) {
            return;
        }
        final LocalDateTime nextTime = eventColl.getNextTriggerTime();
        long delay = -1;
        runStat.todoCounter.addAndGet(-runStat.doneCounter.get());
        if(runStat.failTheadCounter.get()>0){
            raiseScheduler(delay=DELAYSeconds4Fail);
        } else {
            if (null != nextTime) {
                raiseScheduler(delay = Duration.between(LocalDateTime.now(), nextTime).toSeconds() + 2);
            }
        }
        logInfo("runEventGroup", "next:%s delay:%s failThread:%s done:%s todo:%s", nextTime,delay,
                runStat.failTheadCounter.get(),runStat.doneCounter.get(),runStat.todoCounter.get());
    }

    void raiseScheduler(long delaySeconds){
        EventScheduler.Instance().scheduleTimeBasedEvent(experimentKey.getAppId(),experimentKey.getExperimentInstanceId(),Math.max(DELAYSecondsMin, delaySeconds));
    }
    LocalDateTime calcTriggeringTime(LocalDateTime ldtNow, ExperimentSettingCollection exptCollection,TimeBasedEventCollection eventCollection){
        ExperimentTimePoint point;
        LocalDateTime nextTime=LocalDateTime.MAX;
        final long cntPauseSeconds=exptCollection.getCntPauseSeconds();
        for(TimeBasedEventCollection.TimeBasedEventGroup item:eventCollection.getEventGroups()){
            if(null==item.getRawTriggeringTime()){
                point=calcTriggerTime(exptCollection, item.getTriggerType(), item.getTriggerSpan());
                item.setRawTriggeringTime(point.getRealTime()).setTriggeringGameDay(point.getGameDay());
            }
            LocalDateTime triggeringTime=item.getRawTriggeringTime().plusSeconds(cntPauseSeconds);
            item.setTriggeringTime(triggeringTime);
            if( ldtNow.compareTo(triggeringTime)<0 && triggeringTime.compareTo(nextTime)<0  ){
                nextTime=triggeringTime;
            }
        }
        return nextTime.isEqual(LocalDateTime.MAX)?null:nextTime;
    }
    ExperimentTimePoint calcTriggerTime(ExperimentSettingCollection exptCollection, Integer period, EnumEventTriggerSpan span){
        ExperimentSettingCollection.ExperimentPeriodSetting periodSetting=exptCollection.getSettingByPeriod(period);
        if(null==periodSetting){
            return null;
        }
        int minRate=10;
        int maxRate=90;
        switch (span){
            case FRONT ->{minRate=10;maxRate=30;}
            case MIDDLE -> {minRate=35;maxRate=65;}
            case BACK ->{minRate=70;maxRate=90;}
        }
        double rate=randomInteger(minRate, maxRate)/100d;
        int triggerSeconds=periodSetting.getStartSecond()+(int) (rate*periodSetting.getTotalSeconds());
        int gameDay=periodSetting.getStartGameDay()-1+(int)Math.ceil(rate*periodSetting.getTotalDays());
        return new ExperimentTimePoint()
                .setRealTime(exptCollection.getStartTime().plusSeconds(triggerSeconds))
                .setGameDay(gameDay);
    }
    //endregion

    //region load
    Integer loadExperimentState() {
        return getExperimentInstanceDao().getById(experimentKey.getAppId(), experimentKey.getExperimentInstanceId(),
                ExperimentInstanceEntity::getState)
                .map(ExperimentInstanceEntity::getState)
                .orElse(null);
    }


    TimeBasedEventCollection loadEvents(ExperimentCacheKey experimentKey,Integer maxPeriod){
        TimeBasedEventCollection rst=new TimeBasedEventCollection()
                .setExperimentInstanceId(experimentKey.getExperimentInstanceId());
        List<ExperimentEventEntity> rowsEvent=getExperimentEventDao().getTimeEventByExperimentId(experimentKey.getAppId(), experimentKey.getExperimentInstanceId(),
                null, maxPeriod,EnumExperimentEventState.INIT.getCode(),
                ExperimentEventEntity::getId,
                ExperimentEventEntity::getAppId,
                ExperimentEventEntity::getExperimentEventId,
                ExperimentEventEntity::getExperimentInstanceId,
                ExperimentEventEntity::getExperimentGroupId,
                ExperimentEventEntity::getExperimentOrgId,
                ExperimentEventEntity::getExperimentPersonId,
                ExperimentEventEntity::getPersonName,
                ExperimentEventEntity::getPeriods,
                ExperimentEventEntity::getCasePersonId,
                ExperimentEventEntity::getCaseEventId,
                ExperimentEventEntity::getTriggerType,
                ExperimentEventEntity::getTriggerTime,
                ExperimentEventEntity::getTriggerGameDay,
                ExperimentEventEntity::getState
                );
        if(ShareUtil.XCollection.isEmpty(rowsEvent)) {
            return rst;
        }
        Map<String, TimeBasedEventCollection.TimeBasedEventGroup> mapItems=new HashMap<>();
        for(ExperimentEventEntity item:rowsEvent) {
            String key = String.format("%s-%s", item.getCaseEventId(), item.getCasePersonId());
            mapItems.computeIfAbsent(key, k ->
                TimeBasedEventCollection.TimeBasedEventGroup.builder()
                        .casePersonId(item.getCasePersonId())
                        .caseEventId(item.getCaseEventId())
                        .triggerType(item.getTriggerType())
                        .triggerSpan(EnumEventTriggerSpan.of(item.getTriggerSpan()))
                        .eventItems(new ArrayList<>())
                        .build())
                    .getEventItems().add(item);
        }
        rst.setEventGroups(new ArrayList<>(mapItems.values()));
        mapItems.clear();
        rowsEvent.clear();
        return rst;
    }
    //endregion

    //region dao
    private ExperimentInstanceDao experimentInstanceDao;
    private ExperimentInstanceDao getExperimentInstanceDao(){
        if(null==experimentInstanceDao){
            experimentInstanceDao=getDao(ExperimentInstanceDao.class);
        }
        return experimentInstanceDao;
    }


    private ExperimentEventDao experimentEventDao;
    private ExperimentEventDao getExperimentEventDao(){
        if(null==experimentEventDao){
            experimentEventDao=getDao(ExperimentEventDao.class);
        }
        return experimentEventDao;
    }
    private <T> T getDao(Class<T> clazz){
        return CrudContextHolder.getBean(clazz);
    }
    //endregion

    //region tools
    private void logError(String func, String msg,Object... args){
        logError(null, func,msg,args);
    }
    private void logError(Throwable ex, String func, String msg,Object... args){
        String str=String.format("TimeBasedEventTask.%s input:%s %s", func,this.experimentKey,String.format(Optional.ofNullable(msg).orElse(""), args));
        log.error(str,ex);
        log.info(str);
    }
    private void logInfo(String func, String msg,Object... args){
        String str=String.format("TimeBasedEventTask.%s input:%s %s", func,this.experimentKey,String.format(Optional.ofNullable(msg).orElse(""), args));
        log.info(str);
    }

    static int randomInteger(int min, int max){
        return ThreadLocalRandom.current().nextInt(min,max);
    }
    //endregion

    public static class RunStat {
        public RunStat(Integer threadSize){
            theadCounter=new AtomicInteger(threadSize);
        }
        public final AtomicInteger theadCounter;
        public final AtomicInteger failTheadCounter=new AtomicInteger();
        public final AtomicInteger doneCounter=new AtomicInteger();

        public final AtomicInteger todoCounter=new AtomicInteger();
    }


}
