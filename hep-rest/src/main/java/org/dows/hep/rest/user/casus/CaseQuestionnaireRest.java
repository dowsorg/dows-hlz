package org.dows.hep.rest.user.casus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.casus.request.AllocationQuestionnaireSearchRequest;
import org.dows.hep.api.user.casus.request.CaseQuestionnaireResultRequest;
import org.dows.hep.api.user.casus.response.AllocationSchemeResponse;
import org.dows.hep.biz.user.casus.CaseQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:案列:案例问卷
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "案例问卷", description = "案例问卷")
public class CaseQuestionnaireRest {
    private final CaseQuestionnaireBiz caseQuestionnaireBiz;

    /**
    * 获取案例问卷-user
    * @param
    * @return
    */
    @Operation(summary = "获取案例问卷-user")
    @PostMapping("v1/userCasus/caseQuestionnaire/getCaseQuestionnaireOfUser")
    public AllocationSchemeResponse getCaseQuestionnaireOfUser(@RequestBody @Validated AllocationQuestionnaireSearchRequest allocationQuestionnaireSearch ) {
        return caseQuestionnaireBiz.getCaseQuestionnaireOfUser(allocationQuestionnaireSearch);
    }

    /**
    * 提交结果
    * @param
    * @return
    */
    @Operation(summary = "提交结果")
    @PostMapping("v1/userCasus/caseQuestionnaire/submitCaseQuestionnaireResult")
    public Boolean submitCaseQuestionnaireResult(@RequestBody @Validated CaseQuestionnaireResultRequest caseQuestionnaireResult ) {
        return caseQuestionnaireBiz.submitCaseQuestionnaireResult(caseQuestionnaireResult);
    }


}