package org.dows.hep.rest.tenant.casus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CaseOrgQuestionnaireRequest;
import org.dows.hep.api.tenant.casus.response.CaseOrgQuestionnaireResponse;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseOrgQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @description project descr:案例:案例问卷
 * @folder tenant-hep/案例域-问卷分配
 * @author lait.zhang
 * @date 2023年4月17日 下午8:00:11
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "案例问卷分配", description = "案例问卷")
public class TenantCaseOrgQuestionnaireRest {
    private final TenantCaseOrgQuestionnaireBiz tenantCaseOrgQuestionnaireBiz;

    /**
     * 列出未选择的问卷
     * @param
     * @return
     */
    @Operation(summary = "列出未选择的问卷")
    @GetMapping("v1/tenantCasus/caseQuestionnaire/listUnselectedQuestionnaires")
    public List<List<CaseQuestionnaireResponse>> listUnselectedQuestionnaires(@Validated String caseInstanceId ) {
        return tenantCaseOrgQuestionnaireBiz.listUnselectedQuestionnaires(caseInstanceId);
    }

    /**
     * 列出已经选择的问卷
     * @param
     * @return
     */
    @Operation(summary = "列出已经选择的问卷")
    @GetMapping("v1/tenantCasus/caseQuestionnaire/listSelectedQuestionnaires")
    public List<Map<String, CaseOrgQuestionnaireResponse>> listSelectedQuestionnaires(@Validated String caseInstanceId ) {
        return tenantCaseOrgQuestionnaireBiz.listSelectedQuestionnaires(caseInstanceId);
    }

    /**
     * 手动分配
     * @param
     * @return
     */
    @Operation(summary = "手动分配")
    @PostMapping("v1/tenantCasus/caseQuestionnaire/saveOrUpdOrgQuestionnaire")
    public Boolean saveOrUpdOrgQuestionnaire(@RequestBody @Validated List<CaseOrgQuestionnaireRequest> requests ) {
        return tenantCaseOrgQuestionnaireBiz.saveOrUpdOrgQuestionnaire(requests);
    }

    /**
     * 自动分配
     * @param
     * @return
     */
    @Operation(summary = "自动分配")
    @GetMapping("v1/tenantCasus/caseQuestionnaire/autoGenerate")
    public Boolean autoGenerate(@Validated String caseInstanceId ) {
        return tenantCaseOrgQuestionnaireBiz.autoGenerate(caseInstanceId);
    }
}
