package org.dows.hep.rest.tenant.casus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireDelItemRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
* @description project descr:案例:案例问卷
* @folder tenant-hep/案例域-案例问卷
* @author lait.zhang
* @date 2023年4月17日 下午8:00:11
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例问卷", description = "案例问卷")
public class TenantCaseQuestionnaireRest {
    private final TenantCaseQuestionnaireBiz tenantCaseQuestionnaireBiz;

    /**
    * 新增和更新案例问卷
    * @param
    * @return
    */
    @Operation(summary = "新增和更新案例问卷")
    @PostMapping("v1/tenantCasus/caseQuestionnaire/saveCaseQuestionnaire")
    public String saveCaseQuestionnaire(@RequestBody @Validated CaseQuestionnaireRequest caseQuestionnaire ) {
        return tenantCaseQuestionnaireBiz.saveOrUpdCaseQuestionnaire(caseQuestionnaire);
    }

    /**
    * 分页案例问卷
    * @param
    * @return
    */
    @Operation(summary = "列出案例问卷")
    @PostMapping("v1/tenantCasus/caseQuestionnaire/listCaseQuestionnaire")
    public List<CaseQuestionnaireResponse> listCaseQuestionnaire(@RequestBody @Validated CaseQuestionnaireSearchRequest request ) {
        return tenantCaseQuestionnaireBiz.listCaseQuestionnaire(request);
    }

    /**
    * 获取案例问卷
    * @param
    * @return
    */
    @Operation(summary = "获取案例问卷")
    @GetMapping("v1/tenantCasus/caseQuestionnaire/getCaseQuestionnaire")
    public void getCaseQuestionnaire(@Validated String caseQuestionnaireId) {
        tenantCaseQuestionnaireBiz.getCaseQuestionnaire(caseQuestionnaireId);
    }

    /**
     * 获取案例问卷
     * @param
     * @return
     */
    @Operation(summary = "预览案例问卷")
    @GetMapping("v1/tenantCasus/caseQuestionnaire/showCaseQuestionnaire")
    public void showCaseQuestionnaire(@Validated String caseQuestionnaireId) {
        tenantCaseQuestionnaireBiz.showCaseQuestionnaire(caseQuestionnaireId);
    }

    /**
    * 删除案例问卷
    * @param
    * @return
    */
    @Operation(summary = "删除案例问卷")
    @DeleteMapping("v1/tenantCasus/caseQuestionnaire/delCaseQuestionnaire")
    public Boolean delCaseQuestionnaire(@RequestBody List<String> ids ) {
        return tenantCaseQuestionnaireBiz.delCaseQuestionnaire(ids);
    }

    /**
     * 删除案例问卷Item
     * @param
     * @return
     */
    @Operation(summary = "删除案例问卷Item")
    @DeleteMapping("v1/tenantCasus/caseQuestionnaire/delQuestionnaireItem")
    public Boolean delQuestionnaireItem(@RequestBody CaseQuestionnaireDelItemRequest request) {
        return tenantCaseQuestionnaireBiz.delQuestionnaireItem(request.getQuestionSectionId(), request.getQuestionSectionItemId());
    }


}