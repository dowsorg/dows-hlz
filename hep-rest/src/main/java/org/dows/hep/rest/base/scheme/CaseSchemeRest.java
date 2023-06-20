package org.dows.hep.rest.base.scheme;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionSourceEnum;
import org.dows.hep.api.tenant.casus.CaseSchemeSourceEnum;
import org.dows.hep.api.tenant.casus.request.CaseSchemePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeRequest;
import org.dows.hep.api.tenant.casus.response.CaseSchemePageResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseBaseBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseSchemeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @folder admin-hep/案例域-方案设计
 * @author lait.zhang
 * @description project descr:案例:案例方案设计
 * @date 2023年4月17日 下午8:00:11
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "案例方案设计", description = "案例方案设计")
public class CaseSchemeRest {
    private final TenantCaseSchemeBiz tenantCaseSchemeBiz;
    private final TenantCaseBaseBiz baseBiz;

    /**
     * 新增方案设计
     *
     * @param
     * @return
     */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/baseCasus/caseScheme/saveOrUpdCaseScheme")
    public String saveOrUpdCaseScheme(@RequestBody @Validated CaseSchemeRequest caseScheme, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        String accountName = baseBiz.getAccountName(request);
        caseScheme.setAccountId(accountId);
        caseScheme.setAccountName(accountName);
        return tenantCaseSchemeBiz.saveOrUpdCaseScheme(caseScheme, CaseSchemeSourceEnum.ADMIN, QuestionSourceEnum.ADMIN);
    }

    /**
    * 分页案例方案
    * @param
    * @return
    */
    @Operation(summary = "分页案例方案")
    @PostMapping("v1/baseCasus/caseScheme/pageCaseScheme")
    public IPage<CaseSchemePageResponse> pageCaseScheme(@RequestBody @Validated CaseSchemePageRequest caseSchemePage ) {
        return tenantCaseSchemeBiz.pageCaseScheme(caseSchemePage);
    }

    /**
    * 获取案例方案
    * @param
    * @return
    */
    @Operation(summary = "获取案例方案")
    @GetMapping("v1/baseCasus/caseScheme/getCaseScheme")
    public CaseSchemeResponse getCaseScheme(@Validated String caseSchemeId) {
        return tenantCaseSchemeBiz.getCaseScheme(caseSchemeId);
    }

    /**
    * 启用案例方案
    * @param
    * @return
    */
    @Operation(summary = "启用案例方案")
    @GetMapping("v1/baseCasus/caseScheme/enabledCaseScheme")
    public Boolean enabledCaseScheme(@Validated String caseSchemeId) {
        return tenantCaseSchemeBiz.enabledCaseScheme(caseSchemeId);
    }

    /**
    * 禁用案例方案
    * @param
    * @return
    */
    @Operation(summary = "禁用案例方案")
    @GetMapping("v1/baseCasus/caseScheme/disabledCaseScheme")
    public Boolean disabledCaseScheme(@Validated String caseSchemeId) {
        return tenantCaseSchemeBiz.disabledCaseScheme(caseSchemeId);
    }

    /**
     * 删除or批量删除案例方案
     *
     * @param
     * @return
     */
    @Operation(summary = "删除or批量删除案例方案")
    @DeleteMapping("v1/baseCasus/caseScheme/delCaseScheme")
    public Boolean delCaseScheme(@RequestBody List<String> caseSchemeIds) {
        return tenantCaseSchemeBiz.delCaseScheme(caseSchemeIds);
    }

}