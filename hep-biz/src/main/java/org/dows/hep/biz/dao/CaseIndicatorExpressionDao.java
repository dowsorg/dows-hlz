package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseIndicatorExpressionEntity;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.service.CaseIndicatorExpressionItemService;
import org.dows.hep.service.CaseIndicatorExpressionService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/6/13 10:36
 */
@Component
public class CaseIndicatorExpressionDao extends BaseSubDao<CaseIndicatorExpressionService,CaseIndicatorExpressionEntity, CaseIndicatorExpressionItemService, CaseIndicatorExpressionItemEntity> {

    public CaseIndicatorExpressionDao(){
        super("表达式不存在或已删除");
    }

    @Override
    protected SFunction<CaseIndicatorExpressionEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<CaseIndicatorExpressionEntity, String> getColId() {
        return CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId;
    }

    @Override
    protected SFunction<String, ?> setColId(CaseIndicatorExpressionEntity item) {
        return item::setCaseIndicatorExpressionId;
    }

    @Override
    protected SFunction<CaseIndicatorExpressionEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CaseIndicatorExpressionEntity item) {
        return null;
    }

    @Override
    protected SFunction<CaseIndicatorExpressionItemEntity, String> getColLeadId() {
        return CaseIndicatorExpressionItemEntity::getIndicatorExpressionId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(CaseIndicatorExpressionItemEntity item) {
        return item::setIndicatorExpressionId;
    }

    @Override
    protected SFunction<CaseIndicatorExpressionItemEntity, String> getColSubId() {
        return CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(CaseIndicatorExpressionItemEntity item) {
        return item::setCaseIndicatorExpressionItemId;
    }

    public List<CaseIndicatorExpressionEntity> getBySource(Collection<Integer> sources,SFunction<CaseIndicatorExpressionEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(sources)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = sources.size() == 1;
        return service.lambdaQuery()
                .eq(oneFlag, CaseIndicatorExpressionEntity::getSource, sources.iterator().next())
                .in(!oneFlag, CaseIndicatorExpressionEntity::getSource, sources)
                .orderByAsc(CaseIndicatorExpressionEntity::getId)
                .select(cols)
                .list();
    }

    public List<CaseIndicatorExpressionEntity> getByExperssionIds(Collection<String> experssionIds, Collection<Integer> sources,  SFunction<CaseIndicatorExpressionEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(experssionIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = experssionIds.size() == 1;
        return service.lambdaQuery()
                .eq(oneFlag, CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, experssionIds.iterator().next())
                .in(!oneFlag, CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, experssionIds)
                .eq(null != sources && sources.size() == 1, CaseIndicatorExpressionEntity::getSource, sources.iterator().next())
                .in(null != sources && sources.size() > 1, CaseIndicatorExpressionEntity::getSource, sources)
                .orderByAsc(CaseIndicatorExpressionEntity::getId)
                .select(cols)
                .list();
    }

    public List<CaseIndicatorExpressionEntity> getByIndicatorId(Collection<String> indicatorIds,SFunction<CaseIndicatorExpressionEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(indicatorIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = indicatorIds.size() == 1;
        return service.lambdaQuery()
                .eq(oneFlag, CaseIndicatorExpressionEntity::getCasePrincipalId, indicatorIds.iterator().next())
                .in(!oneFlag, CaseIndicatorExpressionEntity::getCasePrincipalId, indicatorIds)
                .orderByAsc(CaseIndicatorExpressionEntity::getId)
                .select(cols)
                .list();
    }
}
