package org.dows.hep.rest.tenant.casus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CaseSchemePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.biz.tenant.casus.CaseSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例方案设计
*
* @author lait.zhang
* @date 2023年4月17日 下午8:00:11
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例方案设计", description = "案例方案设计")
public class CaseSchemeRest {
    private final CaseSchemeBiz caseSchemeBiz;

    /**
    * 新增和更新方案设计
    * @param
    * @return
    */
    @Operation(summary = "新增和更新方案设计")
    @PostMapping("v1/tenantCasus/caseScheme/saveOrUpdCaseScheme")
    public Boolean saveOrUpdCaseScheme(@RequestBody @Validated CaseSchemeRequest caseScheme ) {
        return caseSchemeBiz.saveOrUpdCaseScheme(caseScheme);
    }

    /**
    * 分页案例方案
    * @param
    * @return
    */
    @Operation(summary = "分页案例方案")
    @PostMapping("v1/tenantCasus/caseScheme/pageCaseScheme")
    public List<CaseSchemeResponse> pageCaseScheme(@RequestBody @Validated CaseSchemePageRequest caseSchemePage ) {
        return caseSchemeBiz.pageCaseScheme(caseSchemePage);
    }

    /**
    * 获取案例方案
    * @param
    * @return
    */
    @Operation(summary = "获取案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/getCaseScheme")
    public CaseSchemeResponse getCaseScheme(@Validated String caseSchemeId) {
        return caseSchemeBiz.getCaseScheme(caseSchemeId);
    }

    /**
    * 启用案例方案
    * @param
    * @return
    */
    @Operation(summary = "启用案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/enabledCaseScheme")
    public Boolean enabledCaseScheme(@Validated String caseSchemeId) {
        return caseSchemeBiz.enabledCaseScheme(caseSchemeId);
    }

    /**
    * 禁用案例方案
    * @param
    * @return
    */
    @Operation(summary = "禁用案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/disabledCaseScheme")
    public Boolean disabledCaseScheme(@Validated String caseSchemeId) {
        return caseSchemeBiz.disabledCaseScheme(caseSchemeId);
    }

    /**
    * 删除or批量删除案例方案
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除案例方案")
    @DeleteMapping("v1/tenantCasus/caseScheme/delCaseScheme")
    public Boolean delCaseScheme(@Validated String caseSchemeIds ) {
        return caseSchemeBiz.delCaseScheme(caseSchemeIds);
    }

    /**
    * 
    * @param
    * @return
    */
    @Operation(summary = "")
    @PostMapping("v1/tenantCasus/caseScheme/listC")
    public List<CaseSchemeResponse> listC(@RequestBody @Validated CaseSchemeSearchRequest caseSchemeSearch ) {
        return caseSchemeBiz.listC(caseSchemeSearch);
    }

    /**
    * 
    * @param
    * @return
    */
    @Operation(summary = "")
    @GetMapping("v1/tenantCasus/caseScheme/getC")
    public CaseSchemeResponse getC(@Validated String caseInstanceId) {
        return caseSchemeBiz.getC(caseInstanceId);
    }

    /**
    * 
    * @param
    * @return
    */
    @Operation(summary = "")
    @DeleteMapping("v1/tenantCasus/caseScheme/delC")
    public Boolean delC(@Validated String caseInstanceId ) {
        return caseSchemeBiz.delC(caseInstanceId);
    }


}