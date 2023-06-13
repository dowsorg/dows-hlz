package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.service.IndicatorExpressionItemService;
import org.dows.hep.service.IndicatorExpressionService;
import org.springframework.stereotype.Component;

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
}
