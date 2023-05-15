package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthManagementGoalResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthManagementGoalResponseRs;
import org.dows.hep.biz.base.indicator.IndicatorJudgeHealthManagementGoalBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标健管目标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标健管目标", description = "判断指标健管目标")
public class IndicatorJudgeHealthManagementGoalRest {
    private final IndicatorJudgeHealthManagementGoalBiz indicatorJudgeHealthManagementGoalBiz;

    @Operation(summary = "Rs批量创建或保存查看指标健管目标类")
    @PostMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/batchCreateOrUpdateRs")
    public void batchCreateOrUpdateRs(@RequestBody @Validated BatchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs) {
        indicatorJudgeHealthManagementGoalBiz.batchCreateOrUpdateRs(batchCreateOrUpdateIndicatorJudgeHealthManagementGoalRequestRs);
    }

    @Operation(summary = "Rs批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/batchDeleteRs")
    public void batchDeleteRs(@RequestBody List<String> indicatorJudgeHealthManagementGoalIdList) {
        indicatorJudgeHealthManagementGoalBiz.batchDeleteRs(indicatorJudgeHealthManagementGoalIdList);
    }

    @Operation(summary = "Rs获取查看指标健管目标类")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/getRs")
    public IndicatorJudgeHealthManagementGoalResponseRs getRs(@RequestParam @Validated String indicatorJudgeHealthManagementGoalId) {
        return indicatorJudgeHealthManagementGoalBiz.getRs(indicatorJudgeHealthManagementGoalId);
    }

    @Operation(summary = "Rs根据功能点id获取所有查看指标健管目标类")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/getRsByIndicatorFuncId")
    public List<IndicatorJudgeHealthManagementGoalResponseRs> getRsByIndicatorFuncId(@RequestParam String indicatorFuncId) {
        return indicatorJudgeHealthManagementGoalBiz.getRsByIndicatorFuncId(indicatorFuncId);
    }

//    /**
//    * 创建判断指标健管目标
//    * @param
//    * @return
//    */
//    @Operation(summary = "创建判断指标健管目标")
//    @PostMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/createIndicatorJudgeHealthManagementGoal")
//    public void createIndicatorJudgeHealthManagementGoal(@RequestBody @Validated CreateIndicatorJudgeHealthManagementGoalRequest createIndicatorJudgeHealthManagementGoal ) {
//        indicatorJudgeHealthManagementGoalBiz.createIndicatorJudgeHealthManagementGoal(createIndicatorJudgeHealthManagementGoal);
//    }
//
//    /**
//    * 删除判断指标健管目标
//    * @param
//    * @return
//    */
//    @Operation(summary = "删除判断指标健管目标")
//    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/deleteIndicatorJudgeHealthManagementGoal")
//    public void deleteIndicatorJudgeHealthManagementGoal(@Validated String indicatorJudgeHealthManagementGoalId ) {
//        indicatorJudgeHealthManagementGoalBiz.deleteIndicatorJudgeHealthManagementGoal(indicatorJudgeHealthManagementGoalId);
//    }
//
//    /**
//    * 批量删除
//    * @param
//    * @return
//    */
//    @Operation(summary = "批量删除")
//    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/batchDelete")
//    public void batchDelete(@Validated String string ) {
//        indicatorJudgeHealthManagementGoalBiz.batchDelete(string);
//    }
//
//    /**
//    * 更改启用状态
//    * @param
//    * @return
//    */
//    @Operation(summary = "更改启用状态")
//    @PutMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/updateStatus")
//    public void updateStatus(@Validated UpdateStatusIndicatorJudgeHealthManagementGoalRequest updateStatusIndicatorJudgeHealthManagementGoal ) {
//        indicatorJudgeHealthManagementGoalBiz.updateStatus(updateStatusIndicatorJudgeHealthManagementGoal);
//    }
//
//    /**
//    * 更新判断指标健管目标
//    * @param
//    * @return
//    */
//    @Operation(summary = "更新判断指标健管目标")
//    @PutMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/updateIndicatorJudgeHealthManagementGoal")
//    public void updateIndicatorJudgeHealthManagementGoal(@Validated UpdateIndicatorJudgeHealthManagementGoalRequest updateIndicatorJudgeHealthManagementGoal ) {
//        indicatorJudgeHealthManagementGoalBiz.updateIndicatorJudgeHealthManagementGoal(updateIndicatorJudgeHealthManagementGoal);
//    }
//
//    /**
//    * 获取判断指标健管目标
//    * @param
//    * @return
//    */
//    @Operation(summary = "获取判断指标健管目标")
//    @GetMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/getIndicatorJudgeHealthManagementGoal")
//    public IndicatorJudgeHealthManagementGoalResponse getIndicatorJudgeHealthManagementGoal(@Validated String indicatorJudgeHealthManagementGoalId) {
//        return indicatorJudgeHealthManagementGoalBiz.getIndicatorJudgeHealthManagementGoal(indicatorJudgeHealthManagementGoalId);
//    }
//
//    /**
//    * 筛选判断指标健管目标
//    * @param
//    * @return
//    */
//    @Operation(summary = "筛选判断指标健管目标")
//    @GetMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/listIndicatorJudgeHealthManagementGoal")
//    public List<IndicatorJudgeHealthManagementGoalResponse> listIndicatorJudgeHealthManagementGoal(@Validated String appId, @Validated String indicatorCategoryId, @Validated DecimalRequest point, @Validated Integer status) {
//        return indicatorJudgeHealthManagementGoalBiz.listIndicatorJudgeHealthManagementGoal(appId,indicatorCategoryId,point,status);
//    }
//
//    /**
//    * 获取判断指标健管目标
//    * @param
//    * @return
//    */
//    @Operation(summary = "获取判断指标健管目标")
//    @GetMapping("v1/baseIndicator/indicatorJudgeHealthManagementGoal/pageIndicatorJudgeHealthManagementGoal")
//    public String pageIndicatorJudgeHealthManagementGoal(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated DecimalRequest point, @Validated Integer status) {
//        return indicatorJudgeHealthManagementGoalBiz.pageIndicatorJudgeHealthManagementGoal(pageNo,pageSize,appId,indicatorCategoryId,point,status);
//    }


}