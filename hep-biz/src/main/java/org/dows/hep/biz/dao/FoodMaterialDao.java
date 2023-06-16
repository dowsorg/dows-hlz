package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindFoodRequest;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.FoodMaterialEntity;
import org.dows.hep.entity.FoodMaterialIndicatorEntity;
import org.dows.hep.entity.FoodMaterialNutrientEntity;
import org.dows.hep.service.FoodMaterialIndicatorService;
import org.dows.hep.service.FoodMaterialNutrientService;
import org.dows.hep.service.FoodMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/4/23 19:18
 */

@Component
public class FoodMaterialDao extends BaseSubDao<FoodMaterialService,FoodMaterialEntity,FoodMaterialIndicatorService,FoodMaterialIndicatorEntity>
    implements IPageDao<FoodMaterialEntity, FindFoodRequest>,ICheckCategRef  {

    public FoodMaterialDao(){
        super("食材不存在或已删除,请刷新");
    }

    @Autowired
    protected FoodMaterialNutrientService subServiceX;

    @Autowired
    protected IndicatorExpressionRefDao indicatorExpressionRefDao;


    //region override

    @Override
    protected SFunction<FoodMaterialEntity, String> getColId() {
        return FoodMaterialEntity::getFoodMaterialId;
    }

    @Override
    protected SFunction<String, ?> setColId(FoodMaterialEntity item) {
        return item::setFoodMaterialId;
    }

    @Override
    protected SFunction<FoodMaterialEntity, Integer> getColState() {
        return FoodMaterialEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(FoodMaterialEntity item) {
        return item::setState;
    }

    @Override
    protected SFunction<FoodMaterialEntity,String> getColCateg(){
        return FoodMaterialEntity::getInterveneCategId;
    }

    @Override
    protected SFunction<FoodMaterialIndicatorEntity, String> getColLeadId() {
        return FoodMaterialIndicatorEntity::getFoodMaterialId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(FoodMaterialIndicatorEntity item) {
        return item::setFoodMaterialId;
    }

    @Override
    protected SFunction<FoodMaterialIndicatorEntity, String> getColSubId() {
        return FoodMaterialIndicatorEntity::getFoodMaterialIndicatorId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(FoodMaterialIndicatorEntity item) {
        return item::setFoodMaterialIndicatorId;
    }

    //endregion



    @Override
    public IPage<FoodMaterialEntity> pageByCondition(FindFoodRequest req, SFunction<FoodMaterialEntity, ?>... cols) {
        Page<FoodMaterialEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        page.addOrder(OrderItem.asc("categ_name_path"),OrderItem.asc("energy"));
        return service.page(page, Wrappers.<FoodMaterialEntity>lambdaQuery()
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), FoodMaterialEntity::getInterveneCategId, req.getCategIdLv1())
                .like(ShareUtil.XString.hasLength(req.getKeywords()), FoodMaterialEntity::getFoodMaterialName, req.getKeywords())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .select(cols));
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(FoodMaterialEntity lead, List<FoodMaterialIndicatorEntity> indicators,List<FoodMaterialNutrientEntity> nutrients){
        AssertUtil.falseThenThrow(coreTranSave(lead,indicators,nutrients,false, true,false))
                .throwMessage(failedSaveMessage );
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveWithExpressions(FoodMaterialEntity lead, List<FoodMaterialNutrientEntity> nutrients,List<String> expressionIds){
        AssertUtil.falseThenThrow(coreTranSave(lead,null,nutrients,false, true,defaultUseLogicId))
                .throwMessage(failedSaveMessage );

        return indicatorExpressionRefDao.tranUpdateReasonId(lead.getFoodMaterialId(),expressionIds);
    }



    //region save

    protected boolean coreTranSave(FoodMaterialEntity lead, List<FoodMaterialIndicatorEntity> subs,List<FoodMaterialNutrientEntity> subsX, boolean delSubBefore,boolean delSubXBefore, boolean useLogicId) {
        final boolean existsFlag = ShareUtil.XObject.notEmpty(getColId().apply(lead));
        if (!super.coreTranSave(lead, subs, delSubBefore, useLogicId)) {
            return false;
        }
        final String leadId = getColId().apply(lead);
        if (existsFlag && delSubXBefore) {
            delSubByLeadIdX(leadId, true);
            if (null != subsX) {
                subsX.forEach(i -> i.setId(null));
            }
        }
        return saveOrUpdateBatchX(leadId, subsX, useLogicId, true);
    }

    public boolean saveOrUpdateBatchX(String leadId, List<FoodMaterialNutrientEntity> subs,boolean useLogicId,  boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(subs)) {
            return dftIfEmpty;
        }
        int seq=0;
        for(FoodMaterialNutrientEntity item:subs) {
            item.setFoodMaterialId(leadId)
                    .setSeq(++seq);
            if(ShareUtil.XObject.isEmpty(item.getFoodMaterialNutrientId())){
                item.setFoodMaterialNutrientId(idGenerator.nextIdStr());
            }

        }
        if(!useLogicId){
            return subServiceX.saveOrUpdateBatch(subs);
        }
        boolean rst=true;
        for(FoodMaterialNutrientEntity item:subs) {
            rst &= subServiceX.saveOrUpdate(item, Wrappers.<FoodMaterialNutrientEntity>lambdaUpdate()
                    .eq(FoodMaterialNutrientEntity::getFoodMaterialNutrientId, item.getFoodMaterialNutrientId()));
        }
        return rst;

    }
    //endregion

    //region retrieve
    public List<FoodMaterialNutrientEntity> getNutrientsByLeadIdsX(Collection<String> leadIds,Collection<String> indicatorIds, SFunction<FoodMaterialNutrientEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(leadIds)){
            return Collections.emptyList();
        }
        final boolean oneFlag=leadIds.size()==1;
        return subServiceX.lambdaQuery()
                .eq(oneFlag, FoodMaterialNutrientEntity::getFoodMaterialId,leadIds.iterator().next())
                .in(!oneFlag, FoodMaterialNutrientEntity::getFoodMaterialId,leadIds)
                .in(ShareUtil.XCollection.notEmpty(indicatorIds),FoodMaterialNutrientEntity::getIndicatorInstanceId,indicatorIds)
                .select(cols)
                .list();
    }


    public List<FoodMaterialNutrientEntity> getSubByLeadIdX(String leadId, SFunction<FoodMaterialNutrientEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(leadId)) {
            return Collections.emptyList();
        }
        return subServiceX.lambdaQuery()
                .eq(FoodMaterialNutrientEntity::getFoodMaterialId, leadId)
                .orderByAsc(FoodMaterialNutrientEntity::getId)
                .select(cols)
                .list();
    }
    //endregion

    //region delete
    @Override
    protected boolean coreTranDelete(List<String> ids, boolean delSub, boolean dftIfSubEmpty) {
        if(!super.coreTranDelete(ids, delSub, dftIfSubEmpty)){
            return false;
        }
        if(delSub) {
            delSubByLeadIdX(ids, dftIfSubEmpty);
            indicatorExpressionRefDao.tranDeleteByReasonId(ids);
        }
        return true;
    }
    public boolean delSubByLeadIdX(String leadId,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(leadId)){
            return dftIfEmpty;
        }
        return subServiceX.remove(Wrappers.<FoodMaterialNutrientEntity>lambdaQuery()
                .eq(FoodMaterialNutrientEntity::getFoodMaterialId,leadId));
    }
    public boolean delSubByLeadIdX(List<String> ids,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(ids)){
            return dftIfEmpty;
        }
        final boolean oneFlag=ids.size()==1;
        return subServiceX.remove(Wrappers.<FoodMaterialNutrientEntity>lambdaQuery()
                .eq(oneFlag,FoodMaterialNutrientEntity::getFoodMaterialId,ids.get(0))
                .in(!oneFlag,FoodMaterialNutrientEntity::getFoodMaterialId,ids));
    }


    //endregion


}
