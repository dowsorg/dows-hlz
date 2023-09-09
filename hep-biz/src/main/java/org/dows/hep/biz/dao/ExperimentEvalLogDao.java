package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.ExperimentEvalLogContentEntity;
import org.dows.hep.entity.ExperimentEvalLogEntity;
import org.dows.hep.service.ExperimentEvalLogContentService;
import org.dows.hep.service.ExperimentEvalLogService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/9/6 14:24
 */
@Component
public class ExperimentEvalLogDao extends BaseSubDao<ExperimentEvalLogService, ExperimentEvalLogEntity, ExperimentEvalLogContentService, ExperimentEvalLogContentEntity>{
    public ExperimentEvalLogDao() {
        super("人物计算记录不存在");
    }

    @Override
    protected SFunction<ExperimentEvalLogEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<ExperimentEvalLogEntity, String> getColId() {
        return ExperimentEvalLogEntity::getExperimentEvalLogId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentEvalLogEntity item) {
        return item::setExperimentEvalLogId;
    }

    @Override
    protected SFunction<ExperimentEvalLogEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentEvalLogEntity item) {
        return null;
    }

    @Override
    protected SFunction<ExperimentEvalLogContentEntity, String> getColLeadId() {
        return ExperimentEvalLogContentEntity::getExperimentEvalLogId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(ExperimentEvalLogContentEntity item) {
        return item::setExperimentEvalLogId;
    }

    @Override
    protected SFunction<ExperimentEvalLogContentEntity, String> getColSubId() {
        return ExperimentEvalLogContentEntity::getExperimentEvalLogContentId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(ExperimentEvalLogContentEntity item) {
        return item::setExperimentEvalLogContentId;
    }

    public ExperimentEvalLogEntity getCurrentByPersonId(String experimentPersonId,SFunction<ExperimentEvalLogEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentEvalLogEntity::getExperimentPersonId, experimentPersonId)
                .orderByDesc(ExperimentEvalLogEntity::getEvalNo)
                .select(cols)
                .last("limit 1")
                .one();
    }

    public ExperimentEvalLogEntity getByPersonIdXEvalNo(String experimentPersonId, Integer evalNo,
                                                                    SFunction<ExperimentEvalLogEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentEvalLogEntity::getExperimentPersonId, experimentPersonId)
                .eq(ExperimentEvalLogEntity::getEvalNo, evalNo)
                .orderByDesc(ExperimentEvalLogEntity::getId)
                .select(cols)
                .last("limit 1")
                .one();
    }

    public ExperimentEvalLogContentEntity getByExperimentEvalLogId(String experimentEvalLogId,
                                                                   SFunction<ExperimentEvalLogContentEntity,?>... cols){
        return subService.lambdaQuery()
                .eq(ExperimentEvalLogContentEntity::getExperimentEvalLogId,experimentEvalLogId)
                .orderByDesc(ExperimentEvalLogContentEntity::getId)
                .select(cols)
                .last("limit 1")
                .one();
    }
}
