package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindFoodRequest;
import org.dows.hep.api.intervene.response.FoodMaterialResponse;
import org.dows.hep.api.intervene.response.FoodMaterialInfoResponse;
import org.dows.hep.api.intervene.request.SaveFoodMaterialRequest;
import org.dows.hep.api.intervene.request.DelFoodMaterialRequest;
import org.dows.hep.biz.intervene.FoodMaterialBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:食材
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "食材")
public class FoodMaterialRest {
    private final FoodMaterialBiz foodMaterialBiz;

    /**
    * 获取食材列表
    * @param
    * @return
    */
    @ApiOperation("获取食材列表")
    @PostMapping("v1/intervene/foodMaterial/pageFoodMaterial")
    public FoodMaterialResponse pageFoodMaterial(@RequestBody @Validated FindFoodRequest findFood ) {
        return foodMaterialBiz.pageFoodMaterial(findFood);
    }

    /**
    * 获取食材详细信息
    * @param
    * @return
    */
    @ApiOperation("获取食材详细信息")
    @GetMapping("v1/intervene/foodMaterial/getFoodMaterial")
    public FoodMaterialInfoResponse getFoodMaterial(@Validated String foodMaterialId) {
        return foodMaterialBiz.getFoodMaterial(foodMaterialId);
    }

    /**
    * 保存食材
    * @param
    * @return
    */
    @ApiOperation("保存食材")
    @PostMapping("v1/intervene/foodMaterial/saveFoodMaterial")
    public Boolean saveFoodMaterial(@RequestBody @Validated SaveFoodMaterialRequest saveFoodMaterial ) {
        return foodMaterialBiz.saveFoodMaterial(saveFoodMaterial);
    }

    /**
    * 删除食材
    * @param
    * @return
    */
    @ApiOperation("删除食材")
    @DeleteMapping("v1/intervene/foodMaterial/delFoodMaterial")
    public Boolean delFoodMaterial(@Validated DelFoodMaterialRequest delFoodMaterial ) {
        return foodMaterialBiz.delFoodMaterial(delFoodMaterial);
    }


}