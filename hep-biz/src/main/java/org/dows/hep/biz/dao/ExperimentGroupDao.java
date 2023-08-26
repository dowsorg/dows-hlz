package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/11 10:59
 */
@Component
public class ExperimentGroupDao extends BaseDao<ExperimentGroupService, ExperimentGroupEntity>{

    public ExperimentGroupDao() {
        super("实验小组不存在");
    }

    @Override
    protected SFunction<ExperimentGroupEntity, String> getColId() {
        return ExperimentGroupEntity::getExperimentGroupId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentGroupEntity item) {
        return item::setExperimentGroupId;
    }

    @Override
    protected SFunction<ExperimentGroupEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentGroupEntity item) {
        return null;
    }

    public List<ExperimentGroupEntity> getByExperimentId(String experimentInstanceId,
                                                                SFunction<ExperimentGroupEntity,?>...cols){
        return service.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentInstanceId)
                .select(cols)
                .list();
    }


}
