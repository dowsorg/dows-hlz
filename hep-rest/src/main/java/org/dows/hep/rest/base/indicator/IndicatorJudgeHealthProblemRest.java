package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthProblemResponse;
import org.dows.hep.biz.base.indicator.IndicatorJudgeHealthProblemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标健康问题
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
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
    * 分页获取判断指标健康问题
    * @param
    * @return
    */
    @Operation(summary = "分页获取判断指标健康问题")
    @GetMapping("v1/baseIndicator/indicatorJudgeHealthProblem/pageIndicatorJudgeHealthProblem")
    public void pageIndicatorJudgeHealthProblem(@Validated String todo) {
        indicatorJudgeHealthProblemBiz.pageIndicatorJudgeHealthProblem(todo);
    }


}