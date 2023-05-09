package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.SportItemEntity;
import org.dows.hep.entity.SportItemIndicatorEntity;
import org.dows.hep.service.SportItemIndicatorService;
import org.dows.hep.service.SportItemService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/4/25 17:27
 */
@Component
public class SportItemDao extends BaseSubDao<SportItemService,SportItemEntity,SportItemIndicatorService,SportItemIndicatorEntity>
    implements IPageDao<SportItemEntity, FindSportRequest> {


    public SportItemDao() {
       super("运动项目不存在或已删除，请刷新");
   }

    //region override

    @Override
    protected SFunction<SportItemEntity, String> getColId() {
        return SportItemEntity::getSportItemId;
    }

    @Override
    protected SFunction<String, ?> setColId(SportItemEntity item) {
        return item::setSportItemId;
    }

    @Override
    protected SFunction<SportItemEntity, Integer> getColState() {
        return SportItemEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(SportItemEntity item) {
        return item::setState;
    }

    @Override
    protected SFunction<SportItemIndicatorEntity, String> getColLeadId() {
        return SportItemIndicatorEntity::getSportItemId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(SportItemIndicatorEntity item) {
        return item::setSportItemId;
    }

    @Override
    protected SFunction<SportItemIndicatorEntity, String> getColSubId() {
        return SportItemIndicatorEntity::getSportItemIndicatorId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(SportItemIndicatorEntity item) {
        return item::setSportItemIndicatorId;
    }

    @Override
    protected SFunction<SportItemIndicatorEntity, Integer> getColSubSeq() {
        return SportItemIndicatorEntity::getSeq;
    }

    @Override
    protected SFunction<Integer, ?> setColSubSeq(SportItemIndicatorEntity item) {
        return item::setSeq;
    }

    //endregion

    @Override
    public IPage<SportItemEntity> pageByCondition(FindSportRequest req, SFunction<SportItemEntity,?>... cols) {
        final String categId=req.getCategIdLv1();
        final String keyWords=req.getKeywords();
        Page<SportItemEntity> page=Page.of(req.getPageNo(),req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.page(page,Wrappers.<SportItemEntity>lambdaQuery()
                .likeRight(ShareUtil.XString.hasLength(categId), SportItemEntity::getCategIdPath,categId)
                .like(ShareUtil.XString.hasLength(keyWords), SportItemEntity::getSportItemName,keyWords)
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .select(cols));

    }

}
