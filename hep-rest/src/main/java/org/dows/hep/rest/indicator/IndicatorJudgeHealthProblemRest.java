package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.indicator.request.IndicatorJudgeHealthProblemIdRequest;
import org.dows.hep.api.indicator.request.UpdateStatusIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.indicator.request.IndicatorJudgeHealthProblemIdRequest;
import org.dows.hep.api.indicator.response.IndicatorJudgeHealthProblemResponse;
import org.dows.hep.biz.indicator.IndicatorJudgeHealthProblemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:判断指标健康问题
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "判断指标健康问题")
public class IndicatorJudgeHealthProblemRest {
    private final IndicatorJudgeHealthProblemBiz indicatorJudgeHealthProblemBiz;

    /**
    * 创建判断指标健康问题
    * @param
    * @return
    */
    @ApiOperation("创建判断指标健康问题")
    @PostMapping("v1/indicator/indicatorJudgeHealthProblem/createIndicatorJudgeHealthProblem")
    public void createIndicatorJudgeHealthProblem(@RequestBody @Validated CreateIndicatorJudgeHealthProblemRequest createIndicatorJudgeHealthProblem ) {
        indicatorJudgeHealthProblemBiz.createIndicatorJudgeHealthProblem(createIndicatorJudgeHealthProblem);
    }

    /**
    * 删除判断指标健康问题
    * @param
    * @return
    */
    @ApiOperation("删除判断指标健康问题")
    @DeleteMapping("v1/indicator/indicatorJudgeHealthProblem/deleteIndicatorJudgeHealthProblem")
    public void deleteIndicatorJudgeHealthProblem(@Validated IndicatorJudgeHealthProblemIdRequest indicatorJudgeHealthProblemId ) {
        indicatorJudgeHealthProblemBiz.deleteIndicatorJudgeHealthProblem(indicatorJudgeHealthProblemId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @ApiOperation("更改启用状态")
    @PutMapping("v1/indicator/indicatorJudgeHealthProblem/updateStatus")
    public void updateStatus(@Validated UpdateStatusIndicatorJudgeHealthProblemRequest updateStatusIndicatorJudgeHealthProblem ) {
        indicatorJudgeHealthProblemBiz.updateStatus(updateStatusIndicatorJudgeHealthProblem);
    }

    /**
    * 获取判断指标健康问题
    * @param
    * @return
    */
    @ApiOperation("获取判断指标健康问题")
    @GetMapping("v1/indicator/indicatorJudgeHealthProblem/getIndicatorJudgeHealthProblem")
    public IndicatorJudgeHealthProblemResponse getIndicatorJudgeHealthProblem(@Validated IndicatorJudgeHealthProblemIdRequest indicatorJudgeHealthProblemId) {
        return indicatorJudgeHealthProblemBiz.getIndicatorJudgeHealthProblem(indicatorJudgeHealthProblemId);
    }


}