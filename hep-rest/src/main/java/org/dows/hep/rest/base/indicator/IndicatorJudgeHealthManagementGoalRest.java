package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthManagementGoalResponse;
import org.dows.hep.biz.base.indicator.IndicatorJudgeHealthManagementGoalBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标健管目标
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标健管目标", description = "判断指标健管目标")
public class IndicatorJudgeHealthManagementGoalRest {
    private final IndicatorJudgeHealthManagementGoalBiz indicatorJudgeHealthManagementGoalBiz;

    /**
    * 创建判断指标健管目标
    * @param
    * @return
    */
    @Operation(summary = "创建判断指标健管目标")
    @PostMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/createIndicatorJudgeHealthManagementGoal")
    public void createIndicatorJudgeHealthManagementGoal(@RequestBody @Validated CreateIndicatorJudgeHealthManagementGoalRequest createIndicatorJudgeHealthManagementGoal ) {
        indicatorJudgeHealthManagementGoalBiz.createIndicatorJudgeHealthManagementGoal(createIndicatorJudgeHealthManagementGoal);
    }

    /**
    * 删除判断指标健管目标
    * @param
    * @return
    */
    @Operation(summary = "删除判断指标健管目标")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/deleteIndicatorJudgeHealthManagementGoal")
    public void deleteIndicatorJudgeHealthManagementGoal(@Validated String indicatorJudgeHealthManagementGoalId ) {
        indicatorJudgeHealthManagementGoalBiz.deleteIndicatorJudgeHealthManagementGoal(indicatorJudgeHealthManagementGoalId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeHealthManagementGoalRequest updateStatusIndicatorJudgeHealthManagementGoal ) {
        indicatorJudgeHealthManagementGoalBiz.updateStatus(updateStatusIndicatorJudgeHealthManagementGoal);
    }

    /**
    * 更新判断指标健管目标
    * @param
    * @return
    */
    @Operation(summary = "更新判断指标健管目标")
    @PutMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/updateIndicatorJudgeHealthManagementGoal")
    public void updateIndicatorJudgeHealthManagementGoal(@Validated UpdateIndicatorJudgeHealthManagementGoalRequest updateIndicatorJudgeHealthManagementGoal ) {
        indicatorJudgeHealthManagementGoalBiz.updateIndicatorJudgeHealthManagementGoal(updateIndicatorJudgeHealthManagementGoal);
    }

    /**
    * 获取判断指标健管目标
    * @param
    * @return
    */
    @Operation(summary = "获取判断指标健管目标")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/getIndicatorJudgeHealthManagementGoal")
    public IndicatorJudgeHealthManagementGoalResponse getIndicatorJudgeHealthManagementGoal(@Validated String indicatorJudgeHealthManagementGoalId) {
        return indicatorJudgeHealthManagementGoalBiz.getIndicatorJudgeHealthManagementGoal(indicatorJudgeHealthManagementGoalId);
    }

    /**
    * 分页获取判断指标健管目标
    * @param
    * @return
    */
    @Operation(summary = "分页获取判断指标健管目标")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/pageIndicatorJudgeHealthManagementGoal")
    public void pageIndicatorJudgeHealthManagementGoal(@Validated String todo) {
        indicatorJudgeHealthManagementGoalBiz.pageIndicatorJudgeHealthManagementGoal(todo);
    }


}