package org.dows.hep.biz.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.user.experiment.response.OrgNoticeResponse;
import org.dows.hep.biz.dao.ExperimentEventDao;
import org.dows.hep.biz.spel.SpelInvoker;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.spel.meta.SpelEvalSumResult;
import org.dows.hep.biz.user.experiment.ExperimentOrgNoticeBiz;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.PushWebSocketUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.PushWebScoketResult;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/6/22 22:17
 */
@Component
@Slf4j
public class ExperimentEventRules {
    private static volatile ExperimentEventRules s_instance;
    public static ExperimentEventRules Instance(){
        return s_instance;
    }

    private ExperimentEventRules(){
        s_instance=this;
    }

    @Autowired
    private ExperimentEventDao experimentEventDao;

    @Autowired
    private ExperimentOrgNoticeBiz experimentOrgNoticeBiz;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    //region facade
    public boolean saveTriggeredTimeEvent(List<ExperimentEventEntity> events,boolean saveIndicators) throws JsonProcessingException {
        List<ExperimentEventEntity> saveEvents = ShareUtil.XCollection.map(events, i ->
                ExperimentEventEntity.builder()
                        .id(i.getId())
                        .experimentEventId(i.getExperimentEventId())
                        .periods(i.getPeriods())
                        .triggeringTime(i.getTriggeringTime())
                        .triggeringGameDay(i.getTriggeringGameDay())
                        .triggeredPeriod(i.getTriggeredPeriod())
                        .triggerTime(i.getTriggerTime())
                        .triggerGameDay(i.getTriggerGameDay())
                        .state(i.getState())
                        .build());
        List<String> eventIds = ShareUtil.XCollection.map(events, ExperimentEventEntity::getExperimentEventId);
        Map<String, ExperimentEventEntity> mapEvents = experimentEventDao.getMapByIds(eventIds,
                ExperimentEventEntity::getExperimentEventId,
                ExperimentEventEntity::getEventJson);
        //事件触发通知
        List<ExperimentOrgNoticeEntity> rowsNotice = new ArrayList<>();
        final Map<String, String> mapAvatar = new HashMap<>();
        for (ExperimentEventEntity item : events) {
            Optional.ofNullable(mapEvents.get(item.getExperimentEventId()))
                    .ifPresent(finded -> item.setEventJson(finded.getEventJson()));
            rowsNotice.add(experimentOrgNoticeBiz.createNotice(item, mapAvatar));
        }
        eventIds.clear();
        mapEvents.clear();

        boolean rst = experimentEventDao.tranSaveBatch(saveEvents, false, true, () -> saveTriggeredTimeEventX(rowsNotice, saveIndicators ? events : null));
        if (rst) {
            StringBuilder sb=new StringBuilder();
            try {
                //发送webSocket
                final String experimentId = rowsNotice.get(0).getExperimentInstanceId();
                sb.append(String.format("ExperimentEventRules.pushWebSocket[%s] exptId:%s noticeIds:%s ",
                        Thread.currentThread().getName(),
                        experimentId,
                        rowsNotice.stream()
                                .map(ExperimentOrgNoticeEntity::getExperimentOrgNoticeId)
                                .collect(Collectors.joining(","))));

                Map<String, List<OrgNoticeResponse>> mapNotice = experimentOrgNoticeBiz.getWebSocketNotice(experimentId, rowsNotice);
                if (ShareUtil.XObject.notEmpty(mapNotice)) {
                    mapNotice.forEach((k,v)->{
                        PushWebScoketResult rstPush= PushWebSocketUtil.Instance().pushCommon(EnumWebSocketType.EVENT_TRIGGERED, experimentId,Set.of(k), v);
                        sb.append(String.format( " -loop clientId:%s noticeIds:%s miss:%s",
                                k,
                                v.stream().map(OrgNoticeResponse::getExperimentOrgNoticeId).collect(Collectors.joining(",")),
                                String.join(",", rstPush.getMissClients())));
                    });
                }
            }catch (Exception ex) {
                sb.append(" error.").append(ex.getMessage());
                throw ex;
            }finally {
                log.info(sb.toString());
            }
        }
        return rst;
    }
    boolean saveTriggeredTimeEventX(List<ExperimentOrgNoticeEntity> notices,List<ExperimentEventEntity> events){
        AssertUtil.falseThenThrow(experimentOrgNoticeBiz.add(notices)).throwMessage("事件触发通知保存失败");
        if(ShareUtil.XObject.isEmpty(events)){
            return true;
        }
        final List<String> caseEventIds=ShareUtil.XCollection.map(events, ExperimentEventEntity::getCaseEventId);
        final ExperimentEventEntity topEvent=events.get(0);
        final String experimentId=topEvent.getExperimentInstanceId();
        final String experimentPersonId=topEvent.getExperimentPersonId();
        final Integer periods=topEvent.getTriggeredPeriod();
        SpelInvoker.Instance().saveEventEffect(experimentId, experimentPersonId,periods, caseEventIds);
        return true;
    }
    public boolean saveActionEvent(ExperimentEventEntity event, ExperimentOrgNoticeEntity notice,List<String> actedIds) throws JsonProcessingException {
        final Integer period=event.getActionPeriod();
        Map<String, SpelEvalSumResult> mapSum=new HashMap<>();
        List<SpelEvalResult> evalResults=SpelInvoker.Instance().evalEventAction(event.getExperimentInstanceId(), event.getExperimentPersonId(), period, actedIds,mapSum);

        ExperimentEventEntity saveEvent = ExperimentEventEntity.builder()
                .id(event.getId())
                .experimentEventId(event.getExperimentEventId())
                //.actionJson(event.getActionJson())
                .actionJson(JacksonUtil.toJson(evalResults, true))
                .actionAccountId(event.getActionAccountId())
                .actionAccountName(event.getActionAccountName())
                .actionTime(event.getActionTime())
                .actionPeriod(event.getActionPeriod())
                .actionGameDay(event.getActionGameDay())
                .state(event.getState())
                .build();
        ExperimentOrgNoticeEntity saveNotice = ExperimentOrgNoticeEntity.builder()
                .id(notice.getId())
                .experimentOrgNoticeId(notice.getExperimentOrgNoticeId())
                .eventActions(notice.getEventActions())
                .actionState(notice.getActionState())
                .readState(notice.getReadState())
                .build();
        return experimentEventDao.tranSave(saveEvent, true, () -> {
            AssertUtil.falseThenThrow(experimentOrgNoticeBiz.update(saveNotice)).throwMessage("通知状态更新失败");
            if (ShareUtil.XObject.isEmpty(event)) {
                return true;
            }
            AssertUtil.falseThenThrow(SpelInvoker.Instance().saveIndicator(evalResults,mapSum.values(),period))
                    .throwMessage("影响指标数据保存失败");
            return true;
        });
    }


    //endregion

}
