package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewPhysicalExamRequest;
import org.dows.hep.api.base.indicator.request.IndicatorViewPhysicalExamRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewPhysicalExamRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewPhysicalExamResponse;
import org.dows.hep.biz.base.indicator.IndicatorViewPhysicalExamBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标体格检查类
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "查看指标体格检查类", description = "查看指标体格检查类")
public class IndicatorViewPhysicalExamRest {
    private final IndicatorViewPhysicalExamBiz indicatorViewPhysicalExamBiz;

    /**
    * 创建查看指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "创建查看指标体格检查类")
    @PostMapping("v1/baseIndicator/indicatorViewPhysicalExam/createIndicatorViewPhysicalExam")
    public void createIndicatorViewPhysicalExam(@RequestBody @Validated CreateIndicatorViewPhysicalExamRequest createIndicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.createIndicatorViewPhysicalExam(createIndicatorViewPhysicalExam);
    }

    /**
    * 删除指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "删除指标体格检查类")
    @DeleteMapping("v1/baseIndicator/indicatorViewPhysicalExam/deleteIndicatorViewPhysicalExam")
    public void deleteIndicatorViewPhysicalExam(@Validated String indicatorViewPhysicalExamId ) {
        indicatorViewPhysicalExamBiz.deleteIndicatorViewPhysicalExam(indicatorViewPhysicalExamId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorViewPhysicalExam/updateStatus")
    public void updateStatus(@Validated IndicatorViewPhysicalExamRequest indicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.updateStatus(indicatorViewPhysicalExam);
    }

    /**
    * 查看指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "查看指标体格检查类")
    @PutMapping("v1/baseIndicator/indicatorViewPhysicalExam/updateIndicatorViewPhysicalExam")
    public void updateIndicatorViewPhysicalExam(@Validated UpdateIndicatorViewPhysicalExamRequest updateIndicatorViewPhysicalExam ) {
        indicatorViewPhysicalExamBiz.updateIndicatorViewPhysicalExam(updateIndicatorViewPhysicalExam);
    }

    /**
    * 获取查看指标体格检查类
    * @param
    * @return
    */
    @Operation(summary = "获取查看指标体格检查类")
    @GetMapping("v1/baseIndicator/indicatorViewPhysicalExam/getIndicatorViewPhysicalExam")
    public IndicatorViewPhysicalExamResponse getIndicatorViewPhysicalExam(@Validated String indicatorViewPhysicalExamId) {
        return indicatorViewPhysicalExamBiz.getIndicatorViewPhysicalExam(indicatorViewPhysicalExamId);
    }

    /**
    * 分页获取查看指标监测随访类
    * @param
    * @return
    */
    @Operation(summary = "分页获取查看指标监测随访类")
    @GetMapping("v1/baseIndicator/indicatorViewPhysicalExam/pageIndicatorViewPhysicalExam")
    public void pageIndicatorViewPhysicalExam(@Validated String todo) {
        indicatorViewPhysicalExamBiz.pageIndicatorViewPhysicalExam(todo);
    }


}