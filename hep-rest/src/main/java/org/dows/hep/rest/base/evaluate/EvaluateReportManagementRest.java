package org.dows.hep.rest.base.evaluate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.evaluate.request.EvaluateReportRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateReportManagementResponse;
import org.dows.hep.biz.base.evaluate.EvaluateReportManagementBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:评估:评估报告管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
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
    public void evaluateReportManagement(@RequestBody @Validated EvaluateReportRequest createEvaluateReportManagement ) {
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
    * 获取评估报告管理
    * @param
    * @return
    */
    @Operation(summary = "获取评估报告管理")
    @GetMapping("v1/baseEvaluate/evaluateReportManagement/getEvaluateReportManagement")
    public EvaluateReportManagementResponse getEvaluateReportManagement(@Validated String evaluateReportManagementId) {
        return evaluateReportManagementBiz.getEvaluateReportManagement(evaluateReportManagementId);
    }

    /**
    * 筛选评估报告管理
    * @param
    * @return
    */
    @Operation(summary = "筛选评估报告管理")
    @GetMapping("v1/baseEvaluate/evaluateReportManagement/listEvaluateReportManagement")
    public List<EvaluateReportManagementResponse> listEvaluateReportManagement(@Validated String appId, @Validated String questionnaireId, @Validated String reportName, @Validated String reportDescr, @Validated String assessmentResult, @Validated String suggestion, @Validated Integer minScore, @Validated Integer maxScore) {
        return evaluateReportManagementBiz.listEvaluateReportManagement(appId,questionnaireId,reportName,reportDescr,assessmentResult,suggestion,minScore,maxScore);
    }

    /**
    * 分页筛选评估报告管理
    * @param
    * @return
    */
    @Operation(summary = "分页筛选评估报告管理")
    @GetMapping("v1/baseEvaluate/evaluateReportManagement/pageEvaluateReportManagement")
    public String pageEvaluateReportManagement(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String questionnaireId, @Validated String reportName, @Validated String reportDescr, @Validated String assessmentResult, @Validated String suggestion, @Validated Integer minScore, @Validated Integer maxScore) {
        return evaluateReportManagementBiz.pageEvaluateReportManagement(pageNo,pageSize,appId,questionnaireId,reportName,reportDescr,assessmentResult,suggestion,minScore,maxScore);
    }


}