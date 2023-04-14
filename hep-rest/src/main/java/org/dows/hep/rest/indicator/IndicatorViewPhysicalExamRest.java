package org.dows.hep.rest.indicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.indicator.request.CreateIndicatorViewPhysicalExamRequest;
import org.dows.hep.api.indicator.request.IndicatorViewPhysicalExamRequest;
import org.dows.hep.api.indicator.request.UpdateIndicatorViewPhysicalExamRequest;
import org.dows.hep.api.indicator.response.IndicatorViewPhysicalExamResponse;
import org.dows.hep.biz.indicator.IndicatorViewPhysicalExamBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标体格检查类
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "查看指标体格检查类")
public class IndicatorViewPhysicalExamRest {
    private final IndicatorViewPhysicalExamBiz indicatorViewPhysicalExamBiz;

    /**
    * 创建查看指标体格检查类
    * @param
    * @return
    */
    @ApiOperation("创建查看指标体格检查类")
    @PostMapping("v1/indicator/indicatorViewPhysicalExam/createIndicatorViewPhysicalExam")
    public void createIndicatorViewPhysicalExam(@RequestBody @Validated CreateIndicatorViewPhysicalExamRequest createIndicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.createIndicatorViewPhysicalExam(createIndicatorViewPhysicalExam);
    }

    /**
    * 删除指标体格检查类
    * @param
    * @return
    */
    @ApiOperation("删除指标体格检查类")
    @DeleteMapping("v1/indicator/indicatorViewPhysicalExam/deleteIndicatorViewPhysicalExam")
    public void deleteIndicatorViewPhysicalExam(@Validated String indicatorViewPhysicalExamId ) {
        indicatorViewPhysicalExamBiz.deleteIndicatorViewPhysicalExam(indicatorViewPhysicalExamId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @ApiOperation("更改启用状态")
    @PutMapping("v1/indicator/indicatorViewPhysicalExam/updateStatus")
    public void updateStatus(@Validated IndicatorViewPhysicalExamRequest indicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.updateStatus(indicatorViewPhysicalExam);
    }

    /**
    * 查看指标体格检查类
    * @param
    * @return
    */
    @ApiOperation("查看指标体格检查类")
    @PutMapping("v1/indicator/indicatorViewPhysicalExam/updateIndicatorViewPhysicalExam")
    public void updateIndicatorViewPhysicalExam(@Validated UpdateIndicatorViewPhysicalExamRequest updateIndicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.updateIndicatorViewPhysicalExam(updateIndicatorViewPhysicalExam);
    }

    /**
    * 获取查看指标体格检查类
    * @param
    * @return
    */
    @ApiOperation("获取查看指标体格检查类")
    @GetMapping("v1/indicator/indicatorViewPhysicalExam/getIndicatorViewPhysicalExam")
    public IndicatorViewPhysicalExamResponse getIndicatorViewPhysicalExam(@Validated String indicatorViewPhysicalExamId) {
        return indicatorViewPhysicalExamBiz.getIndicatorViewPhysicalExam(indicatorViewPhysicalExamId);
    }


}