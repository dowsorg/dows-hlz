package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionEntity;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/21 16:04
 */
@Component
public class SnapCaseIndicatorExpressionDao extends BaseDao<SnapCaseIndicatorExpressionService, SnapCaseIndicatorExpressionEntity> {
    public SnapCaseIndicatorExpressionDao() {
        super("指标公式快照不存在");
    }

    @Override
    protected SFunction<SnapCaseIndicatorExpressionEntity, String> getColId() {
        return SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapCaseIndicatorExpressionEntity item) {
        return item::setCaseIndicatorExpressionId;
    }

    @Override
    protected SFunction<SnapCaseIndicatorExpressionEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapCaseIndicatorExpressionEntity item) {
        return null;
    }

    public SnapCaseIndicatorExpressionEntity getByExpressionId(String experimentId, String expressionId,Integer source, SFunction<SnapCaseIndicatorExpressionEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(expressionId)) {
            return null;
        }
        List<SnapCaseIndicatorExpressionEntity> list= service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionEntity::getExperimentInstanceId, experimentId)
                .eq(SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, expressionId)
                .eq(ShareUtil.XObject.notEmpty(source ),SnapCaseIndicatorExpressionEntity::getSource,source)
                .select(cols)
                .list();
        if(list.size()==0){
            return null;
        }
        return list.get(0);
    }

    public List<SnapCaseIndicatorExpressionEntity> getByExpressionId(String experimentId, Collection<String> expressionIds, Integer source, SFunction<SnapCaseIndicatorExpressionEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(expressionIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = expressionIds.size() == 1;
        return service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionEntity::getExperimentInstanceId, experimentId)
                .eq(oneFlag, SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, expressionIds.iterator().next())
                .in(!oneFlag, SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, expressionIds)
                .eq(ShareUtil.XObject.notEmpty(source), SnapCaseIndicatorExpressionEntity::getSource, source)
                .select(cols)
                .list();
    }

    public List<SnapCaseIndicatorExpressionEntity> getByExperimentId(String experimentId,Collection<Integer> sources, SFunction<SnapCaseIndicatorExpressionEntity,?>... cols) {

        return service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionEntity::getExperimentInstanceId, experimentId)
                .eq(null != sources && sources.size() == 1, SnapCaseIndicatorExpressionEntity::getSource, sources.iterator().next())
                .in(null != sources && sources.size() > 1, SnapCaseIndicatorExpressionEntity::getSource, sources)
                .select(cols)
                .list();
    }

    public List<SnapCaseIndicatorExpressionEntity> getByCaseIndicatorIds(String experimentId, Collection<String> indicatorIds, Integer source,
                                                                                   SFunction<SnapCaseIndicatorExpressionEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=indicatorIds.size()==1;
        return service.lambdaQuery()
                .eq(SnapCaseIndicatorExpressionEntity::getExperimentInstanceId, experimentId)
                .eq(oneFlag, SnapCaseIndicatorExpressionEntity::getCasePrincipalId,indicatorIds.iterator().next())
                .in(!oneFlag, SnapCaseIndicatorExpressionEntity::getCasePrincipalId,indicatorIds)
                .eq(ShareUtil.XObject.notEmpty(source), SnapCaseIndicatorExpressionEntity::getSource,source)
                .select(cols)
                .list();
    }

}
