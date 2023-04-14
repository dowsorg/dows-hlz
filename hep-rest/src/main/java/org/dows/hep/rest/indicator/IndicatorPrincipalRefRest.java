package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorPrincipalRefRequest;
import org.dows.hep.api.indicator.response.IndicatorPrincipalRefResponse;
import org.dows.hep.biz.indicator.IndicatorPrincipalRefBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标主体关联关系
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "指标主体关联关系")
public class IndicatorPrincipalRefRest {
    private final IndicatorPrincipalRefBiz indicatorPrincipalRefBiz;

    /**
    * 创建指标主体关联关系
    * @param
    * @return
    */
    @ApiOperation("创建指标主体关联关系")
    @PostMapping("v1/indicator/indicatorPrincipalRef/createIndicatorPrincipalRef")
    public void createIndicatorPrincipalRef(@RequestBody @Validated CreateIndicatorPrincipalRefRequest createIndicatorPrincipalRef ) {
        indicatorPrincipalRefBiz.createIndicatorPrincipalRef(createIndicatorPrincipalRef);
    }

    /**
    * 删除指标主体关联关系
    * @param
    * @return
    */
    @ApiOperation("删除指标主体关联关系")
    @DeleteMapping("v1/indicator/indicatorPrincipalRef/deleteIndicatorPrincipalRef")
    public void deleteIndicatorPrincipalRef(@Validated String indicatorPrincipalRefId ) {
        indicatorPrincipalRefBiz.deleteIndicatorPrincipalRef(indicatorPrincipalRefId);
    }

    /**
    * 查询指标主体关联关系
    * @param
    * @return
    */
    @ApiOperation("查询指标主体关联关系")
    @GetMapping("v1/indicator/indicatorPrincipalRef/getIndicatorPrincipalRef")
    public IndicatorPrincipalRefResponse getIndicatorPrincipalRef(@Validated String indicatorPrincipalRefId) {
        return indicatorPrincipalRefBiz.getIndicatorPrincipalRef(indicatorPrincipalRefId);
    }


}