package org.dows.hep.rest.evaluate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.evaluate.request.CreateEvaluateReportManagementRequest;
import org.dows.hep.api.evaluate.response.EvaluateReportManagementResponse;
import org.dows.hep.biz.evaluate.EvaluateReportManagementBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:评估:评估报告管理
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "评估报告管理")
public class EvaluateReportManagementRest {
    private final EvaluateReportManagementBiz evaluateReportManagementBiz;

    /**
    * 创建评估报告管理
    * @param
    * @return
    */
    @ApiOperation("创建评估报告管理")
    @PostMapping("v1/evaluate/evaluateReportManagement/evaluateReportManagement")
    public void evaluateReportManagement(@RequestBody @Validated CreateEvaluateReportManagementRequest createEvaluateReportManagement ) {
        evaluateReportManagementBiz.evaluateReportManagement(createEvaluateReportManagement);
    }

    /**
    * 删除评估报告管理
    * @param
    * @return
    */
    @ApiOperation("删除评估报告管理")
    @DeleteMapping("v1/evaluate/evaluateReportManagement/deleteEvaluateReportManagement")
    public void deleteEvaluateReportManagement(@Validated String evaluateReportManagementId ) {
        evaluateReportManagementBiz.deleteEvaluateReportManagement(evaluateReportManagementId);
    }

    /**
    * 查看评估报告管理
    * @param
    * @return
    */
    @ApiOperation("查看评估报告管理")
    @GetMapping("v1/evaluate/evaluateReportManagement/getEvaluateReportManagement")
    public EvaluateReportManagementResponse getEvaluateReportManagement(@Validated String evaluateReportManagementId) {
        return evaluateReportManagementBiz.getEvaluateReportManagement(evaluateReportManagementId);
    }


}