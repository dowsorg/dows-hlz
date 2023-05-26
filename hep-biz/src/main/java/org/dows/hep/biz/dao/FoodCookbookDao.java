package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.request.FindFoodRequest;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.FoodCookbookDetailEntity;
import org.dows.hep.entity.FoodCookbookEntity;
import org.dows.hep.entity.FoodCookbookNutrientEntity;
import org.dows.hep.service.FoodCookbookDetailService;
import org.dows.hep.service.FoodCookbookNutrientService;
import org.dows.hep.service.FoodCookbookService;
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
public class FoodCookbookDao extends BaseSubDao<FoodCookbookService, FoodCookbookEntity, FoodCookbookDetailService, FoodCookbookDetailEntity>
        implements IPageDao<FoodCookbookEntity, FindFoodRequest> {

    public FoodCookbookDao(){
        super("食谱不存在或已删除,请刷新");
    }

    @Autowired
    protected FoodCookbookNutrientService subServiceX;

    //region override

    @Override
    protected SFunction<FoodCookbookEntity, String> getColId() {
        return FoodCookbookEntity::getFoodCookbookId;
    }

    @Override
    protected SFunction<String, ?> setColId(FoodCookbookEntity item) {
        return item::setFoodCookbookId;
    }

    @Override
    protected SFunction<FoodCookbookEntity, Integer> getColState() {
        return FoodCookbookEntity::getState;
    }

    @Override
    protected SFunction<Integer, ?> setColState(FoodCookbookEntity item) {
        return item::setState;
    }

    @Override
    protected SFunction<FoodCookbookEntity,String> getColCateg(){
        return FoodCookbookEntity::getInterveneCategId;
    }
    @Override
    protected SFunction<FoodCookbookDetailEntity, String> getColLeadId() {
        return FoodCookbookDetailEntity::getFoodCookbookId;
    }

    @Override
    protected SFunction<String, ?> setColLeadId(FoodCookbookDetailEntity item) {
        return item::setFoodCookbookId;
    }

    @Override
    protected SFunction<FoodCookbookDetailEntity, String> getColSubId() {
        return FoodCookbookDetailEntity::getFoodCookbookDetailId;
    }

    @Override
    protected SFunction<String, ?> setColSubId(FoodCookbookDetailEntity item) {
        return item::setFoodCookbookDetailId;
    }

    //endregion



    @Override
    public IPage<FoodCookbookEntity> pageByCondition(FindFoodRequest req, SFunction<FoodCookbookEntity, ?>... cols) {
        Page<FoodCookbookEntity> page = Page.of(req.getPageNo(), req.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return service.page(page, Wrappers.<FoodCookbookEntity>lambdaQuery()
                .in(ShareUtil.XCollection.notEmpty(req.getCategIdLv1()), FoodCookbookEntity::getInterveneCategId, req.getCategIdLv1())
                .like(ShareUtil.XString.hasLength(req.getKeywords()), FoodCookbookEntity::getFoodCookbookName, req.getKeywords())
                .in(ShareUtil.XCollection.notEmpty(req.getIncIds()), getColId(), req.getIncIds())
                .notIn(ShareUtil.XCollection.notEmpty(req.getExcIds()), getColId(), req.getExcIds())
                .eq(ShareUtil.XObject.notEmpty(req.getState()), getColState(), req.getState())
                .select(cols));
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(FoodCookbookEntity lead, List<FoodCookbookDetailEntity> materials, List<FoodCookbookNutrientEntity> nutrients){
        AssertUtil.falseThenThrow(coreTranSave(lead,materials,nutrients,false, true,false))
                .throwMessage(failedSaveMessage );
        return true;
    }



    //region save

    protected boolean coreTranSave(FoodCookbookEntity lead, List<FoodCookbookDetailEntity> subs,List<FoodCookbookNutrientEntity> subsX, boolean delSubBefore,boolean delSubXBefore, boolean useLogicId) {
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

    public boolean saveOrUpdateBatchX(String leadId, List<FoodCookbookNutrientEntity> subs,boolean useLogicId,  boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(subs)) {
            return dftIfEmpty;
        }
        int seq=0;
        for(FoodCookbookNutrientEntity item:subs) {
            item.setFoodCookbookId(leadId)
                    .setSeq(++seq);
            if(ShareUtil.XObject.isEmpty(item.getFoodCookbookNutrientId())){
                item.setFoodCookbookNutrientId(idGenerator.nextIdStr());
            }

        }
        if(!useLogicId){
            return subServiceX.saveOrUpdateBatch(subs);
        }
        boolean rst=true;
        for(FoodCookbookNutrientEntity item:subs) {
            rst &= subServiceX.saveOrUpdate(item, Wrappers.<FoodCookbookNutrientEntity>lambdaUpdate()
                    .eq(FoodCookbookNutrientEntity::getFoodCookbookNutrientId, item.getFoodCookbookNutrientId()));
        }
        return rst;

    }
    //endregion

    //region retrieve
    public List<FoodCookbookNutrientEntity> getSubByLeadIdX(String leadId, SFunction<FoodCookbookNutrientEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(leadId)) {
            return Collections.emptyList();
        }
        return subServiceX.lambdaQuery()
                .eq(FoodCookbookNutrientEntity::getFoodCookbookId, leadId)
                .orderByAsc(FoodCookbookNutrientEntity::getId)
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
        return subServiceX.remove(Wrappers.<FoodCookbookNutrientEntity>lambdaQuery()
                .eq(FoodCookbookNutrientEntity::getFoodCookbookId,leadId));
    }
    public boolean delSubByLeadIdX(List<String> ids,boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return subServiceX.remove(Wrappers.<FoodCookbookNutrientEntity>lambdaQuery()
                .eq(oneFlag, FoodCookbookNutrientEntity::getFoodCookbookId, ids.get(0))
                .in(!oneFlag, FoodCookbookNutrientEntity::getFoodCookbookId, ids));
    }

    //endregion


}
