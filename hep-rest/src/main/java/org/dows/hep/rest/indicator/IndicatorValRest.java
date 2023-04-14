package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorValRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorValRequest;
import org.dows.hep.api.indicator.response.IndicatorValResponse;
import org.dows.hep.biz.indicator.IndicatorValBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标值
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "指标值")
public class IndicatorValRest {
    private final IndicatorValBiz indicatorValBiz;

    /**
    * 创建指标值
    * @param
    * @return
    */
    @ApiOperation("创建指标值")
    @PostMapping("v1/indicator/indicatorVal/createIndicatorVal")
    public void createIndicatorVal(@RequestBody @Validated CreateIndicatorValRequest createIndicatorVal ) {
        indicatorValBiz.createIndicatorVal(createIndicatorVal);
    }

    /**
    * 删除指标值
    * @param
    * @return
    */
    @ApiOperation("删除指标值")
    @DeleteMapping("v1/indicator/indicatorVal/deleteIndicatorVal")
    public void deleteIndicatorVal(@Validated String indicatorValId ) {
        indicatorValBiz.deleteIndicatorVal(indicatorValId);
    }

    /**
    * 更新指标值
    * @param
    * @return
    */
    @ApiOperation("更新指标值")
    @PutMapping("v1/indicator/indicatorVal/updateIndicatorVal")
    public void updateIndicatorVal(@Validated UpdateIndicatorValRequest updateIndicatorVal ) {
        indicatorValBiz.updateIndicatorVal(updateIndicatorVal);
    }

    /**
    * 获取指标值
    * @param
    * @return
    */
    @ApiOperation("获取指标值")
    @GetMapping("v1/indicator/indicatorVal/indicatorVal")
    public IndicatorValResponse indicatorVal(@Validated String indicatorValId) {
        return indicatorValBiz.indicatorVal(indicatorValId);
    }


}