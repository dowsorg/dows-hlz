package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeHealthGuidanceService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:56
 */
@Component
public class SnapIndicatorJudgeHealthGuidanceDao extends BaseDao<SnapIndicatorJudgeHealthGuidanceService, SnapIndicatorJudgeHealthGuidanceEntity>  {

    public SnapIndicatorJudgeHealthGuidanceDao() {super("快照数据不存在");}


    @Override
    protected SFunction<SnapIndicatorJudgeHealthGuidanceEntity, String> getColId() {
        return SnapIndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapIndicatorJudgeHealthGuidanceEntity item) {
        return item::setIndicatorJudgeHealthGuidanceId;
    }

    @Override
    protected SFunction<SnapIndicatorJudgeHealthGuidanceEntity, Integer> getColState() {
        return SnapIndicatorJudgeHealthGuidanceEntity::getStatus;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapIndicatorJudgeHealthGuidanceEntity item) {
        return item::setStatus;
    }

    public List<SnapIndicatorJudgeHealthGuidanceEntity> getByExperimentId(String experimentId,
                                                                SFunction<SnapIndicatorJudgeHealthGuidanceEntity,?>...cols) {
        if (ShareUtil.XObject.isEmpty(experimentId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapIndicatorJudgeHealthGuidanceEntity::getExperimentInstanceId, experimentId)
                .orderByAsc(SnapIndicatorJudgeHealthGuidanceEntity::getId)
                .select(cols)
                .list();
    }
}
