package org.dows.hep.biz.casus.tenant;

import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.CreateCaseNoticeRequest;
import org.dows.hep.api.casus.tenant.response.CaseNoticeResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:案例:案例公告
*
* @author lait.zhang
* @date 2023年4月14日 下午2:24:35
*/
public class CaseNoticeBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新案例公告
    * @关联表: caseNotice
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public Boolean saveOrUpdCaseNotice(CreateCaseNoticeRequest createCaseNotice ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 列出案例公告
    * @关联表: caseNotice
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public List<CaseNoticeResponse> listCaseNotice(String caseInstanceId ) {
        return new ArrayList<CaseNoticeResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 删除案例公告
    * @关联表: caseNotice
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午2:24:35
    */
    public Boolean delCaseNotice(String caseNoticeId ) {
        return Boolean.FALSE;
    }
}