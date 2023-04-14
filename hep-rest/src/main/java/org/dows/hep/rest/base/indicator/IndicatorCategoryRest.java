package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.biz.base.indicator.IndicatorCategoryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标目录
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标目录", description = "指标目录")
public class IndicatorCategoryRest {
    private final IndicatorCategoryBiz indicatorCategoryBiz;

    /**
    * 创建指标目录
    * @param
    * @return
    */
    @Operation(summary = "创建指标目录")
    @PostMapping("v1/baseIndicator/indicatorCategory/createIndicatorCategory")
    public void createIndicatorCategory(@RequestBody @Validated CreateIndicatorCategoryRequest createIndicatorCategory ) {
        indicatorCategoryBiz.createIndicatorCategory(createIndicatorCategory);
    }

    /**
    * 删除指标目录
    * @param
    * @return
    */
    @Operation(summary = "删除指标目录")
    @DeleteMapping("v1/baseIndicator/indicatorCategory/deleteIndicatorCategory")
    public void deleteIndicatorCategory(@Validated String indicatorCategoryId ) {
        indicatorCategoryBiz.deleteIndicatorCategory(indicatorCategoryId);
    }

    /**
    * 更新指标目录
    * @param
    * @return
    */
    @Operation(summary = "更新指标目录")
    @PutMapping("v1/baseIndicator/indicatorCategory/updateIndicatorCategory")
    public void updateIndicatorCategory(@Validated UpdateIndicatorCategoryRequest updateIndicatorCategory ) {
        indicatorCategoryBiz.updateIndicatorCategory(updateIndicatorCategory);
    }

    /**
    * 查询指标目录
    * @param
    * @return
    */
    @Operation(summary = "查询指标目录")
    @GetMapping("v1/baseIndicator/indicatorCategory/getIndicatorCategory")
    public IndicatorCategoryResponse getIndicatorCategory(@Validated String indicatorCategoryId) {
        return indicatorCategoryBiz.getIndicatorCategory(indicatorCategoryId);
    }

    /**
    * 筛选指标目录
    * @param
    * @return
    */
    @Operation(summary = "筛选指标目录")
    @GetMapping("v1/baseIndicator/indicatorCategory/listIndicatorCategory")
    public List<IndicatorCategoryResponse> listIndicatorCategory(@Validated String appId, @Validated Long pid, @Validated String indicatorCategoryId, @Validated String categoryCode, @Validated String categoryName) {
        return indicatorCategoryBiz.listIndicatorCategory(appId,pid,indicatorCategoryId,categoryCode,categoryName);
    }


}