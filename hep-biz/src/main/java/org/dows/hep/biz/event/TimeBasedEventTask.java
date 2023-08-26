package org.dows.hep.biz.event;

import org.dows.hep.api.enums.EnumEventTriggerSpan;
import org.dows.hep.api.enums.EnumExperimentEventState;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.data.TimeBasedEventCollection;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:31
 */

public class TimeBasedEventTask extends BaseEventTask  {


    //最小间隔
    private final int DELAYSecondsMin=3;
    //轮询间隔
    private final int DELAYSeconds4Poll =30;
    //重试间隔
    private final int DELAYSeconds4Fail=10;
    //子任务并发数
    private final int CONCURRENTNum=3;

    private final int MAXRetry=3;

    public TimeBasedEventTask(ExperimentCacheKey experimentKey){
        super(experimentKey);
    }



    //region run

    @Override
    protected void exceptionally(Exception ex) {
        super.exceptionally(ex);
        raiseScheduler(DELAYSeconds4Fail, false, true);
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
            return RUNCode4Silence;
        }
        if(null==exptColl.getSandStartTime()){
            logError("call", "notStartExperiment");
            raiseScheduler(DELAYSeconds4Poll,false,true);
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
            raiseScheduler(DELAYSeconds4Fail,false,true);
            return RUNCode4Fail;
        }
        if (timePoint.getGameState() == EnumExperimentState.FINISH) {
            logInfo("call", "finishedExperiment");
            return RUNCode4Silence;
        }

