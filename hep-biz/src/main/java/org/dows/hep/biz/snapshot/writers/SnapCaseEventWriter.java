package org.dows.hep.biz.snapshot.writers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentEventState;
import org.dows.hep.api.user.experiment.vo.ExptOrgEventActionVO;
import org.dows.hep.biz.dao.*;
import org.dows.hep.biz.snapshot.BaseSnapshotWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.ExperimentEventJson;
import org.dows.hep.entity.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/7/5 18:07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SnapCaseEventWriter extends BaseSnapshotWriter<SnapCaseEventWriter.SnapData> {


    private final CaseEventDao caseEventDao;
    private final CaseEventActionDao caseEventActionDao;
    private final CasePersonDao casePersonDao;
    private final ExperimentPersonDao experimentPersonDao;

    private final ExperimentEventDao experimentEventDao;

    private final IdGenerator idGenerator;
    @Override
    public EnumSnapshotType getSnapshotType() {
        return EnumSnapshotType.CASEEvent;
    }

    @Override
    public SnapData readSource(SnapshotRequest req) {
        StringBuilder sb=new StringBuilder();
        List<ExperimentPersonEntity> rowsPerson = experimentPersonDao.getByExperimentId(req.getAppId(), req.getExperimentInstanceId(),
                ExperimentPersonEntity::getExperimentPersonId,
                ExperimentPersonEntity::getExperimentGroupId,
                ExperimentPersonEntity::getExperimentOrgId,
                ExperimentPersonEntity::getAccountId,
                ExperimentPersonEntity::getAccountName,
                ExperimentPersonEntity::getUserName,
                ExperimentPersonEntity::getCasePersonId);
        Map<String,List<ExperimentPersonEntity>> mapPerson=ShareUtil.XCollection.groupBy(rowsPerson, ExperimentPersonEntity::getCasePersonId);
        Map<String,CasePersonEntity> mapAccountId = ShareUtil.XCollection.toMap(casePersonDao.getByPersonIds(mapPerson.keySet(),
                CasePersonEntity::getAccountId,
                CasePersonEntity::getCasePersonId), CasePersonEntity::getAccountId);
        List<CaseEventEntity> rowsEvent = caseEventDao.getCaseEventsByPersonIds(mapAccountId.keySet(),
                CaseEventEntity::getAppId,
                CaseEventEntity::getCaseEventId,
                CaseEventEntity::getCaseEventName,
                CaseEventEntity::getPersonId,
                CaseEventEntity::getPersonName,
                CaseEventEntity::getEventCategId,
                CaseEventEntity::getCategName,
                CaseEventEntity::getDescr,
                CaseEventEntity::getTips,
                CaseEventEntity::getTriggerType,
                CaseEventEntity::getTriggerSpan,
                CaseEventEntity::getState);
        List<String> eventIds=ShareUtil.XCollection.map(rowsEvent, CaseEventEntity::getCaseEventId);
        Map<String,List<CaseEventActionEntity>> mapAction=ShareUtil.XCollection.groupBy( caseEventActionDao.getByEventIds(eventIds,
                CaseEventActionEntity::getCaseEventId,
                CaseEventActionEntity::getCaseEventId,
                CaseEventActionEntity::getCaseEventActionId,
                CaseEventActionEntity::getActionDesc), CaseEventActionEntity::getCaseEventId);
        SnapData rst = new SnapData();
        rst.setEvents(new ArrayList<>());
        for(CaseEventEntity event:rowsEvent){
            CasePersonEntity casePerson=mapAccountId.get(event.getPersonId());
            if(ShareUtil.XObject.isEmpty(casePerson)){
                sb.append(String.format("missCasePerson uimAccountId:%s ",event.getPersonId()));
                continue;
            }
            List<ExperimentPersonEntity> exptPersons=mapPerson.get(casePerson.getCasePersonId());
            if(ShareUtil.XObject.isEmpty(exptPersons)){
                sb.append(String.format("missExptPerson casePersonId:%s ",casePerson.getCasePersonId()));
                continue;
            }
            fillExperimentEvents(rst.getEvents(),req,exptPersons,event, mapAction.get(event.getCaseEventId()));
        }
        if(sb.length()>0) {
            logError("readSource", sb.toString());
            sb.setLength(0);
        }
        return rst;
    }
    void fillExperimentEvents(List<ExperimentEventEntity> rst,SnapshotRequest req, List<ExperimentPersonEntity> rowsPerson, CaseEventEntity rowEvent,List<CaseEventActionEntity> rowsAction){
        final String eventJson=buildEventJson(rowEvent,rowsAction);
        rowsPerson.forEach(i-> {
            rst.add(CopyWrapper.create(ExperimentEventEntity::new)
                    .endFrom(i)
                    .setAppId(req.getAppId())
                    .setExperimentInstanceId(req.getExperimentInstanceId())
                    .setCasePersonId(rowEvent.getPersonId())
                    .setCaseEventId(rowEvent.getCaseEventId())
                    .setAccountId(i.getAccountId())
                    .setPersonName(i.getUserName())
                    .setTriggerType(rowEvent.getTriggerType())
                    .setTriggerSpan(rowEvent.getTriggerSpan())
                    .setEventJson(eventJson)
                    .setState(EnumExperimentEventState.INIT.getCode())
                    .setExperimentEventId(idGenerator.nextIdStr())
                    .setId(null)
            );
        });
    }
    String buildEventJson(CaseEventEntity rowEvent,List<CaseEventActionEntity> rowsAction){
        try {
            List<ExptOrgEventActionVO> actions = ShareUtil.XCollection.map(rowsAction, i ->
                    CopyWrapper.create(ExptOrgEventActionVO::new).endFrom(i));
            ExperimentEventJson data = CopyWrapper.create(ExperimentEventJson::new)
                    .endFrom(rowEvent)
                    .setActions(actions);
            return JacksonUtil.toJson(data, true);
        }catch (Exception ex){
            logError(ex,"buildEventJson","caseEventId:%s",rowEvent.getCaseEventId());
            return "";
        }
    }

    @Override
    public boolean write(SnapshotRequest req,SnapData data){
        return saveSnapshotData(req,data);
    }

    @Override
    protected boolean saveSnapshotData(SnapshotRequest req, SnapData data) {
        if (ShareUtil.XObject.anyEmpty(data, () -> data.getEvents())) {
            return true;
        }
        return experimentEventDao.tranSaveSnapshot(req.getExperimentInstanceId(), data.events);
    }

    @Data
    public static class SnapData {
        private List<ExperimentEventEntity> events;

    }
}
