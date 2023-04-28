package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.FoodMaterialEntity;
import org.dows.hep.entity.FoodMaterialIndicatorEntity;
import org.dows.hep.entity.FoodMaterialNutrientEntity;
import org.dows.hep.service.FoodMaterialIndicatorService;
import org.dows.hep.service.FoodMaterialNutrientService;
import org.dows.hep.service.FoodMaterialService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/4/23 19:18
 */

@Component
@RequiredArgsConstructor
public class FoodMaterialDao {
    private final FoodMaterialService foodMaterialService;
    private final FoodMaterialIndicatorService foodMaterialIndicatorService;
    private final FoodMaterialNutrientService foodMaterialNutrientService;

    private final IdGenerator idGenerator;

    private final static SFunction<FoodMaterialEntity,?> COLLogicKey4Material =FoodMaterialEntity::getFoodMaterialId;


    //region trans
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(FoodMaterialEntity material, List<FoodMaterialNutrientEntity> nutrients, List<FoodMaterialIndicatorEntity> indicators){
        AssertUtil.falseThenThrow(coreSave(material,nutrients,indicators))
                .throwMessage("保存失败,请刷新");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranSetState(String materialId, Integer state) {
        AssertUtil.falseThenThrow(setState(materialId,state))
                .throwMessage("食材不存在,请刷新");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranDelete(List<String> ids) {
        AssertUtil.falseThenThrow(coreDelete(ids))
                .throwMessage("食材不存在,请刷新");
        return true;
    }
    //endregion

    //region save
    public boolean coreSave(FoodMaterialEntity material, List<FoodMaterialNutrientEntity> nutrients, List<FoodMaterialIndicatorEntity> indicators){
        final boolean existsFlag=ShareUtil.XObject.notEmpty(material.getFoodMaterialId());
        if(!saveOrUpdate(material)) {
            return false;
        }
        final String materialId=material.getFoodMaterialId();
        if(existsFlag) {
            delByLeadId4Nutrient(materialId);
        }
        if(!saveBatch4Nutrients(materialId,nutrients)){
            return false;
        }
        if(existsFlag) {
            delByLeadId4Indicator(materialId);
        }
        return saveBatch4Indicators(materialId, indicators);
    }
     public boolean saveOrUpdate(FoodMaterialEntity entity){
        if(ShareUtil.XObject.isEmpty(entity)){
            return false;
        }
        if(ShareUtil.XObject.isEmpty(entity.getFoodMaterialId())){
            entity.setFoodMaterialId(idGenerator.nextIdStr());
        }
        return foodMaterialService.saveOrUpdate(entity);
    }

    public boolean saveBatch4Nutrients(String materialId, List<FoodMaterialNutrientEntity> nutrients){
        if(ShareUtil.XObject.isEmpty(nutrients)){
            return true;
        }
        int seq=0;
        for(FoodMaterialNutrientEntity item:nutrients){
            item.setId(null)
                    .setFoodMaterialNutrientId(idGenerator.nextIdStr())
                    .setFoodMaterialId(materialId)
                    .setSeq(++seq);
        }
        return foodMaterialNutrientService.saveBatch(nutrients);
    }
    public boolean saveBatch4Indicators(String materialId,  List<FoodMaterialIndicatorEntity> indicators){
        if(ShareUtil.XObject.isEmpty(indicators)){
            return true;
        }
        int seq=0;
        for(FoodMaterialIndicatorEntity item:indicators){
            item.setId(null)
                    .setFoodMaterialIndicatorId(idGenerator.nextIdStr())
                    .setFoodMaterialId(materialId)
                    .setSeq(++seq);
        }
        return foodMaterialIndicatorService.saveBatch(indicators);
    }
    public boolean setState(String materialId, Integer state) {
        return foodMaterialService.update(Wrappers.<FoodMaterialEntity>lambdaUpdate()
                .eq(COLLogicKey4Material, materialId)
                .set(FoodMaterialEntity::getState, Optional.ofNullable(state).orElse(0) ));
    }
    //endregion


    //region find
    public Page<FoodMaterialEntity> getByCondition(Page<FoodMaterialEntity> page, String keyWords, String categId, SFunction<FoodMaterialEntity,?>... cols){

        return foodMaterialService.page(page,Wrappers.<FoodMaterialEntity>lambdaQuery()
                .likeRight(ShareUtil.XString.hasLength(categId), FoodMaterialEntity::getCategIdPath,categId)
                .like(ShareUtil.XString.hasLength(keyWords), FoodMaterialEntity::getFoodMaterialName,keyWords)
                .select(cols));

    }
    public Optional<FoodMaterialEntity> getByPk(Long pk, SFunction<FoodMaterialEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(pk)){
            return Optional.empty();
        }
        return foodMaterialService.lambdaQuery()
                .eq(FoodMaterialEntity::getId,pk)
                .select(cols)
                .oneOpt();
    }
    public Optional<FoodMaterialEntity> getById(String materialId, SFunction<FoodMaterialEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(materialId)){
            return Optional.empty();
        }
        return foodMaterialService.lambdaQuery()
                .eq(COLLogicKey4Material,materialId)
                .select(cols)
                .oneOpt();
    }

