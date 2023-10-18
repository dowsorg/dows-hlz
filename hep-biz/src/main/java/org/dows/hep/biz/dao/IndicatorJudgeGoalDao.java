package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.indicator.request.FindJudgeGoalRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorJudgeGoalEntity;
import org.dows.hep.service.IndicatorJudgeGoalService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/17 23:28
 */

@Component
public class IndicatorJudgeGoalDao extends BaseCategDao<IndicatorJudgeGoalService, IndicatorJudgeGoalEntity>
        implements IPageDao<IndicatorJudgeGoalEntity, FindJudgeGoalRequest>{

    public IndicatorJudgeGoalDao() {
        super("管理目标不存在","管理目标保存失败");
    }


    @Override
    protected SFunction<IndicatorJudgeGoalEntity, String> getColCateg() {
        return null;
    }

    @Override
    protected SFunction<IndicatorJudgeGoalEntity, String> getColId() {
        return IndicatorJudgeGoalEntity::getIndicatorJudgeGoalId;
    }

    @Override
    protected SFunction<String, ?> setColId(IndicatorJudgeGoalEntity item) {
        return item::setIndicatorJudgeGoalId;
    }

    @Override
    protected SFunction<IndicatorJudgeGoalEntity, Integer> getColState() {
        return IndicatorJudgeGoalEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(IndicatorJudgeGoalEntity item) {
        return item::setState;
    }

    public List<IndicatorJudgeGoalEntity> getAll(String appId, Integer state,
                                                 SFunction<IndicatorJudgeGoalEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),IndicatorJudgeGoalEntity::getAppId,appId)
                .eq(ShareUtil.XObject.notEmpty(state), IndicatorJudgeGoalEntity::getState,state)
                .orderByAsc(IndicatorJudgeGoalEntity::getId)
                .select(cols)
                .list();

    }

    @Override
    public IPage<IndicatorJudgeGoalEntity> pageByCondition(FindJudgeGoalRequest req, SFunction<IndicatorJudgeGoalEntity, ?>... cols) {
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), IndicatorJudgeGoalEntity::getAppId,req.getAppId())
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), IndicatorJudgeGoalEntity::getCategIdLv1, req.getCategIdLv1())
                .like(ShareUtil.XString.hasLength(req.getKeywords()), IndicatorJudgeGoalEntity::getIndicatorJudgeGoalName,req.getKeywords())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .orderByAsc(IndicatorJudgeGoalEntity::getId)
                .select(cols)
                .page(Page.of(req.getPageNo(),req.getPageSize()));
    }
}
