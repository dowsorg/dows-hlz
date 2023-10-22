package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeHealthProblemEntity;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeHealthProblemService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:57
 */
@Component
public class SnapIndicatorJudgeHealthProblemDao extends BaseDao<SnapIndicatorJudgeHealthProblemService, SnapIndicatorJudgeHealthProblemEntity>  {

    public SnapIndicatorJudgeHealthProblemDao() {super("快照数据不存在");}


    @Override
    protected SFunction<SnapIndicatorJudgeHealthProblemEntity, String> getColId() {
        return SnapIndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapIndicatorJudgeHealthProblemEntity item) {
        return item::setIndicatorJudgeHealthProblemId;
    }

    @Override
    protected SFunction<SnapIndicatorJudgeHealthProblemEntity, Integer> getColState() {
        return SnapIndicatorJudgeHealthProblemEntity::getStatus;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapIndicatorJudgeHealthProblemEntity item) {
        return item::setStatus;
    }

    public List<SnapIndicatorJudgeHealthProblemEntity> getByExperimentId(String experimentId,
                                                                SFunction<SnapIndicatorJudgeHealthProblemEntity,?>...cols){
        if (ShareUtil.XObject.isEmpty(experimentId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapIndicatorJudgeHealthProblemEntity::getExperimentInstanceId, experimentId)
                .orderByAsc(SnapIndicatorJudgeHealthProblemEntity::getId)
                .select(cols)
                .list();
    }
}
