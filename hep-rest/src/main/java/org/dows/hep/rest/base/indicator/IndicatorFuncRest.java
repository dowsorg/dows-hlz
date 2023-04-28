package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.response.IndicatorFuncResponse;
import org.dows.hep.biz.base.indicator.IndicatorFuncBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标功能
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标功能", description = "指标功能")
public class IndicatorFuncRest {
    private final IndicatorFuncBiz indicatorFuncBiz;

    /**
    * 创建指标功能
    * @param
    * @return
    */
    @Operation(summary = "创建指标功能")
    @PostMapping("v1/baseIndicator/indicatorFunc/createIndicatorFunc")
    public void createIndicatorFunc(@RequestBody @Validated CreateIndicatorFuncRequest createIndicatorFuncRequest) throws InterruptedException {
        indicatorFuncBiz.createIndicatorFunc(createIndicatorFuncRequest);
    }

    /**
    * 删除指标功能
    * @param
    * @return
    */
    @Operation(summary = "删除指标功能")
    @DeleteMapping("v1/baseIndicator/indicatorFunc/deleteIndicatorFunc")
    public void deleteIndicatorFunc(@Validated String indicatorFunc ) {
        indicatorFuncBiz.deleteIndicatorFunc(indicatorFunc);
    }

    /**
    * 更新指标功能
    * @param
    * @return
    */
    @Operation(summary = "更新指标功能")
    @PutMapping("v1/baseIndicator/indicatorFunc/updateIndicatorFunc")
    public void updateIndicatorFunc(@RequestBody @Validated UpdateIndicatorFuncRequest updateIndicatorFuncRequest) throws InterruptedException {
        indicatorFuncBiz.updateIndicatorFunc(updateIndicatorFuncRequest);
    }

    /**
    * 获取指标功能
    * @param
    * @return
    */
    @Operation(summary = "获取指标功能")
    @GetMapping("v1/baseIndicator/indicatorFunc/getIndicatorFunc")
    public IndicatorFuncResponse getIndicatorFunc(@Validated String indicatorFunc) {
        return indicatorFuncBiz.getIndicatorFunc(indicatorFunc);
    }

    /**
    * 筛选指标类别
    * @param
    * @return
    */
    @Operation(summary = "查询指标类别下所有功能点")
    @GetMapping("v1/baseIndicator/indicatorFunc/listIndicatorFunc")
    public List<IndicatorFuncResponse> listIndicatorFunc(
        @RequestParam @Validated String appId,
        @RequestParam @Validated String indicatorCategoryId) {
        return indicatorFuncBiz.listIndicatorFunc(appId,indicatorCategoryId);
    }

    @Operation(summary = "根据pid查询所有功能点")
    @GetMapping("v1/baseIndicator/indicatorFunc/getByPidAndAppId")
    public List<IndicatorFuncResponse> getByPidAndAppId(
        @RequestParam @Validated String appId,
        @RequestParam @Validated String pid) {
        return indicatorFuncBiz.getByPidAndAppId(appId, pid);
    }

//    /**
//    * 分页筛选指标类别
//    * @param
//    * @return
//    */
//    @Operation(summary = "分页筛选指标类别")


}