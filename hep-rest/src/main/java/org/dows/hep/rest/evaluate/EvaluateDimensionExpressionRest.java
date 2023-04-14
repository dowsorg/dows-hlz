package org.dows.hep.rest.evaluate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.evaluate.request.CreateEvaluateDimensionExpressionRequest;
import org.dows.hep.api.evaluate.response.EvaluateDimensionExpressionResponse;
import org.dows.hep.biz.evaluate.EvaluateDimensionExpressionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:评估:评估维度公式
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "评估维度公式")
public class EvaluateDimensionExpressionRest {
    private final EvaluateDimensionExpressionBiz evaluateDimensionExpressionBiz;

    /**
    * 创建评估维度公式
    * @param
    * @return
    */
    @ApiOperation("创建评估维度公式")
    @PostMapping("v1/evaluate/evaluateDimensionExpression/evaluateDimensionExpression")
    public void evaluateDimensionExpression(@RequestBody @Validated CreateEvaluateDimensionExpressionRequest createEvaluateDimensionExpression ) {
        evaluateDimensionExpressionBiz.evaluateDimensionExpression(createEvaluateDimensionExpression);
    }

    /**
    * 删除评估维度公式
    * @param
    * @return
    */
    @ApiOperation("删除评估维度公式")
    @DeleteMapping("v1/evaluate/evaluateDimensionExpression/deleteEvaluateDimensionExpression")
    public void deleteEvaluateDimensionExpression(@Validated String evaluateDimensionExpressionId ) {
        evaluateDimensionExpressionBiz.deleteEvaluateDimensionExpression(evaluateDimensionExpressionId);
    }

    /**
    * 查看评估维度公式
    * @param
    * @return
    */
    @ApiOperation("查看评估维度公式")
    @GetMapping("v1/evaluate/evaluateDimensionExpression/getEvaluateDimensionExpression")
    public EvaluateDimensionExpressionResponse getEvaluateDimensionExpression(@Validated String evaluateDimensionExpressionId) {
        return evaluateDimensionExpressionBiz.getEvaluateDimensionExpression(evaluateDimensionExpressionId);
    }


}