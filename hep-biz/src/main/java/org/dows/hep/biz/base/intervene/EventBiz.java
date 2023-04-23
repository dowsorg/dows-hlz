package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.EventInfoResponse;
import org.dows.hep.api.base.intervene.response.EventResponse;
import org.dows.hep.api.base.intervene.response.InterveneCategResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:干预:数据库事件
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class EventBiz{
    /**
    * @param
    * @return
    * @说明: 获取类别
    * @关联表: event_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
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
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean setEventState(SetEventStateRequest setEventState ) {
        return Boolean.FALSE;
    }
}