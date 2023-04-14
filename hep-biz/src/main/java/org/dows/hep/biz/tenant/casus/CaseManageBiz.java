package org.dows.hep.biz.tenant.casus;

import org.dows.framework.api.Response;
import org.dows.hep.api.tenant.casus.request.CaseInstanceRequest;
import org.dows.hep.api.tenant.casus.request.CaseInstancePageRequest;
import org.dows.hep.api.tenant.casus.response.CaseInstanceResponse;
import org.dows.hep.api.tenant.casus.response.CaseInstanceResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:案例:案例管理
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
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
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public String saveOrUpdCaseInstance(CaseInstanceRequest caseInstance ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 复制
    * @关联表: caseInstance
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
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
    * @创建时间: 2023年4月14日 下午4:47:53
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
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public CaseInstanceResponse getCaseInstance(String caseInstanceId ) {
        return new CaseInstanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 删除
    * @关联表: caseInstance
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean delCaseInstance(String caseInstanceId ) {
        return Boolean.FALSE;
    }
}