    public List<FoodMaterialNutrientEntity> getByLeadId4Nutrient(String materialId, SFunction<FoodMaterialNutrientEntity,?>... cols) {
        if (ShareUtil.XObject.isEmpty(materialId)) {
            return Collections.emptyList();
        }
        return foodMaterialNutrientService.lambdaQuery()
                .eq(FoodMaterialNutrientEntity::getFoodMaterialId, materialId)
                .orderByAsc(FoodMaterialNutrientEntity::getId)
                .select(cols)
                .list();
    }
    public List<FoodMaterialIndicatorEntity> getByLeadId4Indicator(String materialId, SFunction<FoodMaterialIndicatorEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(materialId)){
            return Collections.emptyList();
        }
        return foodMaterialIndicatorService.lambdaQuery()
                .eq(FoodMaterialIndicatorEntity::getFoodMaterialId,materialId)
                .orderByAsc(FoodMaterialIndicatorEntity::getId)
                .select(cols)
                .list();
    }


    //endregion

    //region delete
    public boolean coreDelete(List<String> ids){
        if(!delByIds(ids)){
            return false;
        }
        delByLeadId4Nutrient(ids);
        delByLeadId4Indicator(ids);
        return true;
    }

    public boolean delByIds(List<String> ids){
        if(ShareUtil.XObject.isEmpty(ids)){
            return false;
        }
        final boolean oneFlag=ids.size()==1;
        return foodMaterialService.remove(Wrappers.<FoodMaterialEntity>lambdaQuery()
                .eq(oneFlag, COLLogicKey4Material,ids.get(0))
                .in(!oneFlag, COLLogicKey4Material,ids));
    }
    public boolean delByLeadId4Nutrient(String materialId){
        if(ShareUtil.XObject.isEmpty(materialId)){
            return false;
        }
        return foodMaterialNutrientService.remove(Wrappers.<FoodMaterialNutrientEntity>lambdaQuery()
                .eq(FoodMaterialNutrientEntity::getFoodMaterialId,materialId));
    }
    public boolean delByLeadId4Nutrient(List<String> ids){
        if(ShareUtil.XObject.isEmpty(ids)){
            return false;
        }
        final boolean oneFlag=ids.size()==1;
        return foodMaterialNutrientService.remove(Wrappers.<FoodMaterialNutrientEntity>lambdaQuery()
                .eq(oneFlag,FoodMaterialNutrientEntity::getFoodMaterialId,ids.get(0))
                .in(!oneFlag,FoodMaterialNutrientEntity::getFoodMaterialId,ids));
    }
    public boolean delByLeadId4Indicator(String materialId){
        if(ShareUtil.XObject.isEmpty(materialId)){
            return false;
        }
        return foodMaterialIndicatorService.remove(Wrappers.<FoodMaterialIndicatorEntity>lambdaQuery()
                .eq(FoodMaterialIndicatorEntity::getFoodMaterialId,materialId));
    }
    public boolean delByLeadId4Indicator(List<String> ids){
        if(ShareUtil.XObject.isEmpty(ids)){
            return false;
        }
        final boolean oneFlag=ids.size()==1;
        return foodMaterialIndicatorService.remove(Wrappers.<FoodMaterialIndicatorEntity>lambdaQuery()
                .eq(oneFlag,FoodMaterialIndicatorEntity::getFoodMaterialId,ids.get(0))
                .in(!oneFlag,FoodMaterialIndicatorEntity::getFoodMaterialId,ids));
    }

    //endregion


}
