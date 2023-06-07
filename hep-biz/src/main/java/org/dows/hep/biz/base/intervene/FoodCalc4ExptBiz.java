package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.enums.EnumFoodCalcType;
import org.dows.hep.api.enums.EnumFoodDetailType;
import org.dows.hep.api.user.experiment.request.CalcExptFoodGraphRequest;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CalcExptFoodCookbookResult;
import org.dows.hep.biz.vo.CalcFoodDetailVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生端 饮食计算
 * @author : wuzl
 * @date : 2023/5/19 14:11
 */
@Service
public class FoodCalc4ExptBiz extends FoodCalcBiz {


    protected InterveneCategCache getCategCache(){
        return InterveneCategCache.Instance;
    }


    /**
     * 学生食谱计算
     * @param calcFoodGraph
     * @return
     */
    public CalcExptFoodCookbookResult calcFoodGraph4Expt(CalcExptFoodGraphRequest calcFoodGraph){
        CalcExptFoodCookbookResult rst=new CalcExptFoodCookbookResult();
        if (ShareUtil.XCollection.isEmpty(calcFoodGraph.getDetails())) {
            return rst;
        }
        rst.setDetails(calcFoodGraph.getDetails());
        Map<String, CalcFoodDetailVO> mapMatWeights = calcMaterialWeight4ExptGraph(calcFoodGraph.getDetails());
        if (ShareUtil.XObject.isEmpty(mapMatWeights)) {
            return rst;
        }
        EnumFoodCalcType calcType = EnumFoodCalcType.of(calcFoodGraph.getCalcType());
        if(calcType.calcEnergy()){
            calcFoodEnergy(rst,mapMatWeights);
        }
        if(calcType.calcCateg()){
            rst.setStatCateg(calcFoodCateg(mapMatWeights));
        }
        return rst;

    }

    /**
     * 按学生食谱明细计算营养统计，膳食宝塔，餐次能量统计
     * @param details
     * @return
     */
    public CalcExptFoodCookbookResult calcFoodGraph4ExptCookbook(List<FoodCookbookDetailVO> details){
        return calcFoodGraph4Expt(new CalcExptFoodGraphRequest()
                .setCalcType(EnumFoodCalcType.ALL.getCode())
                .setDetails(details));
    }

    /**
     * 汇总食材数量
     * @param details
     * @return
     */
    private Map<String, CalcFoodDetailVO> calcMaterialWeight4ExptGraph(List<FoodCookbookDetailVO> details){
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
        calcMaterialWeight(rst, mats, FoodCookbookDetailVO::getInstanceId, FoodCookbookDetailVO::getInstanceName, FoodCookbookDetailVO::getWeight,FoodCookbookDetailVO::getMealTime);
        calcMaterialWeight4Dishes(rst, dishes, FoodCookbookDetailVO::getInstanceId, FoodCookbookDetailVO::getWeight,FoodCookbookDetailVO::getMealTime);
        mats.clear();
        dishes.clear();
        return rst;
    }

    /**
     * 计算营养统计
     * @param rst
     * @param mapMats
     */
    private void calcFoodEnergy(CalcExptFoodCookbookResult rst, Map<String, CalcFoodDetailVO> mapMats){
       /* List<CalcFoodStatVO> statEnergy=new ArrayList<>();
        List<CalcFoodMealTimeStatVO> statMealEnergy=new ArrayList<>();
        rst.setStatEnergy(statEnergy).setStatMealEnergy(statMealEnergy);
        //fill指标id，单位
        EnumFoodNutrient.BASENutrients3.forEach(i->statEnergy.add((CalcFoodStatVO) new CalcFoodStatVO().setInstanceName(i.getName()).set));
        Map<String, IndicatorInstanceEntity> rowsIndicator=ShareUtil.XCollection.toMap(indicatorInstanceDao.getIndicators4Nutrient(
                IndicatorInstanceEntity::getIndicatorInstanceId,
                IndicatorInstanceEntity::getIndicatorName,
                IndicatorInstanceEntity::getUnit),IndicatorInstanceEntity::getIndicatorName, Function.identity());
        statEnergy.forEach(i->{
            Optional.ofNullable(rowsIndicator.get(i.getInstanceName()))
                    .ifPresent(v->{
                        i.setInstanceId(v.getIndicatorInstanceId()).setUnit(v.getUnit());
                        rowsIndicator.remove(i.getInstanceName());
                    });
        });
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
        statEnergy.forEach(i->Optional.ofNullable(rowsIndicatorRule.get(i.getInstanceId())).ifPresent(v->i.setMin(v.getMin()).setMax(v.getMax())));
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
            i.setWeight(BigDecimalUtil.formatDecimal(i.getWeightOptional().getValue(NUMBERScale2),EMPTYValue));
            i.setEnergy(BigDecimalUtil.formatPercent(i.getEnergyOptional().div(box.getValue()).getValue(), EMPTYValue, NUMBERScale2, true));
        });
        //添加能量汇总
        rst.add((CalcFoodStatVO)new CalcFoodStatVO().setInstanceName(EnumFoodNutrient.ENERGY.getName())
                .setUnit(EnumFoodNutrient.ENERGY.getUnit())
                .setWeight(BigDecimalUtil.formatDecimal(box.getValue(NUMBERScale2),EMPTYValue))
                .setEnergy(EMPTYValue));
        return rst;*/
    }



}
