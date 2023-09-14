package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.CrowdsInstanceEntity;
import org.dows.hep.service.CrowdsInstanceService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/9/14 17:35
 */
@Component
public class CrowdsInstanceDao extends BaseDao<CrowdsInstanceService, CrowdsInstanceEntity> {

    public CrowdsInstanceDao() {
        super("人群类型不存在");
    }

    @Override
    protected SFunction<CrowdsInstanceEntity, String> getColId() {
        return CrowdsInstanceEntity::getCrowdsId;
    }

    @Override
    protected SFunction<String, ?> setColId(CrowdsInstanceEntity item) {
        return item::setCrowdsId;
    }

    @Override
    protected SFunction<CrowdsInstanceEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(CrowdsInstanceEntity item) {
        return null;
    }
}
