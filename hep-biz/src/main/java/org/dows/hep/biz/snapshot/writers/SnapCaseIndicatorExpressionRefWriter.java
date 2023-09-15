package org.dows.hep.biz.snapshot.writers;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumStatus;
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

    @Autowired
    private CrowdsInstanceDao crowdsInstanceDao;

    @Autowired
    private RiskModelDao riskModelDao;

    @Autowired
    private CaseIndicatorInstanceDao caseIndicatorInstanceDao;

    @Override
    public List<CaseIndicatorExpressionRefEntity> readSource(SnapshotRequest req) {
        List<CaseIndicatorExpressionRefEntity> rst=new ArrayList<>();

        List<String> casePersonIds=ShareUtil.XCollection.map(experimentPersonDao.getByExperimentId(req.getAppId(), req.getExperimentInstanceId(), ExperimentPersonEntity::getCasePersonId),
                ExperimentPersonEntity::getCasePersonId);
        List<String> caseAccountIds=ShareUtil.XCollection.map(casePersonDao.getByPersonIds(casePersonIds, CasePersonEntity::getAccountId),
                CasePersonEntity::getAccountId);
        List<String> caseEventIds= ShareUtil.XCollection.map(caseEventDao.getCaseEventsByPersonIds(caseAccountIds, CaseEventEntity::getCaseEventId),
                CaseEventEntity::getCaseEventId);
        List<String> caseActionIds=ShareUtil.XCollection.map(caseEventActionDao.getByEventIds(caseEventIds, CaseEventActionEntity::getCaseEventActionId),
                CaseEventActionEntity::getCaseEventActionId);
        caseEventIds.addAll (caseActionIds);
        rst.addAll(caseIndicatorExpressionRefDao.getByReasonId(req.getAppId(), caseEventIds ));
        //治疗效果
        List<String> refItemIds= ShareUtil.XCollection.map(treatItemDao.getAll(req.getAppId(), EnumStatus.ENABLE.getCode(), true,TreatItemEntity::getTreatItemId),
                TreatItemEntity::getTreatItemId);
        List<IndicatorExpressionRefEntity> refItems=indicatorExpressionRefDao.getByReasonId(req.getAppId(), refItemIds);

        //人物指标
        refItemIds= ShareUtil.XCollection.map(caseIndicatorInstanceDao.getByAccountIds(caseAccountIds,CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId),
                CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId);
        rst.addAll(caseIndicatorExpressionRefDao.getByReasonId(req.getAppId(), refItemIds));

        //人群-风险模型
        refItemIds=ShareUtil.XCollection.map(crowdsInstanceDao.getAll(req.getAppId(), true,CrowdsInstanceEntity::getCrowdsId),
                CrowdsInstanceEntity::getCrowdsId);
        refItemIds.addAll(ShareUtil.XCollection.map(riskModelDao.getAll(req.getAppId(), EnumStatus.ENABLE.getCode(), RiskModelEntity::getRiskModelId),
                RiskModelEntity::getRiskModelId));
        refItems.addAll(indicatorExpressionRefDao.getByReasonId(req.getAppId(), refItemIds));

        rst.addAll(ShareUtil.XCollection.map(refItems, i->
                CopyWrapper.create(CaseIndicatorExpressionRefEntity::new)
                        .endFrom(i)
                        .setCaseIndicatorExpressionRefId(i.getIndicatorExpressionRefId())));
        return rst;
    }
}
