package org.dows.hep.biz.snapshot.writers;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.*;
import org.dows.hep.biz.snapshot.BaseSnapshotTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionRefEntity;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
@Slf4j
public class SnapCaseIndicatorExpressionRefWriter extends BaseSnapshotTableWriter<CaseIndicatorExpressionRefEntity,SnapCaseIndicatorExpressionRefEntity, SnapCaseIndicatorExpressionRefService> {
    public SnapCaseIndicatorExpressionRefWriter() {
        super(EnumSnapshotType.CASEIndicatorExpressionRef, SnapCaseIndicatorExpressionRefEntity::new);
    }
    @Autowired
    private CaseIndicatorExpressionRefDao caseIndicatorExpressionRefDao;

    @Autowired
    private IndicatorExpressionRefDao indicatorExpressionRefDao;

    @Autowired
    private ExperimentPersonDao experimentPersonDao;

    @Autowired
    private CasePersonDao casePersonDao;

    @Autowired
    private CaseEventDao caseEventDao;

    @Autowired
    private CaseEventActionDao caseEventActionDao;

    @Autowired
    private TreatItemDao treatItemDao;

    @Override
    public List<CaseIndicatorExpressionRefEntity> readSource(SnapshotRequest req) {
        List<CaseIndicatorExpressionRefEntity> rst=new ArrayList<>();

        List<String> casePersonIds=ShareUtil.XCollection.map(experimentPersonDao.getByExperimentId(req.getAppId(), req.getExperimentInstanceId(), ExperimentPersonEntity::getCasePersonId),
                ExperimentPersonEntity::getCasePersonId);
        List<String> caseAccountIds=ShareUtil.XCollection.map(casePersonDao.getByPersonIds(casePersonIds, CasePersonEntity::getAccountId),
                CasePersonEntity::getAccountId);
        List<String> caseEventIds= ShareUtil.XCollection.map(caseEventDao.getCaseEventsByPersonIds(caseAccountIds, CaseEventEntity::getCaseEventId),
                CaseEventEntity::getCaseEventId);
        List<String> caseActionIds=ShareUtil.XCollection.map(caseEventActionDao.getByEventId(caseEventIds, CaseEventActionEntity::getCaseEventActionId),
                CaseEventActionEntity::getCaseEventActionId);
        caseEventIds.addAll (caseActionIds);
        rst.addAll(caseIndicatorExpressionRefDao.getByReasonId(req.getAppId(), caseEventIds ));
        List<String> treatItemIds= ShareUtil.XCollection.map(treatItemDao.getAll(req.getAppId(), true,TreatItemEntity::getTreatItemId),
                TreatItemEntity::getTreatItemId);
        List<IndicatorExpressionRefEntity> rowsTreatRef=indicatorExpressionRefDao.getByReasonId(req.getAppId(), treatItemIds);
        rst.addAll(ShareUtil.XCollection.map(rowsTreatRef, i->
                CopyWrapper.create(CaseIndicatorExpressionRefEntity::new)
                        .endFrom(i)
                        .setCaseIndicatorExpressionRefId(i.getIndicatorExpressionRefId())));
        return rst;
    }
}
