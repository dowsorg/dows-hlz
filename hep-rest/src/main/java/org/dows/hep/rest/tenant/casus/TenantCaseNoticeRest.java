package org.dows.hep.rest.tenant.casus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CaseNoticeRequest;
import org.dows.hep.api.tenant.casus.response.CaseNoticeResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseNoticeBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例公告
*
* @author lait.zhang
* @date 2023年4月17日 下午8:00:11
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例公告", description = "案例公告")
public class TenantCaseNoticeRest {
    private final TenantCaseNoticeBiz tenantCaseNoticeBiz;

    /**
    * 新增和更新案例公告
    * @param
    * @return
    */
    @Operation(summary = "新增和更新案例公告")
    @PostMapping("v1/tenantCasus/caseNotice/saveOrUpdCaseNotice")
    public String saveOrUpdCaseNotice(@RequestBody @Validated CaseNoticeRequest caseNotice ) {
        return tenantCaseNoticeBiz.saveOrUpdCaseNotice(caseNotice);
    }

    /**
    * 列出案例公告
    * @param
    * @return
    */
    @Operation(summary = "列出案例公告")
    @GetMapping("v1/tenantCasus/caseNotice/listCaseNotice")
    public List<CaseNoticeResponse> listCaseNotice(@Validated String caseInstanceId) {
        return tenantCaseNoticeBiz.listCaseNotice(caseInstanceId);
    }

    /**
    * 删除案例公告
    * @param
    * @return
    */
    @Operation(summary = "删除案例公告")
    @DeleteMapping("v1/tenantCasus/caseNotice/delCaseNotice")
    public Boolean delCaseNotice(@Validated String caseNoticeId ) {
        return tenantCaseNoticeBiz.delCaseNotice(caseNoticeId);
    }


}