package org.dows.hep.rest.tenant.casus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* @description project descr:案例:案例问卷
*
* @author lait.zhang
* @date 2023年4月17日 下午8:00:11
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例问卷", description = "案例问卷")
public class TenantCaseQuestionnaireRest {
    private final TenantCaseQuestionnaireBiz tenantCaseQuestionnaireBiz;

    /**
    * 新增案例问卷
    * @param
    * @return
    */
    @Operation(summary = "新增和更新案例问卷")
    @PostMapping("v1/tenantCasus/caseQuestionnaire/saveCaseQuestionnaire")
    public String saveCaseQuestionnaire(@RequestBody @Validated CaseQuestionnaireRequest caseQuestionnaire ) {
        return tenantCaseQuestionnaireBiz.saveCaseQuestionnaire(caseQuestionnaire);
    }

    /**
     * 更新案例问卷
     * @param
     * @return
     */
    @Operation(summary = "新增和更新案例问卷")
    @PostMapping("v1/tenantCasus/caseQuestionnaire/updCaseQuestionnaire")
    public Boolean updCaseQuestionnaire(@RequestBody @Validated CaseQuestionnaireRequest caseQuestionnaire ) {
        return tenantCaseQuestionnaireBiz.updCaseQuestionnaire(caseQuestionnaire);
    }

    /**
    * 分页案例问卷
    * @param
    * @return
    */
    @Operation(summary = "分页案例问卷")
    @PostMapping("v1/tenantCasus/caseQuestionnaire/pageCaseQuestionnaire")
    public CaseQuestionnaireResponse pageCaseQuestionnaire(@RequestBody @Validated CaseQuestionnaireSearchRequest caseQuestionnaireSearch ) {
        return tenantCaseQuestionnaireBiz.pageCaseQuestionnaire(caseQuestionnaireSearch);
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
    * 删除案例问卷
    * @param
    * @return
    */
    @Operation(summary = "删除案例问卷")
    @DeleteMapping("v1/tenantCasus/caseQuestionnaire/delCaseQuestionnaire")
    public Boolean delCaseQuestionnaire(@Validated String caseQuestionnaireId ) {
        return tenantCaseQuestionnaireBiz.delCaseQuestionnaire(caseQuestionnaireId);
    }


}