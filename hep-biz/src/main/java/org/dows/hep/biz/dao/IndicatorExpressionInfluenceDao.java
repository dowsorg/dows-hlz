package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorExpressionInfluenceEntity;
import org.dows.hep.service.IndicatorExpressionInfluenceService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/12 14:37
 */
@Component
public class IndicatorExpressionInfluenceDao extends BaseDao<IndicatorExpressionInfluenceService, IndicatorExpressionInfluenceEntity>  {

    public IndicatorExpressionInfluenceDao(){
        super("指标作用关系不存在或已删除","指标作用关系保存失败");
    }

    @Override
    protected SFunction<IndicatorExpressionInfluenceEntity, String> getColId() {
        return IndicatorExpressionInfluenceEntity::getIndicatorExpressionInfluenceId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorExpressionInfluenceEntity item) {
        return item::setIndicatorExpressionInfluenceId;
    }

    @Override
    protected SFunction<IndicatorExpressionInfluenceEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorExpressionInfluenceEntity item) {
        return null;
    }


    public List<IndicatorExpressionInfluenceEntity> getByIndicatorIds(Collection<String> indicatorIds, SFunction<IndicatorExpressionInfluenceEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(indicatorIds)) {
            return Collections.emptyList();
        }
        final boolean oneFlag = indicatorIds.size() == 1;
        return service.lambdaQuery()
                .eq(oneFlag, IndicatorExpressionInfluenceEntity::getIndicatorInstanceId, indicatorIds.iterator().next())
                .in(!oneFlag, IndicatorExpressionInfluenceEntity::getIndicatorInstanceId, indicatorIds)
                .select(cols)
                .list();
    }


}
