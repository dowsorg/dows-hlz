package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.request.IndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewMonitorFollowupRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewMonitorFollowupResponse;
import org.dows.hep.biz.base.indicator.IndicatorViewMonitorFollowupBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标监测随访类
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "查看指标监测随访类", description = "查看指标监测随访类")
public class IndicatorViewMonitorFollowupRest {
    private final IndicatorViewMonitorFollowupBiz indicatorViewMonitorFollowupBiz;

    /**
    * 创建查看指标监测随访类
    * @param
    * @return
    */
    @Operation(summary = "创建查看指标监测随访类")
    @PostMapping("v1/baseIndicator/indicatorViewMonitorFollowup/createIndicatorViewMonitorFollowup")
    public void createIndicatorViewMonitorFollowup(@RequestBody @Validated CreateIndicatorViewMonitorFollowupRequest createIndicatorViewMonitorFollowup ) {
        indicatorViewMonitorFollowupBiz.createIndicatorViewMonitorFollowup(createIndicatorViewMonitorFollowup);
    }

    /**
    * 删除指标监测随访类
    * @param
    * @return
    */
    @Operation(summary = "删除指标监测随访类")
    @DeleteMapping("v1/baseIndicator/indicatorViewMonitorFollowup/deleteIndicatorViewMonitorFollowup")
    public void deleteIndicatorViewMonitorFollowup(@Validated String indicatorViewMonitorFollowupId ) {
        indicatorViewMonitorFollowupBiz.deleteIndicatorViewMonitorFollowup(indicatorViewMonitorFollowupId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorViewMonitorFollowup/updateStatus")
    public void updateStatus(@Validated IndicatorViewMonitorFollowupRequest indicatorViewMonitorFollowup ) {
        indicatorViewMonitorFollowupBiz.updateStatus(indicatorViewMonitorFollowup);
    }

    /**
    * 更新查看指标监测随访类
    * @param
    * @return
    */
    @Operation(summary = "更新查看指标监测随访类")
    @PutMapping("v1/baseIndicator/indicatorViewMonitorFollowup/updateIndicatorViewMonitorFollowup")
    public void updateIndicatorViewMonitorFollowup(@Validated UpdateIndicatorViewMonitorFollowupRequest updateIndicatorViewMonitorFollowup ) {
        indicatorViewMonitorFollowupBiz.updateIndicatorViewMonitorFollowup(updateIndicatorViewMonitorFollowup);
    }

    /**
    * 获取查看指标监测随访类
    * @param
    * @return
    */
    @Operation(summary = "获取查看指标监测随访类")
    @GetMapping("v1/baseIndicator/indicatorViewMonitorFollowup/getIndicatorViewMonitorFollowup")
    public IndicatorViewMonitorFollowupResponse getIndicatorViewMonitorFollowup(@Validated String indicatorViewMonitorFollowupId) {
        return indicatorViewMonitorFollowupBiz.getIndicatorViewMonitorFollowup(indicatorViewMonitorFollowupId);
    }

    /**
    * 分页获取查看指标监测随访类
    * @param
    * @return
    */
    @Operation(summary = "分页获取查看指标监测随访类")
    @GetMapping("v1/baseIndicator/indicatorViewMonitorFollowup/pageIndicatorViewMonitorFollowup")
    public void pageIndicatorViewMonitorFollowup(@Validated String todo) {
        indicatorViewMonitorFollowupBiz.pageIndicatorViewMonitorFollowup(todo);
    }


}