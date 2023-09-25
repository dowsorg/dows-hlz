package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.OperateCostEntity;
import org.dows.hep.service.OperateCostService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/23 20:28
 */
@Component
public class OperateCostDao extends BaseDao<OperateCostService, OperateCostEntity>  {
    public OperateCostDao() {
        super("资金流水不存在");
    }

    @Override
    protected SFunction<OperateCostEntity, String> getColId() {
        return OperateCostEntity::getOperateCostId;
    }

    @Override
    protected SFunction<String, ?> setColId(OperateCostEntity item) {
        return item::setOperateCostId;
    }

    @Override
    protected SFunction<OperateCostEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(OperateCostEntity item) {
        return null;
    }

    public List<OperateCostEntity> getRefunds(String experimentId, Integer period,
                                              Collection<String> personIds,
                                              SFunction<OperateCostEntity, ?>...cols){

        return service.lambdaQuery()
                .eq(OperateCostEntity::getExperimentInstanceId, experimentId)
                .eq(OperateCostEntity::getPeriod, period)
                .eq(null!=personIds&&personIds.size()==1, OperateCostEntity::getPatientId,null==personIds?null:personIds.iterator().next())
                .in(null!=personIds&&personIds.size()>1, OperateCostEntity::getPatientId,personIds)
                .isNotNull(OperateCostEntity::getRestitution)
                .select(cols)
                .list();
    }
}
