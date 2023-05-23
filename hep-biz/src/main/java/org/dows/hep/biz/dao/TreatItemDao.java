package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindTreatRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.TreatItemEntity;
import org.dows.hep.entity.TreatItemIndicatorEntity;
import org.dows.hep.service.TreatItemIndicatorService;
import org.dows.hep.service.TreatItemService;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/4/26 15:44
 */

@Component
public class TreatItemDao extends BaseSubDao<TreatItemService,TreatItemEntity,TreatItemIndicatorService,TreatItemIndicatorEntity>
    implements IPageDao<TreatItemEntity, FindTreatRequest> {

    public TreatItemDao() {
        super("干预项目不存在或已删除,请刷新");
    }



    @Override
    protected SFunction<TreatItemEntity, String> getColId() {
        return TreatItemEntity::getTreatItemId;
    }

    @Override
    protected SFunction<String, ?> setColId(TreatItemEntity item) {
        return item::setTreatItemId;
    }

    @Override
    protected SFunction<TreatItemEntity, Integer> getColState() {
        return TreatItemEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(TreatItemEntity item) {
        return item::setState;
    }

    @Override
    protected SFunction<TreatItemEntity,String> getColCateg(){
        return TreatItemEntity::getInterveneCategId;
    }

    @Override
    protected SFunction<TreatItemIndicatorEntity, String> getColLeadId() {
        return TreatItemIndicatorEntity::getTreatItemId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(TreatItemIndicatorEntity item) {
        return item::setTreatItemId;
    }

    @Override
    protected SFunction<TreatItemIndicatorEntity, String> getColSubId() {
        return TreatItemIndicatorEntity::getTreatItemIndicatorId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(TreatItemIndicatorEntity item) {
        return item::setTreatItemIndicatorId;
    }

    @Override
    public IPage<TreatItemEntity> pageByCondition(FindTreatRequest req, SFunction<TreatItemEntity, ?>... cols) {
        final String categId=req.getCategIdLv1();
        final String keyWords=req.getKeywords();
        Page<TreatItemEntity> page=Page.of(req.getPageNo(),req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.page(page, Wrappers.<TreatItemEntity>lambdaQuery()
                .eq(TreatItemEntity::getIndicatorFuncId, req.getIndicatorFuncId())
                .likeRight(ShareUtil.XString.hasLength(categId), TreatItemEntity::getCategIdPath, categId)
                .like(ShareUtil.XString.hasLength(keyWords), TreatItemEntity::getTreatItemName, keyWords)
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .select(cols));
    }
}
