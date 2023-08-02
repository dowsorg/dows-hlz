package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public List<ExperimentInstanceEntity> getRunningExperiment4Sand(String appId, Integer minState, Integer maxState,
                                                                    SFunction<ExperimentInstanceEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),ExperimentInstanceEntity::getAppId,appId)
                .ge(ShareUtil.XObject.notEmpty(minState), ExperimentInstanceEntity::getState,minState)
                .le(ShareUtil.XObject.notEmpty(maxState), ExperimentInstanceEntity::getState,maxState)
                .ne(ExperimentInstanceEntity::getModel, EnumExperimentMode.SCHEME.getCode())
                .orderByAsc(ExperimentInstanceEntity::getStartTime)
                .select(cols)
                .list();

    }
}
