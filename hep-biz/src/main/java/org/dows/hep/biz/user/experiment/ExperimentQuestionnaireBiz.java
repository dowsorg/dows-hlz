package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.request.QuestionSectionResultRequest;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireRequest;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireResponse;
import org.dows.hep.biz.base.question.QuestionSectionResultBiz;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.service.ExperimentQuestionnaireService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 20:43
 */
@AllArgsConstructor
@Service
public class ExperimentQuestionnaireBiz {
    private final QuestionSectionResultBiz questionSectionResultBiz;
    private final ExperimentQuestionnaireService experimentQuestionnaireService;

    /**
     * @author fhb
     * @description
     * @date 2023/6/3 20:53
     * @param
     * @return
     */
    public ExperimentQuestionnaireResponse getQuestionnaire(String experimentInstanceId, String periods, String experimentAccountId) {

        // todo 根据账号获取机构

        // todo 根据实验id、期数、机构id 获取知识答题
        ExperimentQuestionnaireResponse result = new ExperimentQuestionnaireResponse();
        return result;
    }

    /**
     * @author fhb
     * @description
     * @date 2023/6/3 21:02
     * @param 
     * @return 
     */
    public Boolean submitQuestionnaire(ExperimentQuestionnaireRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }
        String experimentSchemeId = Optional.of(request)
                .map(ExperimentQuestionnaireRequest::getExperimentQuestionnaireId)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.PARAMS_NON_NULL));
        QuestionSectionResultRequest questionSectionResultRequest = Optional.of(request)
                .map(ExperimentQuestionnaireRequest::getQuestionSectionResultRequest)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.PARAMS_NON_NULL));

        // save or update
        String questionSectionResultId = questionSectionResultBiz.saveOrUpdQuestionSectionResult(questionSectionResultRequest);

        return experimentQuestionnaireService.lambdaUpdate()
                .eq(ExperimentQuestionnaireEntity::getExperimentQuestionnaireId, experimentSchemeId)
                .set(ExperimentQuestionnaireEntity::getQuestionSectionResultId, questionSectionResultId)
                .update();
    }
}
