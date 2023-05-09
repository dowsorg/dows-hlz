package org.dows.hep.rest.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.*;
import org.dows.hep.biz.base.intervene.FoodPlanBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:干预:饮食方案(菜肴菜谱)
* @folder 饮食方案(菜肴菜谱)
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "饮食方案(菜肴菜谱)", description = "饮食方案(菜肴菜谱)")
public class FoodPlanRest {
    private final FoodPlanBiz foodPlanBiz;

    /**
    * 计算能量占比、膳食宝塔
    * @param
    * @return
    */
    @Operation(summary = "计算能量占比、膳食宝塔")
    @PostMapping("v1/baseIntervene/foodPlan/calcFoodGraph")
    public FoodGraphResponse calcFoodGraph(@RequestBody @Validated CalcFoodGraphRequest calcFoodGraph ) {
        return foodPlanBiz.calcFoodGraph(calcFoodGraph);
    }

    /**
    * 获取菜肴列表
    * @param
    * @return
    */
    @Operation(summary = "获取菜肴列表")
    @PostMapping("v1/baseIntervene/foodPlan/pageFoodDishes")
    public Page<FoodDishesResponse> pageFoodDishes(@RequestBody @Validated FindFoodRequest findFood ) {
        return foodPlanBiz.pageFoodDishes(findFood);
    }

    /**
    * 获取菜肴信息
    * @param
    * @return
    */
    @Operation(summary = "获取菜肴信息")
    @GetMapping("v1/baseIntervene/foodPlan/getFoodDishes")
    public FoodDishesInfoResponse getFoodDishes(@Validated String foodDishesId) {
        return foodPlanBiz.getFoodDishes(foodDishesId);
    }

    /**
    * 保存菜肴
    * @param
    * @return
    */
    @Operation(summary = "保存菜肴")
    @PostMapping("v1/baseIntervene/foodPlan/saveFoodDishes")
    public Boolean saveFoodDishes(@RequestBody @Validated SaveFoodDishesRequest saveFoodDishes ) {
        return foodPlanBiz.saveFoodDishes(saveFoodDishes);
    }

    /**
    * 删除菜肴
    * @param
    * @return
    */
    @Operation(summary = "删除菜肴")
    @DeleteMapping("v1/baseIntervene/foodPlan/delFoodDishes")
    public Boolean delFoodDishes(@RequestBody @Validated DelFoodDishesRequest delFoodDishes ) {
        return foodPlanBiz.delFoodDishes(delFoodDishes);
    }

    /**
     * 删除菜肴下的食材
     *
     * @param delRefItem
     * @return
     */
    @Operation(summary = "删除菜肴下的食材")
    @DeleteMapping("v1/baseIntervene/foodPlan/delFoodDishesRefItem")
    public Boolean delFoodDishesRefItem(@RequestBody @Validated DelRefItemRequest delRefItem ) {
        return foodPlanBiz.delFoodDishesRefItem(delRefItem);
    }

    /**
    * 启用、禁用菜肴
    * @param
    * @return
    */
    @Operation(summary = "启用、禁用菜肴")
    @PostMapping("v1/baseIntervene/foodPlan/setFoodDishesState")
    public Boolean setFoodDishesState(@RequestBody @Validated SetFoodDishesStateRequest setFoodDishesState ) {
        return foodPlanBiz.setFoodDishesState(setFoodDishesState);
    }

    /**
    * 获取菜谱列表
    * @param
    * @return
    */
    @Operation(summary = "获取菜谱列表")
    @PostMapping("v1/baseIntervene/foodPlan/pageFoodCookbook")
    public Page<FoodCookBookResponse> pageFoodCookbook(@RequestBody @Validated FindFoodRequest findFood ) {
        return foodPlanBiz.pageFoodCookbook(findFood);
    }

    /**
    * 获取菜谱详细信息
    * @param
    * @return
    */
    @Operation(summary = "获取菜谱详细信息")
    @GetMapping("v1/baseIntervene/foodPlan/getFoodCookbook")
    public FoodCookBookInfoResponse getFoodCookbook(@Validated String foodCookbookId) {
        return foodPlanBiz.getFoodCookbook(foodCookbookId);
    }

    /**
    * 保存菜谱
    * @param
    * @return
    */
    @Operation(summary = "保存菜谱")
    @PostMapping("v1/baseIntervene/foodPlan/saveFoodCookbook")
    public Boolean saveFoodCookbook(@RequestBody @Validated SaveFoodCookbookRequest saveFoodCookbook ) {
        return foodPlanBiz.saveFoodCookbook(saveFoodCookbook);
    }

    /**
    * 删除菜谱
    * @param
    * @return
    */
    @Operation(summary = "删除菜谱")
    @DeleteMapping("v1/baseIntervene/foodPlan/delFoodCookbook")
    public Boolean delFoodCookbook(@RequestBody @Validated DelFoodCookbookRequest delFoodCookbook ) {
        return foodPlanBiz.delFoodCookbook(delFoodCookbook);
    }

    /**
     * 删除菜谱下的食材菜肴
     * @param delRefItem
     * @return
     */

    @Operation(summary = "删除菜谱下的食材菜肴")
    @DeleteMapping("v1/baseIntervene/foodPlan/delFoodCookbookRefItem")
    public Boolean delFoodCookbookRefItem(@RequestBody @Validated DelRefItemRequest delRefItem ) {
        return foodPlanBiz.delFoodCookbookRefItem(delRefItem);
    }



    /**
    * 启用、禁用菜谱
    * @param
    * @return
    */
    @Operation(summary = "启用、禁用菜谱")
    @PostMapping("v1/baseIntervene/foodPlan/setFoodCookbookState")
    public Boolean setFoodCookbookState(@RequestBody @Validated SetFoodCookbookStateRequest setFoodCookbookState ) {
        return foodPlanBiz.setFoodCookbookState(setFoodCookbookState);
    }


}