package org.dows.hep.biz.casus.tenant;

import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.FindEventRequest;
import org.dows.hep.api.casus.tenant.response.CaseEventResponse;
import org.dows.hep.api.casus.tenant.response.CaseEventInfoResponse;
import org.dows.hep.api.casus.tenant.request.SaveCaseEventRequest;
import org.dows.hep.api.casus.tenant.request.DelCaseEventRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:案例:案例人物事件
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
public class CaseEventBiz{
    /**
    * @param
    * @return
    * @说明: 获取人物事件列表
    * @关联表: case_event
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
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
    * @创建时间: 2023年4月13日 下午7:47:15
    */
    public Boolean delCaseEvent(DelCaseEventRequest delCaseEvent ) {
        return Boolean.FALSE;
    }
}