        exptColl.setPauseSeconds(timePoint.getCntPauseSeconds());
        eventColl.setNextTriggerTime(calcTriggeringTime(dtNow, exptColl,eventColl));
        List<List<TimeBasedEventCollection.TimeBasedEventGroup>> groups=eventColl.splitGroups(CONCURRENTNum);
        final RunStat runStat=new RunStat(groups.size());
        final ExecutorService executor=EventExecutor.Instance().getThreadPool();
        groups.forEach(i->{
            CompletableFuture.runAsync(()->runEventGroup(i,timePoint,eventColl,runStat), executor)
                    .exceptionally(ex-> {
                        logError(ex, "runEventGroup", "caseEventIds:%s", String.join(",",
                                ShareUtil.XCollection.map(i, TimeBasedEventCollection.TimeBasedEventGroup::getCaseEventId)));
                        return null;
                    });
        });
        return RUNCode4Succ;
    }
    void runEventGroup(List<TimeBasedEventCollection.TimeBasedEventGroup> groups, ExperimentTimePoint timePoint, TimeBasedEventCollection eventColl, RunStat runStat){
        List<ExperimentEventEntity> triggeringEvents=new ArrayList<>();
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
                        .setState(EnumExperimentEventState.TRIGGERED.getCode())
                        .setTriggeringTime(ShareUtil.XDate.localDT2Date(group.getTriggeringTime()))
                        .setTriggeringGameDay(group.getTriggeringGameDay());
            });
            triggeringEvents.addAll(group.getEventItems());
            triggeredGroups.add(group);
        }
        boolean exceptionFlag=false;
        if(ShareUtil.XCollection.notEmpty(triggeringEvents)) {
            try {
                ExperimentEventRules.Instance().saveTriggeredTimeEvent(triggeringEvents,true);
                runStat.doneCounter.addAndGet(triggeringEvents.size());
            } catch (Exception ex) {
                groups.forEach(i -> {
                    if(i.getRetryTimes().incrementAndGet()>=MAXRetry){
                        eventColl.removeGroup(i);
                        experimentKey.getMaxRetry().incrementAndGet();
                        logError("runEventGroup", "maxRetry.  eventIds:%s",
                                String.join(",", ShareUtil.XCollection.map(i.getEventItems(), ExperimentEventEntity::getExperimentEventId)));
                    }
                    else {
                        i.setTriggeredTime(null).setTriggeredPeriod(null);
                    }
                });
                logError(ex, "runEventGroup", " fail. eventIds:%s",
                        String.join(",", ShareUtil.XCollection.map(triggeringEvents, ExperimentEventEntity::getExperimentEventId)));
                runStat.failTheadCounter.incrementAndGet();
                exceptionFlag = true;
            }
        }
        if(!exceptionFlag) {
            eventColl.removeGroups(triggeredGroups);
        }
        if(runStat.theadCounter.decrementAndGet()>0) {
            return;
        }
        final LocalDateTime nextTime = eventColl.getNextTriggerTime();
        runStat.todoCounter.addAndGet(-runStat.doneCounter.get());
        if(runStat.failTheadCounter.get()>0){
            raiseScheduler(DELAYSeconds4Fail,false,false);
        } else {
            if (null != nextTime) {
                raiseScheduler(nextTime.plusSeconds(2), true, false);
            }
        }
        logInfo("runEventGroup", "next:%s failThread:%s done:%s todo:%s", nextTime,
                runStat.failTheadCounter.get(),runStat.doneCounter.get(),runStat.todoCounter.get());
    }

    void raiseScheduler(long delaySeconds,boolean resetRetry,boolean incrRetry){
        raiseScheduler(LocalDateTime.now().plusSeconds(delaySeconds), resetRetry, incrRetry);
    }
    void raiseScheduler(LocalDateTime nextTime,boolean resetRetry,boolean incrRetry) {
        if(resetRetry){
            experimentKey.getMaxRetry().set(0);
        } else if ((incrRetry? experimentKey.getMaxRetry().incrementAndGet():experimentKey.getMaxRetry().get()) >= MAXRetry) {
            logError("raiseScheduler", "maxRetry");
            return;
        }

        final LocalDateTime minNext=LocalDateTime.now().plusSeconds(DELAYSecondsMin);
        EventScheduler.Instance().scheduleTimeBasedEvent(experimentKey, minNext.isBefore(nextTime)?nextTime:minNext);
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
            if( ldtNow.compareTo(triggeringTime)<0 && triggeringTime.compareTo(nextTime)<0  ) {
                nextTime = triggeringTime;
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
        double rate=ShareUtil.XRandom.randomInteger(minRate, maxRate)/100d;
        int triggerSeconds=periodSetting.getStartSecond()+(int) (rate*periodSetting.getTotalSeconds());
        int gameDay=periodSetting.getStartGameDay()-1+(int)Math.ceil(rate*periodSetting.getTotalDays());
        return new ExperimentTimePoint()
                .setRealTime(exptCollection.getSandStartTime().plusSeconds(triggerSeconds))
                .setGameDay(gameDay);
    }
    //endregion

    //region load



    TimeBasedEventCollection loadEvents(ExperimentCacheKey experimentKey,Integer maxPeriod){
        TimeBasedEventCollection rst=new TimeBasedEventCollection()
                .setExperimentInstanceId(experimentKey.getExperimentInstanceId());
        List<ExperimentEventEntity> rowsEvent=this.experimentEventDao.getTimeEventByExperimentId(experimentKey.getAppId(), experimentKey.getExperimentInstanceId(),
                null, maxPeriod,EnumExperimentEventState.INIT.getCode(),
                ExperimentEventEntity::getId,
                ExperimentEventEntity::getAppId,
                ExperimentEventEntity::getExperimentEventId,
                ExperimentEventEntity::getExperimentInstanceId,
                ExperimentEventEntity::getExperimentGroupId,
                ExperimentEventEntity::getExperimentOrgId,
                ExperimentEventEntity::getExperimentPersonId,
                ExperimentEventEntity::getAccountId,
                ExperimentEventEntity::getPersonName,
                ExperimentEventEntity::getPeriods,
                ExperimentEventEntity::getCasePersonId,
                ExperimentEventEntity::getCaseEventId,
                ExperimentEventEntity::getTriggerType,
                ExperimentEventEntity::getTriggerSpan,
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



}
