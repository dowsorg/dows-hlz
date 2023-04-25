package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelFoodMaterialRequest;
import org.dows.hep.api.base.intervene.request.FindFoodRequest;
import org.dows.hep.api.base.intervene.request.SaveFoodMaterialRequest;
import org.dows.hep.api.base.intervene.request.SetFoodMaterialStateRequest;
import org.dows.hep.api.base.intervene.response.FoodMaterialInfoResponse;
import org.dows.hep.api.base.intervene.response.FoodMaterialResponse;
import org.dows.hep.api.base.intervene.vo.FoodNutrientVO;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.FoodMaterialDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.FoodMaterialEntity;
import org.dows.hep.entity.FoodMaterialIndicatorEntity;
import org.dows.hep.entity.FoodMaterialNutrientEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @description project descr:干预:食材
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class FoodMaterialBiz{

    private final FoodMaterialDao foodMaterialDao;

    /**
    * @param
    * @return
    * @说明: 获取食材列表
    * @关联表: food_material
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public PageDTO<FoodMaterialResponse> pageFoodMaterial(FindFoodRequest findFood ) {
        Page<FoodMaterialEntity> page=Page.of(findFood.getPageNo(),findFood.getPageSize());
        page=foodMaterialDao.getByCondition4Material(page,findFood.getKeywords(), findFood.getCategIdLv1());
        PageDTO<FoodMaterialResponse> pageDto= new PageDTO<> (page.getCurrent(),page.getSize(),page.getTotal(),page.searchCount());
        pageDto.setRecords(ShareUtil.XCollection.map(page.getRecords(),true, i-> CopyWrapper.create(FoodMaterialResponse::new)
                .endFrom(i)
                .setCategIdLv1(InterveneCategCache.getCategLv1(i.getCategIdPath() ,i.getInterveneCategId()))
                .setCategNameLv1(InterveneCategCache.getCategLv1(i.getCategNamePath() ,i.getCategName()))));
        return pageDto;
    }
    /**
    * @param
    * @return
    * @说明: 获取食材详细信息
    * @关联表: food_material,food_material_indicator,food_material_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public FoodMaterialInfoResponse getFoodMaterial(String foodMaterialId ) {
        FoodMaterialEntity rowMaterial=foodMaterialDao.getById4Material(foodMaterialId)
                .orElseThrow(()->new RuntimeException("食材不存在"));
        List<FoodMaterialNutrientEntity> nutrients=foodMaterialDao.getByMaterialId4Nutrient(foodMaterialId,
                FoodMaterialNutrientEntity::getId,
                FoodMaterialNutrientEntity::getIndicatorInstanceId,
                FoodMaterialNutrientEntity::getNutrientName,
                FoodMaterialNutrientEntity::getWeight,
                FoodMaterialNutrientEntity::getSeq);
        List<FoodMaterialIndicatorEntity> indicators=foodMaterialDao.getByMaterialId4Indicator(foodMaterialId,
                FoodMaterialIndicatorEntity::getId,
                FoodMaterialIndicatorEntity::getIndicatorInstanceId,
                FoodMaterialIndicatorEntity::getExpression,
                FoodMaterialIndicatorEntity::getExpressionDescr,
                FoodMaterialIndicatorEntity::getSeq);
        //TODO 合并营养指标
        List<FoodNutrientVO> voNutrients=ShareUtil.XCollection.map(nutrients,true,
                i->CopyWrapper.create(FoodNutrientVO::new).endFrom(i));
        List<InterveneIndicatorVO> voIndicators=ShareUtil.XCollection.map(indicators,true,
                i->CopyWrapper.create(InterveneIndicatorVO::new).endFrom(i));
        FoodMaterialInfoResponse rst=CopyWrapper.create(FoodMaterialInfoResponse::new).endFrom(rowMaterial)
                .setNutrients(voNutrients)
                .setIndicators(voIndicators);
        return rst;
    }
    /**
    * @param
    * @return
    * @说明: 保存食材
    * @关联表: food_material,food_material_indicator,food_material_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveFoodMaterial(SaveFoodMaterialRequest saveFoodMaterial ) {
        if(null!=saveFoodMaterial.getId()){
            foodMaterialDao.getByPk4Material(saveFoodMaterial.getId())
                    .orElseThrow(()->new RuntimeException("食材不存在"));
        }
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveFoodMaterial.getInterveneCategId())
                        ||null==(categVO=InterveneCategCache.Instance.getById(saveFoodMaterial.getInterveneCategId())))
                .throwMessage("食材类别不存在");

        FoodMaterialEntity row=CopyWrapper.create(FoodMaterialEntity::new)
                .endFrom(saveFoodMaterial)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath());

        //TODO checkExists，calc
        List<FoodMaterialNutrientEntity> rowNutrients=ShareUtil.XCollection.map(saveFoodMaterial.getNutrients(),true,
                i->CopyWrapper.create(FoodMaterialNutrientEntity::new).endFrom(i));
        List<FoodMaterialIndicatorEntity> rowIndicators=ShareUtil.XCollection.map(saveFoodMaterial.getIndicators(),true,
                i->CopyWrapper.create(FoodMaterialIndicatorEntity::new).endFrom(i));
        AssertUtil.falseThenThrow(foodMaterialDao.saveMaterial(row,rowNutrients,rowIndicators))
                .throwMessage("保存失败");
        return true;
    }
    /**
    * @param
    * @return
    * @说明: 删除食材
    * @关联表: food_material,food_material_indicator,food_material_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delFoodMaterial(DelFoodMaterialRequest delFoodMaterial ) {

        //TODO checkRefence
        AssertUtil.falseThenThrow(foodMaterialDao.delMaterials(delFoodMaterial.getIds()))
                .throwMessage("食材不存在");
        return true;
    }

    /**
     *启用，禁用食材
     *
     * @param setFoodMaterialState
     * @return
     */
    public Boolean setFoodMaterialState(SetFoodMaterialStateRequest setFoodMaterialState ){
        AssertUtil.falseThenThrow(foodMaterialDao.updateState(setFoodMaterialState.getFoodMaterialId(),setFoodMaterialState.getState()))
                .throwMessage("食材不存在");
        return true;
    }


}