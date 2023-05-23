package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.SportPlanEntity;
import org.dows.hep.entity.SportPlanItemsEntity;
import org.dows.hep.service.SportPlanItemsService;
import org.dows.hep.service.SportPlanService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/5/4 16:05
 */
@Component
public class SportPlanDao extends BaseSubDao<SportPlanService, SportPlanEntity, SportPlanItemsService, SportPlanItemsEntity>
        implements IPageDao<SportPlanEntity, FindSportRequest> {


    public SportPlanDao() {
        super("运动方案不存在或已删除，请刷新");
    }

    //region override

    @Override
    protected SFunction<SportPlanEntity, String> getColId() {
        return SportPlanEntity::getSportPlanId;
    }

    @Override
    protected SFunction<String, ?> setColId(SportPlanEntity item) {
        return item::setSportPlanId;
    }

    @Override
    protected SFunction<SportPlanEntity, Integer> getColState() {
        return SportPlanEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SportPlanEntity item) {
        return item::setState;
    }

    @Override
    protected SFunction<SportPlanEntity,String> getColCateg(){
        return SportPlanEntity::getInterveneCategId;
    }

    @Override
    protected SFunction<SportPlanItemsEntity, String> getColLeadId() {
        return SportPlanItemsEntity::getSportPlanId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(SportPlanItemsEntity item) {
        return item::setSportPlanId;
    }

    @Override
    protected SFunction<SportPlanItemsEntity, String> getColSubId() {
        return SportPlanItemsEntity::getSportPlanItemsId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(SportPlanItemsEntity item) {
        return item::setSportPlanItemsId;
    }

    @Override
    protected SFunction<SportPlanItemsEntity, Integer> getColSubSeq() {
        return SportPlanItemsEntity::getSeq;
    }

    @Override
    protected SFunction<Integer, ?> setColSubSeq(SportPlanItemsEntity item) {
        return item::setSeq;
    }

    //endregion

    @Override
    public IPage<SportPlanEntity> pageByCondition(FindSportRequest req, SFunction<SportPlanEntity,?>... cols) {
        final String categId=req.getCategIdLv1();
        final String keyWords=req.getKeywords();
        Page<SportPlanEntity> page=Page.of(req.getPageNo(),req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.page(page, Wrappers.<SportPlanEntity>lambdaQuery()
                .likeRight(ShareUtil.XString.hasLength(categId), SportPlanEntity::getCategIdPath,categId)
                .like(ShareUtil.XString.hasLength(keyWords), SportPlanEntity::getSportPlanName,keyWords)
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .select(cols));

    }

}
