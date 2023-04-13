package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorCategoryRequest;
import org.dows.hep.api.indicator.request.CreateOrUpdateIndicatorCategoryRequest;
import org.dows.hep.api.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.biz.indicator.IndicatorCategoryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标目录
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "指标目录")
public class IndicatorCategoryRest {
    private final IndicatorCategoryBiz indicatorCategoryBiz;

    /**
    * 创建指标目录
    * @param
    * @return
    */
    @ApiOperation("创建指标目录")
    @PostMapping("v1/indicator/indicatorCategory/createIndicatorCategory")
    public void createIndicatorCategory(@RequestBody @Validated CreateIndicatorCategoryRequest createIndicatorCategory ) {
        indicatorCategoryBiz.createIndicatorCategory(createIndicatorCategory);
    }

    /**
    * 删除指标目录
    * @param
    * @return
    */
    @ApiOperation("删除指标目录")
    @DeleteMapping("v1/indicator/indicatorCategory/deleteIndicatorCategory")
    public void deleteIndicatorCategory(@Validated String indicatorCategoryId ) {
        indicatorCategoryBiz.deleteIndicatorCategory(indicatorCategoryId);
    }

    /**
    * 更新指标目录
    * @param
    * @return
    */
    @ApiOperation("更新指标目录")
    @PutMapping("v1/indicator/indicatorCategory/updateIndicatorCategory")
    public void updateIndicatorCategory(@Validated String appId, @Validated List<CreateOrUpdateIndicatorCategoryRequest> createOrUpdateIndicatorCategory ) {
        indicatorCategoryBiz.updateIndicatorCategory(appId,createOrUpdateIndicatorCategory);
    }

    /**
    * 查询指标目录
    * @param
    * @return
    */
    @ApiOperation("查询指标目录")
    @GetMapping("v1/indicator/indicatorCategory/getIndicatorCategory")
    public IndicatorCategoryResponse getIndicatorCategory(@Validated String indicatorCategoryId) {
        return indicatorCategoryBiz.getIndicatorCategory(indicatorCategoryId);
    }

    /**
    * 筛选指标目录
    * @param
    * @return
    */
    @ApiOperation("筛选指标目录")
    @GetMapping("v1/indicator/indicatorCategory/listIndicatorCategory")
    public List<IndicatorCategoryResponse> listIndicatorCategory(@Validated String appId, @Validated Long pid, @Validated String indicatorCategoryId, @Validated String categoryCode, @Validated String categoryName) {
        return indicatorCategoryBiz.listIndicatorCategory(appId,pid,indicatorCategoryId,categoryCode,categoryName);
    }


}