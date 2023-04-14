package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorInstanceRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorInstanceRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorInstanceRequest;
import org.dows.hep.api.indicator.response.IndicatorInstanceResponse;
import org.dows.hep.api.indicator.request.VarcharRequest;
import org.dows.hep.api.indicator.response.IndicatorInstanceResponse;
import org.dows.hep.biz.indicator.IndicatorInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:指标实例
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "指标实例")
public class IndicatorInstanceRest {
    private final IndicatorInstanceBiz indicatorInstanceBiz;

    /**
    * 创建指标实例
    * @param
    * @return
    */
    @ApiOperation("创建指标实例")
    @PostMapping("v1/indicator/indicatorInstance/createIndicatorInstance")
    public void createIndicatorInstance(@RequestBody @Validated CreateIndicatorInstanceRequest createIndicatorInstance ) {
        indicatorInstanceBiz.createIndicatorInstance(createIndicatorInstance);
    }

    /**
    * 删除指标
    * @param
    * @return
    */
    @ApiOperation("删除指标")
    @DeleteMapping("v1/indicator/indicatorInstance/deleteIndicatorInstance")
    public void deleteIndicatorInstance(@Validated String indicatorInstanceId ) {
        indicatorInstanceBiz.deleteIndicatorInstance(indicatorInstanceId);
    }

    /**
    * 更新指标
    * @param
    * @return
    */
    @ApiOperation("更新指标")
    @PutMapping("v1/indicator/indicatorInstance/updateIndicatorInstance")
    public void updateIndicatorInstance(@Validated UpdateIndicatorInstanceRequest updateIndicatorInstance ) {
        indicatorInstanceBiz.updateIndicatorInstance(updateIndicatorInstance);
    }

    /**
    * 批量更新指标
    * @param
    * @return
    */
    @ApiOperation("批量更新指标")
    @PutMapping("v1/indicator/indicatorInstance/batchUpdateIndicatorInstance")
    public void batchUpdateIndicatorInstance(@Validated List<UpdateIndicatorInstanceRequest> updateIndicatorInstance ) {
        indicatorInstanceBiz.batchUpdateIndicatorInstance(updateIndicatorInstance);
    }

    /**
    * 查询指标
    * @param
    * @return
    */
    @ApiOperation("查询指标")
    @GetMapping("v1/indicator/indicatorInstance/getIndicatorInstance")
    public IndicatorInstanceResponse getIndicatorInstance(@Validated String indicatorInstanceId) {
        return indicatorInstanceBiz.getIndicatorInstance(indicatorInstanceId);
    }

    /**
    * 筛选指标
    * @param
    * @return
    */
    @ApiOperation("筛选指标")
    @GetMapping("v1/indicator/indicatorInstance/listIndicatorInstance")
    public List<IndicatorInstanceResponse> listIndicatorInstance(@Validated String appId, @Validated VarcharRequest varchar) {
        return indicatorInstanceBiz.listIndicatorInstance(appId,varchar);
    }


}