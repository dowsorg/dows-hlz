package org.dows.hep.biz.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dows.hep.biz.dao.ExperimentEventDao;
import org.dows.hep.biz.user.experiment.ExperimentOrgNoticeBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.entity.ExperimentIndicatorValEntity;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/6/22 22:17
 */
@Component
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

    //region facade
    public boolean saveTriggeredTimeEvent(List<ExperimentEventEntity> events,boolean saveIndicators) throws JsonProcessingException {
        List<ExperimentEventEntity> saveEvents= ShareUtil.XCollection.map(events, i->
                ExperimentEventEntity.builder()
                        .id(i.getId())
                        .experimentEventId(i.getExperimentEventId())
                        .periods(i.getPeriods())
                        .triggeredPeriod(i.getTriggeredPeriod())
                        .triggerTime(i.getTriggerTime())
                        .triggerGameDay(i.getTriggerGameDay())
                        .state(i.getState())
                        .build());
        List<String> eventIds=ShareUtil.XCollection.map(events, ExperimentEventEntity::getExperimentEventId);
        Map<String,ExperimentEventEntity> mapEvents=experimentEventDao.getMapByIds(eventIds,
                ExperimentEventEntity::getExperimentEventId,
                ExperimentEventEntity::getEventJson);
        //事件触发通知
        List<ExperimentOrgNoticeEntity> rowsNotice=new ArrayList<>();
        for(ExperimentEventEntity item:events){
            Optional.ofNullable(mapEvents.get(item.getExperimentEventId()))
                    .ifPresent(finded->item.setEventJson(finded.getEventJson()));
            rowsNotice.add(experimentOrgNoticeBiz.createNotice(item));
        }
        eventIds.clear();
        mapEvents.clear();

        //事件触发指标
        List<ExperimentIndicatorValEntity> rowsIndicatorVal=null;
        if(saveIndicators){

        }
        return experimentEventDao.tranUpdateTriggered(saveEvents,()-> saveTriggeredTimeEventX(rowsNotice,rowsIndicatorVal));
    }
    boolean saveTriggeredTimeEventX(List<ExperimentOrgNoticeEntity> notices,List<ExperimentIndicatorValEntity> indicatorVals){
        experimentOrgNoticeBiz.add(notices);
        return true;
    }
    public boolean saveActionEvent(ExperimentEventEntity event, ExperimentOrgNoticeEntity notice) {
        ExperimentEventEntity saveEvent = ExperimentEventEntity.builder()
                .id(event.getId())
                .experimentEventId(event.getExperimentEventId())
                .actionJson(event.getActionJson())
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
        //处理事件指标


        return experimentEventDao.tranUpdateAcction(saveEvent, () -> saveActionEventX(saveNotice, null));
    }
    boolean saveActionEventX(ExperimentOrgNoticeEntity notice,List<ExperimentIndicatorValEntity> indicatorVals){
        experimentOrgNoticeBiz.add(notice);
        return true;
    }

    //endregion

}
