package org.dows.hep.rest.tenant.casus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.DelCaseEventRequest;
import org.dows.hep.api.tenant.casus.request.FindEventRequest;
import org.dows.hep.api.tenant.casus.request.SaveCaseEventRequest;
import org.dows.hep.api.tenant.casus.response.CaseEventInfoResponse;
import org.dows.hep.api.tenant.casus.response.CaseEventResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseEventBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:案例:案例人物事件
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例人物事件", description = "案例人物事件")
public class CaseEventRest {
    private final TenantCaseEventBiz tenantCaseEventBiz;

    /**
    * 获取人物事件列表
    * @param
    * @return
    */
    @Operation(summary = "获取人物事件列表")
    @PostMapping("v1/tenantCasus/caseEvent/pageCaseEvent")
    public CaseEventResponse pageCaseEvent(@RequestBody @Validated FindEventRequest findEvent ) {
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
    public Boolean saveCaseEvent(@RequestBody @Validated SaveCaseEventRequest saveCaseEvent ) {
        return tenantCaseEventBiz.saveCaseEvent(saveCaseEvent);
    }

    /**
    * 删除事件
    * @param
    * @return
    */
    @Operation(summary = "删除事件")
    @DeleteMapping("v1/tenantCasus/caseEvent/delCaseEvent")
    public Boolean delCaseEvent(@Validated DelCaseEventRequest delCaseEvent ) {
        return tenantCaseEventBiz.delCaseEvent(delCaseEvent);
    }


}