package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorJudgeHealthProblemEntity;
import org.dows.hep.service.IndicatorJudgeHealthProblemService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/21 14:15
 */

@Component
public class IndicatorJudgeHealthProblemDao extends BaseDao<IndicatorJudgeHealthProblemService, IndicatorJudgeHealthProblemEntity> {

    public IndicatorJudgeHealthProblemDao() {
        super("数据不存在");
    }

    @Override
    protected SFunction<IndicatorJudgeHealthProblemEntity, String> getColId() {
        return IndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorJudgeHealthProblemEntity item) {
        return item::setIndicatorJudgeHealthProblemId;
    }

    @Override
    protected SFunction<IndicatorJudgeHealthProblemEntity, Integer> getColState() {
        return IndicatorJudgeHealthProblemEntity::getStatus;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorJudgeHealthProblemEntity item) {
        return item::setStatus;
    }

    public List<IndicatorJudgeHealthProblemEntity> getAll(String appId, Integer state, boolean isAsc, SFunction<IndicatorJudgeHealthProblemEntity, ?>... cols) {
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId), IndicatorJudgeHealthProblemEntity::getAppId, appId)
                .eq(ShareUtil.XObject.notEmpty(state), IndicatorJudgeHealthProblemEntity::getStatus, state)
                .orderBy(true, isAsc, IndicatorJudgeHealthProblemEntity::getId)
                .select(cols)
                .list();
    }
}
