package org.dows.hep.rest.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.FoodMaterialInfoResponse;
import org.dows.hep.api.base.intervene.response.FoodMaterialResponse;
import org.dows.hep.biz.base.intervene.FoodMaterialBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:干预:食材
* @folder admin-hep/食材
 *
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "食材", description = "食材")
public class FoodMaterialRest {
    private final FoodMaterialBiz foodMaterialBiz;

    /**
    * 获取食材列表
    * @param
    * @return
    */
    @Operation(summary = "获取食材列表")
    @PostMapping("v1/baseIntervene/foodMaterial/pageFoodMaterial")
    public Page<FoodMaterialResponse> pageFoodMaterial(@RequestBody @Validated FindFoodRequest findFood ) {
        return foodMaterialBiz.pageFoodMaterial(findFood);
    }

    /**
    * 获取食材详细信息
    * @param
    * @return
    */
    @Operation(summary = "获取食材详细信息")
    @GetMapping("v1/baseIntervene/foodMaterial/getFoodMaterial")
    public FoodMaterialInfoResponse getFoodMaterial(@Validated String appId,  @Validated String foodMaterialId) {
        return foodMaterialBiz.getFoodMaterial(appId, foodMaterialId);
    }

    /**
    * 保存食材
    * @param
    * @return
    */
    @Operation(summary = "保存食材")
    @PostMapping("v1/baseIntervene/foodMaterial/saveFoodMaterial")
    public Boolean saveFoodMaterial(@RequestBody @Validated SaveFoodMaterialRequest saveFoodMaterial ) {
        return foodMaterialBiz.saveFoodMaterial(saveFoodMaterial);
    }

    /**
    * 删除食材
    * @param
    * @return
    */
    @Operation(summary = "删除食材")
    @DeleteMapping("v1/baseIntervene/foodMaterial/delFoodMaterial")
    public Boolean delFoodMaterial(@RequestBody @Validated DelFoodMaterialRequest delFoodMaterial ) {
        return foodMaterialBiz.delFoodMaterial(delFoodMaterial);
    }

    @Operation(summary = "删除食材关联指标")
    @DeleteMapping("v1/baseIntervene/foodMaterial/delRefIndicator")
    public Boolean delRefIndicator(@RequestBody @Validated DelRefIndicatorRequest delRefIndicator ) {
        return foodMaterialBiz.delRefIndicator(delRefIndicator);
    }


    @Operation(summary = "启用禁用食材")
    @PostMapping("v1/baseIntervene/foodMaterial/setFoodMaterialState")
    public Boolean setFoodMaterialState(@RequestBody @Validated SetFoodMaterialStateRequest setFoodMaterialState ){
        return foodMaterialBiz.setFoodMaterialState(setFoodMaterialState);
    }


}