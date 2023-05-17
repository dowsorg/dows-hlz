package org.dows.hep.rest.tenant.casus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.CaseSchemeSourceEnum;
import org.dows.hep.api.tenant.casus.request.CaseSchemeRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @folder tenant-hep/案例域-方案设计
 * @author lait.zhang
 * @description project descr:案例:案例方案设计
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
        caseScheme.setSource(CaseSchemeSourceEnum.TENANT.name());
        return tenantCaseSchemeBiz.saveOrUpdCaseScheme(caseScheme);
    }

    /**
     * 获取方案设计类别以及方案设计名称
     * @param
     * @return
     */
    @Operation(summary = "获取数据库案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/listSchemeGroupOfDS")
    public Map<String, List<CaseSchemeResponse>> listSchemeGroupOfDS(@RequestBody @Validated CaseSchemeSearchRequest caseSchemeSearchRequest) {
        caseSchemeSearchRequest.setSource(CaseSchemeSourceEnum.ADMIN.name());
        return tenantCaseSchemeBiz.listSchemeGroupOfDS(caseSchemeSearchRequest);
    }

    /**
     * 获取数据库案例方案
     * @param
     * @return
     */
    @Operation(summary = "获取数据库案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/getCaseScheme")
    public CaseSchemeResponse getCaseScheme(@Validated String caseSchemeId) {
        return tenantCaseSchemeBiz.getCaseScheme(caseSchemeId);
    }

    /**
    * 获取案例方案
    * @param
    * @return
    */
    @Operation(summary = "获取案例方案")
    @GetMapping("v1/tenantCasus/caseScheme/getCaseSchemeByInstanceId")
    public CaseSchemeResponse getCaseSchemeByInstanceId(@Validated String caseInstanceId) {
        return tenantCaseSchemeBiz.getCaseSchemeByInstanceId(caseInstanceId);
    }

}