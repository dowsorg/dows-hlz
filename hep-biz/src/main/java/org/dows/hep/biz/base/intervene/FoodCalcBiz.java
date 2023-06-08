package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.request.CalcFoodGraphRequest;
import org.dows.hep.api.base.intervene.response.FoodGraphResponse;
import org.dows.hep.api.base.intervene.vo.FoodDetailVO;
import org.dows.hep.api.enums.*;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.FoodDishesDao;
import org.dows.hep.biz.dao.FoodMaterialDao;
import org.dows.hep.biz.dao.IndicatorInstanceDao;
import org.dows.hep.biz.dao.IndicatorRuleDao;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.*;
import org.dows.hep.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  饮食计算
 * @author : wuzl
 * @date : 2023/5/19 14:11
 */
@Service

public class FoodCalcBiz {

    @Autowired
    protected IndicatorInstanceDao indicatorInstanceDao;
    @Autowired
    protected IndicatorRuleDao indicatorRuleDao;
    @Autowired
    protected FoodMaterialDao foodMaterialDao;
    @Autowired
    protected FoodDishesDao foodDishesDao;
    //保留两位小数
    protected static final int NUMBERScale2=2;
    protected static final String EMPTYValue="-";

    protected InterveneCategCache getCategCache(){
        return InterveneCategCache.Instance;
    }

    //region 计算入口
    /**
     * @param
     * @return
     * @说明: 计算能量占比、膳食宝塔
     * @关联表: food_material_nutrient
     * @工时: 2H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public FoodGraphResponse calcFoodGraph(CalcFoodGraphRequest calcFoodGraph ) {
        FoodGraphResponse rst = new FoodGraphResponse();
        if (ShareUtil.XCollection.isEmpty(calcFoodGraph.getDetails())) {
            return rst;
        }
        Map<String, CalcFoodDetailVO> mapMatWeights = calcMaterialWeight4Graph(calcFoodGraph.getDetails());
        if (ShareUtil.XObject.isEmpty(mapMatWeights)) {
            return rst;
        }
        EnumFoodCalcType calcType = EnumFoodCalcType.of(calcFoodGraph.getCalcType());
        List<CalcFoodStatVO> statEnergy = calcType.calcEnergy() ? calcFoodEnergy(mapMatWeights) : null;
        List<CalcFoodStatVO> statCateg = calcType.calcCateg() ? calcFoodCateg(mapMatWeights) : null;
        return new FoodGraphResponse().setCalcType(calcFoodGraph.getCalcType())
                .setStatEnergy(statEnergy)
                .setStatCateg(statCateg);
    }

    /**
     * 按菜肴明细计算营养统计，膳食结构
     * @param details
     * @return
     */
    public CalcFoodDishesResult calcFoodGraph4Dishes(List<FoodDishesMaterialEntity> details){
        CalcFoodDishesResult rst=new CalcFoodDishesResult();
        if(ShareUtil.XCollection.isEmpty(details)){
            return rst;
        }
        Map<String, CalcFoodDetailVO> mapMatWeights = calcMaterialWeight4Dishes(details);
        if (ShareUtil.XObject.isEmpty(mapMatWeights)) {
            return rst;
        }
        List<CalcFoodStatVO> statEnergy = calcFoodEnergy(mapMatWeights);
        List<CalcFoodStatVO> statCateg = calcFoodCateg(mapMatWeights);
        List<FoodDishesNutrientEntity> nutrients = new ArrayList<>();
        if (ShareUtil.XCollection.notEmpty(statEnergy)) {
            statEnergy.forEach(i ->
                    nutrients.add(CopyWrapper.create(FoodDishesNutrientEntity::new)
                            .endFrom(i, v -> v.setInstanceType(EnumFoodStatType.NUTRIENT.getCode()))));
        }
        if (ShareUtil.XCollection.notEmpty(statCateg)) {
            statCateg.forEach(i ->
                    nutrients.add(CopyWrapper.create(FoodDishesNutrientEntity::new)
                            .endFrom(i, v -> v.setInstanceType(EnumFoodStatType.FOODCateg.getCode()))));
        }
        return rst.setNutrients(nutrients).setStatEnergy(statEnergy);
    }

