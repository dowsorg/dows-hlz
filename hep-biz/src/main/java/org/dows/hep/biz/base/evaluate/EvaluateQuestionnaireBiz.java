package org.dows.hep.biz.base.evaluate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.evaluate.EvaluateESCEnum;
import org.dows.hep.api.base.evaluate.EvaluateEnabledEnum;
import org.dows.hep.api.base.evaluate.request.EvaluateQuestionnairePageRequest;
import org.dows.hep.api.base.evaluate.request.EvaluateQuestionnaireRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateCategoryResponse;
import org.dows.hep.api.base.evaluate.response.EvaluateQuestionnairePageResponse;
import org.dows.hep.api.base.evaluate.response.EvaluateQuestionnaireResponse;
import org.dows.hep.api.base.question.dto.QuestionResultRecordDTO;
import org.dows.hep.api.base.question.enums.QuestionEnabledEnum;
import org.dows.hep.api.base.question.enums.QuestionSectionAccessAuthEnum;
import org.dows.hep.api.base.question.enums.QuestionSectionGenerationModeEnum;
import org.dows.hep.api.base.question.enums.QuestionSourceEnum;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.biz.base.question.QuestionDimensionBiz;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.entity.EvaluateQuestionnaireEntity;
import org.dows.hep.entity.QuestionDimensionEntity;
import org.dows.hep.service.EvaluateQuestionnaireService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:评估:评估问卷
 * @date 2023年4月23日 上午9:44:34
 */
@AllArgsConstructor
@Service
public class EvaluateQuestionnaireBiz {
    private final EvaluateBaseBiz baseBiz;
    private final EvaluateQuestionnaireService evaluateQuestionnaireService;
    private final QuestionSectionBiz questionSectionBiz;
    private final EvaluateCategoryBiz categoryBiz;
    private final QuestionDimensionBiz questionDimensionBiz;


