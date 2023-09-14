package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapCrowdsInstanceEntity;
import org.dows.hep.service.snapshot.SnapCrowdsInstanceService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/14 10:10
 */

@Component
public class SnapCrowdsInstanceDao extends BaseDao<SnapCrowdsInstanceService, SnapCrowdsInstanceEntity> {
    
    public SnapCrowdsInstanceDao() {super("人群快照不存在");}

    @Override
    protected SFunction<SnapCrowdsInstanceEntity, String> getColId() {
        return SnapCrowdsInstanceEntity::getCrowdsId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapCrowdsInstanceEntity item) {
        return item::setCrowdsId;
    }

    @Override
    protected SFunction<SnapCrowdsInstanceEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapCrowdsInstanceEntity item) {
        return null;
    }

    public List<SnapCrowdsInstanceEntity> getByExperimentId(String experimentId,
                                                            SFunction<SnapCrowdsInstanceEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(experimentId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapCrowdsInstanceEntity::getExperimentInstanceId, experimentId)
                .orderByAsc(SnapCrowdsInstanceEntity::getId)
                .select(cols)
                .list();
    }
}
