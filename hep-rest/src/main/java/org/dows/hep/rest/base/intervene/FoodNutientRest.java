package org.dows.hep.rest.base.intervene;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.SaveFoodNutientRequest;
import org.dows.hep.api.base.intervene.response.FoodNutientResponse;
import org.dows.hep.biz.base.intervene.FoodNutientBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:干预:饮食关键指标（营养成分）
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "饮食关键指标（营养成分）", description = "饮食关键指标（营养成分）")
public class FoodNutientRest {
    private final FoodNutientBiz foodNutientBiz;

    /**
    * 获取营养关键指标
    * @param
    * @return
    */
    @Operation(summary = "获取营养关键指标")
    @GetMapping("v1/baseIntervene/foodNutient/listFoodNutient")
    public List<FoodNutientResponse> listFoodNutient() {
        return foodNutientBiz.listFoodNutient();
    }

    /**
    * 保存营养关键指标
    * @param
    * @return
    */
    @Operation(summary = "保存营养关键指标")
    @PostMapping("v1/baseIntervene/foodNutient/saveFoodNutient")
    public Boolean saveFoodNutient(@RequestBody @Validated SaveFoodNutientRequest saveFoodNutient ) {
        return foodNutientBiz.saveFoodNutient(saveFoodNutient);
    }


}