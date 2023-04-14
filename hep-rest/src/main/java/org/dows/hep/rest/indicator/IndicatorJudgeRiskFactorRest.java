package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.indicator.request.UpdateStatusIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.indicator.response.IndicatorJudgeRiskFactorResponse;
import org.dows.hep.biz.indicator.IndicatorJudgeRiskFactorBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标危险因素
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "判断指标危险因素")
public class IndicatorJudgeRiskFactorRest {
    private final IndicatorJudgeRiskFactorBiz indicatorJudgeRiskFactorBiz;

    /**
    * 创建危险因素
    * @param
    * @return
    */
    @ApiOperation("创建危险因素")
    @PostMapping("v1/indicator/indicatorJudgeRiskFactor/createIndicatorJudgeRiskFactor")
    public void createIndicatorJudgeRiskFactor(@RequestBody @Validated CreateIndicatorJudgeRiskFactorRequest createIndicatorJudgeRiskFactor ) {
        indicatorJudgeRiskFactorBiz.createIndicatorJudgeRiskFactor(createIndicatorJudgeRiskFactor);
    }

    /**
    * 删除判断指标危险因素
    * @param
    * @return
    */
    @ApiOperation("删除判断指标危险因素")
    @DeleteMapping("v1/indicator/indicatorJudgeRiskFactor/deleteIndicatorJudgeRiskFactor")
    public void deleteIndicatorJudgeRiskFactor(@Validated String indicatorJudgeRiskFactorId ) {
        indicatorJudgeRiskFactorBiz.deleteIndicatorJudgeRiskFactor(indicatorJudgeRiskFactorId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @ApiOperation("更改启用状态")
    @PutMapping("v1/indicator/indicatorJudgeRiskFactor/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeRiskFactorRequest updateStatusIndicatorJudgeRiskFactor ) {
        indicatorJudgeRiskFactorBiz.updateStatus(updateStatusIndicatorJudgeRiskFactor);
    }

    /**
    * 判断指标危险因素
    * @param
    * @return
    */
    @ApiOperation("判断指标危险因素")
    @PutMapping("v1/indicator/indicatorJudgeRiskFactor/updateIndicatorJudgeRiskFactor")
    public void updateIndicatorJudgeRiskFactor(@Validated UpdateIndicatorJudgeRiskFactorRequest updateIndicatorJudgeRiskFactor ) {
        indicatorJudgeRiskFactorBiz.updateIndicatorJudgeRiskFactor(updateIndicatorJudgeRiskFactor);
    }

    /**
    * 判断指标危险因素
    * @param
    * @return
    */
    @ApiOperation("判断指标危险因素")
    @GetMapping("v1/indicator/indicatorJudgeRiskFactor/getIndicatorJudgeRiskFactor")
    public IndicatorJudgeRiskFactorResponse getIndicatorJudgeRiskFactor(@Validated String indicatorJudgeRiskFactorId) {
        return indicatorJudgeRiskFactorBiz.getIndicatorJudgeRiskFactor(indicatorJudgeRiskFactorId);
    }


}