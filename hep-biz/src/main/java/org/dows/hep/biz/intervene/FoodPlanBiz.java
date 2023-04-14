package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.CalcFoodGraphRequest;
import org.dows.hep.api.intervene.response.FoodGraphResponse;
import org.dows.hep.api.intervene.request.FindFoodRequest;
import org.dows.hep.api.intervene.response.FoodDishesResponse;
import org.dows.hep.api.intervene.response.FoodDishesInfoResponse;
import org.dows.hep.api.intervene.request.SaveFoodDishesRequest;
import org.dows.hep.api.intervene.request.DelFoodDishesRequest;
import org.dows.hep.api.intervene.request.SetFoodDishesStateRequest;
import org.dows.hep.api.intervene.request.FindFoodRequest;
import org.dows.hep.api.intervene.response.FoodCookBookResponse;
import org.dows.hep.api.intervene.response.FoodCookBookInfoResponse;
import org.dows.hep.api.intervene.request.SaveFoodCookbookRequest;
import org.dows.hep.api.intervene.request.DelFoodCookbookRequest;
import org.dows.hep.api.intervene.request.SetFoodCookbookStateRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:饮食方案(菜肴菜谱)
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
public class FoodPlanBiz{
    /**
    * @param
    * @return
    * @说明: 计算能量占比、膳食宝塔
    * @关联表: food_material_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public FoodGraphResponse calcFoodGraph(CalcFoodGraphRequest calcFoodGraph ) {
        return new FoodGraphResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取菜肴列表
    * @关联表: food_dishes
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public FoodDishesResponse pageFoodDishes(FindFoodRequest findFood ) {
        return new FoodDishesResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取菜肴信息
    * @关联表: food_dishes,food_dishes_material,food_dishes_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public FoodDishesInfoResponse getFoodDishes(String foodDishesId ) {
        return new FoodDishesInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 保存菜肴
    * @关联表: food_dishes,food_dishes_material,food_dishes_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public Boolean saveFoodDishes(SaveFoodDishesRequest saveFoodDishes ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除菜肴
    * @关联表: food_dishes,food_dishes_material,food_dishes_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public Boolean delFoodDishes(DelFoodDishesRequest delFoodDishes ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用菜肴
    * @关联表: food_dishes
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public Boolean setFoodDishesState(SetFoodDishesStateRequest setFoodDishesState ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取菜谱列表
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public FoodCookBookResponse pageFoodCookbook(FindFoodRequest findFood ) {
        return new FoodCookBookResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取菜谱详细信息
    * @关联表: food_cookbook,food_cookbook_detail,food_cookbook_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public FoodCookBookInfoResponse getFoodCookbook(String foodCookbookId ) {
        return new FoodCookBookInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 保存菜谱
    * @关联表: food_cookbook,food_cookbook_detail,food_cookbook_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public Boolean saveFoodCookbook(SaveFoodCookbookRequest saveFoodCookbook ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除菜谱
    * @关联表: food_cookbook,food_cookbook_detail,food_cookbook_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public Boolean delFoodCookbook(DelFoodCookbookRequest delFoodCookbook ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用菜谱
    * @关联表: food_cookbook
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public Boolean setFoodCookbookState(SetFoodCookbookStateRequest setFoodCookbookState ) {
        return Boolean.FALSE;
    }
}