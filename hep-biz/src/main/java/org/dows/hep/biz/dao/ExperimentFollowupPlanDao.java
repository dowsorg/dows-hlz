package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;
import org.dows.hep.service.ExperimentFollowupPlanService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/9/2 20:56
 */
@Component
public class ExperimentFollowupPlanDao extends BaseDao<ExperimentFollowupPlanService, ExperimentFollowupPlanEntity>{
    public ExperimentFollowupPlanDao() {
        super("随访计划不存在");
    }

    @Override
    protected SFunction<ExperimentFollowupPlanEntity, String> getColId() {
        return ExperimentFollowupPlanEntity::getExperimentFollowupPlanId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentFollowupPlanEntity item) {
        return item::setExperimentFollowupPlanId;
    }

    @Override
    protected SFunction<ExperimentFollowupPlanEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentFollowupPlanEntity item) {
        return null;
    }

    public List<ExperimentFollowupPlanEntity> getByExperimentId(String experimentId,SFunction<ExperimentFollowupPlanEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentFollowupPlanEntity::getExperimentInstanceId, experimentId)
                .select(cols)
                .list();
    }

    public Optional<ExperimentFollowupPlanEntity> getByExperimentPersonId(String experimentPersonId, SFunction<ExperimentFollowupPlanEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentFollowupPlanEntity::getExperimentPersonId, experimentPersonId)
                .orderByDesc(ExperimentFollowupPlanEntity::getId)
                .last("limit 1")
                .oneOpt();
    }
}
