package org.dows.hep.biz.tenant.casus;

import org.dows.hep.api.tenant.casus.request.CaseInstancePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseInstanceRequest;
import org.dows.hep.api.tenant.casus.response.CaseInstanceResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:案例:案例管理
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class CaseManageBiz{
    /**
    * @param
    * @return
    * @说明: 创建和更新案例
    * @关联表: caseInstance
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String saveOrUpdCaseInstance(CaseInstanceRequest caseInstance ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 复制
    * @关联表: caseInstance
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String copyCaseInstance(String oriCaseInstanceId ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 列表
    * @关联表: caseInstance
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public CaseInstanceResponse pageCaseInstance(CaseInstancePageRequest caseInstancePage ) {
        return new CaseInstanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取案例详情
    * @关联表: caseInstance
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public CaseInstanceResponse getCaseInstance(String caseInstanceId ) {
        return new CaseInstanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 删除
    * @关联表: caseInstance
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean delCaseInstance(String caseInstanceId ) {
        return Boolean.FALSE;
    }
}