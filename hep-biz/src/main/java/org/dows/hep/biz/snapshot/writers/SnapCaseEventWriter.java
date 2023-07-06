package org.dows.hep.biz.snapshot.writers;

import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.dao.CaseEventActionDao;
import org.dows.hep.biz.dao.CaseEventDao;
import org.dows.hep.biz.dao.CasePersonDao;
import org.dows.hep.biz.dao.ExperimentPersonDao;
import org.dows.hep.biz.snapshot.BaseSnapshotWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseEventEntity;
import org.dows.hep.entity.CasePersonEntity;
import org.dows.hep.entity.ExperimentEventEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/5 18:07
 */
@Service
@RequiredArgsConstructor
public class SnapCaseEventWriter extends BaseSnapshotWriter<SnapCaseEventWriter.SnapData> {


    private final CaseEventDao caseEventDao;
    private final CaseEventActionDao caseEventActionDao;
    private final CasePersonDao casePersonDao;
    private final ExperimentPersonDao experimentPersonDao;
    @Override
    public EnumSnapshotType getSnapshotType() {
        return EnumSnapshotType.CASEEvent;
    }

    @Override
    public SnapData readSource(SnapshotRequest req) {
        List<ExperimentPersonEntity> rowsPerson = experimentPersonDao.getByExperimentId(req.getAppId(), req.getExperimentInstanceId(),
                ExperimentPersonEntity::getExperimentPersonId,
                ExperimentPersonEntity::getExperimentGroupId,
                ExperimentPersonEntity::getExperimentOrgId,
                ExperimentPersonEntity::getAccountId,
                ExperimentPersonEntity::getCasePersonId);
        List<String> casePersonIds = ShareUtil.XCollection.map(rowsPerson, ExperimentPersonEntity::getCasePersonId);
        List<String> accountIds = ShareUtil.XCollection.map(casePersonDao.getByPersonIds(casePersonIds, CasePersonEntity::getAccountId), CasePersonEntity::getAccountId);
        List<CaseEventEntity> rowsEvent = caseEventDao.getCaseEventsByPersons(accountIds,
                CaseEventEntity::getCaseEventId,
                CaseEventEntity::getAppId,
                CaseEventEntity::getCaseEventName,
                CaseEventEntity::getEventCategId,
                CaseEventEntity::getDescr,
                CaseEventEntity::getTips,
                CaseEventEntity::getTriggerType,
                CaseEventEntity::getTriggerSpan,
                CaseEventEntity::getState);

        SnapData rst = new SnapData();

        return rst;
    }

    @Override
    protected boolean saveSnapshotData(SnapshotRequest req, SnapData data) {
        return false;
    }

    public static class SnapData {
        private List<ExperimentEventEntity> events;

    }
}
