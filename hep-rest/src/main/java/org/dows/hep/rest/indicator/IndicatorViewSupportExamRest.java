package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorViewSupportExamRequest;
import org.dows.hep.api.indicator.request.IndicatorViewSupportExamIdRequest;
import org.dows.hep.api.indicator.request.IndicatorViewSupportExamIdRequest;
import org.dows.hep.api.indicator.response.IndicatorViewSupportExamResponse;
import org.dows.hep.biz.indicator.IndicatorViewSupportExamBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标辅助检查类
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "查看指标辅助检查类")
public class IndicatorViewSupportExamRest {
    private final IndicatorViewSupportExamBiz indicatorViewSupportExamBiz;

    /**
    * 创建查看指标辅助检查类
    * @param
    * @return
    */
    @ApiOperation("创建查看指标辅助检查类")
    @PostMapping("v1/indicator/indicatorViewSupportExam/createIndicatorViewSupportExam")
    public void createIndicatorViewSupportExam(@RequestBody @Validated CreateIndicatorViewSupportExamRequest createIndicatorViewSupportExam ) {
        indicatorViewSupportExamBiz.createIndicatorViewSupportExam(createIndicatorViewSupportExam);
    }

    /**
    * 删除查看指标辅助检查类
    * @param
    * @return
    */
    @ApiOperation("删除查看指标辅助检查类")
    @DeleteMapping("v1/indicator/indicatorViewSupportExam/deleteIndicatorViewSupportExam")
    public void deleteIndicatorViewSupportExam(@Validated IndicatorViewSupportExamIdRequest indicatorViewSupportExamId ) {
        indicatorViewSupportExamBiz.deleteIndicatorViewSupportExam(indicatorViewSupportExamId);
    }

    /**
    * 查看指标辅助检查类
    * @param
    * @return
    */
    @ApiOperation("查看指标辅助检查类")
    @GetMapping("v1/indicator/indicatorViewSupportExam/getIndicatorViewSupportExam")
    public IndicatorViewSupportExamResponse getIndicatorViewSupportExam(@Validated IndicatorViewSupportExamIdRequest indicatorViewSupportExamId) {
        return indicatorViewSupportExamBiz.getIndicatorViewSupportExam(indicatorViewSupportExamId);
    }


}