package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.enums.EnumFoodCalcType;
import org.dows.hep.api.enums.EnumFoodDetailType;
import org.dows.hep.api.enums.EnumFoodNutrient;
import org.dows.hep.api.user.experiment.request.CalcExptFoodGraphRequest;
import org.dows.hep.biz.cache.CategCache;
import org.dows.hep.biz.cache.CategCacheFactory;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CalcExptFoodCookbookResult;
import org.dows.hep.biz.vo.CalcFoodDetailVO;
import org.dows.hep.biz.vo.CalcFoodMealTimeStatVO;
import org.dows.hep.biz.vo.CalcFoodStatVO;
import org.dows.hep.entity.FoodMaterialNutrientEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.entity.IndicatorRuleEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学生端 饮食计算
 * @author : wuzl
 * @date : 2023/5/19 14:11
 */
@Service
public class FoodCalc4ExptBiz extends FoodCalcBiz {


    @Override
    protected CategCache getCategCache() {
        //TODO 实验缓存
        return CategCacheFactory.FOODMaterial.getCache();
    }

    /**
     * 学生食谱计算
     * @param calcFoodGraph
     * @return
     */
    public CalcExptFoodCookbookResult calcFoodGraph4Expt(CalcExptFoodGraphRequest calcFoodGraph){
        final String appId=calcFoodGraph.getAppId();
        CalcExptFoodCookbookResult rst=new CalcExptFoodCookbookResult();
        if (ShareUtil.XCollection.isEmpty(calcFoodGraph.getDetails())) {
            return rst;
        }
        rst.setDetails(calcFoodGraph.getDetails());
        Map<String, CalcFoodDetailVO> mapMatWeights = calcMaterialWeight4ExptGraph(appId,calcFoodGraph.getDetails());
        if (ShareUtil.XObject.isEmpty(mapMatWeights)) {
            return rst;
        }
        EnumFoodCalcType calcType = EnumFoodCalcType.of(calcFoodGraph.getCalcType());
        if(calcType.calcEnergy()){
            calcFoodEnergy(appId, rst,mapMatWeights);
        }
        if(calcType.calcCateg()){
            rst.setStatCateg(calcFoodCateg(appId, mapMatWeights));
        }
        return rst;

    }

    /**
     * 按学生食谱明细计算营养统计，膳食宝塔，餐次能量统计
     * @param details
     * @return
     */
    public CalcExptFoodCookbookResult calcFoodGraph4ExptCookbook(String appId, List<FoodCookbookDetailVO> details){
        return calcFoodGraph4Expt((CalcExptFoodGraphRequest)(new CalcExptFoodGraphRequest()
                .setCalcType(EnumFoodCalcType.ALL.getCode())
                .setDetails(details)
                .setAppId(appId)));
    }

    /**
     * 汇总食材数量
     * @param details
     * @return
     */
    private Map<String, CalcFoodDetailVO> calcMaterialWeight4ExptGraph(String appId, List<FoodCookbookDetailVO> details){
        Map<String,CalcFoodDetailVO> rst=new HashMap<>();
        if(ShareUtil.XCollection.isEmpty(details)){
            return rst;
        }
        List<FoodCookbookDetailVO> mats=new ArrayList<>(details.size());
        List<FoodCookbookDetailVO> dishes=new ArrayList<>(details.size());
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
        calcMaterialWeight(appId, rst, mats, FoodCookbookDetailVO::getInstanceId, FoodCookbookDetailVO::getInstanceName, FoodCookbookDetailVO::getWeight,FoodCookbookDetailVO::getMealTime);
        calcMaterialWeight4Dishes(appId,rst, dishes, FoodCookbookDetailVO::getInstanceId, FoodCookbookDetailVO::getWeight,FoodCookbookDetailVO::getMealTime);
        mats.clear();
        dishes.clear();
        return rst;
    }

