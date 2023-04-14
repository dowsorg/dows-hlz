package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeDiseaseProblemRequest;
import org.dows.hep.api.base.indicator.request.UpdateStatusIndicatorJudgeDiseaseProblemRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorJudgeDiseaseProblemRequest;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeDiseaseProblemResponse;
import org.dows.hep.biz.base.indicator.IndicatorJudgeDiseaseProblemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标疾病问题
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "判断指标疾病问题", description = "判断指标疾病问题")
public class IndicatorJudgeDiseaseProblemRest {
    private final IndicatorJudgeDiseaseProblemBiz indicatorJudgeDiseaseProblemBiz;

    /**
    * 创建判断指标疾病问题
    * @param
    * @return
    */
    @Operation(summary = "创建判断指标疾病问题")
    @PostMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/createIndicatorJudgeDiseaseProblem")
    public void createIndicatorJudgeDiseaseProblem(@RequestBody @Validated CreateIndicatorJudgeDiseaseProblemRequest createIndicatorJudgeDiseaseProblem ) {
        indicatorJudgeDiseaseProblemBiz.createIndicatorJudgeDiseaseProblem(createIndicatorJudgeDiseaseProblem);
    }

    /**
    * 删除判断指标疾病问题
    * @param
    * @return
    */
    @Operation(summary = "删除判断指标疾病问题")
    @DeleteMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/deleteIndicatorJudgeDiseaseProblem")
    public void deleteIndicatorJudgeDiseaseProblem(@Validated String indicatorJudgeDiseaseProblemId ) {
        indicatorJudgeDiseaseProblemBiz.deleteIndicatorJudgeDiseaseProblem(indicatorJudgeDiseaseProblemId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeDiseaseProblemRequest updateStatusIndicatorJudgeDiseaseProblem ) {
        indicatorJudgeDiseaseProblemBiz.updateStatus(updateStatusIndicatorJudgeDiseaseProblem);
    }

    /**
    * 判断指标疾病问题
    * @param
    * @return
    */
    @Operation(summary = "判断指标疾病问题")
    @PutMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/updateIndicatorJudgeDiseaseProblem")
    public void updateIndicatorJudgeDiseaseProblem(@Validated UpdateIndicatorJudgeDiseaseProblemRequest updateIndicatorJudgeDiseaseProblem ) {
        indicatorJudgeDiseaseProblemBiz.updateIndicatorJudgeDiseaseProblem(updateIndicatorJudgeDiseaseProblem);
    }

    /**
    * 获取判断指标疾病问题
    * @param
    * @return
    */
    @Operation(summary = "获取判断指标疾病问题")
    @GetMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/getIndicatorJudgeDiseaseProblem")
    public IndicatorJudgeDiseaseProblemResponse getIndicatorJudgeDiseaseProblem(@Validated String indicatorJudgeDiseaseProblemId) {
        return indicatorJudgeDiseaseProblemBiz.getIndicatorJudgeDiseaseProblem(indicatorJudgeDiseaseProblemId);
    }

    /**
    * 分页获取判断指标疾病问题
    * @param
    * @return
    */
    @Operation(summary = "分页获取判断指标疾病问题")
    @GetMapping("v1/baseIndicator/indicatorJudgeDiseaseProblem/pageIndicatorJudgeDiseaseProblem")
    public void pageIndicatorJudgeDiseaseProblem(@Validated String todo) {
        indicatorJudgeDiseaseProblemBiz.pageIndicatorJudgeDiseaseProblem(todo);
    }


}