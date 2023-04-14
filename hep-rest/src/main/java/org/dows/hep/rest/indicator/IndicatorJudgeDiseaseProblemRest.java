package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorJudgeDiseaseProblemRequest;
import org.dows.hep.api.indicator.request.IndicatorJudgeDiseaseProblemIdRequest;
import org.dows.hep.api.indicator.request.UpdateStatusIndicatorJudgeDiseaseProblemRequest;
import org.dows.hep.api.indicator.request.IndicatorJudgeDiseaseProblemIdRequest;
import org.dows.hep.api.indicator.response.IndicatorJudgeDiseaseProblemResponse;
import org.dows.hep.biz.indicator.IndicatorJudgeDiseaseProblemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标疾病问题
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "判断指标疾病问题")
public class IndicatorJudgeDiseaseProblemRest {
    private final IndicatorJudgeDiseaseProblemBiz indicatorJudgeDiseaseProblemBiz;

    /**
    * 创建判断指标疾病问题
    * @param
    * @return
    */
    @ApiOperation("创建判断指标疾病问题")
    @PostMapping("v1/indicator/indicatorJudgeDiseaseProblem/createIndicatorJudgeDiseaseProblem")
    public void createIndicatorJudgeDiseaseProblem(@RequestBody @Validated CreateIndicatorJudgeDiseaseProblemRequest createIndicatorJudgeDiseaseProblem ) {
        indicatorJudgeDiseaseProblemBiz.createIndicatorJudgeDiseaseProblem(createIndicatorJudgeDiseaseProblem);
    }

    /**
    * 删除判断指标疾病问题
    * @param
    * @return
    */
    @ApiOperation("删除判断指标疾病问题")
    @DeleteMapping("v1/indicator/indicatorJudgeDiseaseProblem/deleteIndicatorJudgeDiseaseProblem")
    public void deleteIndicatorJudgeDiseaseProblem(@Validated IndicatorJudgeDiseaseProblemIdRequest indicatorJudgeDiseaseProblemId ) {
        indicatorJudgeDiseaseProblemBiz.deleteIndicatorJudgeDiseaseProblem(indicatorJudgeDiseaseProblemId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @ApiOperation("更改启用状态")
    @PutMapping("v1/indicator/indicatorJudgeDiseaseProblem/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeDiseaseProblemRequest updateStatusIndicatorJudgeDiseaseProblem ) {
        indicatorJudgeDiseaseProblemBiz.updateStatus(updateStatusIndicatorJudgeDiseaseProblem);
    }

    /**
    * 获取判断指标疾病问题
    * @param
    * @return
    */
    @ApiOperation("获取判断指标疾病问题")
    @GetMapping("v1/indicator/indicatorJudgeDiseaseProblem/getIndicatorJudgeDiseaseProblem")
    public IndicatorJudgeDiseaseProblemResponse getIndicatorJudgeDiseaseProblem(@Validated IndicatorJudgeDiseaseProblemIdRequest indicatorJudgeDiseaseProblemId) {
        return indicatorJudgeDiseaseProblemBiz.getIndicatorJudgeDiseaseProblem(indicatorJudgeDiseaseProblemId);
    }


}