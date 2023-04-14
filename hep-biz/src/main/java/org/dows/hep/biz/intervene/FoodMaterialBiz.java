package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindFoodRequest;
import org.dows.hep.api.intervene.response.FoodMaterialResponse;
import org.dows.hep.api.intervene.response.FoodMaterialInfoResponse;
import org.dows.hep.api.intervene.request.SaveFoodMaterialRequest;
import org.dows.hep.api.intervene.request.DelFoodMaterialRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:食材
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
public class FoodMaterialBiz{
    /**
    * @param
    * @return
    * @说明: 获取食材列表
    * @关联表: food_material
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public FoodMaterialResponse pageFoodMaterial(FindFoodRequest findFood ) {
        return new FoodMaterialResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取食材详细信息
    * @关联表: food_material,food_material_indicator,food_material_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public FoodMaterialInfoResponse getFoodMaterial(String foodMaterialId ) {
        return new FoodMaterialInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 保存食材
    * @关联表: food_material,food_material_indicator,food_material_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public Boolean saveFoodMaterial(SaveFoodMaterialRequest saveFoodMaterial ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除食材
    * @关联表: food_material,food_material_indicator,food_material_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public Boolean delFoodMaterial(DelFoodMaterialRequest delFoodMaterial ) {
        return Boolean.FALSE;
    }
}