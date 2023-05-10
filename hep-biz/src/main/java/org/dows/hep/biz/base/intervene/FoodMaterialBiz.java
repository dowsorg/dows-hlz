package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.FoodMaterialInfoResponse;
import org.dows.hep.api.base.intervene.response.FoodMaterialResponse;
import org.dows.hep.api.base.intervene.vo.FoodNutrientVO;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.FoodMaterialDao;
import org.dows.hep.biz.dao.IndicatorInstanceDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.FoodMaterialEntity;
import org.dows.hep.entity.FoodMaterialIndicatorEntity;
import org.dows.hep.entity.FoodMaterialNutrientEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @description project descr:干预:食材
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class FoodMaterialBiz{

    private final FoodMaterialDao dao;
    private final IndicatorInstanceDao daoIndicator;

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
    public Page<FoodMaterialResponse> pageFoodMaterial(FindFoodRequest findFood ) {
        findFood.setCategIdLv1(ShareBiz.ensureCategPathSuffix(findFood.getCategIdLv1()));
        IPage<FoodMaterialEntity> page= dao.pageByCondition(findFood);
        Page<FoodMaterialResponse> pageDto= Page.of (page.getCurrent(),page.getSize(),page.getTotal(),page.searchCount());
        return pageDto.setRecords(ShareUtil.XCollection.map(page.getRecords(),true, i-> CopyWrapper.create(FoodMaterialResponse::new)
                .endFrom(i)
                .setCategIdLv1(InterveneCategCache.Instance.getCategLv1(i.getCategIdPath() ,i.getInterveneCategId()))
                .setCategNameLv1(InterveneCategCache.Instance.getCategLv1(i.getCategNamePath() ,i.getCategName()))));
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
        FoodMaterialEntity rowMaterial=AssertUtil.getNotNull(dao.getById(foodMaterialId))
                .orElseThrow("食材不存在");

        List<FoodMaterialIndicatorEntity> indicators= dao.getSubByLeadId(foodMaterialId,
                FoodMaterialIndicatorEntity::getId,
                FoodMaterialIndicatorEntity::getIndicatorInstanceId,
                FoodMaterialIndicatorEntity::getExpression,
                FoodMaterialIndicatorEntity::getExpressionDescr,
                FoodMaterialIndicatorEntity::getSeq);
        List<FoodMaterialNutrientEntity> nutrients= dao.getSubByLeadIdX(foodMaterialId,
                FoodMaterialNutrientEntity::getId,
                FoodMaterialNutrientEntity::getIndicatorInstanceId,
                FoodMaterialNutrientEntity::getNutrientName,
                FoodMaterialNutrientEntity::getWeight,
                FoodMaterialNutrientEntity::getSeq);
        //同步最新饮食指标
        List<IndicatorInstanceEntity> defNutrients = daoIndicator.getIndicators4Nutrient(
                IndicatorInstanceEntity::getIndicatorInstanceId,
                IndicatorInstanceEntity::getIndicatorName,
                IndicatorInstanceEntity::getUnit);
        Map<String,FoodMaterialNutrientEntity> mapExists=ShareUtil.XCollection.toMap(nutrients,FoodMaterialNutrientEntity::getIndicatorInstanceId, Function.identity());
        nutrients=new ArrayList<>();
        for(IndicatorInstanceEntity item:defNutrients) {
            nutrients.add(mapExists.getOrDefault(item.getIndicatorInstanceId(), new FoodMaterialNutrientEntity())
                    .setNutrientName(item.getIndicatorName())
                    .setUnit(item.getUnit()));
        }
        defNutrients.clear();
        mapExists.clear();
        List<InterveneIndicatorVO> voIndicators=ShareUtil.XCollection.map(indicators,true,
                i->CopyWrapper.create(InterveneIndicatorVO::new)
                        .endFrom(i,v->v.setRefId(i.getFoodMaterialIndicatorId())));
        List<FoodNutrientVO> voNutrients=ShareUtil.XCollection.map(nutrients,true,
                i->CopyWrapper.create(FoodNutrientVO::new).endFrom(i));
        return CopyWrapper.create(FoodMaterialInfoResponse::new).endFrom(rowMaterial)
                .setIndicators(voIndicators)
                .setNutrients(voNutrients);

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
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveFoodMaterial.getFoodMaterialId())
                        && dao.getById(saveFoodMaterial.getFoodMaterialId(),FoodMaterialEntity::getFoodMaterialId).isEmpty())
                .throwMessage("食材不存在");
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveFoodMaterial.getInterveneCategId())
                        ||null==(categVO=InterveneCategCache.Instance.getById(saveFoodMaterial.getInterveneCategId())))
                .throwMessage("食材类别不存在");

        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveFoodMaterial.getIndicators())
                        &&saveFoodMaterial.getIndicators().stream()
                        .map(InterveneIndicatorVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size()<saveFoodMaterial.getIndicators().size())
                .throwMessage("存在重复的关联指标，请检查");

        FoodMaterialEntity row=CopyWrapper.create(FoodMaterialEntity::new)
                .endFrom(saveFoodMaterial)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath());

        //TODO checkExists，calc
        List<FoodMaterialIndicatorEntity> rowIndicators=ShareUtil.XCollection.map(saveFoodMaterial.getIndicators(),true,
                i->CopyWrapper.create(FoodMaterialIndicatorEntity::new).endFrom(i,v->v.setFoodMaterialIndicatorId(i.getRefId())));
        List<FoodMaterialNutrientEntity> rowNutrients=ShareUtil.XCollection.map(saveFoodMaterial.getNutrients(),true,
                i->CopyWrapper.create(FoodMaterialNutrientEntity::new).endFrom(i));
        return dao.tranSave(row,rowIndicators,rowNutrients);

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
        return dao.tranDelete(delFoodMaterial.getIds(),true);
    }

    /**
     * 删除关联指标
     * @param delRefIndicator
     * @return
     */
    public Boolean delRefIndicator(DelRefIndicatorRequest delRefIndicator ) {
        return dao.tranDeleteSub(delRefIndicator.getIds(),"关联指标不存在或已删除");
    }

    /**
     *启用，禁用食材
     *
     * @param setFoodMaterialState
     * @return
     */
    public Boolean setFoodMaterialState(SetFoodMaterialStateRequest setFoodMaterialState ) {
        return dao.tranSetState(setFoodMaterialState.getFoodMaterialId(), setFoodMaterialState.getState());
    }







}