package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.BatchCreateOrUpdateIndicatorCategoryRequest;
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
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标类别", description = "指标类别")
public class IndicatorCategoryRest {
    private final IndicatorCategoryBiz indicatorCategoryBiz;


    @Operation(summary = "批量创建或修改指标类别")
    @PostMapping("v1/baseIndicator/indicatorCategory/batchCreateOrUpdateRs")
    public void batchCreateOrUpdateRs(@RequestBody @Validated BatchCreateOrUpdateIndicatorCategoryRequest batchCreateOrUpdateIndicatorCategoryRequest) throws InterruptedException {
        indicatorCategoryBiz.batchCreateOrUpdateRs(batchCreateOrUpdateIndicatorCategoryRequest);
    }


    @Operation(summary = "删除指标类别")
    @DeleteMapping("v1/baseIndicator/indicatorCategory/delete")
    public void delete(@RequestParam @Validated String indicatorCategoryId) {
        indicatorCategoryBiz.delete(indicatorCategoryId);
    }

    @Operation(summary = "根据pid查询出所有的指标类别")
    @GetMapping("v1/baseIndicator/indicatorCategory/getByPid")
    public List<IndicatorCategoryResponse> getByPid(
        @RequestParam @Validated String appId,
        @RequestParam(required = false) @Validated String pid
        ) {
        return indicatorCategoryBiz.getByPid(appId, pid);
    }

//    @PostMapping("v1/baseIndicator/indicatorCategory/createIndicatorCategory")
//    public void createIndicatorCategory(@RequestBody @Validated CreateIndicatorCategoryRequest createIndicatorCategory) throws InterruptedException {
//        indicatorCategoryBiz.createIndicatorCategory(createIndicatorCategory);
//    }
//
//    @PutMapping("v1/baseIndicator/indicatorCategory/updateIndicatorCategory")
//    public void updateIndicatorCategory(@Validated UpdateIndicatorCategoryRequest updateIndicatorCategory ) {
//        indicatorCategoryBiz.updateIndicatorCategory(updateIndicatorCategory);
//    }
//
//
//    @GetMapping("v1/baseIndicator/indicatorCategory/getIndicatorCategory")
//    public IndicatorCategoryResponse getIndicatorCategory(@Validated String indicatorCategoryId) {
//        return indicatorCategoryBiz.getIndicatorCategory(indicatorCategoryId);
//    }
//
//    @GetMapping("v1/baseIndicator/indicatorCategory/listIndicatorCategory")
//    public List<IndicatorCategoryResponse> listIndicatorCategory(@Validated String appId, @Validated Long pid, @Validated String indicatorCategoryId, @Validated String categoryCode, @Validated String categoryName) {
//        return indicatorCategoryBiz.listIndicatorCategory(appId,pid,indicatorCategoryId,categoryCode,categoryName);
//    }
//
//    @PostMapping("v1/baseIndicator/indicatorCategory/sync")
//    public void sync(@RequestBody @Validated String appId ) {
//        indicatorCategoryBiz.sync(appId);
//    }


}