    /**
     * 计算营养统计
     * @param rst
     * @param mapMats
     */
    private void calcFoodEnergy(String appId, CalcExptFoodCookbookResult rst, Map<String, CalcFoodDetailVO> mapMats){
        List<CalcFoodStatVO> statEnergy=new ArrayList<>();
        rst.setStatEnergy(statEnergy).setStatMealEnergy(Collections.emptyList());
        //fill指标id，单位
        EnumFoodNutrient.BASENutrients3.forEach(i->statEnergy.add(((CalcFoodStatVO) new CalcFoodStatVO().setInstanceName(i.getName())).setMapMeals(new HashMap<>())));
        Map<String, IndicatorInstanceEntity> rowsIndicator=ShareUtil.XCollection.toMap(indicatorInstanceDao.getIndicators4Nutrient(
                IndicatorInstanceEntity::getIndicatorInstanceId,
                IndicatorInstanceEntity::getIndicatorName,
                IndicatorInstanceEntity::getUnit),IndicatorInstanceEntity::getIndicatorName, Function.identity());
        if(ShareUtil.XCollection.isEmpty(rowsIndicator)){
            statEnergy.forEach(i->i.setWeight(EMPTYValue).setEnergy(EMPTYValue));
            return;
        }
        statEnergy.forEach(i->{
            Optional.ofNullable(rowsIndicator.get(i.getInstanceName()))
                    .ifPresent(rowIndicator->{
                        i.setInstanceId(rowIndicator.getIndicatorInstanceId()).setUnit(rowIndicator.getUnit());
                        rowsIndicator.remove(i.getInstanceName());
                    });
        });
        rowsIndicator.remove("能量");
        rowsIndicator.values().forEach(i->statEnergy.add((CalcFoodStatVO)new CalcFoodStatVO()
                .setInstanceId(i.getIndicatorInstanceId())
                .setInstanceName(i.getIndicatorName())
                .setUnit(i.getUnit())));
        //fill指标推荐值区间
        List<String> indicatorIds=statEnergy.stream().map(CalcFoodStatVO::getInstanceId).collect(Collectors.toList());
        Map<String, IndicatorRuleEntity> rowsIndicatorRule=ShareUtil.XCollection.toMap(indicatorRuleDao.getByIndicatorIds(indicatorIds,
                IndicatorRuleEntity::getVariableId,
                IndicatorRuleEntity::getMin,
                IndicatorRuleEntity::getMax),IndicatorRuleEntity::getVariableId,Function.identity());
        statEnergy.forEach(i->Optional.ofNullable(rowsIndicatorRule.get(i.getInstanceId())).ifPresent(rowRule->i.setMin(rowRule.getMin()).setMax(rowRule.getMax())));
        rowsIndicator.clear();
        rowsIndicatorRule.clear();
        if(ShareUtil.XCollection.isEmpty(mapMats)){
            statEnergy.forEach(i->i.setWeight(EMPTYValue).setEnergy(EMPTYValue));
            return;
        }
        //sum营养成分含量
        Map<String,CalcFoodStatVO> mapEnergy=ShareUtil.XCollection.toMap(statEnergy, CalcFoodStatVO::getInstanceId, Function.identity());
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
            v.forEach(rowNutrient ->{
                CalcFoodStatVO voEnergy=mapEnergy.get(rowNutrient.getIndicatorInstanceId());
                if(null==voEnergy){
                    return;
                }

                //营养成分含量=食材重量*每百克营养成分/100克
                box.setValue(voMat.getWeightOptional().getValue())
                        .mul(BigDecimalUtil.tryParseDecimalElseZero(rowNutrient.getWeight()))
                        .div(BigDecimalUtil.ONEHundred);
                voEnergy.getWeightOptional().add(box.getValue());
                if(null==voEnergy.getMapMeals()||ShareUtil.XCollection.isEmpty(voMat.getMapMeals())){
                    return;
                }
                voMat.getMapMeals().values().forEach(mealMat->{
                    box.setValue(mealMat.getWeightOptional().getValue())
                            .mul(BigDecimalUtil.tryParseDecimalElseZero(rowNutrient.getWeight()))
                            .div(BigDecimalUtil.ONEHundred);
                    voEnergy.getMapMeals().computeIfAbsent(mealMat.getMealTime(),key->new CalcFoodMealTimeStatVO().setMealTime(key))
                            .getWeightOptional().add(box.getValue());
                });
            });
        });
        rowsNutrient.clear();
        mapEnergy.clear();
        box.reset();
        Map<Integer,CalcFoodMealTimeStatVO> mapMealEnergy=new LinkedHashMap<>();
        //计算能量和能量占比
        statEnergy.forEach(i->{
            if(null==i.getMapMeals()){
                return;
            }
            EnumFoodNutrient enumNutrient=EnumFoodNutrient.of(i.getInstanceName());
            i.getEnergyOptional().setValue(enumNutrient.calcEnergy(i.getWeightOptional().getValue()));
            box.add(i.getEnergyOptional().getValue());
            i.getMapMeals().values().forEach(mealNutrient->{
                mapMealEnergy.computeIfAbsent(mealNutrient.getMealTime(),key->new CalcFoodMealTimeStatVO().setMealTime(key))
                        .getEnergyOptional().add(enumNutrient.calcEnergy(mealNutrient.getWeightOptional().getValue()) );
            });
        });
        final String totalEnergy=BigDecimalUtil.formatRoundDecimal(box.getValue(),NUMBERScale2,false, EMPTYValue);
        rst.setEnergy(totalEnergy);
        //营养成分能量占比
        statEnergy.forEach(i->{
            i.setWeight(BigDecimalUtil.formatRoundDecimal(i.getWeightOptional().getValue(),NUMBERScale2,false,EMPTYValue));
            i.setEnergy(BigDecimalUtil.formatPercent(i.getEnergyOptional().div(box.getValue()).getValue(), EMPTYValue, NUMBERScale2, true));
        });
        //餐次能量占比
        mapMealEnergy.values().forEach(i->{
            i.setEnergy(BigDecimalUtil.formatRoundDecimal(i.getEnergyOptional().getValue(),NUMBERScale2,false,EMPTYValue ));
            i.setEnergyRate(BigDecimalUtil.formatPercent(i.getEnergyOptional().div(box.getValue()).getValue(), EMPTYValue, NUMBERScale2, true));
        });
        //添加能量汇总
        statEnergy.add(EnumFoodNutrient.BASENutrients3.size()-1, (CalcFoodStatVO)new CalcFoodStatVO().setInstanceName(EnumFoodNutrient.ENERGY.getName())
                .setUnit(EnumFoodNutrient.ENERGY.getUnit())
                .setWeight(totalEnergy)
                .setEnergy(EMPTYValue));
        rst.setStatMealEnergy(new ArrayList<>( mapMealEnergy.values()));

    }



}