    /**
     * 按食谱明细计算营养统计，膳食结构
     * @param details
     * @return
     */
    public CalcFoodCookbookResult calcFoodGraph4Cookbook(List<FoodCookbookDetailEntity> details) {
        CalcFoodCookbookResult rst=new CalcFoodCookbookResult();
        if (ShareUtil.XCollection.isEmpty(details)) {
            return rst;
        }
        Map<String, CalcFoodDetailVO> mapMatWeights = calcMaterialWeight4Cookbook(details);
        if (ShareUtil.XObject.isEmpty(mapMatWeights)) {
            return rst;
        }
        List<CalcFoodStatVO> statEnergy = calcFoodEnergy(mapMatWeights);
        List<CalcFoodStatVO> statCateg = calcFoodCateg(mapMatWeights);
        List<FoodCookbookNutrientEntity> nutrients = new ArrayList<>();
        if (ShareUtil.XCollection.notEmpty(statEnergy)) {
            statEnergy.forEach(i ->
                    nutrients.add(CopyWrapper.create(FoodCookbookNutrientEntity::new)
                            .endFrom(i, v -> v.setInstanceType(EnumFoodStatType.NUTRIENT.getCode()))));
        }
        if (ShareUtil.XCollection.notEmpty(statCateg)) {
            statCateg.forEach(i ->
                    nutrients.add(CopyWrapper.create(FoodCookbookNutrientEntity::new)
                            .endFrom(i, v -> v.setInstanceType(EnumFoodStatType.FOODCateg.getCode()))));
        }
        return rst.setNutrients(nutrients).setStatEnergy(statEnergy);
    }



    /**
     * 按食材成分明细计算能量
     * @param material
     * @param nutrients
     */
    public void calcFoodEnergy(FoodMaterialEntity material, List<FoodMaterialNutrientEntity> nutrients){
        Map<EnumFoodNutrient, String> baseNutrients=ShareUtil.XCollection.toMap(indicatorInstanceDao.getIndicators4Nutrient(EnumFoodNutrient.BASENutrientNames4,
                IndicatorInstanceEntity::getIndicatorInstanceId,
                IndicatorInstanceEntity::getIndicatorName),i-> EnumFoodNutrient.of(i.getIndicatorName()), IndicatorInstanceEntity::getIndicatorInstanceId);
        FoodMaterialNutrientEntity rowEnergy=null;
        BigDecimalOptional boxEnergy=BigDecimalOptional.create();
        material.setProtein(EMPTYValue).setFat(EMPTYValue).setCho(EMPTYValue).setEnergy(EMPTYValue);
        for(FoodMaterialNutrientEntity item:nutrients){
            EnumFoodNutrient nutrientType=EnumFoodNutrient.of(item.getNutrientName());
            String nutrientId= baseNutrients.get(nutrientType);
            if(null==nutrientId|| !nutrientId.equalsIgnoreCase( item.getIndicatorInstanceId())){
                continue;
            }
            if(nutrientType==EnumFoodNutrient.ENERGY){
                rowEnergy=item;
                continue;
            }
            final BigDecimal val=BigDecimalUtil.tryParseDecimalElseZero(item.getWeight());
            boxEnergy.add(nutrientType.calcEnergy(val));
            item.setWeight(BigDecimalUtil.formatRoundDecimal(val, NUMBERScale2,false, EMPTYValue));
            switch (nutrientType){
                case PROTEIN:
                    material.setProtein(item.getWeight());
                    break;
                case FAT:
                    material.setFat(item.getWeight());
                    break;
                case CHO:
                    material.setCho(item.getWeight());
                    break;
            }
        }
        material.setEnergy(BigDecimalUtil.formatRoundDecimal(boxEnergy.getValue(),NUMBERScale2,false,EMPTYValue));
        if(null!=rowEnergy){
            rowEnergy.setWeight(material.getEnergy());
        }
        baseNutrients.clear();
    }

    //endregion

    //region 汇总食材重量
    protected Map<String,CalcFoodDetailVO> calcMaterialWeight4Dishes(List<FoodDishesMaterialEntity> details){
        Map<String,CalcFoodDetailVO> rst=new HashMap<>();
        if(ShareUtil.XCollection.isEmpty(details)){
            return rst;
        }
        calcMaterialWeight(rst, details, FoodDishesMaterialEntity::getFoodMaterialId,FoodDishesMaterialEntity::getFoodMaterialName, FoodDishesMaterialEntity::getWeight);
        return rst;
    }

