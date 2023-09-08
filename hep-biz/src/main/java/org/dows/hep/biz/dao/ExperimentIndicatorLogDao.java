package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.entity.ExperimentIndicatorLogEntity;
import org.dows.hep.service.ExperimentIndicatorLogService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/9/6 14:30
 */

@Component
public class ExperimentIndicatorLogDao extends BaseDao<ExperimentIndicatorLogService, ExperimentIndicatorLogEntity>{
    protected ExperimentIndicatorLogDao() {
        super("指标算记录不存在");
    }

    @Override
    protected SFunction<ExperimentIndicatorLogEntity, String> getColId() {
        return ExperimentIndicatorLogEntity::getExperimentIndicatorLogId;
    }

    @Override
    protected SFunction<String, ?> setColId(ExperimentIndicatorLogEntity item) {
        return item::setExperimentIndicatorLogId;
    }

    @Override
    protected SFunction<ExperimentIndicatorLogEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(ExperimentIndicatorLogEntity item) {
        return null;
    }
}
