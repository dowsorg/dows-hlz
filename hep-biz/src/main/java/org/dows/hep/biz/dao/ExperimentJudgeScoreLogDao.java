package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.ExperimentJudgeScoreLogEntity;
import org.dows.hep.service.ExperimentJudgeScoreLogService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/22 10:14
 */
@Component
public class ExperimentJudgeScoreLogDao extends BaseDao<ExperimentJudgeScoreLogService, ExperimentJudgeScoreLogEntity>{
    public ExperimentJudgeScoreLogDao() {
        super("判断得分记录不存在");
    }


    @Override
    protected SFunction<ExperimentJudgeScoreLogEntity, String> getColId() {
        return ExperimentJudgeScoreLogEntity::getExperimentJudgeScoreLogId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentJudgeScoreLogEntity item) {
        return item::setExperimentJudgeScoreLogId;
    }

    @Override
    protected SFunction<ExperimentJudgeScoreLogEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentJudgeScoreLogEntity item) {
        return null;
    }

    public List<ExperimentJudgeScoreLogEntity> getAllByPeriod(String experimentId, Integer period,SFunction<ExperimentJudgeScoreLogEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ExperimentJudgeScoreLogEntity::getExperimentInstanceId, experimentId)
                .eq(ExperimentJudgeScoreLogEntity::getPeriod,period)
                .orderByAsc(ExperimentJudgeScoreLogEntity::getExperimentGroupId,
                        ExperimentJudgeScoreLogEntity::getExperimentPersonId,
                        ExperimentJudgeScoreLogEntity::getIndicatorFuncId)
                .select(cols)
                .list();
    }
}
