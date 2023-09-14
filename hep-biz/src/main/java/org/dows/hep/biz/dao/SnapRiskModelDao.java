package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapRiskModelEntity;
import org.dows.hep.service.snapshot.SnapRiskModelService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/14 10:30
 */

@Component
public class SnapRiskModelDao extends BaseDao<SnapRiskModelService, SnapRiskModelEntity> {
    public SnapRiskModelDao() {super("风险模型不存在");}


    @Override
    protected SFunction<SnapRiskModelEntity, String> getColId() {
        return SnapRiskModelEntity::getRiskModelId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapRiskModelEntity item) {
        return item::setRiskModelId;
    }

    @Override
    protected SFunction<SnapRiskModelEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapRiskModelEntity item) {
        return null;
    }

    public List<SnapRiskModelEntity> getByExperimentId(String experimentId,
                                                       SFunction<SnapRiskModelEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(experimentId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapRiskModelEntity::getExperimentInstanceId, experimentId)
                .orderByAsc(SnapRiskModelEntity::getId)
                .select(cols)
                .list();
    }
}
