package org.dows.hep.biz.tenant.casus;

import org.dows.hep.api.tenant.casus.request.CaseSettingRequest;
import org.dows.hep.api.tenant.casus.response.CaseSettingResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:案例:案例问卷设置
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class CaseSettingBiz{
    /**
    * @param
    * @return
    * @说明: 新增和更新案例问卷设置
    * @关联表: caseSetting
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
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
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean delCaseSetting(String caseInstanceId ) {
        return Boolean.FALSE;
    }
}