    protected Map<String, CalcFoodDetailVO> calcMaterialWeight4Cookbook(List<FoodCookbookDetailEntity> details){
        Map<String,CalcFoodDetailVO> rst=new HashMap<>();
        if(ShareUtil.XCollection.isEmpty(details)){
            return rst;
        }
        List<FoodCookbookDetailEntity> mats=new ArrayList<>(details.size());
        List<FoodCookbookDetailEntity> dishes=new ArrayList<>(details.size());
        details.forEach(i->{
            switch (EnumFoodDetailType.of(i.getInstanceType())){
                case MATERIAL:
                    mats.add(i);
                    break;
                case DISHES:
                    dishes.add(i);
                    break;
            }
        });
        calcMaterialWeight(rst, mats, FoodCookbookDetailEntity::getInstanceId, FoodCookbookDetailEntity::getInstanceName, FoodCookbookDetailEntity::getWeight);
        calcMaterialWeight4Dishes(rst, dishes, FoodCookbookDetailEntity::getInstanceId, FoodCookbookDetailEntity::getWeight);
        mats.clear();
        dishes.clear();
        return rst;
    }
    protected Map<String, CalcFoodDetailVO> calcMaterialWeight4Graph(List<FoodDetailVO> details){
        Map<String,CalcFoodDetailVO> rst=new HashMap<>();
        if(ShareUtil.XCollection.isEmpty(details)){
            return rst;
        }
        List<FoodDetailVO> mats=new ArrayList<>(details.size());
        List<FoodDetailVO> dishes=new ArrayList<>(details.size());
        details.forEach(i->{
            switch (EnumFoodDetailType.of(i.getInstanceType())){
                case MATERIAL:
                    mats.add(i);
                    break;
                case DISHES:
                    dishes.add(i);
                    break;
            }
        });
        calcMaterialWeight(rst, mats, FoodDetailVO::getInstanceId, FoodDetailVO::getInstanceName, FoodDetailVO::getWeight);
        calcMaterialWeight4Dishes(rst, dishes, FoodDetailVO::getInstanceId, FoodDetailVO::getWeight);
        mats.clear();
        dishes.clear();
        return rst;
    }

    protected <T> void calcMaterialWeight(Map<String, CalcFoodDetailVO> dst,List<T> src,Function<T,String> getMaterialId,Function<T,String> getMaterialName, Function<T,String> getMaterialWeight){
        calcMaterialWeight(dst,src,getMaterialId,getMaterialName,getMaterialWeight,null);
    }
    protected <T> void calcMaterialWeight(Map<String, CalcFoodDetailVO> dst,List<T> src,Function<T,String> getMaterialId,Function<T,String> getMaterialName, Function<T,String> getMaterialWeight,Function<T,Integer> getMealTime) {
        if(ShareUtil.XCollection.isEmpty(src)){
            return;
        }
        src.forEach(i->{
            final String materialId=getMaterialId.apply(i);
            CalcFoodDetailVO dstItem= dst.computeIfAbsent(materialId, v->(CalcFoodDetailVO)new CalcFoodDetailVO()
                    .setInstanceType(EnumFoodDetailType.MATERIAL.getCode())
                    .setInstanceId(materialId)
                    .setInstanceName(getMaterialName.apply(i)));
            BigDecimal val=BigDecimalUtil.tryParseDecimalElseZero(getMaterialWeight.apply(i));
            dstItem.getWeightOptional().add(val);
            if(null==getMealTime){
                return;
            }
            EnumFoodMealTime mealTimeType=EnumFoodMealTime.of(getMealTime.apply(i));
            if(mealTimeType==EnumFoodMealTime.NONE){
                return;
            }
            dstItem.getMapMeals().computeIfAbsent(mealTimeType,v->new CalcFoodMealTimeStatVO().setMealTime(v.getCode()))
                    .getWeightOptional().add(val);

        });
    }
    /**
     * 计算菜肴下的食材重量
     * @param dst
     * @param src
     * @param getDishId
     * @param getDishWeight
     * @param <T>
     */
    protected <T> void calcMaterialWeight4Dishes(Map<String, CalcFoodDetailVO> dst, List<T> src, Function<T,String> getDishId, Function<T,String> getDishWeight){
        calcMaterialWeight4Dishes(dst,src,getDishId,getDishWeight,null);
    }
    protected <T> void calcMaterialWeight4Dishes(Map<String, CalcFoodDetailVO> dst, List<T> src, Function<T,String> getDishId, Function<T,String> getDishWeight,Function<T,Integer> getMealTime){
        if(ShareUtil.XCollection.isEmpty(src)){
            return;
        }
        List<String> dishIds=src.stream().map(getDishId).collect(Collectors.toList());
        Map<String,List<FoodDishesMaterialEntity>> rowsMaterials=ShareUtil.XCollection.groupBy(foodDishesDao.getSubByLeadIds (dishIds,
                FoodDishesMaterialEntity::getFoodDishesId,
                FoodDishesMaterialEntity::getFoodMaterialId,
                FoodDishesMaterialEntity::getWeight),FoodDishesMaterialEntity::getFoodDishesId);
        final BigDecimalOptional box=BigDecimalOptional.create();
        src.forEach(i->{
            final BigDecimal dishWeight=BigDecimalUtil.tryParseDecimalElseZero(getDishWeight.apply(i));
            if(dishWeight.compareTo(BigDecimal.ZERO)==0){
                return;
            }
            List<FoodDishesMaterialEntity> mats=rowsMaterials.get(getDishId.apply(i));
            if(ShareUtil.XCollection.isEmpty(mats)){
                return;
            }
            final BigDecimal totalWeight=mats.stream().map(e->BigDecimalUtil.tryParseDecimalElseZero(e.getWeight()))
                    .reduce(BigDecimal.ZERO,BigDecimalUtil::add);
            mats.forEach(e->{
                CalcFoodDetailVO dstItem= dst.computeIfAbsent(e.getFoodMaterialId(), v->(CalcFoodDetailVO)new CalcFoodDetailVO()
                        .setInstanceType(EnumFoodDetailType.MATERIAL.getCode())
                        .setInstanceId(e.getFoodMaterialId())
                        .setInstanceName(e.getFoodMaterialName()));
                //食材重量=菜肴输入重量*单个食材重量/多个食材总重量
                box.setValue(dishWeight)
                        .mul(BigDecimalUtil.tryParseDecimalElseZero(e.getWeight()))
                        .div(totalWeight);
                dstItem.getWeightOptional().add(box.getValue());
                if(null==getMealTime){
                    return;
                }
                EnumFoodMealTime mealTimeType=EnumFoodMealTime.of(getMealTime.apply(i));
                if(mealTimeType==EnumFoodMealTime.NONE){
                    return;
                }
                dstItem.getMapMeals().computeIfAbsent(mealTimeType,v->new CalcFoodMealTimeStatVO().setMealTime(v.getCode()))
                        .getWeightOptional().add(box.getValue());
            });
        });

    }
    //endregion


