package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.intervene.response.InterveneCategResponse;
import org.dows.hep.api.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.api.intervene.request.DelInterveneCategRequest;
import org.dows.hep.api.intervene.request.FindEventRequest;
import org.dows.hep.api.intervene.response.EventResponse;
import org.dows.hep.api.intervene.response.EventInfoResponse;
import org.dows.hep.api.intervene.request.SaveEventRequest;
import org.dows.hep.api.intervene.request.DelEventRequest;
import org.dows.hep.api.intervene.request.SetEventStateRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:数据库事件
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
public class EventBiz{
    /**
    * @param
    * @return
    * @说明: 获取类别
    * @关联表: event_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public List<InterveneCategResponse> listEventCateg(FindInterveneCategRequest findInterveneCateg ) {
        return new ArrayList<InterveneCategResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 保存类别
    * @关联表: event_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public Boolean saveEventCateg(SaveInterveneCategRequest saveInterveneCateg ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除类别
    * @关联表: event_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public Boolean delEventCateg(DelInterveneCategRequest delInterveneCateg ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取事件列表
    * @关联表: event
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public EventResponse pageEvent(FindEventRequest findEvent ) {
        return new EventResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取事件详细
    * @关联表: event,event_eval,event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public EventInfoResponse getEvent(String eventId ) {
        return new EventInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 保存事件
    * @关联表: event,event_eval,event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public Boolean saveEvent(SaveEventRequest saveEvent ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除事件
    * @关联表: event,event_eval,event_action
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public Boolean delEvent(DelEventRequest delEvent ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用事件
    * @关联表: event
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午3:45:06
    */
    public Boolean setEventState(SetEventStateRequest setEventState ) {
        return Boolean.FALSE;
    }
}