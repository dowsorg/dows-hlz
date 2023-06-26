package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindFoodRequest;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/4/28 14:30
 */
@Component
public class FoodDishesDao extends BaseSubDao<FoodDishesService, FoodDishesEntity, FoodDishesMaterialService, FoodDishesMaterialEntity>
        implements IPageDao<FoodDishesEntity, FindFoodRequest> {

    public FoodDishesDao(){
        super("菜肴不存在或已删除,请刷新");
    }

    @Autowired
    protected FoodDishesNutrientService subServiceX;

    //region override

    @Override
    protected SFunction<FoodDishesEntity, String> getColId() {
        return FoodDishesEntity::getFoodDishesId;
    }

    @Override
    protected SFunction<String, ?> setColId(FoodDishesEntity item) {
        return item::setFoodDishesId;
    }

    @Override
    protected SFunction<FoodDishesEntity, Integer> getColState() {
        return FoodDishesEntity::getState;
    }

    @Override
    protected SFunction<FoodDishesEntity,String> getColCateg(){
        return FoodDishesEntity::getInterveneCategId;
    }
    @Override
    protected SFunction<Integer, ?> setColState(FoodDishesEntity item) {
        return item::setState;
    }

    @Override
    protected SFunction<FoodDishesMaterialEntity, String> getColLeadId() {
        return FoodDishesMaterialEntity::getFoodDishesId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(FoodDishesMaterialEntity item) {
        return item::setFoodDishesId;
    }

    @Override
    protected SFunction<FoodDishesMaterialEntity, String> getColSubId() {
        return FoodDishesMaterialEntity::getFoodDishesMaterialId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(FoodDishesMaterialEntity item) {
        return item::setFoodDishesMaterialId;
    }

    @Override
    protected SFunction<FoodDishesMaterialEntity, Integer> getColSubSeq() {
        return FoodDishesMaterialEntity::getSeq;
    }

    @Override
    protected SFunction<Integer, ?> setColSubSeq(FoodDishesMaterialEntity item) {
        return item::setSeq;
    }

    //endregion



    @Override
    public IPage<FoodDishesEntity> pageByCondition(FindFoodRequest req, SFunction<FoodDishesEntity, ?>... cols) {
        Page<FoodDishesEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.lambdaQuery()
                .eq(ShareUtil.XObject.notEmpty(req.getAppId()), FoodDishesEntity::getAppId,req.getAppId())
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), FoodDishesEntity::getInterveneCategId, req.getCategIdLv1())
                .like(ShareUtil.XString.hasLength(req.getKeywords()), FoodDishesEntity::getFoodDishesName, req.getKeywords())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .select(cols)
                .page(page);
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(FoodDishesEntity lead, List<FoodDishesMaterialEntity> materials, List<FoodDishesNutrientEntity> nutrients){
        AssertUtil.falseThenThrow(coreTranSave(lead,materials,nutrients,false, true,false))
                .throwMessage(failedSaveMessage );
        return true;
    }




    //region save

    protected boolean coreTranSave(FoodDishesEntity lead, List<FoodDishesMaterialEntity> subs,List<FoodDishesNutrientEntity> subsX, boolean delSubBefore,boolean delSubXBefore, boolean useLogicId) {
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

    public boolean saveOrUpdateBatchX(String leadId, List<FoodDishesNutrientEntity> subs,boolean useLogicId,  boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(subs)) {
            return dftIfEmpty;
        }
        int seq=0;
        for(FoodDishesNutrientEntity item:subs) {
            item.setFoodDishesId(leadId)
                    .setSeq(++seq);
            if(ShareUtil.XObject.isEmpty(item.getFoodDishesNutrientId())){
                item.setFoodDishesNutrientId(idGenerator.nextIdStr());
            }

        }
        if(!useLogicId){
            return subServiceX.saveOrUpdateBatch(subs);
        }
        boolean rst=true;
        for(FoodDishesNutrientEntity item:subs) {
            rst &= subServiceX.saveOrUpdate(item, Wrappers.<FoodDishesNutrientEntity>lambdaUpdate()
                    .eq(FoodDishesNutrientEntity::getFoodDishesNutrientId, item.getFoodDishesNutrientId()));
        }
        return rst;

    }
    //endregion

    //region retrieve

    public List<FoodDishesNutrientEntity> getSubByLeadIdX(String leadId, SFunction<FoodDishesNutrientEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(leadId)) {
            return Collections.emptyList();
        }
        return subServiceX.lambdaQuery()
                .eq(FoodDishesNutrientEntity::getFoodDishesId, leadId)
                .orderByAsc(FoodDishesNutrientEntity::getSeq,FoodDishesNutrientEntity::getId)
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
        }
        return true;
    }
    public boolean delSubByLeadIdX(String leadId,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(leadId)){
            return dftIfEmpty;
        }
        return subServiceX.remove(Wrappers.<FoodDishesNutrientEntity>lambdaQuery()
                .eq(FoodDishesNutrientEntity::getFoodDishesId,leadId));
    }
    public boolean delSubByLeadIdX(List<String> ids,boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return subServiceX.remove(Wrappers.<FoodDishesNutrientEntity>lambdaQuery()
                .eq(oneFlag, FoodDishesNutrientEntity::getFoodDishesId, ids.get(0))
                .in(!oneFlag, FoodDishesNutrientEntity::getFoodDishesId, ids));
    }

    //endregion


}
