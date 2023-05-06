package org.dows.hep.rest.tenant.casus;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CaseSchemePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeRequest;
import org.dows.hep.api.tenant.casus.response.CaseSchemePageResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseSchemeBiz;
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
public class TenantCaseSchemeRest {
    private final TenantCaseSchemeBiz tenantCaseSchemeBiz;

    /**
     * 新增方案设计
     *
     * @param
     * @return
     */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/tenantCasus/caseScheme/saveOrUpdCaseScheme")
    public String saveOrUpdCaseScheme(@RequestBody @Validated CaseSchemeRequest caseScheme) {
        return tenantCaseSchemeBiz.saveOrUpdCaseScheme(caseScheme);
    }

    /**
    * 分页案例方案
    * @param
    * @return
    */
    @Operation(summary = "分页案例方案")
    @PostMapping("v1/tenantCasus/caseScheme/pageCaseScheme")
    public Page<CaseSchemePageResponse> pageCaseScheme(@RequestBody @Validated CaseSchemePageRequest caseSchemePage ) {
        return tenantCaseSchemeBiz.pageCaseScheme(caseSchemePage);
    }

    /**
    * 获取案例方案
    * @param
    * @return
    */
    @Operation(summary = "获取案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/getCaseScheme")
    public CaseSchemeResponse getCaseScheme(@Validated String caseSchemeId) {
        return tenantCaseSchemeBiz.getCaseScheme(caseSchemeId);
    }

    /**
    * 启用案例方案
    * @param
    * @return
    */
    @Operation(summary = "启用案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/enabledCaseScheme")
    public Boolean enabledCaseScheme(@Validated String caseSchemeId) {
        return tenantCaseSchemeBiz.enabledCaseScheme(caseSchemeId);
    }

    /**
    * 禁用案例方案
    * @param
    * @return
    */
    @Operation(summary = "禁用案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/disabledCaseScheme")
    public Boolean disabledCaseScheme(@Validated String caseSchemeId) {
        return tenantCaseSchemeBiz.disabledCaseScheme(caseSchemeId);
    }

    /**
    * 删除or批量删除案例方案
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除案例方案")
    @DeleteMapping("v1/tenantCasus/caseScheme/delCaseScheme")
    public Boolean delCaseScheme(List<String> caseSchemeIds ) {
        return tenantCaseSchemeBiz.delCaseScheme(caseSchemeIds);
    }

}