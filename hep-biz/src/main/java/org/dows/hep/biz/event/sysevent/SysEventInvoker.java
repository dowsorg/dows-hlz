package org.dows.hep.biz.event.sysevent;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.ExperimentSysEventDao;
import org.dows.hep.biz.event.EventScheduler;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/8/23 15:52
 */
@Slf4j
@Component
public class SysEventInvoker {

    private static volatile SysEventInvoker s_instance;

    public static SysEventInvoker Instance() {
        return s_instance;
    }

    private SysEventInvoker() {
        s_instance = this;
    }

    @Autowired
    private ExperimentSysEventDao experimentSysEventDao;

    /**
     * 手动触发 方案设计小组结束
     * @param triggeringTime
     * @param appId
     * @param exptId
     * @param groupId
     * @return
     */
    public boolean triggeringSchemaGroupEnd(Date triggeringTime,String appId,String exptId,String groupId){
        return manulTriggering(triggeringTime,EnumSysEventDealType.SCHEMAGroupEnd, appId,exptId,null, groupId,null);
    }
    /**
     * 手动调用 实验准备
     * @param appId
     * @param exptId
     * @return
     */
    public boolean dealExperimentReady(String appId,String exptId){
        return manualDeal(EnumSysEventDealType.EXPERIMENTReady,appId,exptId,null, null, null);
    }

    /**
     * 手动设置触发时间
     *
     * @param triggeringTime
     * @param dealType
     * @param appId
     * @param exptId
     * @param period
     * @param exptGroupId
     * @param exptPersonId
     * @return
     */

