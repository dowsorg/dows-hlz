package org.dows.hep.biz.event.sysevent;

import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.dao.ExperimentSysEventDao;
import org.dows.hep.biz.event.*;
import org.dows.hep.biz.event.data.*;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSysEventEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:31
 */

public class SysEventTask extends BaseEventTask {

    //最小间隔
    private final int DELAYSecondsMin=2;
    //轮询间隔
    private final int DELAYSeconds4Poll =30;
    //重试间隔
    private final int DELAYSeconds4Fail=4;

    private final int MAXRetry=5;
    private final ExperimentSysEventDao experimentSysEventDao;
    private final SysEventInvoker sysEventInvoker;

    public SysEventTask(ExperimentCacheKey experimentKey){
        super(experimentKey);
        experimentSysEventDao= getBean(ExperimentSysEventDao.class);
        sysEventInvoker =getBean(SysEventInvoker.class);
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
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMode())){
            logError("call", "missSetting");
            return 0;
        }
        Integer experimentState=loadExperimentState();
        if(ShareUtil.XObject.isEmpty(experimentState)){
            logError("call", "missExperiment");
            return 0;
        }
        /*if(experimentState.equals(EnumExperimentState.FINISH.getState())){
            logInfo("call", "finishedExperiment");
            return RUNCode4Silence;
        }*/
        if(experimentState.equals(EnumExperimentState.SUSPEND.getState())){
            logInfo("call", "pausedExperiment");
            raiseScheduler(DELAYSeconds4Poll,false,true);
            return RUNCode4Silence;
        }
        SysEventCollection eventColl= SysEventCache.Instance().caffineCache()
                .get(experimentKey, k->loadEvents(experimentKey,exptColl));
        if(ShareUtil.XObject.isEmpty(eventColl.getEventRows()) ){
            logError("call", "emptyEvents");
            raiseScheduler(DELAYSeconds4Poll,false,true);
            return RUNCode4Silence;
        }
        if(!eventColl.isInitFlag()){
            eventColl.setInitFlag(experimentSysEventDao.saveOrUpdateBatch(ShareUtil.XCollection.map(eventColl.getEventRows(), SysEventRow::getEntity)
                    ,false,true));
        }
        LocalDateTime ldtNow=LocalDateTime.now();
        ExperimentTimePoint timePoint= ExperimentSettingCache.getTimePointByRealTimeSilence(exptColl, experimentKey, ldtNow, true);
        if(ShareUtil.XObject.anyEmpty(timePoint,()->timePoint.getCntPauseSeconds())) {
            logError("call", "missTimePoint");
            raiseScheduler(DELAYSeconds4Fail,false,true);
            return RUNCode4Fail;
        }
        SysEventRunStat runStat=new SysEventRunStat();
        runStat.curTimePoint.set(timePoint);
        runStat.nextTriggerIime.set(calcTriggeringTime(ldtNow, runStat, exptColl, eventColl));
        final ExecutorService executor= EventExecutor.Instance().getThreadPool();
        CompletableFuture.runAsync(()->runSysEvents(runStat,exptColl,eventColl), executor)
                .exceptionally(ex-> {
                    logError(ex, "runSysEvents", "error. stat:%s", runStat);
                    return null;
                });
        return 1;
    }
    void runSysEvents(SysEventRunStat stat,ExperimentSettingCollection exptColl,SysEventCollection eventColl){
        try {
            final List<SysEventRow> eventRows = eventColl.getEventRows();
            if (ShareUtil.XCollection.isEmpty(eventRows)) {
                stat.append("emptyRows");
                return;
            }
            LocalDateTime curTime = stat.curTimePoint.get().getRealTime();
            final List<SysEventRow> triggerRows = new ArrayList<>();
            for (SysEventRow item : eventRows) {
                if (item.isDealt()
                        ||null==item.getDealType()
                        ||EnumSysEventDealType.NONE==item.getDealType()) {
                    continue;
                }
                stat.todoCounter.incrementAndGet();
                if (!item.canTrigger(curTime, true)) {
                    if(triggerRows.size()>0||!item.getDealType().getDealer().breakOnUnreached()) {
                        continue;
                    }
                    final LocalDateTime unReachedTime=item.getTriggeringTime();
                    if (null != unReachedTime) {
                        stat.nextTriggerIime.set(unReachedTime);
                        stat.append("waitNext[%s-%s]",item.getDealType(),item.getEventId());
                        raiseScheduler(unReachedTime, false, false);
                        return;
                    }
                    stat.append("waitPoll[%s-%s]",item.getDealType(),item.getEventId());
                    raiseScheduler(DELAYSeconds4Poll,false,true);
                    return;
                }
                triggerRows.add(item);
            }
            for(int i=0;i<triggerRows.size();i++) {
                if (i + 1 < triggerRows.size()) {
                    triggerRows.get(i).setNext(triggerRows.get(i + 1));
                }
            }
            for (SysEventRow item : triggerRows) {
                ISysEventDealer dealer=item.getDealType().getDealer();
                if(null==dealer){
                    continue;
                }
                stat.todoCounter.decrementAndGet();
                if(null==item.getTriggeredTime()) {
                    item.setTriggerd(stat.curTimePoint.get());
                }
                if (dealer.dealEvent(item, stat)) {
                    stat.doneCounter.incrementAndGet();
                } else {
                    stat.failCounter.incrementAndGet();
                    if (dealer.breakOnFail()) {
                        stat.append("breakOnFail[%s-%s]",item.getDealType(),item.getEventId());
                        break;
                    }
                }
            }

            if (stat.failCounter.get() > 0) {
                stat.append("failed[cnt-%s]",stat.failCounter.get());
                raiseScheduler(DELAYSeconds4Fail, false, true);
                return;
            }
            final LocalDateTime nextTime = stat.nextTriggerIime.get();
            if (null != nextTime) {
                stat.append("succNext");
                raiseScheduler(nextTime, true, false);
                return;
            }
            if (stat.todoCounter.get() == 0){
                stat.append("succEnd");
                SysEventCache.Instance().caffineCache().invalidate(experimentKey);
                return;
            }
            stat.append("succPoll");
            raiseScheduler(DELAYSeconds4Poll,false,true);

        }finally {
            logInfo("runSysEvents", "stat:%s", stat);
            stat.clear();
        }
    }

    LocalDateTime calcTriggeringTime(LocalDateTime ldtNow, SysEventRunStat stat,ExperimentSettingCollection exptColl,SysEventCollection eventColl) {
        if (ShareUtil.XObject.isEmpty(eventColl.getEventRows())) {
            return null;
        }
        ExperimentTimePoint point;
        LocalDateTime nextTime = LocalDateTime.MAX;
        ExperimentSysEventEntity entity = null;
        for (SysEventRow item : eventColl.getEventRows()) {
            entity = item.getEntity();
            if (null == item.getTriggeringTime()||null==item.getTriggeredTime()) {
                point = sysEventInvoker.getTriggerTime(entity, exptColl, stat.curTimePoint.get().getCntPauseSeconds());
                if (null != point) {
                    item.setTrigging(point);
                }
            }
            final LocalDateTime triggeringTime =item.getTriggeringTime();
            if (null == triggeringTime) {
                continue;
            }
            if (ldtNow.compareTo(triggeringTime) < 0 && triggeringTime.compareTo(nextTime) < 0) {
                nextTime = triggeringTime;
            }
        }
        return nextTime.isEqual(LocalDateTime.MAX) ? null : nextTime;
    }

    //endregion

    //region scheduler
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
        EventScheduler.Instance().scheduleSysEvent(experimentKey, minNext.isBefore(nextTime)?nextTime:minNext);
    }
    //endregion


    //region load

    SysEventCollection loadEvents(ExperimentCacheKey exptKey,ExperimentSettingCollection exptColl) {
        final String experimentId = exptKey.getExperimentInstanceId();
        SysEventCollection rst = new SysEventCollection()
                .setExperimentInstanceId(experimentId);
        List<ExperimentSysEventEntity> rowsEvent = this.experimentSysEventDao.getByExperimentId(null, experimentId,null);
        if (!ShareUtil.XCollection.isEmpty(rowsEvent)) {
            rst.setInitFlag(true);

        } else{
            rowsEvent = sysEventInvoker.buildEvents(exptColl);
            rst.setInitFlag(false);
        }
        rst.setEventRows(ShareUtil.XCollection.map(rowsEvent, i -> new SysEventRow(i)));
        rowsEvent.clear();
        return rst;
    }
    //endregion






}
