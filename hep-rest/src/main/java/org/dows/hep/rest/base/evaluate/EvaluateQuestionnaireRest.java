package org.dows.hep.rest.base.evaluate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.evaluate.request.EvaluateQuestionnaireRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateQuestionnaireResponse;
import org.dows.hep.biz.base.evaluate.EvaluateQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:评估:评估问卷
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "评估问卷", description = "评估问卷")
public class EvaluateQuestionnaireRest {
    private final EvaluateQuestionnaireBiz evaluateQuestionnaireBiz;

    /**
    * 创建评估问卷
    * @param
    * @return
    */
    @Operation(summary = "创建评估问卷")
    @PostMapping("v1/baseEvaluate/evaluateQuestionnaire/createEvaluateQuestionnaire")
    public void createEvaluateQuestionnaire(@RequestBody @Validated EvaluateQuestionnaireRequest createEvaluateQuestionnaire ) {
        evaluateQuestionnaireBiz.createEvaluateQuestionnaire(createEvaluateQuestionnaire);
    }

    /**
    * 删除评估问卷
    * @param
    * @return
    */
    @Operation(summary = "删除评估问卷")
    @DeleteMapping("v1/baseEvaluate/evaluateQuestionnaire/deleteEvaluateQuestionnaire")
    public void deleteEvaluateQuestionnaire(@Validated String evaluateQuestionnaireId ) {
        evaluateQuestionnaireBiz.deleteEvaluateQuestionnaire(evaluateQuestionnaireId);
    }

    /**
    * 获取评估问卷
    * @param
    * @return
    */
    @Operation(summary = "获取评估问卷")
    @GetMapping("v1/baseEvaluate/evaluateQuestionnaire/getEvaluateQuestionnaire")
    public EvaluateQuestionnaireResponse getEvaluateQuestionnaire(@Validated String evaluateQuestionnaireId) {
        return evaluateQuestionnaireBiz.getEvaluateQuestionnaire(evaluateQuestionnaireId);
    }

    /**
    * 筛选评估问卷
    * @param
    * @return
    */
    @Operation(summary = "筛选评估问卷")
    @GetMapping("v1/baseEvaluate/evaluateQuestionnaire/listEvaluateQuestionnaire")
    public List<EvaluateQuestionnaireResponse> listEvaluateQuestionnaire(@Validated String appId, @Validated String questionSectionId) {
        return evaluateQuestionnaireBiz.listEvaluateQuestionnaire(appId,questionSectionId);
    }

    /**
    * 分页筛选评估问卷
    * @param
    * @return
    */
    @Operation(summary = "分页筛选评估问卷")
    @GetMapping("v1/baseEvaluate/evaluateQuestionnaire/pageEvaluateQuestionnaire")
    public String pageEvaluateQuestionnaire(@Validated Integer pageNo, @Validated Integer pageSize, @Validated String appId, @Validated String questionSectionId) {
        return evaluateQuestionnaireBiz.pageEvaluateQuestionnaire(pageNo,pageSize,appId,questionSectionId);
    }


}