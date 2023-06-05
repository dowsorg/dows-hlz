package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.request.QuestionSectionResultRequest;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireRequest;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireResponse;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.base.question.QuestionSectionResultBiz;
import org.dows.hep.entity.CaseQuestionnaireEntity;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.service.CaseQuestionnaireService;
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
    private final QuestionSectionBiz questionSectionBiz;
    private final CaseQuestionnaireService caseQuestionnaireService;
    private final ExperimentQuestionnaireService experimentQuestionnaireService;

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/6/3 20:53
     */
    public ExperimentQuestionnaireResponse getQuestionnaire(String experimentInstanceId, String periods, String experimentOrgId, String experimentGroupId, String experimentAccountId) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(periods, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(experimentOrgId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(experimentGroupId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(experimentAccountId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 根据实验id、期数、小组id， 机构id 获取知识答题
        ExperimentQuestionnaireEntity entity = experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentInstanceId, experimentAccountId)
                .eq(ExperimentQuestionnaireEntity::getPeriods, periods)
                .eq(ExperimentQuestionnaireEntity::getExperimentOrgId, experimentOrgId)
                .eq(ExperimentQuestionnaireEntity::getExperimentGroupId, experimentAccountId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.QUESTIONNAIRE_NOT_NULL));

        ExperimentQuestionnaireResponse result = new ExperimentQuestionnaireResponse();
        result.setExperimentQuestionnaireId(entity.getExperimentQuestionnaireId());
        String caseQuestionnaireId = entity.getCaseQuestionnaireId();
        String questionSectionResultId = entity.getQuestionSectionResultId();
        // 无 Result
        if (StrUtil.isBlank(questionSectionResultId)) {
            CaseQuestionnaireResponse questionnaireResponse = getCaseQuestionnaireResponse(caseQuestionnaireId);
            result.setCaseQuestionnaireResponse(questionnaireResponse);
            return result;
        }
        QuestionSectionResponse questionSectionResult = questionSectionResultBiz.getQuestionSectionResult(questionSectionResultId);
        if (BeanUtil.isEmpty(questionSectionResult)) {
            CaseQuestionnaireResponse questionnaireResponse = getCaseQuestionnaireResponse(caseQuestionnaireId);
            result.setCaseQuestionnaireResponse(questionnaireResponse);
            return result;
        }

        // 有 Result
        CaseQuestionnaireResponse questionnaireResponse = getCaseQuestionnaireResponse(caseQuestionnaireId, questionSectionResult);
        result.setCaseQuestionnaireResponse(questionnaireResponse);
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

    private CaseQuestionnaireResponse getCaseQuestionnaireResponse(String caseQuestionnaireId) {
        return getCaseQuestionnaireResponse0(caseQuestionnaireId, null);
    }

    private CaseQuestionnaireResponse getCaseQuestionnaireResponse(String caseQuestionnaireId, QuestionSectionResponse questionSectionResult) {
        return getCaseQuestionnaireResponse0(caseQuestionnaireId, questionSectionResult);
    }

    private CaseQuestionnaireResponse getCaseQuestionnaireResponse0(String caseQuestionnaireId, QuestionSectionResponse questionSectionResult) {
        CaseQuestionnaireEntity entity = getById(caseQuestionnaireId);
        if (BeanUtil.isEmpty(entity)) {
            throw new BizException(CaseESCEnum.DATA_NULL);
        }
        CaseQuestionnaireResponse result = BeanUtil.copyProperties(entity, CaseQuestionnaireResponse.class);

        // set question-section
        String questionSectionId = entity.getQuestionSectionId();
        if (BeanUtil.isEmpty(questionSectionResult)) {
            QuestionSectionResponse questionSectionResponse = questionSectionBiz.getQuestionSection(questionSectionId);
            result.setQuestionSectionResponse(questionSectionResponse);
        } else {
            result.setQuestionSectionResponse(questionSectionResult);
        }

        return result;
    }

    private CaseQuestionnaireEntity getById(String caseQuestionnaireId) {
        return caseQuestionnaireService.lambdaQuery()
                .eq(CaseQuestionnaireEntity::getCaseQuestionnaireId, caseQuestionnaireId)
                .one();
    }
}
