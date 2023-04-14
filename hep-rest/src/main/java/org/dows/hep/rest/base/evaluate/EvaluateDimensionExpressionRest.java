package org.dows.hep.rest.base.evaluate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.evaluate.request.CreateEvaluateDimensionExpressionRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateDimensionExpressionResponse;
import org.dows.hep.biz.base.evaluate.EvaluateDimensionExpressionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:评估:评估维度公式
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "评估维度公式", description = "评估维度公式")
public class EvaluateDimensionExpressionRest {
    private final EvaluateDimensionExpressionBiz evaluateDimensionExpressionBiz;

    /**
    * 创建评估维度公式
    * @param
    * @return
    */
    @Operation(summary = "创建评估维度公式")
    @PostMapping("v1/baseEvaluate/evaluateDimensionExpression/evaluateDimensionExpression")
    public void evaluateDimensionExpression(@RequestBody @Validated CreateEvaluateDimensionExpressionRequest createEvaluateDimensionExpression ) {
        evaluateDimensionExpressionBiz.evaluateDimensionExpression(createEvaluateDimensionExpression);
    }

    /**
    * 删除评估维度公式
    * @param
    * @return
    */
    @Operation(summary = "删除评估维度公式")
    @DeleteMapping("v1/baseEvaluate/evaluateDimensionExpression/deleteEvaluateDimensionExpression")
    public void deleteEvaluateDimensionExpression(@Validated String evaluateDimensionExpressionId ) {
        evaluateDimensionExpressionBiz.deleteEvaluateDimensionExpression(evaluateDimensionExpressionId);
    }

    /**
    * 查看评估维度公式
    * @param
    * @return
    */
    @Operation(summary = "查看评估维度公式")
    @GetMapping("v1/baseEvaluate/evaluateDimensionExpression/getEvaluateDimensionExpression")
    public EvaluateDimensionExpressionResponse getEvaluateDimensionExpression(@Validated String evaluateDimensionExpressionId) {
        return evaluateDimensionExpressionBiz.getEvaluateDimensionExpression(evaluateDimensionExpressionId);
    }


}