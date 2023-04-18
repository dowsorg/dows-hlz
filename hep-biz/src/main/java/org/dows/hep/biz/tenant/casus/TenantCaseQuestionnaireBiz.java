package org.dows.hep.biz.tenant.casus;

import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:案例:案例问卷
*
* @author lait.zhang
* @date 2023年4月17日 下午8:00:11
*/
@Service
public class TenantCaseQuestionnaireBiz {
    /**
    * @param
    * @return
    * @说明: 新增和更新案例问卷
    * @关联表: caseQuestionnaire
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月17日 下午8:00:11
    */
    public Boolean saveOrUpdCaseQuestionnaire(CaseQuestionnaireRequest caseQuestionnaire ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 分页案例问卷
    * @关联表: caseQuestionnaire
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月17日 下午8:00:11
    */
    public CaseQuestionnaireResponse pageCaseQuestionnaire(CaseQuestionnaireSearchRequest caseQuestionnaireSearch ) {
        return new CaseQuestionnaireResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取案例问卷
    * @关联表: caseQuestionnaire
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月17日 下午8:00:11
    */
    public void getCaseQuestionnaire(String caseQuestionnaireId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除案例问卷
    * @关联表: caseQuestionnaire
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月17日 下午8:00:11
    */
    public Boolean delCaseQuestionnaire(String caseQuestionnaireId ) {
        return Boolean.FALSE;
    }
}