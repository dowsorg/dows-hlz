package org.dows.hep.biz.user.casus;

import org.dows.hep.api.user.casus.request.AllocationQuestionnaireSearchRequest;
import org.dows.hep.api.user.casus.request.CaseQuestionnaireResultRequest;
import org.dows.hep.api.user.casus.response.AllocationSchemeResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:案列:案例问卷
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class CaseQuestionnaireBiz{
    /**
    * @param
    * @return
    * @说明: 获取案例问卷-user
    * @关联表: caseScheme,caseSchemeResult
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public AllocationSchemeResponse getCaseQuestionnaireOfUser(AllocationQuestionnaireSearchRequest allocationQuestionnaireSearch ) {
        return new AllocationSchemeResponse();
    }
    /**
    * @param
    * @return
    * @说明: 提交结果
    * @关联表: caseQuestionnaireResult
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean submitCaseQuestionnaireResult(CaseQuestionnaireResultRequest caseQuestionnaireResult ) {
        return Boolean.FALSE;
    }
}