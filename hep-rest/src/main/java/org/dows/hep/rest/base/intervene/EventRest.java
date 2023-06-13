package org.dows.hep.rest.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.EventInfoResponse;
import org.dows.hep.api.base.intervene.response.EventResponse;
import org.dows.hep.biz.base.intervene.EventBiz;
import org.dows.hep.biz.vo.CategVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:数据库事件
* @folder admin-hep/数据库事件
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
    @Operation(summary = "获取事件类别")
    @PostMapping("v1/baseIntervene/event/listEventCateg")
    public List<CategVO> listEventCateg(@RequestBody @Validated FindEventCategRequest findEventCateg ) {
        return eventBiz.listEventCateg(findEventCateg);
    }

    /**
    * 保存类别
    * @param
    * @return
    */
    @Operation(summary = "批量保存事件类别")
    @PostMapping("v1/baseIntervene/event/saveEventCateg")
    public Boolean saveEventCategs(@RequestBody @Validated List<SaveEventCategRequest> saveEventCateg ) {
        return eventBiz.saveEventCategs(saveEventCateg);
    }

    /**
    * 删除类别
    * @param
    * @return
    */
    @Operation(summary = "删除事件类别")
    @DeleteMapping("v1/baseIntervene/event/delEventCateg")
    public Boolean delEventCateg(@RequestBody @Validated DelEventCategRequest delEventCateg ) {
        return eventBiz.delEventCateg(delEventCateg);
    }

    /**
    * 获取事件列表
    * @param
    * @return
    */
    @Operation(summary = "获取事件列表")
    @PostMapping("v1/baseIntervene/event/pageEvent")
    public Page<EventResponse> pageEvent(@RequestBody @Validated FindEventRequest findEvent ) {
        return eventBiz.pageEvent(findEvent);
    }

    /**
    * 获取事件详细
    * @param
    * @return
    */
    @Operation(summary = "获取事件详细信息")
    @GetMapping("v1/baseIntervene/event/getEvent")
    public EventInfoResponse getEvent(@Validated String appId,  @Validated String eventId) {
        return eventBiz.getEvent(appId,eventId);
    }

    /**
    * 保存事件
    * @param
    * @return
    */
    @Operation(summary = "保存事件")
    @PostMapping("v1/baseIntervene/event/saveEvent")
    public Boolean saveEvent(@RequestBody @Validated SaveEventRequest saveEvent , HttpServletRequest request) {
        return eventBiz.saveEvent(saveEvent,request);
    }

    /**
    * 删除事件
    * @param
    * @return
    */
    @Operation(summary = "删除事件")
    @DeleteMapping("v1/baseIntervene/event/delEvent")
    public Boolean delEvent(@RequestBody @Validated DelEventRequest delEvent ) {
        return eventBiz.delEvent(delEvent);
    }


    /**
     * 删除事件触发条件
     * @param delRefItemRequest
     * @return
     */
    @Operation(summary = "删除事件触发条件")
    @DeleteMapping("v1/baseIntervene/event/delRefEval")
    public Boolean delRefEval(@RequestBody @Validated DelRefItemRequest delRefItemRequest ) {
        return eventBiz.delRefEval(delRefItemRequest);
    }

    /**
     * 删除处理措施
     * @param delRefItemRequest
     * @return
     */
    @Operation(summary = "删除处理措施")
    @DeleteMapping("v1/baseIntervene/event/delRefAction")
    public Boolean delRefAction(@RequestBody @Validated DelRefItemRequest delRefItemRequest){
        return eventBiz.delRefAction(delRefItemRequest);
    }

    /**
     * 删除事件影响指标
     * @param delRefItemRequest
     * @return
     */
    @Operation(summary = "删除事件影响指标")
    @DeleteMapping("v1/baseIntervene/event/delRefEventIndicator")
    public Boolean delRefEventIndicator(@RequestBody @Validated  DelRefItemRequest delRefItemRequest){
        return eventBiz.delRefEventIndicator(delRefItemRequest);
    }

    /**
     * 删除处理措施影响指标
     * @param delRefItemRequest
     * @return
     */
    @Operation(summary = "删除处理措施影响指标")
    @DeleteMapping("v1/baseIntervene/event/delRefActionIndicator")
    public Boolean delRefActionIndicator(@RequestBody @Validated  DelRefItemRequest delRefItemRequest){
        return eventBiz.delRefActionIndicator(delRefItemRequest);
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