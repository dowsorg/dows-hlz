package org.dows.hep.rest.evaluate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.evaluate.request.CreateEvaluateQuestionnaireRequest;
import org.dows.hep.api.evaluate.response.EvaluateQuestionnaireResponse;
import org.dows.hep.biz.evaluate.EvaluateQuestionnaireBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:评估:评估问卷
*
* @author lait.zhang
* @date 2023年4月13日 下午7:47:15
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "评估问卷")
public class EvaluateQuestionnaireRest {
    private final EvaluateQuestionnaireBiz evaluateQuestionnaireBiz;

    /**
    * 创建评估问卷
    * @param
    * @return
    */
    @ApiOperation("创建评估问卷")
    @PostMapping("v1/evaluate/evaluateQuestionnaire/createEvaluateQuestionnaire")
    public void createEvaluateQuestionnaire(@RequestBody @Validated CreateEvaluateQuestionnaireRequest createEvaluateQuestionnaire ) {
        evaluateQuestionnaireBiz.createEvaluateQuestionnaire(createEvaluateQuestionnaire);
    }

    /**
    * 删除评估问卷
    * @param
    * @return
    */
    @ApiOperation("删除评估问卷")
    @DeleteMapping("v1/evaluate/evaluateQuestionnaire/deleteEvaluateQuestionnaire")
    public void deleteEvaluateQuestionnaire(@Validated String evaluateQuestionnaireId ) {
        evaluateQuestionnaireBiz.deleteEvaluateQuestionnaire(evaluateQuestionnaireId);
    }

    /**
    * 查询评估问卷
    * @param
    * @return
    */
    @ApiOperation("查询评估问卷")
    @GetMapping("v1/evaluate/evaluateQuestionnaire/getEvaluateQuestionnaire")
    public EvaluateQuestionnaireResponse getEvaluateQuestionnaire(@Validated String evaluateQuestionnaireId) {
        return evaluateQuestionnaireBiz.getEvaluateQuestionnaire(evaluateQuestionnaireId);
    }


}