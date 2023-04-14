package org.dows.hep.biz.base.evaluate;

import org.dows.framework.api.Response;
import org.dows.hep.api.base.evaluate.request.CreateEvaluateDimensionExpressionRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateDimensionExpressionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:评估:评估维度公式
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class EvaluateDimensionExpressionBiz{
    /**
    * @param
    * @return
    * @说明: 创建评估维度公式
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void evaluateDimensionExpression(CreateEvaluateDimensionExpressionRequest createEvaluateDimensionExpression ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除评估维度公式
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public void deleteEvaluateDimensionExpression(String evaluateDimensionExpressionId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查看评估维度公式
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public EvaluateDimensionExpressionResponse getEvaluateDimensionExpression(String evaluateDimensionExpressionId ) {
        return new EvaluateDimensionExpressionResponse();
    }
}