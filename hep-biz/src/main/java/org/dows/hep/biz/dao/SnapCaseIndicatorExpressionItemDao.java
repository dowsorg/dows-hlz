package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionItemService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/21 16:28
 */
@Component
public class SnapCaseIndicatorExpressionItemDao extends BaseDao<SnapCaseIndicatorExpressionItemService, SnapCaseIndicatorExpressionItemEntity> {


    public SnapCaseIndicatorExpressionItemDao() {
        super("指标公式细项快照不存在");
    }

    @Override
    protected SFunction<SnapCaseIndicatorExpressionItemEntity, String> getColId() {
        return SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapCaseIndicatorExpressionItemEntity item) {
        return item::setCaseIndicatorExpressionItemId;
    }

    @Override
    protected SFunction<SnapCaseIndicatorExpressionItemEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapCaseIndicatorExpressionItemEntity item) {
        return null;
    }

    public List<SnapCaseIndicatorExpressionItemEntity> getByExpressionId(String experimentId, String expressionId, SFunction<SnapCaseIndicatorExpressionItemEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(expressionId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionItemEntity::getExperimentInstanceId, experimentId)
                .eq(SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId, expressionId)
                .orderByAsc(SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId,SnapCaseIndicatorExpressionItemEntity::getSeq)
                .select(cols)
                .list();
    }

    public List<SnapCaseIndicatorExpressionItemEntity> getByExpressionId(String experimentId, Collection<String> expressionIds, SFunction<SnapCaseIndicatorExpressionItemEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(expressionIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = expressionIds.size() == 1;
        return service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionItemEntity::getExperimentInstanceId, experimentId)
                .eq(oneFlag, SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId, expressionIds.iterator().next())
                .in(!oneFlag, SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId, expressionIds)
                .orderByAsc(SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId, SnapCaseIndicatorExpressionItemEntity::getSeq)
                .select(cols)
                .list();
    }
    public List<SnapCaseIndicatorExpressionItemEntity> getByExpressionItemId(String experimentId, Collection<String> expressionItemIds, SFunction<SnapCaseIndicatorExpressionItemEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(expressionItemIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = expressionItemIds.size() == 1;
        return service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionItemEntity::getExperimentInstanceId, experimentId)
                .eq(oneFlag, SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, expressionItemIds.iterator().next())
                .in(!oneFlag, SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, expressionItemIds)
                .orderByAsc(SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId, SnapCaseIndicatorExpressionItemEntity::getSeq)
                .select(cols)
                .list();
    }


}
