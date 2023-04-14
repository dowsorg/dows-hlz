package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
import org.dows.hep.biz.intervene.FoodPlanBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:饮食方案(菜肴菜谱)
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "饮食方案(菜肴菜谱)")
public class FoodPlanRest {
    private final FoodPlanBiz foodPlanBiz;

    /**
    * 计算能量占比、膳食宝塔
    * @param
    * @return
    */
    @ApiOperation("计算能量占比、膳食宝塔")
    @PostMapping("v1/intervene/foodPlan/calcFoodGraph")
    public FoodGraphResponse calcFoodGraph(@RequestBody @Validated CalcFoodGraphRequest calcFoodGraph ) {
        return foodPlanBiz.calcFoodGraph(calcFoodGraph);
    }

    /**
    * 获取菜肴列表
    * @param
    * @return
    */
    @ApiOperation("获取菜肴列表")
    @PostMapping("v1/intervene/foodPlan/pageFoodDishes")
    public FoodDishesResponse pageFoodDishes(@RequestBody @Validated FindFoodRequest findFood ) {
        return foodPlanBiz.pageFoodDishes(findFood);
    }

    /**
    * 获取菜肴信息
    * @param
    * @return
    */
    @ApiOperation("获取菜肴信息")
    @GetMapping("v1/intervene/foodPlan/getFoodDishes")
    public FoodDishesInfoResponse getFoodDishes(@Validated String foodDishesId) {
        return foodPlanBiz.getFoodDishes(foodDishesId);
    }

    /**
    * 保存菜肴
    * @param
    * @return
    */
    @ApiOperation("保存菜肴")
    @PostMapping("v1/intervene/foodPlan/saveFoodDishes")
    public Boolean saveFoodDishes(@RequestBody @Validated SaveFoodDishesRequest saveFoodDishes ) {
        return foodPlanBiz.saveFoodDishes(saveFoodDishes);
    }

    /**
    * 删除菜肴
    * @param
    * @return
    */
    @ApiOperation("删除菜肴")
    @DeleteMapping("v1/intervene/foodPlan/delFoodDishes")
    public Boolean delFoodDishes(@Validated DelFoodDishesRequest delFoodDishes ) {
        return foodPlanBiz.delFoodDishes(delFoodDishes);
    }

    /**
    * 启用、禁用菜肴
    * @param
    * @return
    */
    @ApiOperation("启用、禁用菜肴")
    @PostMapping("v1/intervene/foodPlan/setFoodDishesState")
    public Boolean setFoodDishesState(@RequestBody @Validated SetFoodDishesStateRequest setFoodDishesState ) {
        return foodPlanBiz.setFoodDishesState(setFoodDishesState);
    }

    /**
    * 获取菜谱列表
    * @param
    * @return
    */
    @ApiOperation("获取菜谱列表")
    @PostMapping("v1/intervene/foodPlan/pageFoodCookbook")
    public FoodCookBookResponse pageFoodCookbook(@RequestBody @Validated FindFoodRequest findFood ) {
        return foodPlanBiz.pageFoodCookbook(findFood);
    }

    /**
    * 获取菜谱详细信息
    * @param
    * @return
    */
    @ApiOperation("获取菜谱详细信息")
    @GetMapping("v1/intervene/foodPlan/getFoodCookbook")
    public FoodCookBookInfoResponse getFoodCookbook(@Validated String foodCookbookId) {
        return foodPlanBiz.getFoodCookbook(foodCookbookId);
    }

    /**
    * 保存菜谱
    * @param
    * @return
    */
    @ApiOperation("保存菜谱")
    @PostMapping("v1/intervene/foodPlan/saveFoodCookbook")
    public Boolean saveFoodCookbook(@RequestBody @Validated SaveFoodCookbookRequest saveFoodCookbook ) {
        return foodPlanBiz.saveFoodCookbook(saveFoodCookbook);
    }

    /**
    * 删除菜谱
    * @param
    * @return
    */
    @ApiOperation("删除菜谱")
    @DeleteMapping("v1/intervene/foodPlan/delFoodCookbook")
    public Boolean delFoodCookbook(@Validated DelFoodCookbookRequest delFoodCookbook ) {
        return foodPlanBiz.delFoodCookbook(delFoodCookbook);
    }

    /**
    * 启用、禁用菜谱
    * @param
    * @return
    */
    @ApiOperation("启用、禁用菜谱")
    @PostMapping("v1/intervene/foodPlan/setFoodCookbookState")
    public Boolean setFoodCookbookState(@RequestBody @Validated SetFoodCookbookStateRequest setFoodCookbookState ) {
        return foodPlanBiz.setFoodCookbookState(setFoodCookbookState);
    }


}