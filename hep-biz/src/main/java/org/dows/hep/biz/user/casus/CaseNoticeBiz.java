package org.dows.hep.biz.user.casus;

import org.dows.hep.api.user.casus.request.CaseNoticeSearchRequest;
import org.dows.hep.api.user.casus.response.CaseNoticeResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:案列:案例公告
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class CaseNoticeBiz{
    /**
    * @param
    * @return
    * @说明: 获取案例公告
    * @关联表: caseNotice
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public CaseNoticeResponse getCaseNotice(CaseNoticeSearchRequest caseNoticeSearch ) {
        return new CaseNoticeResponse();
    }
}