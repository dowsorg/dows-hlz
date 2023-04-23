package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.request.DecimalRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthProblemResponse;
import org.dows.hep.biz.base.indicator.IndicatorJudgeHealthProblemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标健康问题
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标健康问题", description = "判断指标健康问题")
public class IndicatorJudgeHealthProblemRest {
    private final IndicatorJudgeHealthProblemBiz indicatorJudgeHealthProblemBiz;

    /**
    * 创建判断指标健康问题
    * @param
    * @return
    */
    @Operation(summary = "创建判断指标健康问题")
    @PostMapping("v1/baseIndicator/indicatorJudgeHealthProblem/createIndicatorJudgeHealthProblem")
    public void createIndicatorJudgeHealthProblem(@RequestBody @Validated CreateIndicatorJudgeHealthProblemRequest createIndicatorJudgeHealthProblem ) {
        indicatorJudgeHealthProblemBiz.createIndicatorJudgeHealthProblem(createIndicatorJudgeHealthProblem);
    }

    /**
    * 删除判断指标健康问题
    * @param
    * @return
    */
    @Operation(summary = "删除判断指标健康问题")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthProblem/deleteIndicatorJudgeHealthProblem")
    public void deleteIndicatorJudgeHealthProblem(@Validated String indicatorJudgeHealthProblemId ) {
        indicatorJudgeHealthProblemBiz.deleteIndicatorJudgeHealthProblem(indicatorJudgeHealthProblemId);
    }

    /**
    * 批量删除
    * @param
    * @return
    */
    @Operation(summary = "批量删除")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeHealthProblem/batchDelete")
    public void batchDelete(@Validated String string ) {
        indicatorJudgeHealthProblemBiz.batchDelete(string);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorJudgeHealthProblem/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeHealthProblemRequest updateStatusIndicatorJudgeHealthProblem ) {
        indicatorJudgeHealthProblemBiz.updateStatus(updateStatusIndicatorJudgeHealthProblem);
    }

    /**
    * 判断指标健康问题
    * @param
    * @return
    */
    @Operation(summary = "判断指标健康问题")
    @PutMapping("v1/baseIndicator/indicatorJudgeHealthProblem/updateIndicatorJudgeHealthProblem")
    public void updateIndicatorJudgeHealthProblem(@Validated UpdateIndicatorJudgeHealthProblemRequest updateIndicatorJudgeHealthProblem ) {
        indicatorJudgeHealthProblemBiz.updateIndicatorJudgeHealthProblem(updateIndicatorJudgeHealthProblem);
    }

    /**
    * 获取判断指标健康问题
    * @param
    * @return
    */
    @Operation(summary = "获取判断指标健康问题")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthProblem/getIndicatorJudgeHealthProblem")
    public IndicatorJudgeHealthProblemResponse getIndicatorJudgeHealthProblem(@Validated String indicatorJudgeHealthProblemId) {
        return indicatorJudgeHealthProblemBiz.getIndicatorJudgeHealthProblem(indicatorJudgeHealthProblemId);
    }

    /**
    * 筛选判断指标健康问题
    * @param
    * @return
    */
    @Operation(summary = "筛选判断指标健康问题")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthProblem/listIndicatorJudgeHealthProblem")
    public List<IndicatorJudgeHealthProblemResponse> listIndicatorJudgeHealthProblem(@Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
        return indicatorJudgeHealthProblemBiz.listIndicatorJudgeHealthProblem(appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
    }

    /**
    * 分页筛选判断指标健康问题
    * @param
    * @return
    */
    @Operation(summary = "分页筛选判断指标健康问题")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthProblem/pageIndicatorJudgeHealthProblem")
    public String pageIndicatorJudgeHealthProblem(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String indicatorCategoryId, @Validated String name, @Validated String type, @Validated DecimalRequest point, @Validated String expression, @Validated String resultExplain, @Validated Integer status) {
        return indicatorJudgeHealthProblemBiz.pageIndicatorJudgeHealthProblem(pageNo,pageSize,appId,indicatorCategoryId,name,type,point,expression,resultExplain,status);
    }


}