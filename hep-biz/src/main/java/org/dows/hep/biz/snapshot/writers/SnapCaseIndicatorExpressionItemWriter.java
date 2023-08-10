package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.dao.CaseIndicatorExpressionDao;
import org.dows.hep.biz.dao.IndicatorExpressionDao;
import org.dows.hep.biz.snapshot.BaseSnapshotTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseIndicatorExpressionEntity;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapCaseIndicatorExpressionItemWriter extends BaseSnapshotTableWriter<CaseIndicatorExpressionItemEntity,  SnapCaseIndicatorExpressionItemEntity, SnapCaseIndicatorExpressionItemService> {
    public SnapCaseIndicatorExpressionItemWriter() {
        super(EnumSnapshotType.CASEIndicatorExpressionItem, SnapCaseIndicatorExpressionItemEntity::new);
    }

    @Autowired
    private CaseIndicatorExpressionDao caseIndicatorExpressionDao;

    @Autowired
    private IndicatorExpressionDao indicatorExpressionDao;


    @Override
    public List<CaseIndicatorExpressionItemEntity> readSource(SnapshotRequest req) {
        List<CaseIndicatorExpressionItemEntity> rst=new ArrayList<>();
        final List<String> expressionIds=new ArrayList<>();
        final List<String> expressonItemIds=new ArrayList<>();
        List<CaseIndicatorExpressionEntity> rowsCaseExpression=caseIndicatorExpressionDao.getBySource(List.of(EnumIndicatorExpressionSource.EMERGENCY_TRIGGER_CONDITION.getSource(),
                EnumIndicatorExpressionSource.EMERGENCY_INFLUENCE_INDICATOR.getSource(),
                EnumIndicatorExpressionSource.EMERGENCY_ACTION_INFLUENCE_INDICATOR.getSource()),
                CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId,
                CaseIndicatorExpressionEntity::getMinIndicatorExpressionItemId,
                CaseIndicatorExpressionEntity::getMaxIndicatorExpressionItemId
                );
        rowsCaseExpression.forEach(i->{
            expressionIds.add(i.getCaseIndicatorExpressionId());
            if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                expressonItemIds.add(i.getMinIndicatorExpressionItemId());
            }
            if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())){
                expressonItemIds.add(i.getMaxIndicatorExpressionItemId());
            }
        });
        rst.addAll(caseIndicatorExpressionDao.getSubByLeadIds(expressionIds));
        rst.addAll(caseIndicatorExpressionDao.getSubBySubIds(expressonItemIds));
        expressionIds.clear();
        expressonItemIds.clear();
        List<IndicatorExpressionEntity> rowsBaseExpression=indicatorExpressionDao.getBySource(List.of(EnumIndicatorExpressionSource.INDICATOR_OPERATOR_NO_REPORT_TWO_LEVEL.getSource(),
                EnumIndicatorExpressionSource.INDICATOR_OPERATOR_HAS_REPORT_FOUR_LEVEL.getSource()),
                IndicatorExpressionEntity::getIndicatorExpressionId,
                IndicatorExpressionEntity::getMinIndicatorExpressionItemId,
                IndicatorExpressionEntity::getMaxIndicatorExpressionItemId
                );
        rowsBaseExpression.forEach(i->{
            expressionIds.add(i.getIndicatorExpressionId());
            if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                expressonItemIds.add(i.getMinIndicatorExpressionItemId());
            }
            if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())){
                expressonItemIds.add(i.getMaxIndicatorExpressionItemId());
            }
        });
        final List<IndicatorExpressionItemEntity> rowsBaseExpressionItem=indicatorExpressionDao.getSubByLeadIds(expressionIds);
        rowsBaseExpressionItem.addAll(indicatorExpressionDao.getSubBySubIds(expressonItemIds));
        rst.addAll(ShareUtil.XCollection.map(rowsBaseExpressionItem, i->
                CopyWrapper.create(CaseIndicatorExpressionItemEntity::new)
                        .endFrom(i)
                        .setCaseIndicatorExpressionItemId(i.getIndicatorExpressionItemId())));
        return rst;
    }
}
