package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.SportItemEntity;
import org.dows.hep.entity.SportItemIndicatorEntity;
import org.dows.hep.service.SportItemIndicatorService;
import org.dows.hep.service.SportItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    protected IndicatorExpressionRefDao indicatorExpressionRefDao;

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
    protected SFunction<SportItemEntity,String> getColCateg(){
        return SportItemEntity::getInterveneCategId;
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

        Page<SportItemEntity> page=Page.of(req.getPageNo(),req.getPageSize());
        page.addOrder(OrderItem.asc("categ_name_lv1"),OrderItem.asc("strength_met"));
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), SportItemEntity::getAppId,req.getAppId())
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), SportItemEntity::getInterveneCategId, req.getCategIdLv1())
                .like(ShareUtil.XString.hasLength(req.getKeywords()), SportItemEntity::getSportItemName,req.getKeywords())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .select(cols)
                .page(page);

    }

    public List<SportItemEntity> listByCondition(FindSportRequest req, SFunction<SportItemEntity,?>... cols) {
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), SportItemEntity::getAppId,req.getAppId())
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), SportItemEntity::getInterveneCategId, req.getCategIdLv1())
                .like(ShareUtil.XString.hasLength(req.getKeywords()), SportItemEntity::getSportItemName,req.getKeywords())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .orderByAsc(SportItemEntity::getCategNameLv1,SportItemEntity::getStrengthMet)
                .select(cols)
                .list();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveWithExpressions(SportItemEntity lead,  List<String> expressionIds){
        AssertUtil.falseThenThrow(coreTranSave(lead,null,false, defaultUseLogicId))
                .throwMessage(failedSaveMessage );

        return indicatorExpressionRefDao.tranUpdateReasonId(lead.getSportItemId(),expressionIds);
    }

    @Override
    protected boolean coreTranDelete(List<String> ids, boolean delSub, boolean dftIfSubEmpty) {
        if(!super.coreTranDelete(ids, false, dftIfSubEmpty)){
            return false;
        }
        indicatorExpressionRefDao.tranDeleteByReasonId(ids);
        return true;
    }

}
