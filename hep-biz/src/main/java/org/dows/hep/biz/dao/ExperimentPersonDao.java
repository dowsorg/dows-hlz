package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.service.ExperimentPersonService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/6 13:53
 */
@Component
public class ExperimentPersonDao extends BaseDao<ExperimentPersonService, ExperimentPersonEntity> {

    public ExperimentPersonDao() {
        super("实验人物不存在");
    }

    @Override
    protected SFunction<ExperimentPersonEntity, String> getColAppId() {
        return ExperimentPersonEntity::getAppId;
    }

    @Override
    protected SFunction<ExperimentPersonEntity, String> getColId() {
        return ExperimentPersonEntity::getExperimentPersonId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentPersonEntity item) {
        return item::setExperimentPersonId;
    }

    @Override
    protected SFunction<ExperimentPersonEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentPersonEntity item) {
        return null;
    }




    public List<ExperimentPersonEntity> getByExperimentId(String appId, String experimentId,
                                                          SFunction<ExperimentPersonEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentInstanceId,experimentId)
                .orderByAsc(ExperimentPersonEntity::getCasePersonId)
                .select(cols)
                .list();
    }

    public List<ExperimentPersonEntity> getByOrgId(String experimentOrgId,SFunction<ExperimentPersonEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentOrgId,experimentOrgId)
                .select(cols)
                .list();
    }

}
