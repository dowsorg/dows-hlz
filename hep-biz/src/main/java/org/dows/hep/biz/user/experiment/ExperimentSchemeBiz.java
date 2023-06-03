package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.request.QuestionSectionResultRequest;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.base.question.QuestionSectionResultBiz;
import org.dows.hep.entity.CaseSchemeEntity;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.service.CaseSchemeService;
import org.dows.hep.service.ExperimentSchemeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author lait.zhang
 * @description project descr:实验:实验方案
 * @date 2023年4月23日 上午9:44:34
 */
@AllArgsConstructor
@Service
public class ExperimentSchemeBiz {
    private final ExperimentSchemeService experimentSchemeService;
    private final CaseSchemeService caseSchemeService;
    private final QuestionSectionBiz questionSectionBiz;
    private final QuestionSectionResultBiz questionSectionResultBiz;

    /**
     * @param
     * @return
     * @说明: 获取实验方案-有Result则带Result返回，没有则返回空白试卷
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public ExperimentSchemeResponse getCaseScheme(String experimentInstanceId, String experimentGroupId) {
        if (StrUtil.isBlank(experimentGroupId) || StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        ExperimentSchemeEntity entity = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentSchemeEntity::getExperimentGroupId, experimentGroupId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_NOT_NULL));

        ExperimentSchemeResponse result = new ExperimentSchemeResponse();
        result.setExperimentSchemeId(entity.getExperimentSchemeId());
        String caseSchemeId = entity.getCaseSchemeId();
        String questionSectionResultId = entity.getQuestionSectionResultId();
        // 无 Result
        if (StrUtil.isBlank(questionSectionResultId)) {
            CaseSchemeResponse caseScheme = getCaseSchemeResponse(caseSchemeId);
            result.setCaseSchemeResponse(caseScheme);
            return result;
        }
        QuestionSectionResponse questionSectionResult = questionSectionResultBiz.getQuestionSectionResult(questionSectionResultId);
        if (BeanUtil.isEmpty(questionSectionResult)) {
            CaseSchemeResponse caseScheme = getCaseSchemeResponse(caseSchemeId);
            result.setCaseSchemeResponse(caseScheme);
            return result;
        }

        // 有 Result
        CaseSchemeResponse caseScheme = getCaseSchemeResponse(caseSchemeId, questionSectionResultId);
        result.setCaseSchemeResponse(caseScheme);
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 提交方案Result
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean submitScheme(ExperimentSchemeRequest experimentSchemeRequest) {
        if (BeanUtil.isEmpty(experimentSchemeRequest)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }
        String experimentSchemeId = Optional.of(experimentSchemeRequest)
                .map(ExperimentSchemeRequest::getExperimentSchemeId)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.PARAMS_NON_NULL));
        QuestionSectionResultRequest questionSectionResultRequest = Optional.of(experimentSchemeRequest)
                .map(ExperimentSchemeRequest::getQuestionSectionResultRequest)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.PARAMS_NON_NULL));

        // save or update
        String questionSectionResultId = questionSectionResultBiz.saveOrUpdQuestionSectionResult(questionSectionResultRequest);

        return experimentSchemeService.lambdaUpdate()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, experimentSchemeId)
                .set(ExperimentSchemeEntity::getQuestionSectionResultId, questionSectionResultId)
                .update();
    }

    private CaseSchemeResponse getCaseSchemeResponse(String caseSchemeId) {
        return getCaseScheme0(caseSchemeId, null);
    }

    private CaseSchemeResponse getCaseSchemeResponse(String caseSchemeId, String questionSectionResultId) {
        return getCaseScheme0(caseSchemeId, questionSectionResultId);
    }

    private CaseSchemeResponse getCaseScheme0(String caseSchemeId, String questionSectionResultId) {
        CaseSchemeEntity caseSchemeEntity = getById(caseSchemeId);
        if (BeanUtil.isEmpty(caseSchemeEntity)) {
            throw new BizException(CaseESCEnum.DATA_NULL);
        }
        CaseSchemeResponse result = BeanUtil.copyProperties(caseSchemeEntity, CaseSchemeResponse.class);
        // set question-section
        String questionSectionId = caseSchemeEntity.getQuestionSectionId();
        fillResponseQS(questionSectionId, questionSectionResultId, result);
        return result;
    }

    private CaseSchemeEntity getById(String caseSchemeId) {
        return caseSchemeService.lambdaQuery()
                .eq(CaseSchemeEntity::getCaseSchemeId, caseSchemeId)
                .one();
    }

    private void fillResponseQS(String questionSectionId, String questionSectionResultId, CaseSchemeResponse result) {
        // get and set question-section
        QuestionSectionResponse questionSectionResponse = null;
        if (StrUtil.isNotBlank(questionSectionResultId)) {
            questionSectionResponse = questionSectionResultBiz.getQuestionSectionResult(questionSectionResultId);
        } else {
            questionSectionResponse = questionSectionBiz.getQuestionSection(questionSectionId);
        }
        if (BeanUtil.isEmpty(questionSectionResponse)) {
            return;
        }

        List<QuestionSectionItemResponse> sectionItemList = questionSectionResponse.getSectionItemList();
        List<QuestionSectionDimensionResponse> questionSectionDimensionList = questionSectionResponse.getQuestionSectionDimensionList();
        Map<String, List<QuestionSectionDimensionResponse>> questionSectionDimensionMap = questionSectionResponse.getQuestionSectionDimensionMap();
        result.setSectionItemList(sectionItemList);
        result.setQuestionSectionDimensionList(questionSectionDimensionList);
        result.setQuestionSectionDimensionMap(questionSectionDimensionMap);
    }
}