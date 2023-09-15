package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindTreatRequest;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.TreatItemEntity;
import org.dows.hep.entity.TreatItemIndicatorEntity;
import org.dows.hep.service.TreatItemIndicatorService;
import org.dows.hep.service.TreatItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    protected IndicatorExpressionRefDao indicatorExpressionRefDao;


    @Override
    protected SFunction<TreatItemEntity, String> getColAppId() {
        return TreatItemEntity::getAppId;
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
        Page<TreatItemEntity> page=Page.of(req.getPageNo(),req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return  service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), TreatItemEntity::getAppId,req.getAppId())
                .eq(TreatItemEntity::getIndicatorFuncId, req.getIndicatorFuncId())
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), TreatItemEntity::getCategIdLv1, req.getCategIdLv1())
                .like(ShareUtil.XString.hasLength(req.getKeywords()), TreatItemEntity::getTreatItemName, req.getKeywords())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .select(cols)
                .page(page);
    }

    public List<TreatItemEntity> getAll(String appId, Integer state, boolean isAsc, SFunction<TreatItemEntity,?>... cols){
        return service.lambdaQuery()
                .eq(null!=getColAppId()&&ShareUtil.XObject.notEmpty(appId),getColAppId(),appId)
                .eq(ShareUtil.XObject.notEmpty(state), TreatItemEntity::getState,state)
                .orderBy(true, isAsc, TreatItemEntity::getId)
                .select(cols)
                .list();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveWithExpressions(TreatItemEntity lead, List<String> expressionIds){
        AssertUtil.falseThenThrow(coreTranSave(lead,null,false, true))
                .throwMessage(failedSaveMessage );

        return indicatorExpressionRefDao.tranUpdateReasonId(lead.getTreatItemId(),expressionIds);
    }

    @Override
    protected boolean coreTranDelete(List<String> ids, boolean delSub, boolean dftIfSubEmpty) {
        if(!super.coreTranDelete(ids, false, dftIfSubEmpty)){
            return false;
        }
        indicatorExpressionRefDao.tranDeleteByReasonId(ids);
        return true;
    }

    public List<TreatItemEntity> getByIndicatorFuncId(String appId, String indicatorFuncId,
                                                      SFunction<TreatItemEntity,?>... cols){
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(appId),TreatItemEntity::getAppId,appId)
                .eq(ShareUtil.XObject.notEmpty(indicatorFuncId), TreatItemEntity::getIndicatorFuncId, indicatorFuncId)
                .orderByAsc(TreatItemEntity::getInterveneCategId, TreatItemEntity::getId)
                .select(cols)
                .list();

    }


}
