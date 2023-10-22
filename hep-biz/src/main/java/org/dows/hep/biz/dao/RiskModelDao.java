package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.RiskModelEntity;
import org.dows.hep.service.RiskModelService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/14 17:35
 */

@Component
public class RiskModelDao extends BaseDao<RiskModelService, RiskModelEntity>  {

    public RiskModelDao() {
        super("风险模型不存在");
    }

    @Override
    protected SFunction<RiskModelEntity, String> getColId() {
        return RiskModelEntity::getRiskModelId;
    }

    @Override
    protected SFunction<String, ?> setColId(RiskModelEntity item) {
        return item::setRiskModelId;
    }

    @Override
    protected SFunction<RiskModelEntity, Integer> getColState() {
        return RiskModelEntity::getStatus;
    }

    @Override
    protected SFunction<Integer, ?> setColState(RiskModelEntity item) {
        return item::setStatus;
    }

    public List<RiskModelEntity> getAll(String appId, Integer state, SFunction<RiskModelEntity,?>... cols){
        return service.lambdaQuery()
                .eq(null!=getColAppId()&& ShareUtil.XObject.notEmpty(appId),getColAppId(),appId)
                .eq(ShareUtil.XObject.notEmpty(state), RiskModelEntity::getStatus,state)
                .orderByAsc(RiskModelEntity::getId)
                .select(cols)
                .list();
    }
}
