package org.dows.hep.rest.casus.tenant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.casus.tenant.request.CaseQuestionnaireRequest;
import org.dows.hep.api.casus.tenant.request.CaseQuestionnaireSearchRequest;
import org.dows.hep.api.casus.tenant.response.CaseQuestionnaireResponse;
import org.dows.hep.biz.casus.tenant.CaseQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:案例:案例问卷
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "案例问卷")
public class CaseQuestionnaireRest {
    private final CaseQuestionnaireBiz caseQuestionnaireBiz;

    /**
    * 新增和更新案例问卷
    * @param
    * @return
    */
    @ApiOperation("新增和更新案例问卷")
    @PostMapping("v1/casusTenant/caseQuestionnaire/saveOrUpdCaseQuestionnaire")
    public Boolean saveOrUpdCaseQuestionnaire(@RequestBody @Validated CaseQuestionnaireRequest caseQuestionnaire ) {
        return caseQuestionnaireBiz.saveOrUpdCaseQuestionnaire(caseQuestionnaire);
    }

    /**
    * 分页案例问卷
    * @param
    * @return
    */
    @ApiOperation("分页案例问卷")
    @PostMapping("v1/casusTenant/caseQuestionnaire/pageCaseQuestionnaire")
    public CaseQuestionnaireResponse pageCaseQuestionnaire(@RequestBody @Validated CaseQuestionnaireSearchRequest caseQuestionnaireSearch ) {
        return caseQuestionnaireBiz.pageCaseQuestionnaire(caseQuestionnaireSearch);
    }

    /**
    * 获取案例问卷
    * @param
    * @return
    */
    @ApiOperation("获取案例问卷")
    @GetMapping("v1/casusTenant/caseQuestionnaire/getCaseQuestionnaire")
    public void getCaseQuestionnaire(@Validated String caseQuestionnaireId) {
        caseQuestionnaireBiz.getCaseQuestionnaire(caseQuestionnaireId);
    }

    /**
    * 删除案例问卷
    * @param
    * @return
    */
    @ApiOperation("删除案例问卷")
    @DeleteMapping("v1/casusTenant/caseQuestionnaire/delCaseQuestionnaire")
    public Boolean delCaseQuestionnaire(@Validated String caseQuestionnaireId ) {
        return caseQuestionnaireBiz.delCaseQuestionnaire(caseQuestionnaireId);
    }


}