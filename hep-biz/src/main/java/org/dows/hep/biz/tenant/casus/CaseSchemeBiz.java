package org.dows.hep.biz.tenant.casus;

import org.dows.framework.api.Response;
import org.dows.hep.api.tenant.casus.request.CaseSchemeRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemePageRequest;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.tenant.casus.request.CaseSchemeSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:案例:案例方案设计
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class CaseSchemeBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新方案设计
    * @关联表: caseScheme
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean saveOrUpdCaseScheme(CaseSchemeRequest caseScheme ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 分页案例方案
    * @关联表: caseScheme
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public List<CaseSchemeResponse> pageCaseScheme(CaseSchemePageRequest caseSchemePage ) {
        return new ArrayList<CaseSchemeResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 获取案例方案
    * @关联表: caseScheme
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public CaseSchemeResponse getCaseScheme(String caseSchemeId ) {
        return new CaseSchemeResponse();
    }
    /**
    * @param
    * @return
    * @说明: 启用案例方案
    * @关联表: caseScheme
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean enabledCaseScheme(String caseSchemeId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 禁用案例方案
    * @关联表: caseScheme
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean disabledCaseScheme(String caseSchemeId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除or批量删除案例方案
    * @关联表: caseScheme
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean delCaseScheme(String caseSchemeIds ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 
    * @关联表: caseScheme
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public List<CaseSchemeResponse> listC(CaseSchemeSearchRequest caseSchemeSearch ) {
        return new ArrayList<CaseSchemeResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 
    * @关联表: caseScheme
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public CaseSchemeResponse getC(String caseInstanceId ) {
        return new CaseSchemeResponse();
    }
    /**
    * @param
    * @return
    * @说明: 
    * @关联表: caseScheme
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean delC(String caseInstanceId ) {
        return Boolean.FALSE;
    }
}