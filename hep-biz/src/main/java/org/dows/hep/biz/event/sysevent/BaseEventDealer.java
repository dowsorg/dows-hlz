package org.dows.hep.biz.event.sysevent;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.user.experiment.response.IntervalResponse;
import org.dows.hep.biz.dao.ExperimentSysEventDao;
import org.dows.hep.biz.event.ExperimentFlowRules;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.data.*;
import org.dows.hep.biz.util.PushWebSocketUtil;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.PushWebScoketResult;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
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

    @Autowired
    protected ExperimentFlowRules experimentFlowRules;

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
                .setExptId(entity.getExperimentInstanceId())
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

            if (row.isDealt()) {
                return rst.setSucc(true)
                        .append("nowDealt")
                        .isSucc();
            }
            if (row.tillMaxRetry()) {
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
        } catch (Exception ex) {
            row.getRetryTimes().incrementAndGet();
            rst.setSucc(false).append("dealError:%s", ex.getMessage());
            logError(ex, "dealEvent", "dealError. rst:%s",rst);
            dealFlag=!row.tillMaxRetry();
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
                            .setDealMsg(row.tillMaxRetry()?null:rst.toString());
                    if(rst.isSucc()) {
                        entity.setState(EnumSysEventState.DEALT.getCode());
                    }
                    saveDeal(entity);
                } catch (Exception ex) {
                    entity.setState(curState);
                    rst.setSucc(false).append("saveError:%s", ex.getMessage());
                    row.getRetryTimes().incrementAndGet();
                    logError(ex, "dealEvent", "saveError. rst:%s",rst);
                }
            }
            logInfo("dealEvent", "rst:%s", rst);
        }

        return rst.isSucc();
    }

    protected abstract boolean coreDeal(EventDealResult rst,SysEventRow row, SysEventRunStat stat);

    protected int getDealSeq(int period,EnumSysEventDealType dealType,EnumSysEventTriggerType triggerType) {
        return period*1000+dealType.getDealSeq();
    }
    protected ExperimentSysEventEntity buildEvent(ExperimentSettingCollection exptColl,Integer period,EnumSysEventDealType dealType,EnumSysEventTriggerType triggerType){
        period=Optional.ofNullable(period).orElse(0);
        return ExperimentSysEventEntity.builder()
                .experimentSysEventId(idGenerator.nextIdStr())
                .appId(exptColl.getAppId())
                .experimentInstanceId(exptColl.getExperimentInstanceId())
                .periods(period)
                .eventType(dealType.getCode())
                .triggerType(triggerType.getCode())
                .dealSeq(getDealSeq(period,dealType,triggerType))
                .dealTimes(0)
                .state(EnumSysEventState.INIT.getCode())
                .build();
    }

    protected boolean saveDeal(ExperimentSysEventEntity row){
        return experimentSysEventDao.saveOrUpdate(row,true);
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
    protected PushWebScoketResult pushTimeState(EventDealResult rst, ExperimentCacheKey exptKey, ExperimentSettingCollection exptColl, EnumWebSocketType socketType, SysEventRow checkPush){
        Set<String> clientIds= ShareBiz.getAccountIdsByExperimentId(exptKey.getExperimentInstanceId());
        if(ShareUtil.XCollection.isEmpty(clientIds)){
            PushWebScoketResult pushRst=new PushWebScoketResult().setType(socketType.toString())
                    .append("missAccounts");
            rst.getPushStat().add(pushRst);
            return pushRst;
        }
        return pushTimeState(rst,exptKey,exptColl,socketType,clientIds,checkPush);

    }
    protected PushWebScoketResult pushTimeState(EventDealResult rst, ExperimentCacheKey exptKey, ExperimentSettingCollection exptColl, EnumWebSocketType socketType, Set<String> clientIds, SysEventRow checkPush) {
        try {
            if (null != checkPush && !requirePush(checkPush)) {
                PushWebScoketResult pushRst= new PushWebScoketResult().setType(socketType.toString())
                        .append("unRequirePush[%s] ", this.getPushType());
                rst.getPushStat().add(pushRst);
                return pushRst;
            }
            final String experimentInstanceId = exptKey.getExperimentInstanceId();
            IntervalResponse pushData = experimentFlowRules.countdown(exptKey, exptColl,false);
            PushWebScoketResult pushRst = PushWebSocketUtil.Instance().pushCommon(socketType, experimentInstanceId, clientIds, pushData);
            rst.getPushStat().add(pushRst);
            return pushRst;
        }catch (Exception ex){
            PushWebScoketResult pushRst= new PushWebScoketResult().append("pushError:%s",ex.getMessage());
            rst.getPushStat().add(pushRst);
            logError(ex, "dealEvent", "pushError. rst:%s",rst);
            return pushRst;
        }
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
