package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.indicator.request.UpdateStatusIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.indicator.response.IndicatorJudgeHealthManagementGoalResponse;
import org.dows.hep.biz.indicator.IndicatorJudgeHealthManagementGoalBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标健管目标
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "判断指标健管目标")
public class IndicatorJudgeHealthManagementGoalRest {
    private final IndicatorJudgeHealthManagementGoalBiz indicatorJudgeHealthManagementGoalBiz;

    /**
    * 创建判断指标健管目标
    * @param
    * @return
    */
    @ApiOperation("创建判断指标健管目标")
    @PostMapping("v1/indicator/indicatorJudgeHealthManagementGoal/createIndicatorJudgeHealthManagementGoal")
    public void createIndicatorJudgeHealthManagementGoal(@RequestBody @Validated CreateIndicatorJudgeHealthManagementGoalRequest createIndicatorJudgeHealthManagementGoal ) {
        indicatorJudgeHealthManagementGoalBiz.createIndicatorJudgeHealthManagementGoal(createIndicatorJudgeHealthManagementGoal);
    }

    /**
    * 删除判断指标健管目标
    * @param
    * @return
    */
    @ApiOperation("删除判断指标健管目标")
    @DeleteMapping("v1/indicator/indicatorJudgeHealthManagementGoal/deleteIndicatorJudgeHealthManagementGoal")
    public void deleteIndicatorJudgeHealthManagementGoal(@Validated String indicatorJudgeHealthManagementGoalId ) {
        indicatorJudgeHealthManagementGoalBiz.deleteIndicatorJudgeHealthManagementGoal(indicatorJudgeHealthManagementGoalId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @ApiOperation("更改启用状态")
    @PutMapping("v1/indicator/indicatorJudgeHealthManagementGoal/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeHealthManagementGoalRequest updateStatusIndicatorJudgeHealthManagementGoal ) {
        indicatorJudgeHealthManagementGoalBiz.updateStatus(updateStatusIndicatorJudgeHealthManagementGoal);
    }

    /**
    * 更新判断指标健管目标
    * @param
    * @return
    */
    @ApiOperation("更新判断指标健管目标")
    @PutMapping("v1/indicator/indicatorJudgeHealthManagementGoal/updateIndicatorJudgeHealthManagementGoal")
    public void updateIndicatorJudgeHealthManagementGoal(@Validated UpdateIndicatorJudgeHealthManagementGoalRequest updateIndicatorJudgeHealthManagementGoal ) {
        indicatorJudgeHealthManagementGoalBiz.updateIndicatorJudgeHealthManagementGoal(updateIndicatorJudgeHealthManagementGoal);
    }

    /**
    * 获取判断指标健管目标
    * @param
    * @return
    */
    @ApiOperation("获取判断指标健管目标")
    @GetMapping("v1/indicator/indicatorJudgeHealthManagementGoal/getIndicatorJudgeHealthManagementGoal")
    public IndicatorJudgeHealthManagementGoalResponse getIndicatorJudgeHealthManagementGoal(@Validated String indicatorJudgeHealthManagementGoalId) {
        return indicatorJudgeHealthManagementGoalBiz.getIndicatorJudgeHealthManagementGoal(indicatorJudgeHealthManagementGoalId);
    }

    /**
    * 分页获取判断指标健管目标
    * @param
    * @return
    */
    @ApiOperation("分页获取判断指标健管目标")
    @GetMapping("v1/indicator/indicatorJudgeHealthManagementGoal/pageIndicatorJudgeHealthManagementGoal")
    public void pageIndicatorJudgeHealthManagementGoal(@Validated String todo) {
        indicatorJudgeHealthManagementGoalBiz.pageIndicatorJudgeHealthManagementGoal(todo);
    }


}