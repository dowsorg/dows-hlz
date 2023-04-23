package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorInstanceRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorInstanceRequest;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponse;
import org.dows.hep.biz.base.indicator.IndicatorInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标实例
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标实例", description = "指标实例")
public class IndicatorInstanceRest {
    private final IndicatorInstanceBiz indicatorInstanceBiz;

    /**
    * 创建指标实例
    * @param
    * @return
    */
    @Operation(summary = "创建指标实例")
    @PostMapping("v1/baseIndicator/indicatorInstance/createIndicatorInstance")
    public void createIndicatorInstance(@RequestBody @Validated CreateIndicatorInstanceRequest createIndicatorInstance ) {
        indicatorInstanceBiz.createIndicatorInstance(createIndicatorInstance);
    }

    /**
    * 删除指标
    * @param
    * @return
    */
    @Operation(summary = "删除指标")
    @DeleteMapping("v1/baseIndicator/indicatorInstance/deleteIndicatorInstance")
    public void deleteIndicatorInstance(@Validated String indicatorInstanceId ) {
        indicatorInstanceBiz.deleteIndicatorInstance(indicatorInstanceId);
    }

    /**
    * 更新指标
    * @param
    * @return
    */
    @Operation(summary = "更新指标")
    @PutMapping("v1/baseIndicator/indicatorInstance/updateIndicatorInstance")
    public void updateIndicatorInstance(@Validated UpdateIndicatorInstanceRequest updateIndicatorInstance ) {
        indicatorInstanceBiz.updateIndicatorInstance(updateIndicatorInstance);
    }

    /**
    * 批量更新指标
    * @param
    * @return
    */
    @Operation(summary = "批量更新指标")
    @PutMapping("v1/baseIndicator/indicatorInstance/batchUpdateIndicatorInstance")
    public void batchUpdateIndicatorInstance(@Validated List<UpdateIndicatorInstanceRequest> updateIndicatorInstance ) {
        indicatorInstanceBiz.batchUpdateIndicatorInstance(updateIndicatorInstance);
    }

    /**
    * 查询指标
    * @param
    * @return
    */
    @Operation(summary = "查询指标")
    @GetMapping("v1/baseIndicator/indicatorInstance/getIndicatorInstance")
    public IndicatorInstanceResponse getIndicatorInstance(@Validated String indicatorInstanceId) {
        return indicatorInstanceBiz.getIndicatorInstance(indicatorInstanceId);
    }

    /**
    * 筛选指标
    * @param
    * @return
    */
    @Operation(summary = "筛选指标")
    @GetMapping("v1/baseIndicator/indicatorInstance/listIndicatorInstance")
    public List<IndicatorInstanceResponse> listIndicatorInstance(@Validated String appId, @Validated Integer core, @Validated Integer food, @Validated String indicatorCategoryId) {
        return indicatorInstanceBiz.listIndicatorInstance(appId,core,food,indicatorCategoryId);
    }

    /**
    * 分页筛选指标
    * @param
    * @return
    */
    @Operation(summary = "分页筛选指标")
    @GetMapping("v1/baseIndicator/indicatorInstance/pageIndicatorInstance")
    public String pageIndicatorInstance(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated Integer core, @Validated Integer food, @Validated String indicatorCategoryId) {
        return indicatorInstanceBiz.pageIndicatorInstance(pageNo,pageSize,appId,core,food,indicatorCategoryId);
    }


}