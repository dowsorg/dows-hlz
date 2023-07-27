package org.dows.hep.biz.event;

import org.dows.hep.api.enums.EnumExperimentEventState;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.data.PersonBasedEventCollection;
import org.dows.hep.biz.spel.SpelInvoker;
import org.dows.hep.biz.spel.SpelPersonContext;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author : wuzl
 * @date : 2023/7/19 13:33
 */
public class PersonBasedEventTask extends BaseEventTask{

    //子任务并发数
    private final int CONCURRENTNum=3;
    private final int MAXRetry=3;
    public PersonBasedEventTask(ExperimentCacheKey experimentKey) {
        super(experimentKey);
    }

    public static void runPersonBasedEvent(String appId,String experimentInstanceId) {
        new PersonBasedEventTask(ExperimentCacheKey.create(appId, experimentInstanceId)).run();
    }
    public static void runPersonBasedEventAsync(String appId,String experimentInstanceId) {
        CompletableFuture.runAsync(() -> runPersonBasedEvent(appId, experimentInstanceId));
    }
    @Override
    public Integer call() throws Exception {
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(experimentKey,true);
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMapPeriod())){
            logError("call", "missSetting");
            return RUNCode4Fail;
        }
        PersonBasedEventCollection eventColl=PersonBasedEventCache.Instance().loadingCache().get(experimentKey);
        if(ShareUtil.XObject.isEmpty(eventColl.getEventGroups()) ){
            logInfo("call", "emptyEvents");
            PersonBasedEventCache.Instance().caffineCache().invalidate(experimentKey);
            return RUNCode4Silence;
        }
        LocalDateTime dtNow=LocalDateTime.now();
        ExperimentTimePoint timePoint= ExperimentSettingCache.getTimePointByRealTimeSilence(exptColl, experimentKey, dtNow, true);
        if(ShareUtil.XObject.anyEmpty(timePoint,()->timePoint.getCntPauseSeconds())) {
            logError("call", "missTimePoint");
            return RUNCode4Fail;
        }
        if (timePoint.getGameState() == EnumExperimentState.FINISH) {
            logInfo("call", "finishedExperiment");
            return RUNCode4Silence;
        }

        List<List<PersonBasedEventCollection.PersonBasedEventGroup>> groups=eventColl.splitGroups(CONCURRENTNum);
        final RunStat runStat=new RunStat(groups.size());
        final ExecutorService executor=EventExecutor.Instance().getThreadPool();
        groups.forEach(i->{
            CompletableFuture.runAsync(()->runEventGroup(i,timePoint,eventColl,runStat), executor)
                    .exceptionally(ex-> {
                        logError(ex, "runEventGroup", "exptPersonIds:%s", String.join(",",
                                ShareUtil.XCollection.map(i, PersonBasedEventCollection.PersonBasedEventGroup::getExperimentPersonId)));
                        return null;
                    });
        });
        return RUNCode4Succ;
    }
    void runEventGroup(List<PersonBasedEventCollection.PersonBasedEventGroup> groups, ExperimentTimePoint timePoint, PersonBasedEventCollection eventColl, RunStat runStat){
        final Date dtNow=ShareUtil.XDate.localDT2Date(timePoint.getRealTime());
        List<ExperimentEventEntity> triggeringEvents=new ArrayList<>();
        List<PersonBasedEventCollection.PersonBasedEventGroup> triggeringGroups=new ArrayList<>();
        for(PersonBasedEventCollection.PersonBasedEventGroup group:groups ) {
            int cntTriggered=0;
            int cntTriggering=0;
            SpelPersonContext spelContext=new SpelPersonContext().setVariables(group.getExperimentPersonId(), timePoint.getPeriod());
            for(ExperimentEventEntity event:group.getEventItems()){
                if(null!=event.getTriggerTime()) {
                    cntTriggered++;
                    continue;
                }
                if(!SpelInvoker.Instance().checkEventCondition(eventColl.getExperimentInstanceId(), group.getExperimentPersonId(),
                        event.getCaseEventId(),spelContext ) ){
                    continue;
                }
                event.setTriggerTime(dtNow)
                        .setTriggeredPeriod(timePoint.getPeriod())
                        .setTriggerGameDay(timePoint.getGameDay())
                        .setState(EnumExperimentEventState.TRIGGERED.getCode());
                triggeringEvents.add(event);
                cntTriggering++;
            }
            if(group.getEventItems().size()<=cntTriggered){
                eventColl.removeGroup(group);
                continue;
            }
            if(cntTriggering>0){
                triggeringGroups.add(group);
            }
            runStat.todoCounter.addAndGet(group.getEventItems().size()-cntTriggered);
        }
        if(ShareUtil.XCollection.notEmpty(triggeringEvents)) {
            try {
                ExperimentEventRules.Instance().saveTriggeredTimeEvent(triggeringEvents, false);
                runStat.doneCounter.addAndGet(triggeringEvents.size());
            } catch (Exception ex) {
                triggeringEvents.forEach(i -> i.setTriggerTime(null));
                triggeringGroups.forEach(i -> {
                    if (i.getRetryTimes().incrementAndGet() < MAXRetry) {
                        return;
                    }
                    eventColl.removeGroup(i);
                    experimentKey.getMaxRetry().incrementAndGet();
                    logError("runEventGroup", "maxRetry.  eventIds:%s",
                            String.join(",", ShareUtil.XCollection.map(i.getEventItems(), ExperimentEventEntity::getExperimentEventId)));

                });
                logError(ex, "runEventGroup", " fail. eventIds:%s",
                        String.join(",", ShareUtil.XCollection.map(triggeringEvents, ExperimentEventEntity::getExperimentEventId)));
                runStat.failTheadCounter.incrementAndGet();
            }
        }
        if(runStat.theadCounter.decrementAndGet()>0) {
            return;
        }
        runStat.todoCounter.addAndGet(-runStat.doneCounter.get());
        logInfo("runEventGroup", "failThread:%s done:%s todo:%s",
                runStat.failTheadCounter.get(),runStat.doneCounter.get(),runStat.todoCounter.get());
    }



}
