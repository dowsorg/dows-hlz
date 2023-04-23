package org.dows.hep.rest.base.intervene;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.EventInfoResponse;
import org.dows.hep.api.base.intervene.response.EventResponse;
import org.dows.hep.api.base.intervene.response.InterveneCategResponse;
import org.dows.hep.biz.base.intervene.EventBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:数据库事件
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "数据库事件", description = "数据库事件")
public class EventRest {
    private final EventBiz eventBiz;

    /**
    * 获取类别
    * @param
    * @return
    */
    @Operation(summary = "获取类别")
    @PostMapping("v1/baseIntervene/event/listEventCateg")
    public List<InterveneCategResponse> listEventCateg(@RequestBody @Validated FindInterveneCategRequest findInterveneCateg ) {
        return eventBiz.listEventCateg(findInterveneCateg);
    }

    /**
    * 保存类别
    * @param
    * @return
    */
    @Operation(summary = "保存类别")
    @PostMapping("v1/baseIntervene/event/saveEventCateg")
    public Boolean saveEventCateg(@RequestBody @Validated SaveInterveneCategRequest saveInterveneCateg ) {
        return eventBiz.saveEventCateg(saveInterveneCateg);
    }

    /**
    * 删除类别
    * @param
    * @return
    */
    @Operation(summary = "删除类别")
    @DeleteMapping("v1/baseIntervene/event/delEventCateg")
    public Boolean delEventCateg(@Validated DelInterveneCategRequest delInterveneCateg ) {
        return eventBiz.delEventCateg(delInterveneCateg);
    }

    /**
    * 获取事件列表
    * @param
    * @return
    */
    @Operation(summary = "获取事件列表")
    @PostMapping("v1/baseIntervene/event/pageEvent")
    public EventResponse pageEvent(@RequestBody @Validated FindEventRequest findEvent ) {
        return eventBiz.pageEvent(findEvent);
    }

    /**
    * 获取事件详细
    * @param
    * @return
    */
    @Operation(summary = "获取事件详细")
    @GetMapping("v1/baseIntervene/event/getEvent")
    public EventInfoResponse getEvent(@Validated String eventId) {
        return eventBiz.getEvent(eventId);
    }

    /**
    * 保存事件
    * @param
    * @return
    */
    @Operation(summary = "保存事件")
    @PostMapping("v1/baseIntervene/event/saveEvent")
    public Boolean saveEvent(@RequestBody @Validated SaveEventRequest saveEvent ) {
        return eventBiz.saveEvent(saveEvent);
    }

    /**
    * 删除事件
    * @param
    * @return
    */
    @Operation(summary = "删除事件")
    @DeleteMapping("v1/baseIntervene/event/delEvent")
    public Boolean delEvent(@Validated DelEventRequest delEvent ) {
        return eventBiz.delEvent(delEvent);
    }

    /**
    * 启用、禁用事件
    * @param
    * @return
    */
    @Operation(summary = "启用、禁用事件")
    @PostMapping("v1/baseIntervene/event/setEventState")
    public Boolean setEventState(@RequestBody @Validated SetEventStateRequest setEventState ) {
        return eventBiz.setEventState(setEventState);
    }


}