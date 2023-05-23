package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.api.enums.EnumIndicatorRuleType;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorRuleEntity;
import org.dows.hep.service.IndicatorRuleService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 基本指标值
 * @author : wuzl
 * @date : 2023/5/18 19:49
 */
@Component
public class IndicatorRuleDao extends BaseDao<IndicatorRuleService, IndicatorRuleEntity>  {

    public IndicatorRuleDao(){
       super("指标规则不存在或已删除");
    }

    @Override
    protected SFunction<IndicatorRuleEntity, String> getColId() {
        return IndicatorRuleEntity::getIndicatorRuleId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorRuleEntity item) {
        return item::setIndicatorRuleId;
    }

    @Override
    protected SFunction<IndicatorRuleEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorRuleEntity item) {
        return null;
    }

    /**
     * 根据指标id列表查询规则
     * @param indicatorIds
     * @param cols
     * @return
     */
    public List<IndicatorRuleEntity> getByIndicatorIds(Collection<String> indicatorIds,SFunction<IndicatorRuleEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(indicatorIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=indicatorIds.size()==1;
        return service.lambdaQuery()
                .eq(IndicatorRuleEntity::getRuleType, EnumIndicatorRuleType.INDICATOR.getCode())
                .eq(oneFlag, IndicatorRuleEntity::getVariableId,indicatorIds.iterator().next())
                .in(!oneFlag, IndicatorRuleEntity::getVariableId,indicatorIds)
                .select(cols)
                .list();
    }




}
