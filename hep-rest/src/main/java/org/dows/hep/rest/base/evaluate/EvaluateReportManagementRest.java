package org.dows.hep.rest.base.evaluate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.evaluate.request.CreateEvaluateReportManagementRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateReportManagementResponse;
import org.dows.hep.biz.base.evaluate.EvaluateReportManagementBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:评估:评估报告管理
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "评估报告管理", description = "评估报告管理")
public class EvaluateReportManagementRest {
    private final EvaluateReportManagementBiz evaluateReportManagementBiz;

    /**
    * 创建评估报告管理
    * @param
    * @return
    */
    @Operation(summary = "创建评估报告管理")
    @PostMapping("v1/baseEvaluate/evaluateReportManagement/evaluateReportManagement")
    public void evaluateReportManagement(@RequestBody @Validated CreateEvaluateReportManagementRequest createEvaluateReportManagement ) {
        evaluateReportManagementBiz.evaluateReportManagement(createEvaluateReportManagement);
    }

    /**
    * 删除评估报告管理
    * @param
    * @return
    */
    @Operation(summary = "删除评估报告管理")
    @DeleteMapping("v1/baseEvaluate/evaluateReportManagement/deleteEvaluateReportManagement")
    public void deleteEvaluateReportManagement(@Validated String evaluateReportManagementId ) {
        evaluateReportManagementBiz.deleteEvaluateReportManagement(evaluateReportManagementId);
    }

    /**
    * 查看评估报告管理
    * @param
    * @return
    */
    @Operation(summary = "查看评估报告管理")
    @GetMapping("v1/baseEvaluate/evaluateReportManagement/getEvaluateReportManagement")
    public EvaluateReportManagementResponse getEvaluateReportManagement(@Validated String evaluateReportManagementId) {
        return evaluateReportManagementBiz.getEvaluateReportManagement(evaluateReportManagementId);
    }


}