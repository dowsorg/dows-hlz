package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeRiskFactorResponse;
import org.dows.hep.biz.base.indicator.IndicatorJudgeRiskFactorBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标危险因素
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
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
    * 分页获取判断指标危险因素
    * @param
    * @return
    */
    @Operation(summary = "分页获取判断指标危险因素")
    @GetMapping("v1/baseIndicator/indicatorJudgeRiskFactor/pageIndicatorViewSupportExam")
    public void pageIndicatorViewSupportExam(@Validated String todo) {
        indicatorJudgeRiskFactorBiz.pageIndicatorViewSupportExam(todo);
    }


}