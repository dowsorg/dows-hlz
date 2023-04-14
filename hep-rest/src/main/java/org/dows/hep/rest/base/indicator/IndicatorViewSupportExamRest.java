package org.dows.hep.rest.base.indicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewSupportExamRequest;
import org.dows.hep.api.base.indicator.request.IndicatorViewSupportExamRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorViewSupportExamRequest;
import org.dows.hep.api.base.indicator.response.IndicatorViewSupportExamResponse;
import org.dows.hep.biz.base.indicator.IndicatorViewSupportExamBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:指标:查看指标辅助检查类
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "查看指标辅助检查类", description = "查看指标辅助检查类")
public class IndicatorViewSupportExamRest {
    private final IndicatorViewSupportExamBiz indicatorViewSupportExamBiz;

    /**
    * 创建查看指标辅助检查类
    * @param
    * @return
    */
    @Operation(summary = "创建查看指标辅助检查类")
    @PostMapping("v1/baseIndicator/indicatorViewSupportExam/createIndicatorViewSupportExam")
    public void createIndicatorViewSupportExam(@RequestBody @Validated CreateIndicatorViewSupportExamRequest createIndicatorViewSupportExam ) {
        indicatorViewSupportExamBiz.createIndicatorViewSupportExam(createIndicatorViewSupportExam);
    }

    /**
    * 删除查看指标辅助检查类
    * @param
    * @return
    */
    @Operation(summary = "删除查看指标辅助检查类")
    @DeleteMapping("v1/baseIndicator/indicatorViewSupportExam/deleteIndicatorViewSupportExam")
    public void deleteIndicatorViewSupportExam(@Validated String indicatorViewSupportExamId ) {
        indicatorViewSupportExamBiz.deleteIndicatorViewSupportExam(indicatorViewSupportExamId);
    }

    /**
    * 更改启用状态
    * @param
    * @return
    */
    @Operation(summary = "更改启用状态")
    @PutMapping("v1/baseIndicator/indicatorViewSupportExam/updateStatus")
    public void updateStatus(@Validated IndicatorViewSupportExamRequest indicatorViewSupportExam ) {
        indicatorViewSupportExamBiz.updateStatus(indicatorViewSupportExam);
    }

    /**
    * 查看指标辅助检查类
    * @param
    * @return
    */
    @Operation(summary = "查看指标辅助检查类")
    @PutMapping("v1/baseIndicator/indicatorViewSupportExam/updateIndicatorViewSupportExam")
    public void updateIndicatorViewSupportExam(@Validated UpdateIndicatorViewSupportExamRequest updateIndicatorViewSupportExam ) {
        indicatorViewSupportExamBiz.updateIndicatorViewSupportExam(updateIndicatorViewSupportExam);
    }

    /**
    * 查看指标辅助检查类
    * @param
    * @return
    */
    @Operation(summary = "查看指标辅助检查类")
    @GetMapping("v1/baseIndicator/indicatorViewSupportExam/getIndicatorViewSupportExam")
    public IndicatorViewSupportExamResponse getIndicatorViewSupportExam(@Validated String indicatorViewSupportExamId) {
        return indicatorViewSupportExamBiz.getIndicatorViewSupportExam(indicatorViewSupportExamId);
    }

    /**
    * 分页获取查看指标辅助检查类
    * @param
    * @return
    */
    @Operation(summary = "分页获取查看指标辅助检查类")
    @GetMapping("v1/baseIndicator/indicatorViewSupportExam/pageIndicatorViewSupportExam")
    public void pageIndicatorViewSupportExam(@Validated String todo) {
        indicatorViewSupportExamBiz.pageIndicatorViewSupportExam(todo);
    }


}