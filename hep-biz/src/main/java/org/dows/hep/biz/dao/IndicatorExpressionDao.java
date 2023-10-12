package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;
import org.dows.hep.service.IndicatorExpressionItemService;
import org.dows.hep.service.IndicatorExpressionService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/6/13 10:36
 */
@Component
public class IndicatorExpressionDao extends BaseSubDao<IndicatorExpressionService,IndicatorExpressionEntity, IndicatorExpressionItemService, IndicatorExpressionItemEntity> {

    public IndicatorExpressionDao(){
        super("表达式不存在或已删除");
    }

    @Override
    protected SFunction<IndicatorExpressionEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<IndicatorExpressionEntity, String> getColId() {
        return IndicatorExpressionEntity::getIndicatorExpressionId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorExpressionEntity item) {
        return item::setIndicatorExpressionId;
    }

    @Override
    protected SFunction<IndicatorExpressionEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorExpressionEntity item) {
        return null;
    }

    @Override
    protected SFunction<IndicatorExpressionItemEntity, String> getColLeadId() {
        return IndicatorExpressionItemEntity::getIndicatorExpressionId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(IndicatorExpressionItemEntity item) {
        return item::setIndicatorExpressionId;
    }

    @Override
    protected SFunction<IndicatorExpressionItemEntity, String> getColSubId() {
        return IndicatorExpressionItemEntity::getIndicatorExpressionItemId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(IndicatorExpressionItemEntity item) {
        return item::setIndicatorExpressionItemId;
    }

    public List<IndicatorExpressionEntity> getBySource(Collection<Integer> sources,SFunction<IndicatorExpressionEntity,?>... cols) {
        if(ShareUtil.XObject.isEmpty(sources)){
            return Collections.emptyList();
        }
        final boolean oneFlag=sources.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, IndicatorExpressionEntity::getSource,sources.iterator().next())
                .in(!oneFlag, IndicatorExpressionEntity::getSource, sources)
                .orderByAsc(IndicatorExpressionEntity::getId)
                .select(cols)
                .list();
    }

    public List<IndicatorExpressionEntity> getByExpressionId(Collection<String> expressionIds, Integer source, SFunction<IndicatorExpressionEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(expressionIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = expressionIds.size() == 1;
        return service.lambdaQuery()
                .eq(oneFlag, IndicatorExpressionEntity::getIndicatorExpressionId, expressionIds.iterator().next())
                .in(!oneFlag, IndicatorExpressionEntity::getIndicatorExpressionId, expressionIds)
                .eq(ShareUtil.XObject.notEmpty(source), IndicatorExpressionEntity::getSource, source)
                .select(cols)
                .list();
    }

    public List<IndicatorExpressionItemEntity> getSubByExpressionId(Collection<String> expressionIds, SFunction<IndicatorExpressionItemEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(expressionIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = expressionIds.size() == 1;
        return subService.lambdaQuery()
                .eq(oneFlag, IndicatorExpressionItemEntity::getIndicatorExpressionId, expressionIds.iterator().next())
                .in(!oneFlag, IndicatorExpressionItemEntity::getIndicatorExpressionId, expressionIds)
                .orderByAsc(IndicatorExpressionItemEntity::getIndicatorExpressionId, IndicatorExpressionItemEntity::getSeq)
                .select(cols)
                .list();
    }
}
