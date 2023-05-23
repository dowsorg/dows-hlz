package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.service.IndicatorInstanceService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 基本指标
 * @author : wuzl
 * @date : 2023/5/9 16:34
 */
@Component
public class IndicatorInstanceDao extends BaseDao<IndicatorInstanceService, IndicatorInstanceEntity> {

    public IndicatorInstanceDao(){
        super("基本指标不存在或已删除，请刷新");
    }

    @Override
    protected SFunction<IndicatorInstanceEntity, String> getColId() {
        return IndicatorInstanceEntity::getIndicatorInstanceId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorInstanceEntity item) {
        return item::setIndicatorInstanceId;
    }

    @Override
    protected SFunction<IndicatorInstanceEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorInstanceEntity item) {
        return null;
    }

    /**
     * 获取所有营养成分
     * @param cols
     * @return
     */
    public List<IndicatorInstanceEntity> getIndicators4Nutrient(SFunction<IndicatorInstanceEntity,?>...cols ){
        final Integer foodFlag=1;
        return service.lambdaQuery()
                .eq(IndicatorInstanceEntity::getFood,foodFlag)
                .orderByAsc(IndicatorInstanceEntity::getId)
                .select(cols)
                .list();
    }

    /**
     * 获取三大营养成分
     * @param nutrientNames
     * @param cols
     * @return
     */
    public List<IndicatorInstanceEntity> getIndicators4Nutrient(Collection<String> nutrientNames, SFunction<IndicatorInstanceEntity, ?>...cols ){
        final Integer foodFlag=1;
        return service.lambdaQuery()
                .eq(IndicatorInstanceEntity::getFood,foodFlag)
                .in(IndicatorInstanceEntity::getIndicatorName,nutrientNames)
                .orderByAsc(IndicatorInstanceEntity::getId)
                .select(cols)
                .list();
    }



}
