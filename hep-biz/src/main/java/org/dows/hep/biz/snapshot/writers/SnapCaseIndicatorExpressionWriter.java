package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.dao.*;
import org.dows.hep.biz.snapshot.BaseSnapshotTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.biz.spel.SnapshotRefValidator;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionRefEntity;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapCaseIndicatorExpressionWriter extends BaseSnapshotTableWriter<CaseIndicatorExpressionEntity, SnapCaseIndicatorExpressionEntity, SnapCaseIndicatorExpressionService> {
    public SnapCaseIndicatorExpressionWriter() {
        super(EnumSnapshotType.CASEIndicatorExpression, SnapCaseIndicatorExpressionEntity::new);
    }

    @Autowired
    private CaseIndicatorExpressionDao caseIndicatorExpressionDao;

    @Autowired
    private IndicatorExpressionDao indicatorExpressionDao;

    @Autowired
    private ExperimentPersonDao experimentPersonDao;
    @Autowired
    private CasePersonDao casePersonDao;

    @Autowired
    private CaseIndicatorInstanceDao caseIndicatorInstanceDao;
    @Autowired
    private SnapCaseIndicatorExpressionRefDao snapCaseIndicatorExpressionRefDao;

    @Override
    public List<CaseIndicatorExpressionEntity> readSource(SnapshotRequest req) {
        final List<CaseIndicatorExpressionEntity> rst=new ArrayList<>();
        final String experimentId=req.getExperimentInstanceId();
        SnapshotRefValidator refValidator=new SnapshotRefValidator(experimentId);
        final String refExperimentId4ExpressionRef=refValidator.checkExpressionRef().getExpressionRefId();
        if(ShareUtil.XObject.isEmpty(refExperimentId4ExpressionRef)){
            logError("SNAPTRACE--expression","missExpresionRefRef:%s",experimentId);
            AssertUtil.justThrow("未找到公式关联快照");
        }
        List<IndicatorExpressionEntity> rowsExpression=indicatorExpressionDao.getBySource(List.of(EnumIndicatorExpressionSource.INDICATOR_OPERATOR_NO_REPORT_TWO_LEVEL.getSource(),
                EnumIndicatorExpressionSource.INDICATOR_OPERATOR_HAS_REPORT_FOUR_LEVEL.getSource(),
                EnumIndicatorExpressionSource.CROWDS.getSource(),
                EnumIndicatorExpressionSource.RISK_MODEL.getSource()
        ));
        rst.addAll(ShareUtil.XCollection.map(rowsExpression, i->
                CopyWrapper.create(CaseIndicatorExpressionEntity::new)
                        .endFrom(i)
                        .setCaseIndicatorExpressionId(i.getIndicatorExpressionId())
                        .setCasePrincipalId(i.getPrincipalId())));
        final Set<String> experssionIds=ShareUtil.XCollection.toSet(snapCaseIndicatorExpressionRefDao.getByExperiment(refExperimentId4ExpressionRef,
                SnapCaseIndicatorExpressionRefEntity::getIndicatorExpressionId),
                SnapCaseIndicatorExpressionRefEntity::getIndicatorExpressionId);

        rowsExpression.forEach(i->experssionIds.remove(i.getIndicatorExpressionId()));
        rst.addAll(caseIndicatorExpressionDao.getByExperssionIds(experssionIds, List.of(EnumIndicatorExpressionSource.EMERGENCY_TRIGGER_CONDITION.getSource(),
                EnumIndicatorExpressionSource.EMERGENCY_INFLUENCE_INDICATOR.getSource(),
                EnumIndicatorExpressionSource.EMERGENCY_ACTION_INFLUENCE_INDICATOR.getSource(),
                EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource()
                )
        ));
        return rst;
    }


    public List<CaseIndicatorExpressionEntity> readSourceOld(SnapshotRequest req) {

        List<CaseIndicatorExpressionEntity> rst=new ArrayList<>();
        rst.addAll(caseIndicatorExpressionDao.getBySource(List.of(EnumIndicatorExpressionSource.EMERGENCY_TRIGGER_CONDITION.getSource(),
                EnumIndicatorExpressionSource.EMERGENCY_INFLUENCE_INDICATOR.getSource(),
                EnumIndicatorExpressionSource.EMERGENCY_ACTION_INFLUENCE_INDICATOR.getSource()
                )));
        List<IndicatorExpressionEntity> rowsExpression=indicatorExpressionDao.getBySource(List.of(EnumIndicatorExpressionSource.INDICATOR_OPERATOR_NO_REPORT_TWO_LEVEL.getSource(),
                EnumIndicatorExpressionSource.INDICATOR_OPERATOR_HAS_REPORT_FOUR_LEVEL.getSource(),
                EnumIndicatorExpressionSource.CROWDS.getSource(),
                EnumIndicatorExpressionSource.RISK_MODEL.getSource()
                ));
        rst.addAll(ShareUtil.XCollection.map(rowsExpression, i->
                CopyWrapper.create(CaseIndicatorExpressionEntity::new)
                        .endFrom(i)
                        .setCaseIndicatorExpressionId(i.getIndicatorExpressionId())
                        .setCasePrincipalId(i.getPrincipalId())));

        //人物指标
        List<String> casePersonIds=ShareUtil.XCollection.map(experimentPersonDao.getByExperimentId(req.getAppId(), req.getExperimentInstanceId(), ExperimentPersonEntity::getCasePersonId),
                ExperimentPersonEntity::getCasePersonId);
        List<String> caseAccountIds=ShareUtil.XCollection.map(casePersonDao.getByPersonIds(casePersonIds, CasePersonEntity::getAccountId),
                CasePersonEntity::getAccountId);
        List<String> refItemIds= ShareUtil.XCollection.map(caseIndicatorInstanceDao.getByAccountIds(caseAccountIds,CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId),
                CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId);
        rst.addAll(caseIndicatorExpressionDao.getByIndicatorId(refItemIds));

        return rst;
    }
}
