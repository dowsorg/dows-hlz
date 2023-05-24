package org.dows.hep.rest.base.evaluate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.evaluate.request.EvaluateReportRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateReportManagementResponse;
import org.dows.hep.biz.base.evaluate.EvaluateReportBiz;
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
    private final EvaluateReportBiz evaluateReportBiz;

    /**
    * 新增或更新评估报告管理
    * @param
    * @return
    */
    @Operation(summary = "新增或更新评估报告管理")
    @PostMapping("v1/baseEvaluate/evaluateReportManagement/saveOrUpdEvaluateReport")
    public Boolean saveOrUpdEvaluateReport(@RequestBody @Validated List<EvaluateReportRequest> requests ) {
        return evaluateReportBiz.saveOrUpdEvaluateReport(requests);
    }

    /**
     * 获取问卷下评估报告
     * @param
     * @return
     */
    @Operation(summary = "获取问卷下评估报告")
    @GetMapping("v1/baseEvaluate/evaluateReportManagement/listByQuestionnaireId")
    public List<EvaluateReportManagementResponse> listByQuestionnaireId(String evaluateQuestionnaireId) {
        return evaluateReportBiz.listByQuestionnaireId(evaluateQuestionnaireId);
    }

    /**
     * 获取评估报告管理
     * @param
     * @return
     */
    @Operation(summary = "获取评估报告管理")
    @GetMapping("v1/baseEvaluate/evaluateReportManagement/getEvaluateReportManagement")
    public EvaluateReportManagementResponse getEvaluateReportManagement(@Validated String evaluateReportManagementId) {
        return evaluateReportBiz.getEvaluateReportManagement(evaluateReportManagementId);
    }

    /**
    * 删除or批量删除评估报告管理
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除评估报告管理")
    @DeleteMapping("v1/baseEvaluate/evaluateReportManagement/deleteEvaluateReportManagement")
    public Boolean deleteEvaluateReportManagement(@RequestBody List<String> evaluateReportManagementIds ) {
        return evaluateReportBiz.deleteEvaluateReportManagement(evaluateReportManagementIds);
    }

}