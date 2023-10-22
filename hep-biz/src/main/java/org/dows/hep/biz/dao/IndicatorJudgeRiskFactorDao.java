package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorJudgeRiskFactorEntity;
import org.dows.hep.service.IndicatorJudgeRiskFactorService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/21 14:16
 */
@Component
public class IndicatorJudgeRiskFactorDao extends BaseDao<IndicatorJudgeRiskFactorService, IndicatorJudgeRiskFactorEntity> {

    public IndicatorJudgeRiskFactorDao() {
        super("数据不存在");
    }

    @Override
    protected SFunction<IndicatorJudgeRiskFactorEntity, String> getColId() {
        return IndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorJudgeRiskFactorEntity item) {
        return item::setIndicatorJudgeRiskFactorId;
    }

    @Override
    protected SFunction<IndicatorJudgeRiskFactorEntity, Integer> getColState() {
        return IndicatorJudgeRiskFactorEntity::getStatus;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorJudgeRiskFactorEntity item) {
        return item::setStatus;
    }

    public List<IndicatorJudgeRiskFactorEntity> getAll(String appId, Integer state, boolean isAsc, SFunction<IndicatorJudgeRiskFactorEntity, ?>... cols) {
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId), IndicatorJudgeRiskFactorEntity::getAppId, appId)
                .eq(ShareUtil.XObject.notEmpty(state), IndicatorJudgeRiskFactorEntity::getStatus, state)
                .orderBy(true, isAsc, IndicatorJudgeRiskFactorEntity::getId)
                .select(cols)
                .list();
    }
}
