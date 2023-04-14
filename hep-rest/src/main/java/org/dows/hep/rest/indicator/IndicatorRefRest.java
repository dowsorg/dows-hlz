package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorRefRequest;
import org.dows.hep.api.indicator.response.IndicatorRefResponse;
import org.dows.hep.biz.indicator.IndicatorRefBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标引用
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "指标引用")
public class IndicatorRefRest {
    private final IndicatorRefBiz indicatorRefBiz;

    /**
    * 创建指标引用
    * @param
    * @return
    */
    @ApiOperation("创建指标引用")
    @PostMapping("v1/indicator/indicatorRef/createIndicatorRef")
    public void createIndicatorRef(@RequestBody @Validated CreateIndicatorRefRequest createIndicatorRef ) {
        indicatorRefBiz.createIndicatorRef(createIndicatorRef);
    }

    /**
    * 删除指标引用
    * @param
    * @return
    */
    @ApiOperation("删除指标引用")
    @DeleteMapping("v1/indicator/indicatorRef/deleteIndicatorRef")
    public void deleteIndicatorRef(@Validated String indicatorRefId ) {
        indicatorRefBiz.deleteIndicatorRef(indicatorRefId);
    }

    /**
    * 获取指标引用列表
    * @param
    * @return
    */
    @ApiOperation("获取指标引用列表")
    @GetMapping("v1/indicator/indicatorRef/listIndicatorRef")
    public List<IndicatorRefResponse> listIndicatorRef(@Validated String indicatorInstanceId) {
        return indicatorRefBiz.listIndicatorRef(indicatorInstanceId);
    }


}