package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.service.IndicatorFuncService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/4/26 10:12
 */
@Component
public class IndicatorFuncDao extends BaseDao<IndicatorFuncService,IndicatorFuncEntity> {

    public IndicatorFuncDao(){
        super("指标功能点不存在或已删除，请刷新");
    }


    @Override
    protected SFunction<IndicatorFuncEntity, String> getColId() {
        return IndicatorFuncEntity::getIndicatorFuncId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorFuncEntity item) {
        return item::setIndicatorFuncId;
    }

    @Override
    protected SFunction<IndicatorFuncEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorFuncEntity item) {
        return null;
    }


}
