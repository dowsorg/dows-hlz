package org.dows.hep.biz.event.followupplan;

import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.event.*;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author : wuzl
 * @date : 2023/9/2 19:14
 */
public class FollowupPlanTask extends BaseEventTask {

    //最小间隔
    private final int DELAYSecondsMin=3;
    //轮询间隔
    private final int DELAYSeconds4Poll =30;
    //重试间隔
    private final int DELAYSeconds4Fail=10;


    private final int MAXRetry=3;

    private final int MAXRetry4Item=3;

    public FollowupPlanTask(ExperimentCacheKey experimentKey){
        super(experimentKey);
    }


    // region run
    @Override
    protected void exceptionally(Exception ex) {
        super.exceptionally(ex);
        raiseScheduler(getRetryDelaySeconds(), false, true);
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
        if(experimentState.equals(EnumExperimentState.FINISH.getState())){
            logInfo("call", "finishedExperiment");
            return 0;
        }
        if(experimentState.equals(EnumExperimentState.SUSPEND.getState())){
            logInfo("call", "pausedExperiment");
            raiseScheduler(DELAYSeconds4Poll,false,true);
            return 0;
        }
        FollowupPlanCollection planColl=FollowupPlanCache.Instance().loadingCache().get(experimentKey);
        if(ShareUtil.XObject.isEmpty(planColl.getMapPlanRows()) ){
            logError("call", "emptyPlans");
            raiseScheduler(DELAYSeconds4Poll,false,true);
            return 0;
        }

        LocalDateTime ldtNow=LocalDateTime.now();
        ExperimentTimePoint timePoint= ExperimentSettingCache.getTimePointByRealTimeSilence(exptColl, experimentKey, ldtNow, true);
        if(ShareUtil.XObject.anyEmpty(timePoint,()->timePoint.getPeriod())) {
            logError("call", "missTimePoint");
            raiseScheduler(getRetryDelaySeconds(),false,true);
            return 0;
        }
        final List<FollowupPlanRow> planRows = planColl.getMapPlanRows().values().stream().toList();
        FollowupPlanRunStat runStat=new FollowupPlanRunStat();
        runStat.curTimePoint.set(timePoint);
        runStat.nextTriggerIime.set(calcTriggeringTime(runStat, exptColl, planRows));
        final ExecutorService executor= EventExecutor.Instance().getThreadPool();
        CompletableFuture.runAsync(()->runPlans(runStat,exptColl,planRows), executor);
        return 1;
    }

    void runPlans(FollowupPlanRunStat stat, ExperimentSettingCollection exptColl, List<FollowupPlanRow> planRows){
        try {

            if (ShareUtil.XCollection.isEmpty(planRows)) {
                stat.append("emptyRows");
                return;
            }
            final LocalDateTime curTime = stat.curTimePoint.get().getRealTime();
            for (FollowupPlanRow item : planRows) {
                if (null != item.getTodoDay()) {
                    stat.todoCounter.incrementAndGet();
                }
                if(!item.canTrigger(curTime)
                        ||item.getRetryTimes().get()>=MAXRetry4Item){
                    continue;
                }
                try{
                    if(!ExperimentFollowupPlanRules.Instance().saveTriggeredFollowupPlan(item
                            .setTriggered(stat.curTimePoint.get())
                            .toSaveEntity(),stat.curTimePoint.get())){
                        AssertUtil.justThrow("failSaveFollowUpPlan");
                    }
                    item.saveNextTodoDay();
                    stat.doneCounter.incrementAndGet();
                }catch (Exception ex){
                    stat.failCounter.incrementAndGet();
                    if(item.getRetryTimes().incrementAndGet()>=MAXRetry4Item){
                        experimentKey.getRetryTimes().incrementAndGet();
                    }
                    stat.append("runPlanItemError:%s[id:%s]", ex.getMessage(),item.getEntity().getExperimentFollowupPlanId());
                    logError(ex, "runPlanItemError", "error. stat:%s", stat);
                }
                stat.todoCounter.decrementAndGet();
            }
            if (stat.failCounter.get() > 0) {
                final  long failDelay=getRetryDelaySeconds();
                stat.append("failDelay[cnt-%s delay-%s]",stat.failCounter.get(),failDelay);
                raiseScheduler(getRetryDelaySeconds(), false, false);
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
                FollowupPlanCache.Instance().caffineCache().invalidate(experimentKey);
                return;
            }
            stat.append("succPoll");
            raiseScheduler(DELAYSeconds4Poll,false,true);

        }catch (Exception ex){
            stat.append("runError:%s", ex.getMessage());
            logError(ex, "runPlans", "error. stat:%s", stat);
            raiseScheduler(getRetryDelaySeconds(), false, true);
        } finally {
            logInfo("runPlans", "stat:%s", stat);
            stat.clear();
        }
    }

