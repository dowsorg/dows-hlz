package org.dows.hep.biz.casus.tenant;

import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.CaseSettingRequest;
import org.dows.hep.api.casus.tenant.response.CaseSettingResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:案例:案例问卷设置
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
public class CaseSettingBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新案例问卷设置
    * @关联表: caseSetting
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean saveOrUpdCaseSetting(CaseSettingRequest caseSetting ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取案例问卷设置
    * @关联表: caseSetting
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public CaseSettingResponse getCaseSetting(String caseInstanceId ) {
        return new CaseSettingResponse();
    }
    /**
    * @param
    * @return
    * @说明: 删除案例问卷设置
    * @关联表: caseSetting
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean delCaseSetting(String caseInstanceId ) {
        return Boolean.FALSE;
    }
}