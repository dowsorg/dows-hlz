package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorRuleRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorRuleRequest;
import org.dows.hep.api.indicator.response.IndicatorRuleResponse;
import org.dows.hep.biz.indicator.IndicatorRuleBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标规则
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "指标规则")
public class IndicatorRuleRest {
    private final IndicatorRuleBiz indicatorRuleBiz;

    /**
    * 创建指标规则
    * @param
    * @return
    */
    @ApiOperation("创建指标规则")
    @PostMapping("v1/indicator/indicatorRule/createIndicatorRule")
    public void createIndicatorRule(@RequestBody @Validated CreateIndicatorRuleRequest createIndicatorRule ) {
        indicatorRuleBiz.createIndicatorRule(createIndicatorRule);
    }

    /**
    * 删除指标规则
    * @param
    * @return
    */
    @ApiOperation("删除指标规则")
    @DeleteMapping("v1/indicator/indicatorRule/deleteIndicatorRule")
    public void deleteIndicatorRule(@Validated String indicatorRuleId ) {
        indicatorRuleBiz.deleteIndicatorRule(indicatorRuleId);
    }

    /**
    * 更新指标规则
    * @param
    * @return
    */
    @ApiOperation("更新指标规则")
    @PutMapping("v1/indicator/indicatorRule/updateIndicatorRule")
    public void updateIndicatorRule(@Validated UpdateIndicatorRuleRequest updateIndicatorRule ) {
        indicatorRuleBiz.updateIndicatorRule(updateIndicatorRule);
    }

    /**
    * 获取指标规则
    * @param
    * @return
    */
    @ApiOperation("获取指标规则")
    @GetMapping("v1/indicator/indicatorRule/getIndicatorRule")
    public IndicatorRuleResponse getIndicatorRule(@Validated String indicatorRuleId) {
        return indicatorRuleBiz.getIndicatorRule(indicatorRuleId);
    }


}