package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.FoodMaterialInfoResponse;
import org.dows.hep.api.base.intervene.response.FoodMaterialResponse;
import org.dows.hep.api.base.intervene.vo.FoodNutrientVO;
import org.dows.hep.api.base.intervene.vo.IndicatorExpressionVO;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.FoodMaterialDao;
import org.dows.hep.biz.dao.IndicatorExpressionRefDao;
import org.dows.hep.biz.dao.IndicatorInstanceDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.FoodMaterialEntity;
import org.dows.hep.entity.FoodMaterialNutrientEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private final IndicatorExpressionRefDao daoExpressionRef;

    private final IndicatorExpressionBiz indicatorExpressionBiz;

    private final FoodCalcBiz foodCalcBiz;

    protected InterveneCategCache getCategCache(){
        return InterveneCategCache.Instance;
    }

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
        findFood.setCategIdLv1(getCategCache().getLeafIds(findFood.getCategIdLv1()));
        return ShareBiz.buildPage(dao.pageByCondition(findFood), i -> CopyWrapper.create(FoodMaterialResponse::new)
                .endFrom(refreshCateg(i))
                .setCategIdLv1(getCategCache().getCategLv1(i.getCategIdPath(), i.getInterveneCategId()))
                .setCategNameLv1(getCategCache().getCategLv1(i.getCategNamePath(), i.getCategName())));
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
    public FoodMaterialInfoResponse getFoodMaterial(String appId, String foodMaterialId ) {
        FoodMaterialEntity row=AssertUtil.getNotNull(dao.getById(foodMaterialId))
                .orElseThrow("食材不存在");


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
                    .setIndicatorInstanceId(item.getIndicatorInstanceId())
                    .setNutrientName(item.getIndicatorName())
                    .setUnit(item.getUnit()));
        }
        defNutrients.clear();
        mapExists.clear();
        List<FoodNutrientVO> voNutrients=ShareUtil.XCollection.map(nutrients,
                i->CopyWrapper.create(FoodNutrientVO::new).endFrom(i));
        List<IndicatorExpressionResponseRs> expressions=ShareBiz.getExpressionsByReasonId(indicatorExpressionBiz,appId,foodMaterialId);
        return CopyWrapper.create(FoodMaterialInfoResponse::new)
                .endFrom(refreshCateg(row))
                .setNutrients(voNutrients)
                .setExpresssions(expressions);

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
                        ||null==(categVO=getCategCache().getById(saveFoodMaterial.getInterveneCategId())))
                .throwMessage("食材类别不存在");

        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveFoodMaterial.getExpresssions())
                        &&saveFoodMaterial.getExpresssions().stream()
                        .map(IndicatorExpressionVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size()<saveFoodMaterial.getExpresssions().size())
                .throwMessage("存在重复的关联指标，请检查");

        FoodMaterialEntity row=CopyWrapper.create(FoodMaterialEntity::new)
                .endFrom(saveFoodMaterial)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath());

        //TODO checkExists
        List<FoodMaterialNutrientEntity> rowNutrients=ShareUtil.XCollection.map(saveFoodMaterial.getNutrients(),
                i->CopyWrapper.create(FoodMaterialNutrientEntity::new).endFrom(i));
        foodCalcBiz.calcFoodEnergy(row,rowNutrients);
        List<String> expressionIds=new ArrayList<>();
        Optional.ofNullable(saveFoodMaterial.getExpresssions())
            .ifPresent(i->i.forEach(expression-> expressionIds.add(expression.getIndicatorExpressionId())));
        return dao.tranSaveWithExpressions(row,rowNutrients, expressionIds);

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
        return daoExpressionRef.tranDeleteByExpressionId(delRefIndicator.getIds());
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

    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected FoodMaterialEntity refreshCateg(FoodMaterialEntity src) {
        if (ShareUtil.XObject.isEmpty(src.getInterveneCategId())) {
            return src;
        }
        CategVO cacheItem = getCategCache().getById(src.getInterveneCategId());
        if (null == cacheItem) {
            return src;
        }
        return src.setCategName(cacheItem.getCategName())
                .setCategIdPath(cacheItem.getCategIdPath())
                .setCategNamePath(cacheItem.getCategNamePath());

    }







}