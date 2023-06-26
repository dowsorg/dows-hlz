package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:37
 */
@Component
public class ExperimentInstanceDao extends BaseDao<ExperimentInstanceService, ExperimentInstanceEntity> {
    public ExperimentInstanceDao(){
        super("实验实例不存在");
    }

    @Override
    protected SFunction<ExperimentInstanceEntity, String> getColAppId() {
        return ExperimentInstanceEntity::getAppId;
    }

    @Override
    protected SFunction<ExperimentInstanceEntity, String> getColId() {
        return ExperimentInstanceEntity::getExperimentInstanceId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentInstanceEntity item) {
        return item::setExperimentInstanceId;
    }

    @Override
    protected SFunction<ExperimentInstanceEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentInstanceEntity item) {
        return null;
    }
}
