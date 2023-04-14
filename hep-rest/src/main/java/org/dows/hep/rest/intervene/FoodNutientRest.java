package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.response.FoodNutientResponse;
import org.dows.hep.api.intervene.request.SaveFoodNutientRequest;
import org.dows.hep.biz.intervene.FoodNutientBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:饮食关键指标（营养成分）
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "饮食关键指标（营养成分）")
public class FoodNutientRest {
    private final FoodNutientBiz foodNutientBiz;

    /**
    * 获取营养关键指标
    * @param
    * @return
    */
    @ApiOperation("获取营养关键指标")
    @GetMapping("v1/intervene/foodNutient/listFoodNutient")
    public List<FoodNutientResponse> listFoodNutient() {
        return foodNutientBiz.listFoodNutient();
    }

    /**
    * 保存营养关键指标
    * @param
    * @return
    */
    @ApiOperation("保存营养关键指标")
    @PostMapping("v1/intervene/foodNutient/saveFoodNutient")
    public Boolean saveFoodNutient(@RequestBody @Validated SaveFoodNutientRequest saveFoodNutient ) {
        return foodNutientBiz.saveFoodNutient(saveFoodNutient);
    }


}