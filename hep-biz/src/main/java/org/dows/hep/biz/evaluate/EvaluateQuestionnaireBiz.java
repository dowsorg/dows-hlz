package org.dows.hep.biz.evaluate;

import org.dows.framework.api.Response;
import org.dows.hep.api.evaluate.request.CreateEvaluateQuestionnaireRequest;
import org.dows.hep.api.evaluate.response.EvaluateQuestionnaireResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:评估:评估问卷
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
public class EvaluateQuestionnaireBiz{
    /**
    * @param
    * @return
    * @说明: 创建评估问卷
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void createEvaluateQuestionnaire(CreateEvaluateQuestionnaireRequest createEvaluateQuestionnaire ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除评估问卷
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public void deleteEvaluateQuestionnaire(String evaluateQuestionnaireId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 查询评估问卷
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public EvaluateQuestionnaireResponse getEvaluateQuestionnaire(String evaluateQuestionnaireId ) {
        return new EvaluateQuestionnaireResponse();
    }
}