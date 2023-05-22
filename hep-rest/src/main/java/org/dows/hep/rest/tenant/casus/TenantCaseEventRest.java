package org.dows.hep.rest.tenant.casus;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelRefItemRequest;
import org.dows.hep.api.tenant.casus.request.CopyCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.DelCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.FindCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.SaveCaseEventRequest;
import org.dows.hep.api.tenant.casus.response.CaseEventInfoResponse;
import org.dows.hep.api.tenant.casus.response.CaseEventResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseEventBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:案例:案例人物事件
*@folder 案例人物事件
 *
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例人物事件", description = "案例人物事件")
public class TenantCaseEventRest {
    private final TenantCaseEventBiz tenantCaseEventBiz;

    /**
    * 获取人物事件列表
    * @param
    * @return
    */
    @Operation(summary = "获取人物事件列表")
    @PostMapping("v1/tenantCasus/caseEvent/pageCaseEvent")
    public Page<CaseEventResponse> pageCaseEvent(@RequestBody @Validated FindCaseEventRequest findEvent ) {
        return tenantCaseEventBiz.pageCaseEvent(findEvent);
    }

    /**
    * 获取人物事件详细
    * @param
    * @return
    */
    @Operation(summary = "获取人物事件详细")
    @GetMapping("v1/tenantCasus/caseEvent/getCaseEvent")
    public CaseEventInfoResponse getCaseEvent(@Validated String caseEventId) {
        return tenantCaseEventBiz.getCaseEvent(caseEventId);
    }

    /**
    * 保存人物事件
    * @param
    * @return
    */
    @Operation(summary = "保存人物事件")
    @PostMapping("v1/tenantCasus/caseEvent/saveCaseEvent")
    public Boolean saveCaseEvent(@RequestBody @Validated SaveCaseEventRequest saveCaseEvent , HttpServletRequest request) {
        return tenantCaseEventBiz.saveCaseEvent(saveCaseEvent,request);
    }

    /**
     * 批量添加人物事件
     * @param
     * @return
     */
    @Operation(summary = "批量添加人物事件")
    @PostMapping("v1/tenantCasus/caseEvent/copyCaseEvent")
    public Boolean copyCaseEvent(@RequestBody @Validated CopyCaseEventRequest copyCaseEvent , HttpServletRequest request) {
        return tenantCaseEventBiz.copyCaseEvent(copyCaseEvent,request);
    }

    /**
    * 删除事件
    * @param
    * @return
    */
    @Operation(summary = "删除人物事件")
    @DeleteMapping("v1/tenantCasus/caseEvent/delCaseEvent")
    public Boolean delCaseEvent(@RequestBody @Validated DelCaseEventRequest delCaseEvent ) {
        return tenantCaseEventBiz.delCaseEvent(delCaseEvent);
    }

    /**
     * 删除事件触发条件
     * @param delRefItemRequest
     * @return
     */
    @Operation(summary = "删除事件触发条件")
    @DeleteMapping("v1/tenantCasus/caseEvent/delRefEval")
    public Boolean delRefEval(@RequestBody @Validated DelRefItemRequest delRefItemRequest ) {
        return tenantCaseEventBiz.delRefEval(delRefItemRequest);
    }

    /**
     * 删除处理措施
     * @param delRefItemRequest
     * @return
     */
    @Operation(summary = "删除处理措施")
    @DeleteMapping("v1/tenantCasus/caseEvent/delRefAction")
    public Boolean delRefAction(@RequestBody @Validated DelRefItemRequest delRefItemRequest){
        return tenantCaseEventBiz.delRefAction(delRefItemRequest);
    }

    /**
     * 删除事件影响指标
     * @param delRefItemRequest
     * @return
     */
    @Operation(summary = "删除事件影响指标")
    @DeleteMapping("v1/tenantCasus/caseEvent/delRefEventIndicator")
    public Boolean delRefEventIndicator(@RequestBody @Validated  DelRefItemRequest delRefItemRequest){
        return tenantCaseEventBiz.delRefEventIndicator(delRefItemRequest);
    }

    /**
     * 删除处理措施影响指标
     * @param delRefItemRequest
     * @return
     */
    @Operation(summary = "删除处理措施影响指标")
    @DeleteMapping("v1/tenantCasus/caseEvent/delRefActionIndicator")
    public Boolean delRefActionIndicator(@RequestBody @Validated  DelRefItemRequest delRefItemRequest){
        return tenantCaseEventBiz.delRefActionIndicator(delRefItemRequest);
    }


}