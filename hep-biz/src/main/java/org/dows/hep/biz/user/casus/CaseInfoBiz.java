package org.dows.hep.biz.user.casus;

import org.dows.hep.api.user.casus.request.CaseInfoRequest;
import org.springframework.stereotype.Service;

/**
* @description project descr:案列:案例基础信息
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class CaseInfoBiz{
    /**
    * @param
    * @return
    * @说明: 获取案例基础信息[背景|帮助中心|评分提示]
    * @关联表: CaseInstance
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public String getCaseInfo(CaseInfoRequest caseInfo ) {
        return new String();
    }
}