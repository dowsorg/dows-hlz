package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorVarRequest;
import org.dows.hep.api.indicator.request.IndicatorVarIdRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorVarRequest;
import org.dows.hep.api.indicator.request.IndicatorVarIdRequest;
import org.dows.hep.api.indicator.response.IndicatorVarResponse;
import org.dows.hep.biz.indicator.IndicatorVarBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标变量
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "指标变量")
public class IndicatorVarRest {
    private final IndicatorVarBiz indicatorVarBiz;

    /**
    * 创建指标变量
    * @param
    * @return
    */
    @ApiOperation("创建指标变量")
    @PostMapping("v1/indicator/indicatorVar/createIndicatorVar")
    public void createIndicatorVar(@RequestBody @Validated CreateIndicatorVarRequest createIndicatorVar ) {
        indicatorVarBiz.createIndicatorVar(createIndicatorVar);
    }

    /**
    * 删除指标变量
    * @param
    * @return
    */
    @ApiOperation("删除指标变量")
    @DeleteMapping("v1/indicator/indicatorVar/deleteIndicatorVar")
    public void deleteIndicatorVar(@Validated IndicatorVarIdRequest indicatorVarId ) {
        indicatorVarBiz.deleteIndicatorVar(indicatorVarId);
    }

    /**
    * 更新指标变量
    * @param
    * @return
    */
    @ApiOperation("更新指标变量")
    @PutMapping("v1/indicator/indicatorVar/updateIndicatorVar")
    public void updateIndicatorVar(@Validated UpdateIndicatorVarRequest updateIndicatorVar ) {
        indicatorVarBiz.updateIndicatorVar(updateIndicatorVar);
    }

    /**
    * 查询指标变量
    * @param
    * @return
    */
    @ApiOperation("查询指标变量")
    @GetMapping("v1/indicator/indicatorVar/getIndicatorVar")
    public IndicatorVarResponse getIndicatorVar(@Validated IndicatorVarIdRequest indicatorVarId) {
        return indicatorVarBiz.getIndicatorVar(indicatorVarId);
    }


}