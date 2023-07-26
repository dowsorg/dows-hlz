package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.hep.service.ExperimentIndicatorValRsService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/20 17:41
 */
@Component
public class ExperimentIndicatorValRsDao  extends BaseDao<ExperimentIndicatorValRsService, ExperimentIndicatorValRsEntity>{

    public ExperimentIndicatorValRsDao() {
        super("人物指标值不存在");
    }

    @Override
    protected SFunction<ExperimentIndicatorValRsEntity, String> getColId() {
        return ExperimentIndicatorValRsEntity::getExperimentIndicatorValId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentIndicatorValRsEntity item) {
        return item::setExperimentIndicatorValId;
    }

    @Override
    protected SFunction<ExperimentIndicatorValRsEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentIndicatorValRsEntity item) {
        return null;
    }

    public List<ExperimentIndicatorValRsEntity> getByExperimentIdAndIndicatorIds(String experimentId, Collection<String> indicatorIds, Integer period,
                                                                                 SFunction<ExperimentIndicatorValRsEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=indicatorIds.size()==1;
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(experimentId), ExperimentIndicatorValRsEntity::getExperimentId,experimentId)
                .eq(ExperimentIndicatorValRsEntity::getPeriods, period)
                .eq(oneFlag,ExperimentIndicatorValRsEntity::getIndicatorInstanceId,indicatorIds.iterator().next())
                .in(!oneFlag,ExperimentIndicatorValRsEntity::getIndicatorInstanceId,indicatorIds)
                .select(cols)
                .list();
    }

    public boolean updateIndicatorCurrent(String experimentIndicatorId, Integer periods, String val) {
        if(ShareUtil.XObject.isEmpty(experimentIndicatorId)){
            return true;
        }
        return service.lambdaUpdate()
                .set(ExperimentIndicatorValRsEntity::getCurrentVal,val)
                .eq(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorId)
                .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                .update();
    }
    public boolean updateIndicatorCurrent(List<ExperimentIndicatorValRsEntity> rows){
        if(ShareUtil.XObject.isEmpty(rows)){
            return true;
        }
        boolean rst=true;
        for(ExperimentIndicatorValRsEntity item: rows) {
            rst &= updateIndicatorCurrent(item.getIndicatorInstanceId(), item.getPeriods(), item.getCurrentVal());
        }
        return rst;
    }
}
