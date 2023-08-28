package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/11 10:59
 */
@Component
public class ExperimentParticipatorDao extends BaseDao<ExperimentParticipatorService, ExperimentParticipatorEntity>{

    public ExperimentParticipatorDao() {
        super("实验参与者不存在");
    }

    @Override
    protected SFunction<ExperimentParticipatorEntity, String> getColId() {
        return ExperimentParticipatorEntity::getExperimentParticipatorId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentParticipatorEntity item) {
        return item::setExperimentParticipatorId;
    }

    @Override
    protected SFunction<ExperimentParticipatorEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentParticipatorEntity item) {
        return null;
    }

    public List<ExperimentParticipatorEntity> getByExperimentId(String experimentInstanceId,
                                                                SFunction<ExperimentParticipatorEntity,?>...cols){
        return service.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .select(cols)
                .list();
    }

    public List<ExperimentParticipatorEntity> getAccountIdsByGroupId(String experimentInstanceId, String experimentGroupId,
                                                                     SFunction<ExperimentParticipatorEntity,?>...cols) {
        return service.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupId)
                .select(cols)
                .list();

    }

    public List<ExperimentParticipatorEntity> getAccountIdsByGroupId(String experimentInstanceId, Collection<String> experimentGroupIds,
                                                                     SFunction<ExperimentParticipatorEntity,?>...cols) {
        final boolean oneFlag = experimentGroupIds.size() == 1;
        return service.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(oneFlag, ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupIds.iterator().next())
                .in(!oneFlag, ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupIds)
                .select(cols)
                .list();

    }
}
