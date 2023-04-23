package org.dows.hep.rest.user.casus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.casus.request.AllocationSchemeRequest;
import org.dows.hep.api.user.casus.request.AllocationSchemeSearchRequest;
import org.dows.hep.api.user.casus.request.CaseSchemeResultRequest;
import org.dows.hep.api.user.casus.response.AllocationSchemeResponse;
import org.dows.hep.biz.user.casus.CaseSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:案列:案例方案设计
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例方案设计", description = "案例方案设计")
public class CaseSchemeRest {
    private final CaseSchemeBiz caseSchemeBiz;

    /**
    * 方案分配
    * @param
    * @return
    */
    @Operation(summary = "方案分配")
    @PostMapping("v1/userCasus/caseScheme/allocationCaseScheme")
    public Boolean allocationCaseScheme(@RequestBody @Validated AllocationSchemeRequest allocationScheme ) {
        return caseSchemeBiz.allocationCaseScheme(allocationScheme);
    }

    /**
    * 获取案例方案设计
    * @param
    * @return
    */
    @Operation(summary = "获取案例方案设计")
    @PostMapping("v1/userCasus/caseScheme/getCaseScheme")
    public AllocationSchemeResponse getCaseScheme(@RequestBody @Validated AllocationSchemeSearchRequest allocationSchemeSearch ) {
        return caseSchemeBiz.getCaseScheme(allocationSchemeSearch);
    }

    /**
    * 提交案例方案
    * @param
    * @return
    */
    @Operation(summary = "提交案例方案")
    @PostMapping("v1/userCasus/caseScheme/submitCaseSchemeResult")
    public Boolean submitCaseSchemeResult(@RequestBody @Validated CaseSchemeResultRequest caseSchemeResult ) {
        return caseSchemeBiz.submitCaseSchemeResult(caseSchemeResult);
    }


}