    long getRetryDelaySeconds(){
        return DELAYSeconds4Fail*(1+experimentKey.getRetryTimes().get());
    }


    LocalDateTime calcTriggeringTime(FollowupPlanRunStat stat, ExperimentSettingCollection exptColl, List<FollowupPlanRow> planRows){
        if (ShareUtil.XObject.isEmpty(planRows)) {
            return null;
        }
        final LocalDateTime ldtNow=stat.curTimePoint.get().getRealTime();
        LocalDateTime nextTime = LocalDateTime.MAX;
        for (FollowupPlanRow item : planRows) {
            if(!item.isTriggering()){
                continue;
            }
            item.setTriggering(calcTriggeringTime(stat, exptColl, item.getTodoDay()));
            item.setNextTodoDay(calcNextTodoDay(stat, exptColl, item));
            if (null == item.getDoingTime()) {
                continue;
            }
            LocalDateTime triggeringTime =item.getDoingTime();
            if(triggeringTime.compareTo(ldtNow)<0){
                triggeringTime =calcTriggeringTime(stat, exptColl, item.getNextTodoDay());
                item.setNextTodoDay(calcNextTodoDay(stat, exptColl, item.saveNextTodoDay()));
            }
            if (triggeringTime.compareTo(nextTime) < 0) {
                nextTime = triggeringTime;
            }
        }
        return nextTime.isEqual(LocalDateTime.MAX) ? null : nextTime;
    }
    LocalDateTime calcTriggeringTime(FollowupPlanRunStat stat, ExperimentSettingCollection exptColl,Integer todoDays){
        final ExperimentTimePoint timePoint=stat.curTimePoint.get();
        if(ShareUtil.XObject.anyEmpty(timePoint,todoDays)){
            return null;
        }
        if(exptColl.getTotalDays()< todoDays){
            return null;
        }
        if(todoDays<= timePoint.getGameDay()){
            return timePoint.getRealTime();
        }
        Integer rawSeconds=  exptColl.getRawSecondsByGameDay(todoDays);
        return exptColl.getSandStartTime().plusSeconds(rawSeconds+timePoint.getCntPauseSeconds());

    }
    Integer calcNextTodoDay(FollowupPlanRunStat stat,ExperimentSettingCollection exptColl,FollowupPlanRow row){
        final ExperimentTimePoint timePoint=stat.curTimePoint.get();
        final ExperimentFollowupPlanEntity entity=row.getEntity();
        final int dueDays=Math.max(30,entity.getDueDays());
        final int setAtDay=entity.getSetAtDay();
        final int curDay=Math.max(timePoint.getGameDay(), entity.getTodoDay());
        final int curTimes=Math.max(0,(curDay-setAtDay)/dueDays);
        int nextTodoDay=0;
        for(int i=1;i<10;i++){
            nextTodoDay=setAtDay+(curTimes+i)*dueDays;
            if(curDay<nextTodoDay){
                break;
            }
        }
        if(nextTodoDay<=exptColl.getTotalDays()){
            return nextTodoDay;
        }
        return null;
    }
    //endregion

    //region scheduler
    void raiseScheduler(long delaySeconds,boolean resetRetry,boolean incrRetry){
        raiseScheduler(LocalDateTime.now().plusSeconds(delaySeconds), resetRetry, incrRetry);
    }
    void raiseScheduler(LocalDateTime nextTime,boolean resetRetry,boolean incrRetry) {
        if(resetRetry){
            experimentKey.getRetryTimes().set(0);
        } else if ((incrRetry? experimentKey.getRetryTimes().incrementAndGet():experimentKey.getRetryTimes().get()) >= MAXRetry) {
            logError("raiseScheduler", "maxRetry");
            return;
        }

        final LocalDateTime minNext=LocalDateTime.now().plusSeconds(DELAYSecondsMin);
        EventScheduler.Instance().scheduleFollowUpPlan(experimentKey, minNext.isBefore(nextTime)?nextTime:minNext);
    }
    //endregion


}
