package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeRiskFactorResponse;
import org.dows.hep.biz.base.indicator.IndicatorJudgeRiskFactorBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标危险因素
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标危险因素", description = "判断指标危险因素")
public class IndicatorJudgeRiskFactorRest {
    private final IndicatorJudgeRiskFactorBiz indicatorJudgeRiskFactorBiz;

    /**
    * 创建危险因素
    * @param
    * @return
    */
    @Operation(summary = "创建危险因素")
    @PostMapping("v1/baseIndicator/indicatorJudgeRiskFactor/createIndicatorJudgeRiskFactor")
    public void createIndicatorJudgeRiskFactor(@RequestBody @Validated CreateIndicatorJudgeRiskFactorRequest createIndicatorJudgeRiskFactor ) {
        indicatorJudgeRiskFactorBiz.createIndicatorJudgeRiskFactor(createIndicatorJudgeRiskFactor);
    }

    /**
    * 删除判断指标危险因素
    * @param
    * @return
    */
    @Operation(summary = "删除判断指标危险因素")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeRiskFactor/deleteIndicatorJudgeRiskFactor")
    public void deleteIndicatorJudgeRiskFactor(@Validated String indicatorJudgeRiskFactorId ) {
        indicatorJudgeRiskFactorBiz.deleteIndicatorJudgeRiskFactor(indicatorJudgeRiskFactorId);
    }

    /**
    * 批量删除
    * @param
    * @return
    */
    @Operation(summary = "批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeRiskFactor/batchDelete")
    public void batchDelete(@Validated String string ) {
        indicatorJudgeRiskFactorBiz.batchDelete(string);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorJudgeRiskFactor/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeRiskFactorRequest updateStatusIndicatorJudgeRiskFactor ) {
        indicatorJudgeRiskFactorBiz.updateStatus(updateStatusIndicatorJudgeRiskFactor);
    }

    /**
    * 判断指标危险因素
    * @param
    * @return
    */
    @Operation(summary = "判断指标危险因素")
    @PutMapping("v1/baseIndicator/indicatorJudgeRiskFactor/updateIndicatorJudgeRiskFactor")
    public void updateIndicatorJudgeRiskFactor(@Validated UpdateIndicatorJudgeRiskFactorRequest updateIndicatorJudgeRiskFactor ) {
        indicatorJudgeRiskFactorBiz.updateIndicatorJudgeRiskFactor(updateIndicatorJudgeRiskFactor);
    }

    /**
    * 判断指标危险因素
    * @param
    * @return
    */
    @Operation(summary = "判断指标危险因素")
    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/getIndicatorJudgeRiskFactor")
    public IndicatorJudgeRiskFactorResponse getIndicatorJudgeRiskFactor(@Validated String indicatorJudgeRiskFactorId) {
        return indicatorJudgeRiskFactorBiz.getIndicatorJudgeRiskFactor(indicatorJudgeRiskFactorId);
    }

    /**
    * 筛选判断指标危险因素
    * @param
    * @return
    */
    @Operation(summary = "筛选判断指标危险因素")
    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/listIndicatorJudgeRiskFactor")
    public List<IndicatorJudgeRiskFactorResponse> listIndicatorJudgeRiskFactor(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
        return indicatorJudgeRiskFactorBiz.listIndicatorJudgeRiskFactor(appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
    }

    /**
    * 分页筛选判断指标危险因素
    * @param
    * @return
    */
    @Operation(summary = "分页筛选判断指标危险因素")
    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/pageIndicatorJudgeRiskFactor")
    public String pageIndicatorJudgeRiskFactor(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
        return indicatorJudgeRiskFactorBiz.pageIndicatorJudgeRiskFactor(pageNo,pageSize,appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
    }


}