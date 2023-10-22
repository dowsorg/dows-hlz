package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.service.IndicatorJudgeHealthGuidanceService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/21 14:16
 */
@Component
public class IndicatorJudgeHealthGuidanceDao extends BaseDao<IndicatorJudgeHealthGuidanceService, IndicatorJudgeHealthGuidanceEntity> {

    public IndicatorJudgeHealthGuidanceDao() {
        super("数据不存在");
    }

    @Override
    protected SFunction<IndicatorJudgeHealthGuidanceEntity, String> getColId() {
        return null;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorJudgeHealthGuidanceEntity item) {
        return null;
    }

    @Override
    protected SFunction<IndicatorJudgeHealthGuidanceEntity, Integer> getColState() {
        return null;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorJudgeHealthGuidanceEntity item) {
        return null;
    }

    public List<IndicatorJudgeHealthGuidanceEntity> getAll(String appId, Integer state, boolean isAsc, SFunction<IndicatorJudgeHealthGuidanceEntity, ?>... cols) {
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId), IndicatorJudgeHealthGuidanceEntity::getAppId, appId)
                .eq(ShareUtil.XObject.notEmpty(state), IndicatorJudgeHealthGuidanceEntity::getStatus, state)
                .orderBy(true, isAsc, IndicatorJudgeHealthGuidanceEntity::getId)
                .select(cols)
                .list();
    }
}
