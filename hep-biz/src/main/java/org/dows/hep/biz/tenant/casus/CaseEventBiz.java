package org.dows.hep.biz.tenant.casus;

import org.dows.hep.api.tenant.casus.request.DelCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.FindEventRequest;
import org.dows.hep.api.tenant.casus.request.SaveCaseEventRequest;
import org.dows.hep.api.tenant.casus.response.CaseEventInfoResponse;
import org.dows.hep.api.tenant.casus.response.CaseEventResponse;
import org.springframework.stereotype.Service;

/**
* @description project descr:案例:案例人物事件
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class CaseEventBiz{
    /**
    * @param
    * @return
    * @说明: 获取人物事件列表
    * @关联表: case_event
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public CaseEventResponse pageCaseEvent(FindEventRequest findEvent ) {
        return new CaseEventResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取人物事件详细
    * @关联表: case_event,case_event_eval,case_event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public CaseEventInfoResponse getCaseEvent(String caseEventId ) {
        return new CaseEventInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 保存人物事件
    * @关联表: case_event,case_event_eval,case_event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean saveCaseEvent(SaveCaseEventRequest saveCaseEvent ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除事件
    * @关联表: case_event,case_event_eval,case_event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean delCaseEvent(DelCaseEventRequest delCaseEvent ) {
        return Boolean.FALSE;
    }
}