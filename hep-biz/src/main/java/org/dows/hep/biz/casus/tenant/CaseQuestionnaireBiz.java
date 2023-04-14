package org.dows.hep.biz.casus.tenant;

import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.CaseQuestionnaireRequest;
import org.dows.hep.api.casus.tenant.request.CaseQuestionnaireSearchRequest;
import org.dows.hep.api.casus.tenant.response.CaseQuestionnaireResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:案例:案例问卷
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
public class CaseQuestionnaireBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新案例问卷
    * @关联表: caseQuestionnaire
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:31:43
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
    * @创建时间: 2023年4月14日 下午3:31:43
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
    * @创建时间: 2023年4月14日 下午3:31:43
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
    * @创建时间: 2023年4月14日 下午3:31:43
    */
    public Boolean delCaseQuestionnaire(String caseQuestionnaireId ) {
        return Boolean.FALSE;
    }
}