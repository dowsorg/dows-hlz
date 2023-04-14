package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
import org.dows.hep.biz.intervene.EventBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:数据库事件
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "数据库事件")
public class EventRest {
    private final EventBiz eventBiz;

    /**
    * 获取类别
    * @param
    * @return
    */
    @ApiOperation("获取类别")
    @PostMapping("v1/intervene/event/listEventCateg")
    public List<InterveneCategResponse> listEventCateg(@RequestBody @Validated FindInterveneCategRequest findInterveneCateg ) {
        return eventBiz.listEventCateg(findInterveneCateg);
    }

    /**
    * 保存类别
    * @param
    * @return
    */
    @ApiOperation("保存类别")
    @PostMapping("v1/intervene/event/saveEventCateg")
    public Boolean saveEventCateg(@RequestBody @Validated SaveInterveneCategRequest saveInterveneCateg ) {
        return eventBiz.saveEventCateg(saveInterveneCateg);
    }

    /**
    * 删除类别
    * @param
    * @return
    */
    @ApiOperation("删除类别")
    @DeleteMapping("v1/intervene/event/delEventCateg")
    public Boolean delEventCateg(@Validated DelInterveneCategRequest delInterveneCateg ) {
        return eventBiz.delEventCateg(delInterveneCateg);
    }

    /**
    * 获取事件列表
    * @param
    * @return
    */
    @ApiOperation("获取事件列表")
    @PostMapping("v1/intervene/event/pageEvent")
    public EventResponse pageEvent(@RequestBody @Validated FindEventRequest findEvent ) {
        return eventBiz.pageEvent(findEvent);
    }

    /**
    * 获取事件详细
    * @param
    * @return
    */
    @ApiOperation("获取事件详细")
    @GetMapping("v1/intervene/event/getEvent")
    public EventInfoResponse getEvent(@Validated String eventId) {
        return eventBiz.getEvent(eventId);
    }

    /**
    * 保存事件
    * @param
    * @return
    */
    @ApiOperation("保存事件")
    @PostMapping("v1/intervene/event/saveEvent")
    public Boolean saveEvent(@RequestBody @Validated SaveEventRequest saveEvent ) {
        return eventBiz.saveEvent(saveEvent);
    }

    /**
    * 删除事件
    * @param
    * @return
    */
    @ApiOperation("删除事件")
    @DeleteMapping("v1/intervene/event/delEvent")
    public Boolean delEvent(@Validated DelEventRequest delEvent ) {
        return eventBiz.delEvent(delEvent);
    }

    /**
    * 启用、禁用事件
    * @param
    * @return
    */
    @ApiOperation("启用、禁用事件")
    @PostMapping("v1/intervene/event/setEventState")
    public Boolean setEventState(@RequestBody @Validated SetEventStateRequest setEventState ) {
        return eventBiz.setEventState(setEventState);
    }


}