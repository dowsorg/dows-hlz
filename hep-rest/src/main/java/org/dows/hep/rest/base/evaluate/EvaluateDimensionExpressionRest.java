package org.dows.hep.rest.base.evaluate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.evaluate.request.EvaluateDimensionExpressionRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateDimensionExpressionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.biz.base.evaluate.EvaluateDimensionExpressionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description project descr:评估:评估维度公式
 *
 * @author lait.zhang
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "评估维度公式", description = "评估维度公式")
public class EvaluateDimensionExpressionRest {
    private final EvaluateDimensionExpressionBiz evaluateDimensionExpressionBiz;

    /**
     * 根据问题集ID维度公式
     *
     * @param
     * @return
     */
    @Operation(summary = "根据问题集ID维度公式")
    @GetMapping("v1/baseEvaluate/evaluateDimensionExpression/listSectionDimension")
    public List<QuestionSectionDimensionResponse> listSectionDimension(String questionSectionId) {
        return evaluateDimensionExpressionBiz.listSectionDimension(questionSectionId);
    }

    /**
     * 新增或更新评估维度公式
     *
     * @param
     * @return
     */
    @Operation(summary = "新增或更新评估维度公式")
    @PostMapping("v1/baseEvaluate/evaluateDimensionExpression/saveOrUpdDimensionExpression")
    public Boolean saveOrUpdDimensionExpression(@RequestBody @Validated List<EvaluateDimensionExpressionRequest> requests) {
        return evaluateDimensionExpressionBiz.saveOrUpdDimensionExpression(requests);
    }

    /**
     * 列出问卷下评估维度公式
     *
     * @param
     * @return
     */
    @Operation(summary = "列出问卷下评估维度公式")
    @GetMapping("v1/baseEvaluate/evaluateDimensionExpression/listByQuestionnaireId")
    public List<EvaluateDimensionExpressionResponse> listByQuestionnaireId(String evaluateQuestionnaireId) {
        return evaluateDimensionExpressionBiz.listByQuestionnaireId(evaluateQuestionnaireId);
    }

    /**
    * 获取评估维度公式
    * @param
    * @return
    */
    @Operation(summary = "获取评估维度公式")
    @GetMapping("v1/baseEvaluate/evaluateDimensionExpression/getEvaluateDimensionExpression")
    public EvaluateDimensionExpressionResponse getEvaluateDimensionExpression(@Validated String evaluateDimensionExpressionId) {
        return evaluateDimensionExpressionBiz.getEvaluateDimensionExpression(evaluateDimensionExpressionId);
    }

    /**
     * 删除or批量删除评估维度公式
     *
     * @param
     * @return
     */
    @Operation(summary = "删除or批量删除评估维度公式")
    @DeleteMapping("v1/baseEvaluate/evaluateDimensionExpression/deleteEvaluateDimensionExpression")
    public Boolean deleteEvaluateDimensionExpression(@RequestBody List<String> ids) {
        return evaluateDimensionExpressionBiz.deleteEvaluateDimensionExpression(ids);
    }


}