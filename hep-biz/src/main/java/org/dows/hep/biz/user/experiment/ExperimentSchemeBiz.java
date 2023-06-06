package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.request.QuestionSectionResultRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.base.question.QuestionSectionResultBiz;
import org.dows.hep.entity.CaseSchemeEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.service.CaseSchemeService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentSchemeService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lait.zhang
 * @description project descr:实验:实验方案
 * @date 2023年4月23日 上午9:44:34
 */
@AllArgsConstructor
@Service
public class ExperimentSchemeBiz {
    private final ExperimentSchemeService experimentSchemeService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final CaseSchemeService caseSchemeService;
    private final QuestionSectionBiz questionSectionBiz;
    private final QuestionSectionResultBiz questionSectionResultBiz;

    /**
     * @param
     * @return
     * @author fhb
     * @description 获取实验方案缩略图
     * @date 2023/5/30 19:25
     */
    public List<ExperimentSchemeItemResponse> getThumbnailScheme(String experimentInstanceId, String experimentGroupId) {
        if (StrUtil.isBlank(experimentGroupId) || StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        ExperimentSchemeEntity entity = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentSchemeEntity::getExperimentGroupId, experimentGroupId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_NOT_NULL));
        String caseSchemeId = entity.getCaseSchemeId();
        CaseSchemeResponse caseSchemeResponse = getCaseSchemeResponse(caseSchemeId);
        if (BeanUtil.isEmpty(caseSchemeResponse)) {
            throw new BizException(ExperimentESCEnum.SCHEME_NOT_NULL);
        }

        return convertCSR2ESR(caseSchemeResponse);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 获取组员实验方案缩略图
     * @date 2023/5/30 19:25
     */
    public List<ExperimentSchemeItemResponse> getThumbnailScheme(String experimentInstanceId, String experimentGroupId, String accountId) {
        List<ExperimentSchemeItemResponse> result = getThumbnailScheme(experimentInstanceId, experimentGroupId);
        if (BeanUtil.isEmpty(result)) {
            return result;
        }

        result.forEach(item -> item.setCanEdit(Boolean.FALSE));
        ExperimentParticipatorEntity experimentParticipatorEntity = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getAccountId, accountId)
                .oneOpt()
                .orElse(null);
        if (BeanUtil.isEmpty(experimentParticipatorEntity)) {
            return result;
        }

        String experimentSchemeItemIds = experimentParticipatorEntity.getExperimentSchemeItemIds();
        result.forEach(item -> {
            String questionSectionItemId = item.getQuestionSectionItemId();
            if (experimentSchemeItemIds.contains(questionSectionItemId)) {
                item.setCanEdit(Boolean.TRUE);
            }
        });
        return result;
    }

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
    public ExperimentSchemeResponse getScheme(String experimentInstanceId, String experimentGroupId) {
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
        CaseSchemeResponse caseScheme = getCaseSchemeResponse(caseSchemeId, questionSectionResult);
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

    private List<ExperimentSchemeItemResponse> convertCSR2ESR(CaseSchemeResponse caseSchemeResponse) {
        List<QuestionSectionItemResponse> sectionItemList = caseSchemeResponse.getSectionItemList();
        if (CollUtil.isEmpty(sectionItemList)) {
            return new ArrayList<>();
        }

        List<ExperimentSchemeItemResponse> result = new ArrayList<>();
        sectionItemList.forEach(sectionItem -> {
            QuestionResponse question = sectionItem.getQuestion();
            String questionSectionItemId = sectionItem.getQuestionSectionItemId();
            ExperimentSchemeItemResponse itemResponse = buildItemResponse(question, questionSectionItemId);
            if (BeanUtil.isNotEmpty(itemResponse)) {
                result.add(itemResponse);
            }
        });

        // set video-question
        Integer containsVideo = caseSchemeResponse.getContainsVideo();
        if (Objects.nonNull(containsVideo) && containsVideo == 1) {
            ExperimentSchemeItemResponse itemResponse = ExperimentSchemeItemResponse.builder()
                    .questionSectionItemId("1008610010")
                    .question(ExperimentSchemeItemResponse.Question.builder()
                            .questionId("1008610011")
                            .questionTitle("上传视频")
                            .build())
                    .build();
            result.add(itemResponse);
        }

        return result;
    }

    private ExperimentSchemeItemResponse buildItemResponse(QuestionResponse question, String questionSectionItemId) {
        return ExperimentSchemeItemResponse.builder()
                .questionSectionItemId(questionSectionItemId)
                .question(buildQuestion(question))
                .build();
    }

    private ExperimentSchemeItemResponse.Question buildQuestion(QuestionResponse question) {
        // 判空
        if (BeanUtil.isEmpty(question)) {
            return null;
        }

        // 处理当前结点
        ExperimentSchemeItemResponse.Question result = ExperimentSchemeItemResponse.Question.builder()
                .questionId(question.getQuestionInstanceId())
                .questionTitle(question.getQuestionTitle())
                .build();

        // 是否有子类
        List<QuestionResponse> children = question.getChildren();
        if (CollUtil.isEmpty(children)) {
            return result;
        }

        // 处理子类
        List<ExperimentSchemeItemResponse.Question> itemList = new ArrayList<>();
        children.forEach(questionResponse -> {
            ExperimentSchemeItemResponse.Question itemQuestion = buildQuestion(questionResponse);
            itemList.add(itemQuestion);
        });
        result.setChildren(itemList);

        return result;
    }

    private CaseSchemeResponse getCaseSchemeResponse(String caseSchemeId) {
        return getCaseScheme0(caseSchemeId, null);
    }

    private CaseSchemeResponse getCaseSchemeResponse(String caseSchemeId, QuestionSectionResponse questionSectionResult) {
        return getCaseScheme0(caseSchemeId, questionSectionResult);
    }

    private CaseSchemeResponse getCaseScheme0(String caseSchemeId, QuestionSectionResponse questionSectionResult) {
        CaseSchemeEntity caseSchemeEntity = getById(caseSchemeId);
        if (BeanUtil.isEmpty(caseSchemeEntity)) {
            throw new BizException(CaseESCEnum.DATA_NULL);
        }
        CaseSchemeResponse result = BeanUtil.copyProperties(caseSchemeEntity, CaseSchemeResponse.class);

        // set question-section
        String questionSectionId = caseSchemeEntity.getQuestionSectionId();
        if (BeanUtil.isEmpty(questionSectionResult)) {
            QuestionSectionResponse questionSectionResponse = questionSectionBiz.getQuestionSection(questionSectionId);
            fillResponseQS(questionSectionResponse, result);
        } else {
            fillResponseQS(questionSectionResult, result);
        }

        return result;
    }

    private CaseSchemeEntity getById(String caseSchemeId) {
        return caseSchemeService.lambdaQuery()
                .eq(CaseSchemeEntity::getCaseSchemeId, caseSchemeId)
                .one();
    }

    private void fillResponseQS(QuestionSectionResponse questionSectionResponse, CaseSchemeResponse result) {
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