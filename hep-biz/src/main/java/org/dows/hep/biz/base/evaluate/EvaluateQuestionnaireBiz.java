package org.dows.hep.biz.base.evaluate;

import org.dows.framework.api.Response;
import org.dows.hep.api.base.evaluate.request.CreateEvaluateQuestionnaireRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateQuestionnaireResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:评估:评估问卷
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class EvaluateQuestionnaireBiz{
    /**
    * @param
    * @return
    * @说明: 创建评估问卷
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
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
    * @创建时间: 2023年4月14日 下午4:47:53
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
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public EvaluateQuestionnaireResponse getEvaluateQuestionnaire(String evaluateQuestionnaireId ) {
        return new EvaluateQuestionnaireResponse();
    }
}