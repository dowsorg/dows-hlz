package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.indicator.request.IndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.indicator.response.IndicatorViewMonitorFollowupResponse;
import org.dows.hep.biz.indicator.IndicatorViewMonitorFollowupBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标监测随访类
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "查看指标监测随访类")
public class IndicatorViewMonitorFollowupRest {
    private final IndicatorViewMonitorFollowupBiz indicatorViewMonitorFollowupBiz;

    /**
    * 创建查看指标监测随访类
    * @param
    * @return
    */
    @ApiOperation("创建查看指标监测随访类")
    @PostMapping("v1/indicator/indicatorViewMonitorFollowup/createIndicatorViewMonitorFollowup")
    public void createIndicatorViewMonitorFollowup(@RequestBody @Validated CreateIndicatorViewMonitorFollowupRequest createIndicatorViewMonitorFollowup ) {
        indicatorViewMonitorFollowupBiz.createIndicatorViewMonitorFollowup(createIndicatorViewMonitorFollowup);
    }

    /**
    * 删除指标监测随访类
    * @param
    * @return
    */
    @ApiOperation("删除指标监测随访类")
    @DeleteMapping("v1/indicator/indicatorViewMonitorFollowup/deleteIndicatorViewMonitorFollowup")
    public void deleteIndicatorViewMonitorFollowup(@Validated String indicatorViewMonitorFollowupId ) {
        indicatorViewMonitorFollowupBiz.deleteIndicatorViewMonitorFollowup(indicatorViewMonitorFollowupId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @ApiOperation("更改启用状态")
    @PutMapping("v1/indicator/indicatorViewMonitorFollowup/updateStatus")
    public void updateStatus(@Validated IndicatorViewMonitorFollowupRequest indicatorViewMonitorFollowup ) {
        indicatorViewMonitorFollowupBiz.updateStatus(indicatorViewMonitorFollowup);
    }

    /**
    * 更新查看指标监测随访类
    * @param
    * @return
    */
    @ApiOperation("更新查看指标监测随访类")
    @PutMapping("v1/indicator/indicatorViewMonitorFollowup/updateIndicatorViewMonitorFollowup")
    public void updateIndicatorViewMonitorFollowup(@Validated UpdateIndicatorViewMonitorFollowupRequest updateIndicatorViewMonitorFollowup ) {
        indicatorViewMonitorFollowupBiz.updateIndicatorViewMonitorFollowup(updateIndicatorViewMonitorFollowup);
    }

    /**
    * 获取查看指标监测随访类
    * @param
    * @return
    */
    @ApiOperation("获取查看指标监测随访类")
    @GetMapping("v1/indicator/indicatorViewMonitorFollowup/getIndicatorViewMonitorFollowup")
    public IndicatorViewMonitorFollowupResponse getIndicatorViewMonitorFollowup(@Validated String indicatorViewMonitorFollowupId) {
        return indicatorViewMonitorFollowupBiz.getIndicatorViewMonitorFollowup(indicatorViewMonitorFollowupId);
    }


}