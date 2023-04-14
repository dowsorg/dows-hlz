package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorRuleRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorRuleRequest;
import org.dows.hep.api.base.indicator.response.IndicatorRuleResponse;
import org.dows.hep.biz.base.indicator.IndicatorRuleBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标规则
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "指标规则", description = "指标规则")
public class IndicatorRuleRest {
    private final IndicatorRuleBiz indicatorRuleBiz;

    /**
    * 创建指标规则
    * @param
    * @return
    */
    @Operation(summary = "创建指标规则")
    @PostMapping("v1/baseIndicator/indicatorRule/createIndicatorRule")
    public void createIndicatorRule(@RequestBody @Validated CreateIndicatorRuleRequest createIndicatorRule ) {
        indicatorRuleBiz.createIndicatorRule(createIndicatorRule);
    }

    /**
    * 删除指标规则
    * @param
    * @return
    */
    @Operation(summary = "删除指标规则")
    @DeleteMapping("v1/baseIndicator/indicatorRule/deleteIndicatorRule")
    public void deleteIndicatorRule(@Validated String indicatorRuleId ) {
        indicatorRuleBiz.deleteIndicatorRule(indicatorRuleId);
    }

    /**
    * 更新指标规则
    * @param
    * @return
    */
    @Operation(summary = "更新指标规则")
    @PutMapping("v1/baseIndicator/indicatorRule/updateIndicatorRule")
    public void updateIndicatorRule(@Validated UpdateIndicatorRuleRequest updateIndicatorRule ) {
        indicatorRuleBiz.updateIndicatorRule(updateIndicatorRule);
    }

    /**
    * 获取指标规则
    * @param
    * @return
    */
    @Operation(summary = "获取指标规则")
    @GetMapping("v1/baseIndicator/indicatorRule/getIndicatorRule")
    public IndicatorRuleResponse getIndicatorRule(@Validated String indicatorRuleId) {
        return indicatorRuleBiz.getIndicatorRule(indicatorRuleId);
    }


}