    /**
     * @param
     * @return
     * @说明: 创建评估问卷
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public String saveOrUpdEQ(EvaluateQuestionnaireRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        EvaluateQuestionnaireEntity entity = convertRequest2Entity(request, QuestionSourceEnum.ADMIN);
        evaluateQuestionnaireService.saveOrUpdate(entity);

        return entity.getEvaluateQuestionnaireId();
    }

    /**
     * @param
     * @return
     * @说明: 分页筛选评估问卷
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public IPage<EvaluateQuestionnairePageResponse> pageEvaluateQuestionnaire(EvaluateQuestionnairePageRequest request) {
        if (BeanUtil.isEmpty(request)) {
            return new Page<>();
        }

        // page
        Page<EvaluateQuestionnaireEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<EvaluateQuestionnaireEntity> pageResult = evaluateQuestionnaireService.lambdaQuery()
                .in(request.getCategIds() != null && !request.getCategIds().isEmpty(), EvaluateQuestionnaireEntity::getEvaluateCategId, request.getCategIds())
                .like(StrUtil.isNotBlank(request.getKeyword()), EvaluateQuestionnaireEntity::getEvaluateQuestionnaireName, request.getKeyword())
                .page(page);

        // convert
        Page<EvaluateQuestionnairePageResponse> result = baseBiz.convertPage(pageResult, EvaluateQuestionnairePageResponse.class);
        fillPageResponse(result);
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 获取评估问卷
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public EvaluateQuestionnaireResponse getEvaluateQuestionnaire(String evaluateQuestionnaireId, QuestionResultRecordDTO recordDTO) {
        if (StrUtil.isBlank(evaluateQuestionnaireId)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        EvaluateQuestionnaireEntity entity = getById(evaluateQuestionnaireId);
        EvaluateQuestionnaireResponse result = BeanUtil.copyProperties(entity, EvaluateQuestionnaireResponse.class);
        // set question-section
        String questionSectionId = entity.getQuestionSectionId();
        fillResponseQS(questionSectionId, result, recordDTO);

        return result;
    }

    /**
     * @param
     * @return
     * @说明: 获取评估问卷
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public EvaluateQuestionnaireEntity getById(String evaluateQuestionnaireId) {
        LambdaQueryWrapper<EvaluateQuestionnaireEntity> queryWrapper = new LambdaQueryWrapper<EvaluateQuestionnaireEntity>()
                .eq(EvaluateQuestionnaireEntity::getEvaluateQuestionnaireId, evaluateQuestionnaireId);
        return evaluateQuestionnaireService.getOne(queryWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 启用
     * @关联表:
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean enabledQuestionnaire(String questionInstanceId ) {
        if (StrUtil.isBlank(questionInstanceId)) {
            return false;
        }

        return changeEnable(questionInstanceId, EvaluateEnabledEnum.ENABLED);
    }

    /**
     * @param
     * @return
     * @说明: 禁用
     * @关联表:
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean disabledQuestionnaire(String questionInstanceId ) {
        if (StrUtil.isBlank(questionInstanceId)) {
            return false;
        }

        return changeEnable(questionInstanceId, EvaluateEnabledEnum.DISABLED);
    }

    /**
     * @param
     * @return
     * @说明: 删除评估问卷
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean deleteEvaluateQuestionnaire(List<String> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        // 删除 self
        LambdaQueryWrapper<EvaluateQuestionnaireEntity> remWrapper = new LambdaQueryWrapper<EvaluateQuestionnaireEntity>()
                .in(EvaluateQuestionnaireEntity::getEvaluateQuestionnaireId, ids);
        boolean removeRes1 = evaluateQuestionnaireService.remove(remWrapper);

        // TODO: 2023/5/24
        // 删除关联

        return removeRes1;
    }

    private EvaluateQuestionnaireEntity convertRequest2Entity(EvaluateQuestionnaireRequest request, QuestionSourceEnum questionSourceEnum) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        // save or upd question-section
        String questionSectionId = saveOrUpdQuestionSection(request, questionSourceEnum);

        // save or upd questionnaire
        EvaluateQuestionnaireEntity result = EvaluateQuestionnaireEntity.builder()
                .appId(baseBiz.getAppId())
                .evaluateQuestionnaireId(request.getEvaluateQuestionnaireId())
                .evaluateCategId(request.getEvaluateCategId())
                .evaluateQuestionnaireName(request.getEvaluateQuestionnaireName())
                .evaluateQuestionnaireDesc(request.getEvaluateQuestionnaireDesc())
                .operationPrompt(request.getOperationPrompt())
                .tips(request.getTips())
                .enabled(request.getEnabled())
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .questionSectionId(questionSectionId)
                .build();

        String uniqueId = result.getEvaluateQuestionnaireId();
        if (StrUtil.isBlank(uniqueId)) {
            result.setEvaluateQuestionnaireId(baseBiz.getIdStr());
            if (result.getEnabled() == null) {
                result.setEnabled(EvaluateEnabledEnum.ENABLED.getCode());
            }
        } else {
            EvaluateQuestionnaireEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException(EvaluateESCEnum.DATA_NULL);
            }
            result.setId(entity.getId());
        }

        return result;
    }

    private String saveOrUpdQuestionSection(EvaluateQuestionnaireRequest request, QuestionSourceEnum questionSourceEnum) {
        QuestionSectionRequest questionSectionRequest = evaluateQuestionnaire2QS(request);
        return questionSectionBiz.saveOrUpdQuestionSection(questionSectionRequest, QuestionSectionAccessAuthEnum.PRIVATE_VIEWING, questionSourceEnum);
    }

    private QuestionSectionRequest evaluateQuestionnaire2QS(EvaluateQuestionnaireRequest request) {
        return QuestionSectionRequest.builder()
                .questionSectionId(request.getQuestionSectionId())
                .name(request.getEvaluateQuestionnaireName())
                .tips(null)
                .descr(request.getEvaluateQuestionnaireDesc())
                .enabled(QuestionEnabledEnum.ENABLED.getCode())
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .sectionItemList(request.getSectionItemList())
                .questionSectionDimensionList(request.getQuestionSectionDimensionList())
                .generationMode(QuestionSectionGenerationModeEnum.ADD_NEW)
                .build();
    }

    private void fillPageResponse(Page<EvaluateQuestionnairePageResponse> result) {
        List<EvaluateQuestionnairePageResponse> records = result.getRecords();
        if (records != null && !records.isEmpty()) {
            List<String> categIds = records.stream()
                    .map(EvaluateQuestionnairePageResponse::getEvaluateCategId)
                    .toList();
            List<EvaluateCategoryResponse> responseList = categoryBiz.listCaseCategory(categIds);
            Map<String, String> collect = responseList.stream()
                    .collect(Collectors.toMap(EvaluateCategoryResponse::getEvaluateCategId, EvaluateCategoryResponse::getEvaluateCategName, (v1, v2) -> v1));
            records.forEach(item -> {
                item.setEvaluateCategName(collect.get(item.getEvaluateCategId()));
            });
        }
    }

    private void fillResponseQS(String questionSectionId, EvaluateQuestionnaireResponse result, QuestionResultRecordDTO recordDTO) {
        // get and set question-section
        QuestionSectionResponse questionSectionResponse = questionSectionBiz.getQuestionSection(questionSectionId, recordDTO);
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

    private boolean changeEnable(String evaluateQuestionnaireId, EvaluateEnabledEnum evaluateEnabledEnum) {
        LambdaUpdateWrapper<EvaluateQuestionnaireEntity> updateWrapper = new LambdaUpdateWrapper<EvaluateQuestionnaireEntity>()
                .eq(EvaluateQuestionnaireEntity::getEvaluateQuestionnaireId, evaluateQuestionnaireId)
                .set(EvaluateQuestionnaireEntity::getEnabled, evaluateEnabledEnum.getCode());
        return evaluateQuestionnaireService.update(updateWrapper);
    }

    public List<Question> listQuestionByDimension(String evaluateQuestionnaireId, String questionSectionDimensionId) {
        if (StrUtil.isBlank(evaluateQuestionnaireId) || StrUtil.isBlank(questionSectionDimensionId)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        EvaluateQuestionnaireResponse evaluateQuestionnaire = getEvaluateQuestionnaire(evaluateQuestionnaireId, null);
        if (BeanUtil.isEmpty(evaluateQuestionnaire)) {
            return new ArrayList<>();
        }
        List<QuestionSectionItemResponse> sectionItemList = evaluateQuestionnaire.getSectionItemList();
        if (CollUtil.isEmpty(sectionItemList)) {
            return new ArrayList<>();
        }
        List<QuestionResponse> questionList = sectionItemList.stream()
                .map(QuestionSectionItemResponse::getQuestion)
                .toList();
        if (CollUtil.isEmpty(questionList)) {
            return new ArrayList<>();
        }

        // 获取问题关联的问题集维度ID
        List<String> dimensionIdList = questionList.stream()
                .map(QuestionResponse::getDimensionId)
                .toList();
        List<QuestionDimensionEntity> entityList = questionDimensionBiz.listDimensionByIds(dimensionIdList);
        if (CollUtil.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        Map<String, String> collect = entityList.stream()
                .collect(Collectors.toMap(QuestionDimensionEntity::getQuestionInstanceId, QuestionDimensionEntity::getQuestionSectionDimensionId, (v1, v2) -> v1));


        List<Question> result = new ArrayList<>();
        for (int i = 0; i < questionList.size(); i++) {
            QuestionResponse question = questionList.get(i);
            Question resultItem = new Question();
            resultItem.setQuestionInstanceId(question.getQuestionInstanceId());
            resultItem.setSeq(i);
            resultItem.setQuestionSectionDimensionId(collect.get(question.getQuestionInstanceId()));
            result.add(resultItem);
        }

        result = result.stream()
                .filter(item -> item.getQuestionSectionDimensionId().equals(questionSectionDimensionId))
                .toList();
        return result;
    }

    @Data
    @NoArgsConstructor
    public static class Question {
        private String questionSectionDimensionId;
        private String questionInstanceId;
        private Integer seq;
    }
}