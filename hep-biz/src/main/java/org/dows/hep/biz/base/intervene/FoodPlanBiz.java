package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.*;
import org.springframework.stereotype.Service;

/**
* @description project descr:干预:饮食方案(菜肴菜谱)
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class FoodPlanBiz{
    /**
    * @param
    * @return
    * @说明: 计算能量占比、膳食宝塔
    * @关联表: food_material_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean setFoodCookbookState(SetFoodCookbookStateRequest setFoodCookbookState ) {
        return Boolean.FALSE;
    }
}