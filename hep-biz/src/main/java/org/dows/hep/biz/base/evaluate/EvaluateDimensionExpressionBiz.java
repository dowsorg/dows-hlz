package org.dows.hep.biz.base.evaluate;

import org.dows.hep.api.base.evaluate.request.EvaluateDimensionExpressionRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateDimensionExpressionResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:评估:评估维度公式
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void evaluateDimensionExpression(EvaluateDimensionExpressionRequest createEvaluateDimensionExpression ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除评估维度公式
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteEvaluateDimensionExpression(String evaluateDimensionExpressionId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取评估维度公式
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public EvaluateDimensionExpressionResponse getEvaluateDimensionExpression(String evaluateDimensionExpressionId ) {
        return new EvaluateDimensionExpressionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选评估维度公式
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<EvaluateDimensionExpressionResponse> listEvaluateDimensionExpression(String appId, String questionnaireId, String dimensionId, String expression ) {
        return new ArrayList<EvaluateDimensionExpressionResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选评估维度公式
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageEvaluateDimensionExpression(Integer pageNo, Integer pageSize, String appId, String questionnaireId, String dimensionId, String expression ) {
        return new String();
    }
}