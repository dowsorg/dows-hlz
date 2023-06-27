package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireRequest;
import org.dows.hep.api.user.experiment.request.ExptQuestionnaireSearchRequest;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireResponse;
import org.dows.hep.biz.base.evaluate.EvaluateBaseBiz;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author lait.zhang
 * @description project descr:实验:实验知识答题
 * @folder user-hep/实验知识答题
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "实验知识答题", description = "实验知识答题")
public class ExperimentQuestionnaireRest {
    private final EvaluateBaseBiz baseBiz;
    private final ExperimentQuestionnaireBiz experimentQuestionnaireBiz;

    /**
     * 获取实验知识答题
     *
     * @param
     * @return
     */
    @Operation(summary = "获取实验知识答题")
    @GetMapping("v1/userExperiment/experimentQuestionnaire/getQuestionnaire")
    public ExperimentQuestionnaireResponse getQuestionnaire(ExptQuestionnaireSearchRequest searchRequest, HttpServletRequest request) {
        searchRequest.setExperimentAccountId(baseBiz.getAccountId(request));
        return experimentQuestionnaireBiz.getQuestionnaire(searchRequest);
    }

    /**
     * 保存知识答题
     * @param
     * @return
     */
    @Operation(summary = "保存知识答题")
    @PostMapping("v1/userExperiment/experimentQuestionnaire/updateQuestionnaire")
    public Boolean updateQuestionnaire(@RequestBody @Validated ExperimentQuestionnaireRequest eqRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentQuestionnaireBiz.updateQuestionnaire(eqRequest, accountId);
    }

    /**
     * 提交知识答题
     * @param
     * @return
     */
    @Operation(summary = "提交知识答题")
    @PutMapping("v1/userExperiment/experimentQuestionnaire/submitQuestionnaire")
    public Boolean submitQuestionnaire(@NotBlank String experimentQuestionnaireId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return experimentQuestionnaireBiz.submitQuestionnaire(experimentQuestionnaireId, accountId);
    }
}
