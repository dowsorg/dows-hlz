package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorExpressionItemRsEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionRsEntity;
import org.dows.hep.service.ExperimentIndicatorExpressionItemRsService;
import org.dows.hep.service.ExperimentIndicatorExpressionRsService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/8/13 21:50
 */
public class ExperimentIndicatorExpressionRsDao extends BaseSubDao<ExperimentIndicatorExpressionRsService, ExperimentIndicatorExpressionRsEntity,
        ExperimentIndicatorExpressionItemRsService, ExperimentIndicatorExpressionItemRsEntity>{

    public ExperimentIndicatorExpressionRsDao() {
        super("实验指标公式不存在");
    }

    @Override
    protected SFunction<ExperimentIndicatorExpressionRsEntity, String> getColId() {
        return ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentIndicatorExpressionRsEntity item) {
        return item::setExperimentIndicatorExpressionId;
    }

    @Override
    protected SFunction<ExperimentIndicatorExpressionRsEntity, Integer> getColState() {
        return null;
    }
    @Override
    protected SFunction<ExperimentIndicatorExpressionRsEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentIndicatorExpressionRsEntity item) {
        return null;
    }

    @Override
    protected SFunction<ExperimentIndicatorExpressionItemRsEntity, String> getColLeadId() {
        return ExperimentIndicatorExpressionItemRsEntity::getIndicatorExpressionId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(ExperimentIndicatorExpressionItemRsEntity item) {
        return item::setExperimentIndicatorExpressionItemId;
    }

    @Override
    protected SFunction<ExperimentIndicatorExpressionItemRsEntity, String> getColSubId() {
        return ExperimentIndicatorExpressionItemRsEntity::getExperimentIndicatorExpressionItemId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(ExperimentIndicatorExpressionItemRsEntity item) {
        return item::setExperimentIndicatorExpressionItemId;
    }

    public List<ExperimentIndicatorExpressionRsEntity> getByExperimentIndicatorIds(Collection<String> indicatorIds,Integer source,
                                                                                   SFunction<ExperimentIndicatorExpressionRsEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=indicatorIds.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, ExperimentIndicatorExpressionRsEntity::getPrincipalId,indicatorIds.iterator().next())
                .in(!oneFlag, ExperimentIndicatorExpressionRsEntity::getPrincipalId,indicatorIds)
                .eq(ShareUtil.XObject.notEmpty(source), ExperimentIndicatorExpressionRsEntity::getSource,source)
                .select(cols)
                .list();
    }


}