    //region 计算营养统计,膳食宝塔
     /**
     * 计算营养统计
     * @param mapMats
     * @return
     */
    protected List<CalcFoodStatVO> calcFoodEnergy(Map<String, CalcFoodDetailVO> mapMats){
        List<CalcFoodStatVO> rst=new ArrayList<>();
        //fill指标id，单位
        EnumFoodNutrient.BASENutrients3.forEach(i->rst.add((CalcFoodStatVO) new CalcFoodStatVO().setInstanceName(i.getName())));
        Map<String, IndicatorInstanceEntity> rowsIndicator=ShareUtil.XCollection.toMap(indicatorInstanceDao.getIndicators4Nutrient(EnumFoodNutrient.BASENutrientNames3,
                IndicatorInstanceEntity::getIndicatorInstanceId,
                IndicatorInstanceEntity::getIndicatorName,
                IndicatorInstanceEntity::getUnit),IndicatorInstanceEntity::getIndicatorName, Function.identity());
        rst.forEach(i-> Optional.ofNullable( rowsIndicator.get(i.getInstanceName())).ifPresent(v->i.setInstanceId(v.getIndicatorInstanceId()).setUnit(v.getUnit())));
        //fill指标推荐值区间
        List<String> indicatorIds=rowsIndicator.values().stream().map(IndicatorInstanceEntity::getIndicatorInstanceId).collect(Collectors.toList());
        Map<String,IndicatorRuleEntity> rowsIndicatorRule=ShareUtil.XCollection.toMap(indicatorRuleDao.getByIndicatorIds(indicatorIds,
                IndicatorRuleEntity::getVariableId,
                IndicatorRuleEntity::getMin,
                IndicatorRuleEntity::getMax),IndicatorRuleEntity::getVariableId,Function.identity());
        rst.forEach(i->Optional.ofNullable(rowsIndicatorRule.get(i.getInstanceId())).ifPresent(v->i.setMin(v.getMin()).setMax(v.getMax())));
        rowsIndicator.clear();
        rowsIndicatorRule.clear();
        if(ShareUtil.XCollection.isEmpty(mapMats)){
            rst.forEach(i->i.setWeight(EMPTYValue).setEnergy(EMPTYValue));
            return rst;
        }
        //sum营养成分含量
        Map<String,CalcFoodStatVO> mapRst=ShareUtil.XCollection.toMap(rst, CalcFoodStatVO::getInstanceId, Function.identity());
        Map<String,List<FoodMaterialNutrientEntity>> rowsNutrient=ShareUtil.XCollection.groupBy(foodMaterialDao.getNutrientsByLeadIdsX(mapMats.keySet(), indicatorIds,
                        FoodMaterialNutrientEntity::getFoodMaterialId,
                        FoodMaterialNutrientEntity::getIndicatorInstanceId,
                        FoodMaterialNutrientEntity::getWeight),FoodMaterialNutrientEntity::getFoodMaterialId);
        final BigDecimalOptional box=BigDecimalOptional.create();
        rowsNutrient.forEach((k,v)-> {
            CalcFoodDetailVO voMat = mapMats.get(k);
            if (null == voMat || voMat.getWeightOptional().isEmpty()) {
                return;
            }
            //营养成分含量=食材重量*每百克营养成分/100克
            v.forEach(vi -> mapRst.get(vi.getIndicatorInstanceId()).getWeightOptional()
                    .add(box.setValue(voMat.getWeightOptional().getValue())
                            .mul(BigDecimalUtil.tryParseDecimalElseZero(vi.getWeight()))
                            .div(BigDecimalUtil.ONEHundred)
                            .getValue()));

        });
        rowsNutrient.clear();
        mapRst.clear();
        box.reset();
        //计算能量和能量占比
        rst.forEach(i->{
            i.getEnergyOptional().setValue(EnumFoodNutrient.of(i.getInstanceName()).calcEnergy(i.getWeightOptional().getValue()));
            box.add(i.getEnergyOptional().getValue());
        });
        rst.forEach(i->{
            i.setWeight(BigDecimalUtil.formatRoundDecimal(i.getWeightOptional().getValue(),NUMBERScale2,false,EMPTYValue));
            i.setEnergy(BigDecimalUtil.formatPercent(i.getEnergyOptional().div(box.getValue()).getValue(), EMPTYValue, NUMBERScale2, true));
        });
        //添加能量汇总
        rst.add((CalcFoodStatVO)new CalcFoodStatVO().setInstanceName(EnumFoodNutrient.ENERGY.getName())
                .setUnit(EnumFoodNutrient.ENERGY.getUnit())
                .setWeight(BigDecimalUtil.formatRoundDecimal(box.getValue(),NUMBERScale2,false,EMPTYValue))
                .setEnergy(EMPTYValue));
        return rst;
    }