    public boolean manulTriggering(Date triggeringTime,EnumSysEventDealType dealType, String appId,String exptId,Integer period, String exptGroupId,String exptPersonId){
        appId= ShareBiz.checkAppId(appId,exptId);
        final ExperimentCacheKey exptKey=ExperimentCacheKey.create(appId, exptId);
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(exptKey,true);
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMode())){
            logError("manulTriggering", "missSetting expt:%s",exptKey);
            return false;
        }
        SysEventCollection eventColl= SysEventCache.Instance().caffineCache().getIfPresent(exptKey);
        if(ShareUtil.XObject.anyEmpty(eventColl,()-> eventColl.getEventRows()) ){
            logError("manulTriggering", "emptyEvents expt:%s",exptKey);
            return false;
        }
        List<SysEventRow> todoEvents= filterEvents(eventColl,dealType,period,exptGroupId,exptPersonId);
        if(ShareUtil.XObject.isEmpty(todoEvents)){
            logError("manulTriggering", "missEvents expt:%s deal:%s period:%s group:%s person:%s",
                    exptKey,dealType,period,exptGroupId,exptPersonId);
            return false;
        }
        List<ExperimentSysEventEntity> rowsSave=new ArrayList<>();
        for (SysEventRow item : todoEvents) {
           if(null!=item.getEntity().getTriggeringTime()){
               continue;
           }
           item.setTriggeringTime(ShareUtil.XDate.localDT4Date(triggeringTime));
           rowsSave.add(item.getEntity());
        }
        if(ShareUtil.XObject.isEmpty(rowsSave)){
            return true;
        }
        if(!experimentSysEventDao.saveOrUpdateBatch(rowsSave,false,true)){
            logError("manulTriggering", "failSave time:%s expt:%s deal:%s period:%s group:%s person:%s",
                    triggeringTime.getTime(),exptKey,dealType,period,exptGroupId,exptPersonId);
        }
        SysEventCache.Instance().caffineCache().invalidate(exptKey);
        EventScheduler.Instance().scheduleSysEvent(appId, exptId, 3);
        return true;
    }

    /**
     * 手动处理事件
     * @param dealType 处理类型
     * @param appId
     * @param exptId 实验id
     * @param period 期数
     * @param exptGroupId 小组id
     * @param exptPersonId 人物id
     * @return
     */

    public boolean manualDeal(EnumSysEventDealType dealType, String appId,String exptId,Integer period, String exptGroupId,String exptPersonId){
        appId= ShareBiz.checkAppId(appId,exptId);
        final ExperimentCacheKey exptKey=ExperimentCacheKey.create(appId, exptId);
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(exptKey,true);
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMode())){
            logError("manualDeal", "missSetting expt:%s",exptKey);
            return false;
        }
        LocalDateTime ldtNow=LocalDateTime.now();
        ExperimentTimePoint timePoint= ExperimentSettingCache.getTimePointByRealTimeSilence(exptColl, exptKey, ldtNow, true);
        if(ShareUtil.XObject.anyEmpty(timePoint,()->timePoint.getCntPauseSeconds())) {
            logError("manualDeal", "missTimePoint expt:%s",exptKey);
            return false;
        }
        SysEventCollection eventColl= SysEventCache.Instance().caffineCache().getIfPresent(exptKey);
        if(ShareUtil.XObject.isEmpty(eventColl.getEventRows()) ){
            logError("manualDeal", "emptyEvents expt:%s",exptKey);
            return false;
        }
        List<SysEventRow> todoEvents= filterEvents(eventColl,dealType,period,exptGroupId,exptPersonId);
        if(ShareUtil.XObject.isEmpty(todoEvents)){
            logError("manualDeal", "missEvents expt:%s deal:%s period:%s group:%s person:%s",
                    exptKey,dealType,period,exptGroupId,exptPersonId);
            return false;
        }
        SysEventRunStat stat=new SysEventRunStat();
        stat.curTimePoint.set(timePoint);
        for (SysEventRow item : todoEvents) {
            if (item.isDealt()
                    ||null==item.getDealType()
                    ||EnumSysEventDealType.NONE==item.getDealType()) {
                continue;
            }
            ISysEventDealer dealer=item.getDealType().getDealer();
            if(null==dealer){
                continue;
            }
            stat.todoCounter.incrementAndGet();
            if(null==item.getTriggeringTime()){
                item.setTrigging(stat.curTimePoint.get());
            }
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
        SysEventCache.Instance().caffineCache().invalidate(exptKey);
        EventScheduler.Instance().scheduleSysEvent(appId, exptId, 3);
        logInfo("manualDeal", "expt:%s deal:%s period:%s stat:%s",
                exptKey,dealType,period,stat);
        return stat.failCounter.get()==0;
    }

    private List<SysEventRow> filterEvents(SysEventCollection eventColl,EnumSysEventDealType dealType,Integer period, String exptGroupId,String exptPersonId){
        List<SysEventRow> rst=new ArrayList<>();
        eventColl.getEventRows().forEach(item->{
            if(item.getDealType()!=dealType){
                return;
            }
            ExperimentSysEventEntity entity=item.getEntity();
            if(ShareUtil.XObject.notEmpty(period)&&!period.equals(entity.getPeriods())){
                return;
            }
            if(ShareUtil.XObject.notEmpty(exptGroupId)&&!exptGroupId.equals(entity.getExperimentGroupId())){
                return;
            }
            if(ShareUtil.XObject.notEmpty(exptPersonId)&&!exptPersonId.equals(entity.getExperimentPersonId())){
                return;
            }
            rst.add(item);
        });
        return rst;
    }
    public boolean dealEvent(SysEventRow row, SysEventRunStat stat){
       if(null==row||null==row.getEntity()){
           return false;
       }
       EnumSysEventDealType dealType=EnumSysEventDealType.of(row.getEntity().getEventType());
       if(null==dealType){
           return false;
       }
       return dealType.dealEvent(row,stat);
    }

    public List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        List<ExperimentSysEventEntity> rst=new ArrayList<>();
        Arrays.stream(EnumSysEventDealType.values()).forEach(item->{
            if(item==EnumSysEventDealType.NONE){
                return;
            }
            List<ExperimentSysEventEntity> events=item.buildEvents(exptColl);
            if(ShareUtil.XObject.isEmpty(events)){
                return;
            }
            rst.addAll(events);
        });
        rst.sort(Comparator.comparingInt(ExperimentSysEventEntity::getPeriods)
                .thenComparingInt(ExperimentSysEventEntity::getDealSeq));
        return rst;
    }

    public ExperimentTimePoint getTriggerTime(ExperimentSysEventEntity entity,ExperimentSettingCollection exptColl,long cntPauseSeconds) {
        if (ShareUtil.XObject.isEmpty(entity)) {
            return null;
        }
        EnumSysEventTriggerType triggerType = EnumSysEventTriggerType.of(entity.getTriggerType());
        if (null == triggerType||triggerType==EnumSysEventTriggerType.MANUAL) {
            return null;
        }
        return triggerType.getTriggerTime(exptColl, entity.getPeriods(), cntPauseSeconds);
    }

    //region log
    protected void logError(String func, String msg,Object... args){
        logError(null, func,msg,args);
    }
    protected void logError(Throwable ex, String func, String msg,Object... args){
        String str=String.format("%s.%s@%s[%s] %s", this.getClass().getName(), func, LocalDateTime.now(),this.hashCode(),
                String.format(Optional.ofNullable(msg).orElse(""), args));
        log.error(str,ex);
        //log.info(str);
    }
    protected void logInfo(String func, String msg,Object... args) {
        String str = String.format("%s.%s@%s[%s] %s", this.getClass().getName(), func, LocalDateTime.now(),this.hashCode(),
                String.format(Optional.ofNullable(msg).orElse(""), args));
        log.info(str);
    }
    //endregion
}
