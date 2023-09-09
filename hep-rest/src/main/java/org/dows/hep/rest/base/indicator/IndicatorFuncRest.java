package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.response.IndicatorFuncOrgResponse;
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

    @Operation(summary = "创建指标功能")
    @PostMapping("v1/baseIndicator/indicatorFunc/create")
    public void create(@RequestBody @Validated CreateIndicatorFuncRequest createIndicatorFuncRequest) throws InterruptedException {
        indicatorFuncBiz.create(createIndicatorFuncRequest);
    }

    @Operation(summary = "更新指标功能")
    @PutMapping("v1/baseIndicator/indicatorFunc/update")
    public void update(@RequestBody @Validated UpdateIndicatorFuncRequest updateIndicatorFuncRequest) throws InterruptedException {
        indicatorFuncBiz.update(updateIndicatorFuncRequest);
    }

    @Operation(summary = "根据pid查询所有功能点")
    @GetMapping("v1/baseIndicator/indicatorFunc/getByPidAndAppId")
    public List<IndicatorFuncResponse> getByPidAndAppId(
        @RequestParam @Validated String appId,
        @RequestParam @Validated String pid) {
        return indicatorFuncBiz.getByPidAndAppId(appId, pid);
    }

    @Operation(summary = "根据指标类目获取功能点提示")
    @GetMapping("v1/baseIndicator/indicatorFunc/getFuncTip")
    public IndicatorFuncResponse getFuncTip(
            @RequestParam @Validated String appId,
            @RequestParam @Validated String indicatorCategoryId,
            @RequestParam @Validated String indicatorFuncName) {
        return indicatorFuncBiz.getFuncTip(appId, indicatorCategoryId,indicatorFuncName);
    }



    @Operation(summary = "删除指标功能")
    @DeleteMapping("v1/baseIndicator/indicatorFunc/delete")
    public void delete(@Validated String indicatorFuncId) {
        indicatorFuncBiz.delete(indicatorFuncId);
    }

    @Operation(summary = "自定义-机构管理-编辑功能")
    @GetMapping("v1/baseIndicator/indicatorFunc/getOrgEditFunc")
    public List<IndicatorFuncOrgResponse> getOrgEditFuncByAppId(@RequestParam @Validated String appId) {
        return indicatorFuncBiz.getOrgEditFuncByAppId(appId);
    }
}