package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.biz.base.indicator.IndicatorCategoryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标类别
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标类别", description = "指标类别")
public class IndicatorCategoryRest {
    private final IndicatorCategoryBiz indicatorCategoryBiz;

    /**
    * 创建指标类别
    * @param
    * @return
    */
    @Operation(summary = "创建指标类别")
    @PostMapping("v1/baseIndicator/indicatorCategory/createIndicatorCategory")
    public void createIndicatorCategory(@RequestBody @Validated CreateIndicatorCategoryRequest createIndicatorCategory ) {
        indicatorCategoryBiz.createIndicatorCategory(createIndicatorCategory);
    }

    /**
    * 删除指标类别
    * @param
    * @return
    */
    @Operation(summary = "删除指标类别")
    @DeleteMapping("v1/baseIndicator/indicatorCategory/deleteIndicatorCategory")
    public void deleteIndicatorCategory(@Validated String indicatorCategoryId ) {
        indicatorCategoryBiz.deleteIndicatorCategory(indicatorCategoryId);
    }

    /**
    * 更新指标类别
    * @param
    * @return
    */
    @Operation(summary = "更新指标类别")
    @PutMapping("v1/baseIndicator/indicatorCategory/updateIndicatorCategory")
    public void updateIndicatorCategory(@Validated UpdateIndicatorCategoryRequest updateIndicatorCategory ) {
        indicatorCategoryBiz.updateIndicatorCategory(updateIndicatorCategory);
    }

    /**
    * 查询指标类别
    * @param
    * @return
    */
    @Operation(summary = "查询指标类别")
    @GetMapping("v1/baseIndicator/indicatorCategory/getIndicatorCategory")
    public IndicatorCategoryResponse getIndicatorCategory(@Validated String indicatorCategoryId) {
        return indicatorCategoryBiz.getIndicatorCategory(indicatorCategoryId);
    }

    /**
    * 筛选指标类别
    * @param
    * @return
    */
    @Operation(summary = "筛选指标类别")
    @GetMapping("v1/baseIndicator/indicatorCategory/listIndicatorCategory")
    public List<IndicatorCategoryResponse> listIndicatorCategory(@Validated String appId, @Validated Long pid, @Validated String indicatorCategoryId, @Validated String categoryCode, @Validated String categoryName) {
        return indicatorCategoryBiz.listIndicatorCategory(appId,pid,indicatorCategoryId,categoryCode,categoryName);
    }

//    /**
//    * 分页筛选指标类别
//    * @param
//    * @return
//    */
//    @Operation(summary = "分页筛选指标类别")

    /**
    * 一键同步（非常复杂）
    * @param
    * @return
    */
    @Operation(summary = "一键同步（非常复杂）")
    @PostMapping("v1/baseIndicator/indicatorCategory/sync")
    public void sync(@RequestBody @Validated String appId ) {
        indicatorCategoryBiz.sync(appId);
    }


}