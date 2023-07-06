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
    private final int DELAYSecondsMin=2;
    //暂停轮询间隔
    private final int DELAYSeconds4Pause=5;
    //失败重试间隔
    private final int DELAYSeconds4Fail=5;

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
        if(null==exptColl.getStartTime()){
            logError("call", "notStartExperiment");
            raiseScheduler(DELAYSeconds4Pause);
            return RUNCode4Silence;
        }
        Integer experimentState=loadExperimentState();
        if(ShareUtil.XObject.isEmpty(experimentState)){
            logError("call", "missExperiment");
            return RUNCode4Fail;
        }
        if(experimentState.equals(EnumExperimentState.SUSPEND.getState())){
            logInfo("call", "pausedExperiment");
            raiseScheduler(DELAYSeconds4Pause);
            return RUNCode4Silence;
        }
        if(experimentState.equals(EnumExperimentState.FINISH.getState())){
            logInfo("call", "finishedExperiment");
            return RUNCode4Silence;
        }
        TimeBasedEventCollection eventColl=TimeBasedEventCache.Instance().caffineCache().get(experimentKey,this::loadEvents);
        if(ShareUtil.XObject.isEmpty(eventColl.getEventGroups()) ){
            logInfo("call", "emptyEvents");
            return RUNCode4Silence;
        }
        LocalDateTime dtNow=LocalDateTime.now();
        ExperimentTimePoint timePoint= ExperimentSettingCache.getTimePointByRealTimeSilence(exptColl, experimentKey, dtNow, false);
        if(ShareUtil.XObject.anyEmpty(timePoint,()->timePoint.getCntPauseSeconds())){
            logError("call", "missTimePoint");
            raiseScheduler(DELAYSeconds4Fail);
            return RUNCode4Fail;
        }
        exptColl.setPauseSeconds(timePoint.getCntPauseSeconds());
        eventColl.setNextTriggerTime(calcTriggeringTime(exptColl,eventColl));
        final int paral=4;
        List<List<TimeBasedEventCollection.TimeBasedEventGroup>> groups=eventColl.splitGroups(paral);
        AtomicInteger runCounter=new AtomicInteger(groups.size());
        AtomicInteger failCounter=new AtomicInteger();
        groups.forEach(i->{
            CompletableFuture.runAsync(()->runEventGroup(i,eventColl,runCounter,failCounter), EventExecutor.Instance().getThreadPool());
        });
        return RUNCode4Succ;
    }
    void runEventGroup(List<TimeBasedEventCollection.TimeBasedEventGroup> groups, TimeBasedEventCollection eventColl, AtomicInteger runCounter,AtomicInteger failCounter){
        List<ExperimentEventEntity> triggeredEvents=new ArrayList<>();
        LocalDateTime triggerTime=LocalDateTime.now();
        for(TimeBasedEventCollection.TimeBasedEventGroup group:groups ) {
            if (null == group.getTriggeringTime()
                    || triggerTime.isBefore(group.getTriggeringTime())
                    || null != group.getTriggeredTime()) continue;
            group.setTriggeredTime(triggerTime);
            group.getEventItems().forEach(i -> {
                i.setTriggerTime(ShareUtil.XDate.localDT2Date(triggerTime))
                        .setTriggerGameDay(group.getTriggeringGameDay())
                        .setState(EnumExperimentEventState.TRIGGERED.getCode());
            });
            triggeredEvents.addAll(group.getEventItems());
        }
        boolean exceptionFlag=false;
        if(ShareUtil.XCollection.notEmpty(triggeredEvents)) {
            try {
                ExperimentEventRules.Instance().saveTriggeredTimeEvent(triggeredEvents);
            } catch (Exception ex) {
                groups.forEach(i -> i.setTriggeredTime(null));
                failCounter.incrementAndGet();
                exceptionFlag=true;
            }
        }
        if(!exceptionFlag) {
            eventColl.removeGroups(groups);
        }
        if(runCounter.decrementAndGet()>0) {
            return;
        }
        if(failCounter.get()>0){
            raiseScheduler(DELAYSeconds4Fail);
            return;
        }
        final LocalDateTime nextTime=eventColl.getNextTriggerTime();
        if(null!= nextTime) {
            long delay = Duration.between(LocalDateTime.now(), nextTime).toSeconds() + 2;
            raiseScheduler(Math.max(DELAYSecondsMin, delay));
        }
    }

    void raiseScheduler(long delaySeconds){
        EventScheduler.Instance().scheduleTimeBasedEvent(experimentKey.getAppId(),experimentKey.getExperimentInstanceId(),delaySeconds);
    }
    LocalDateTime calcTriggeringTime(ExperimentSettingCollection exptCollection,TimeBasedEventCollection eventCollection){
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
            if(triggeringTime.compareTo(nextTime)<0){
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


    TimeBasedEventCollection loadEvents(ExperimentCacheKey experimentKey){
        TimeBasedEventCollection rst=new TimeBasedEventCollection()
                .setExperimentInstanceId(experimentKey.getExperimentInstanceId());
        List<ExperimentEventEntity> rowsEvents=getExperimentEventDao().getTimeEventByExperimentId(experimentKey.getAppId(), experimentKey.getExperimentInstanceId(),
                null, EnumExperimentEventState.INIT.getCode(),
                ExperimentEventEntity::getId,
                ExperimentEventEntity::getExperimentEventId,
                ExperimentEventEntity::getExperimentInstanceId,
                ExperimentEventEntity::getExperimentGroupId,
                ExperimentEventEntity::getExperimentOrgId,
                ExperimentEventEntity::getExperimentPersonId,
                ExperimentEventEntity::getPeriods,
                ExperimentEventEntity::getCasePersonId,
                ExperimentEventEntity::getCaseEventId,
                ExperimentEventEntity::getTriggerType,
                ExperimentEventEntity::getTriggerTime,
                ExperimentEventEntity::getTriggerGameDay,
                ExperimentEventEntity::getState
                );
        if(ShareUtil.XCollection.isEmpty(rowsEvents)) {
            return rst;
        }
        Map<String, TimeBasedEventCollection.TimeBasedEventGroup> mapItems=new HashMap<>();
        for(ExperimentEventEntity item:rowsEvents) {
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
}
