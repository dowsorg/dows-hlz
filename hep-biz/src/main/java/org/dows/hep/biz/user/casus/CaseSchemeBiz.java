package org.dows.hep.biz.user.casus;

import org.dows.hep.api.user.casus.request.AllocationSchemeRequest;
import org.dows.hep.api.user.casus.request.AllocationSchemeSearchRequest;
import org.dows.hep.api.user.casus.request.CaseSchemeResultRequest;
import org.dows.hep.api.user.casus.response.AllocationSchemeResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:案列:案例方案设计
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class CaseSchemeBiz{
    /**
    * @param
    * @return
    * @说明: 方案分配
    * @关联表: caseScheme,caseSchemeResult
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean allocationCaseScheme(AllocationSchemeRequest allocationScheme ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取案例方案设计
    * @关联表: caseScheme,caseSchemeResult
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public AllocationSchemeResponse getCaseScheme(AllocationSchemeSearchRequest allocationSchemeSearch ) {
        return new AllocationSchemeResponse();
    }
    /**
    * @param
    * @return
    * @说明: 提交案例方案
    * @关联表: caseSchemeResult
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean submitCaseSchemeResult(CaseSchemeResultRequest caseSchemeResult ) {
        return Boolean.FALSE;
    }
}