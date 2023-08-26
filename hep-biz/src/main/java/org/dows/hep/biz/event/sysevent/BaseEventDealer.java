package org.dows.hep.biz.event.sysevent;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.ExperimentSysEventDao;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author : wuzl
 * @date : 2023/8/24 19:13
 */
@Slf4j
public abstract class BaseEventDealer implements ISysEventDealer {

    @Autowired
    protected IdGenerator idGenerator;
    @Autowired
    protected ExperimentSysEventDao experimentSysEventDao;

    protected final long TRYLockTimeOutSeconds=30;

    @Override
    public boolean breakOnUnreached() {
        return true;
    }

    @Override
    public boolean breakOnFail() {
        return true;
    }

    @Override
    public EnumSysEventPushType getPushType() {
        return EnumSysEventPushType.NEWEST;
    }


    @Override
    public boolean dealEvent(SysEventRow row, SysEventRunStat stat) {
        final ExperimentSysEventEntity entity = row.getEntity();
        EventDealResult rst = new EventDealResult()
                .setFlowStat(stat)
                .setStartTime(LocalDateTime.now());
        boolean lockFlag = false;
        boolean dealFlag=false;
        try {
            SysEventRow next=row.getNext();
            if(null!=next){
                rst.setNextDeal(EnumSysEventDealType.of(next.getEntity().getEventType()))
                        .setNextTime(next.getTriggeringTime());
            }
            if (!preDeal(rst, row, stat)) {
                dealFlag=true;
                return rst.setSucc(false)
                        .append("preHalt")
                        .isSucc();
            }
            if (row.isDealt()) {
                return rst.setSucc(true)
                        .append("hasDealt")
                        .isSucc();
            }
            final int maxRetry = this.maxRetryTimes();
            if (maxRetry > 0 && row.getRetryTimes().get() >= maxRetry) {
                dealFlag=true;
                return rst.setSucc(false)
                        .append("maxRetry")
                        .isSucc();
            }
            lockFlag = row.getLock().tryLock(TRYLockTimeOutSeconds, TimeUnit.SECONDS);
            ExperimentSysEventEntity poNew=reloadDeal(entity.getExperimentSysEventId());
            if(null==poNew){
                dealFlag=true;
                return rst.setSucc(false)
                        .append("failReload")
                        .isSucc();
            }
            entity.setState(poNew.getState())
                    .setDealTimes(poNew.getDealTimes())
                    .setDealTime(poNew.getDealTime());
            if (row.isDealt()) {
                return rst.setSucc(true)
                        .append("newDealt")
                        .isSucc();
            }
            dealFlag=true;
            rst.setSucc(coreDeal(rst,row, stat));
            postDeal(rst, row, stat);
        } catch (Exception ex) {
            rst.setSucc(false)
                    .append("error:%s", ex.getMessage());
            row.getRetryTimes().incrementAndGet();
            logError(ex, "dealEvent", "error. rst:%s",rst);
        } finally {
            if (lockFlag) {
                row.getLock().unlock();
            }
            if(dealFlag){
                Integer curState=entity.getState();
                try {
                    rst.setRetryTimes(row.getRetryTimes().get())
                            .setEndTime(LocalDateTime.now());
                    entity.setDealTimes(Optional.ofNullable(entity.getDealTimes()).orElse(0) + 1)
                            .setDealTime(new Date())
                            .setDealMsg(rst.toString());
                    if(rst.isSucc()){
                        entity.setState(EnumSysEventState.DEALT.getCode());
                    }
                    saveDeal(entity);
                } catch (Exception ex) {
                    entity.setState(curState);
                    rst.append("error:%s", ex.getMessage());
                    row.getRetryTimes().incrementAndGet();
                    logError(ex, "dealEvent", "error. rst:%s",rst);
                }
            }
        }
        logInfo("dealEvent", "rst:%s", rst);
        return rst.isSucc();
    }

    protected boolean preDeal(EventDealResult rst,SysEventRow row, SysEventRunStat stat){
        return true;
    }
    protected void postDeal(EventDealResult rst, SysEventRow row, SysEventRunStat stat){

    }

    protected abstract boolean coreDeal(EventDealResult rst,SysEventRow row, SysEventRunStat stat);

    protected int getDealSeq(int period,int eventType,int triggerType) {
        return period*1000+EnumSysEventDealType.of(eventType).getDealSeq();
    }
    protected ExperimentSysEventEntity buildEvent(ExperimentSettingCollection exptColl,Integer period,int eventType,int triggerType){
        period=Optional.ofNullable(period).orElse(0);
        return ExperimentSysEventEntity.builder()
                .experimentSysEventId(idGenerator.nextIdStr())
                .appId(exptColl.getAppId())
                .experimentInstanceId(exptColl.getExperimentInstanceId())
                .periods(period)
                .eventType(eventType)
                .triggerType(triggerType)
                .dealSeq(getDealSeq(period,eventType,triggerType))
                .dealTimes(0)
                .state(EnumSysEventState.INIT.getCode())
                .build();
    }

    protected boolean saveDeal(ExperimentSysEventEntity row){
        return experimentSysEventDao.saveOrUpdate(row);
    }

    protected ExperimentSysEventEntity reloadDeal(String eventId){
        return experimentSysEventDao.getById(eventId,
                ExperimentSysEventEntity::getId,
                ExperimentSysEventEntity::getAppId,
                ExperimentSysEventEntity::getExperimentSysEventId,
                ExperimentSysEventEntity::getPeriods,
                ExperimentSysEventEntity::getEventType,
                ExperimentSysEventEntity::getTriggerType,
                ExperimentSysEventEntity::getTriggeringTime,
                ExperimentSysEventEntity::getTriggeringGameDay,
                ExperimentSysEventEntity::getTriggeredTime,
                ExperimentSysEventEntity::getTriggeredPeriod,
                ExperimentSysEventEntity::getTriggeredGameDay,
                ExperimentSysEventEntity::getDealSeq,
                ExperimentSysEventEntity::getDealTimes,
                ExperimentSysEventEntity::getDealTime,
                ExperimentSysEventEntity::getDealMsg,
                ExperimentSysEventEntity::getState).orElse(null);
    }



    //region push

    /**
     * 是否需要推送
     * @param row
     * @return
     */
    protected boolean requirePush(SysEventRow row){
        EnumSysEventPushType pushType= this.getPushType();
        return pushType==EnumSysEventPushType.ALWAYS
                ||pushType==EnumSysEventPushType.NEWEST&&null== row.getNext();
    }
    //endregion

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
