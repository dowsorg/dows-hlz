package org.dows.hep.rest.casus.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.FindEventRequest;
import org.dows.hep.api.casus.tenant.response.CaseEventResponse;
import org.dows.hep.api.casus.tenant.response.CaseEventInfoResponse;
import org.dows.hep.api.casus.tenant.request.SaveCaseEventRequest;
import org.dows.hep.api.casus.tenant.request.DelCaseEventRequest;
import org.dows.hep.biz.casus.tenant.CaseEventBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例人物事件
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "案例人物事件")
public class CaseEventRest {
    private final CaseEventBiz caseEventBiz;

    /**
    * 获取人物事件列表
    * @param
    * @return
    */
    @ApiOperation("获取人物事件列表")
    @PostMapping("v1/casusTenant/caseEvent/pageCaseEvent")
    public CaseEventResponse pageCaseEvent(@RequestBody @Validated FindEventRequest findEvent ) {
        return caseEventBiz.pageCaseEvent(findEvent);
    }

    /**
    * 获取人物事件详细
    * @param
    * @return
    */
    @ApiOperation("获取人物事件详细")
    @GetMapping("v1/casusTenant/caseEvent/getCaseEvent")
    public CaseEventInfoResponse getCaseEvent(@Validated String caseEventId) {
        return caseEventBiz.getCaseEvent(caseEventId);
    }

    /**
    * 保存人物事件
    * @param
    * @return
    */
    @ApiOperation("保存人物事件")
    @PostMapping("v1/casusTenant/caseEvent/saveCaseEvent")
    public Boolean saveCaseEvent(@RequestBody @Validated SaveCaseEventRequest saveCaseEvent ) {
        return caseEventBiz.saveCaseEvent(saveCaseEvent);
    }

    /**
    * 删除事件
    * @param
    * @return
    */
    @ApiOperation("删除事件")
    @DeleteMapping("v1/casusTenant/caseEvent/delCaseEvent")
    public Boolean delCaseEvent(@Validated DelCaseEventRequest delCaseEvent ) {
        return caseEventBiz.delCaseEvent(delCaseEvent);
    }


}