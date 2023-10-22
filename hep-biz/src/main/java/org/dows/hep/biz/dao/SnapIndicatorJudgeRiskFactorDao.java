package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeRiskFactorEntity;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeRiskFactorService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:57
 */

@Component
public class SnapIndicatorJudgeRiskFactorDao extends BaseDao<SnapIndicatorJudgeRiskFactorService, SnapIndicatorJudgeRiskFactorEntity> {

    public SnapIndicatorJudgeRiskFactorDao() {super("快照数据不存在");}


    @Override
    protected SFunction<SnapIndicatorJudgeRiskFactorEntity, String> getColId() {
        return null;
    }

    @Override
    protected SFunction<String, ?> setColId(SnapIndicatorJudgeRiskFactorEntity item) {
        return null;
    }

    @Override
    protected SFunction<SnapIndicatorJudgeRiskFactorEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SnapIndicatorJudgeRiskFactorEntity item) {
        return null;
    }

    public List<SnapIndicatorJudgeRiskFactorEntity> getByExperimentId(String experimentId,
                                                                         SFunction<SnapIndicatorJudgeRiskFactorEntity,?>...cols) {
        if (ShareUtil.XObject.isEmpty(experimentId)) {
            return Collections.emptyList();
        }
        return service.lambdaQuery()
                .eq(SnapIndicatorJudgeRiskFactorEntity::getExperimentInstanceId, experimentId)
                .orderByAsc(SnapIndicatorJudgeRiskFactorEntity::getId)
                .select(cols)
                .list();
    }
}
