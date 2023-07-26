package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionRefEntity;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionRefService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/21 16:02
 */
@Component
public class SnapCaseIndicatorExpressionRefDao extends BaseDao<SnapCaseIndicatorExpressionRefService, SnapCaseIndicatorExpressionRefEntity> {

    public SnapCaseIndicatorExpressionRefDao() {
        super("指标公式关联快照不存在");
    }

    @Override
    protected SFunction<SnapCaseIndicatorExpressionRefEntity, String> getColId() {
        return SnapCaseIndicatorExpressionRefEntity::getCaseIndicatorExpressionRefId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapCaseIndicatorExpressionRefEntity item) {
        return item::setCaseIndicatorExpressionRefId;
    }

    @Override
    protected SFunction<SnapCaseIndicatorExpressionRefEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapCaseIndicatorExpressionRefEntity item) {
        return null;
    }

    public List<SnapCaseIndicatorExpressionRefEntity> getByReasonId(String experimentId, String reasonId, SFunction<SnapCaseIndicatorExpressionRefEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(reasonId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionRefEntity::getExperimentInstanceId, experimentId)
                .eq(SnapCaseIndicatorExpressionRefEntity::getReasonId, reasonId)
                .select(cols)
                .list();
    }
    public List<SnapCaseIndicatorExpressionRefEntity> getByReasonId(String experimentId, Collection<String> reasonIds, SFunction<SnapCaseIndicatorExpressionRefEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(reasonIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag=reasonIds.size()==1;
        return service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionRefEntity::getExperimentInstanceId, experimentId)
                .eq(oneFlag, SnapCaseIndicatorExpressionRefEntity::getReasonId,reasonIds.iterator().next())
                .in(!oneFlag, SnapCaseIndicatorExpressionRefEntity::getReasonId,reasonIds)
                .select(cols)
                .list();
    }
}
