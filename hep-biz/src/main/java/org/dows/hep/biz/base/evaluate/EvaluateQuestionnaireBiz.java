package org.dows.hep.biz.base.evaluate;

import org.dows.hep.api.base.evaluate.request.CreateEvaluateQuestionnaireRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateQuestionnaireResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:评估:评估问卷
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteEvaluateQuestionnaire(String evaluateQuestionnaireId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取评估问卷
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public EvaluateQuestionnaireResponse getEvaluateQuestionnaire(String evaluateQuestionnaireId ) {
        return new EvaluateQuestionnaireResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选评估问卷
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<EvaluateQuestionnaireResponse> listEvaluateQuestionnaire(String appId, String questionSectionId ) {
        return new ArrayList<EvaluateQuestionnaireResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选评估问卷
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageEvaluateQuestionnaire(Integer pageNo, Integer pageSize, String appId, String questionSectionId ) {
        return new String();
    }
}