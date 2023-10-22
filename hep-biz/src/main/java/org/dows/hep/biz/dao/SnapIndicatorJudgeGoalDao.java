package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeGoalEntity;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeGoalService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:55
 */
@Component
public class SnapIndicatorJudgeGoalDao extends BaseDao<SnapIndicatorJudgeGoalService, SnapIndicatorJudgeGoalEntity> {

    public SnapIndicatorJudgeGoalDao() {super("管理目标快照不存在");}


    @Override
    protected SFunction<SnapIndicatorJudgeGoalEntity, String> getColId() {
        return SnapIndicatorJudgeGoalEntity::getIndicatorJudgeGoalId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapIndicatorJudgeGoalEntity item) {
        return item::setIndicatorJudgeGoalId;
    }

    @Override
    protected SFunction<SnapIndicatorJudgeGoalEntity, Integer> getColState() {
        return SnapIndicatorJudgeGoalEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapIndicatorJudgeGoalEntity item) {
        return item::setState;
    }

    public List<SnapIndicatorJudgeGoalEntity> getByExperimentId(String experimentId,
                                                       SFunction<SnapIndicatorJudgeGoalEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(experimentId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapIndicatorJudgeGoalEntity::getExperimentInstanceId, experimentId)
                .orderByAsc(SnapIndicatorJudgeGoalEntity::getId)
                .select(cols)
                .list();
    }
}
