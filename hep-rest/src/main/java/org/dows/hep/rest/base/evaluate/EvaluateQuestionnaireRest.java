package org.dows.hep.rest.base.evaluate;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.evaluate.request.EvaluateQuestionnairePageRequest;
import org.dows.hep.api.base.evaluate.request.EvaluateQuestionnaireRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateQuestionnairePageResponse;
import org.dows.hep.api.base.evaluate.response.EvaluateQuestionnaireResponse;
import org.dows.hep.biz.base.evaluate.EvaluateBaseBiz;
import org.dows.hep.biz.base.evaluate.EvaluateQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:评估:评估问卷
* @folder admin-hep/评估问卷
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "评估问卷", description = "评估问卷")
public class EvaluateQuestionnaireRest {
    private final EvaluateBaseBiz baseBiz;
    private final EvaluateQuestionnaireBiz evaluateQuestionnaireBiz;

    /**
    * 新增或更新评估问卷
    * @param
    * @return
    */
    @Operation(summary = "新增或更新评估问卷")
    @PostMapping("v1/baseEvaluate/evaluateQuestionnaire/saveOrUpdEvaluateQuestionnaire")
    public String saveOrUpdEvaluateQuestionnaire(@RequestBody @Validated EvaluateQuestionnaireRequest evaluateQuestionnaireRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        String accountName = baseBiz.getAccountName(request);
        evaluateQuestionnaireRequest.setAccountId(accountId);
        evaluateQuestionnaireRequest.setAccountName(accountName);
        return evaluateQuestionnaireBiz.saveOrUpdEQ(evaluateQuestionnaireRequest);
    }

    /**
     * 分页筛选评估问卷
     * @param
     * @return
     */
    @Operation(summary = "分页筛选评估问卷")
    @PostMapping("v1/baseEvaluate/evaluateQuestionnaire/pageEvaluateQuestionnaire")
    public IPage<EvaluateQuestionnairePageResponse> pageEvaluateQuestionnaire(@RequestBody EvaluateQuestionnairePageRequest request) {
        return evaluateQuestionnaireBiz.pageEvaluateQuestionnaire(request);
    }

    /**
     * 获取评估问卷
     * @param
     * @return
     */
    @Operation(summary = "获取评估问卷")
    @GetMapping("v1/baseEvaluate/evaluateQuestionnaire/getEvaluateQuestionnaire")
    public EvaluateQuestionnaireResponse getEvaluateQuestionnaire(@Validated String evaluateQuestionnaireId) {
        return evaluateQuestionnaireBiz.getEvaluateQuestionnaire(evaluateQuestionnaireId, null);
    }

    /**
     * 启用
     * @param
     * @return
     */
    @Operation(summary = "启用")
    @GetMapping("v1/baseEvaluate/evaluateQuestionnaire/enabledQuestionnaire")
    public Boolean enabledQuestion(@Validated String questionInstanceId) {
        return evaluateQuestionnaireBiz.enabledQuestionnaire(questionInstanceId);
    }

    /**
     * 禁用
     * @param
     * @return
     */
    @Operation(summary = "禁用")
    @GetMapping("v1/baseEvaluate/evaluateQuestionnaire/disabledQuestionnaire")
    public Boolean disabledQuestion(@Validated String questionInstanceId) {
        return evaluateQuestionnaireBiz.disabledQuestionnaire(questionInstanceId);
    }

    /**
    * 删除or批量删除评估问卷
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除评估问卷")
    @DeleteMapping("v1/baseEvaluate/evaluateQuestionnaire/deleteEvaluateQuestionnaire")
    public Boolean deleteEvaluateQuestionnaire(@RequestBody List<String> evaluateQuestionnaireIds ) {
        return evaluateQuestionnaireBiz.deleteEvaluateQuestionnaire(evaluateQuestionnaireIds);
    }

    /**
     * 根据维度获取题目
     * @param
     * @return
     */
    @Operation(summary = "根据维度获取题目")
    @GetMapping("v1/baseEvaluate/evaluateQuestionnaire/listQuestionByDimension")
    public List<EvaluateQuestionnaireBiz.Question> listQuestionByDimension(@RequestParam String evaluateQuestionnaireId, @RequestParam String questionSectionDimensionId ) {
        return evaluateQuestionnaireBiz.listQuestionByDimension(evaluateQuestionnaireId, questionSectionDimensionId);
    }
}