    /**
     * 计算膳食宝塔
     * @param mapMats
     * @return
     */
    protected List<CalcFoodStatVO> calcFoodCateg(Map<String, CalcFoodDetailVO> mapMats) {
        Map<String, CalcFoodStatVO> mapRst = ShareUtil.XCollection.toMap(getCategCache().getPrimeCategs(),
                LinkedHashMap::new, CategVO::getCategId, i -> {
                    CalcFoodStatVO dst = new CalcFoodStatVO();
                    dst.setInstanceId(i.getCategId()).setInstanceName(i.getCategName());
                    Optional.ofNullable(i.getExtend()).ifPresent(v -> dst.setUnit(v.getUnit()).setMin(v.getMin()).setMax(v.getMax()));
                    return dst;
                }, false);
        List<FoodMaterialEntity> rowsMaterial = foodMaterialDao.getByIds(mapMats.keySet(), FoodMaterialEntity::getFoodMaterialId, FoodMaterialEntity::getInterveneCategId);
        rowsMaterial.forEach(i -> {
            CategVO cacheCateg = getCategCache().getById(i.getInterveneCategId());
            if (null == cacheCateg) {
                return;
            }
            mapMats.get(i.getFoodMaterialId()).setCategIdLv1(getCategCache().getCategLv1(cacheCateg.getCategIdPath(), cacheCateg.getCategId()));
        });
        rowsMaterial.clear();
        mapMats.values().forEach(i -> {
            CalcFoodStatVO voStat = null;
            if (ShareUtil.XObject.isEmpty(i.getCategIdLv1()) || null == (voStat = mapRst.get(i.getCategIdLv1()))) {
                return;
            }
            voStat.getWeightOptional().add(i.getWeightOptional().getValue());
        });
        List<CalcFoodStatVO> rst = new ArrayList<>(mapRst.values());
        rst.forEach(i->{
            i.setWeight(BigDecimalUtil.formatRoundDecimal(i.getWeightOptional().getValue(),NUMBERScale2,false,EMPTYValue));
        });
        mapRst.clear();
        return rst;
    }

    //endregion



}
