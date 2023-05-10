package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.tenant.casus.QuestionSelectModeEnum;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.biz.base.question.QuestionInstanceBiz;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.tenant.casus.handler.CaseQuestionnaireFactory;
import org.dows.hep.biz.tenant.casus.handler.CaseQuestionnaireHandler;
import org.dows.hep.entity.CaseInstanceEntity;
import org.dows.hep.entity.CaseQuestionnaireEntity;
import org.dows.hep.entity.QuestionSectionEntity;
import org.dows.hep.service.CaseQuestionnaireService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:案例:案例问卷
 * @date 2023年4月17日 下午8:00:11
 */
@RequiredArgsConstructor
@Service
public class TenantCaseQuestionnaireBiz {
    private final TenantCaseBaseBiz baseBiz;
    private final QuestionSectionBiz questionSectionBiz;
    private final TenantCaseManageBiz caseManageBiz;
    private final QuestionInstanceBiz questionInstanceBiz;
    private final CaseQuestionnaireService caseQuestionnaireService;

    /**
     * @param
     * @return
     * @说明: 新增案例问卷
     * @关联表: caseQuestionnaire
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    @DSTransactional
    public String saveCaseQuestionnaire(CaseQuestionnaireRequest caseQuestionnaire) {
        if (BeanUtil.isEmpty(caseQuestionnaire) || StrUtil.isBlank(caseQuestionnaire.getCaseInstanceId())) {
            return "";
        }
        String caseInstanceId = caseQuestionnaire.getCaseInstanceId();

        // prepare base-info
        // get case-instance
        CaseInstanceEntity caseInstance = caseManageBiz.getById(caseInstanceId);
        // file caseQuestionnaire
        fillCaseQuestionnaire(caseQuestionnaire, caseInstance);
        
        // core
        // save question-section
        String questionSectionId = saveQuestionSection(caseQuestionnaire);
        // save case-questionnaire
        CaseQuestionnaireEntity caseQuestionnaireEntity = saveCaseQuestionnaire(caseQuestionnaire, questionSectionId);

        return caseQuestionnaireEntity.getCaseQuestionnaireId();
    }

    

    /**
     * @param
     * @return
     * @说明: 更新案例问卷
     * @关联表: caseQuestionnaire
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public Boolean updCaseQuestionnaire(CaseQuestionnaireRequest caseQuestionnaire) {
        return Boolean.FALSE;
    }

    /**
     * @param
     * @return
     * @说明: 分页案例问卷
     * @关联表: caseQuestionnaire
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public CaseQuestionnaireResponse pageCaseQuestionnaire(CaseQuestionnaireSearchRequest caseQuestionnaireSearch) {
        return new CaseQuestionnaireResponse();
    }

    /**
     * @param
     * @return
     * @说明: 列出可用的问题，从问题数据源中
     * @关联表: caseQuestionnaire
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public List<QuestionResponse> listUsableQuestionFromSource(QuestionSearchRequest questionSearchRequest, String caseInstanceId) {
        return listUsableQuestionFromSource0(questionSearchRequest, caseInstanceId);
    }

    /**
     * @param
     * @return
     * @说明: 列出可用的问题，从问题数据源中， 返回题型及数量
     * @关联表: caseQuestionnaire
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public Map<String, Long> collectQuestionCountOfUsableQuestion(QuestionSearchRequest questionSearchRequest, String caseInstanceId) {
        Map<String, List<QuestionResponse>> collectList = collectQuestionOfUsableQuestion0(questionSearchRequest, caseInstanceId);
        if (collectList.isEmpty()) {
            return new HashMap<>();
        }

        HashMap<String, Long> result = new HashMap<>();
        collectList.forEach((key, value) -> {
            Long size = (long) value.size();
            result.put(key, size);
        });
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 列出可用的问题，从问题数据源中， 返回题型及对应题目集合
     * @关联表: caseQuestionnaire
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public Map<String, List<QuestionResponse>> collectQuestionOfUsableQuestion(QuestionSearchRequest questionSearchRequest, String caseInstanceId) {
        return collectQuestionOfUsableQuestion0(questionSearchRequest, caseInstanceId);
    }

    /**
     * @param
     * @return
     * @说明: 获取案例问卷
     * @关联表: caseQuestionnaire
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public void getCaseQuestionnaire(String caseQuestionnaireId ) {

    }

    /**
     * @param
     * @return
     * @说明: 删除案例问卷
     * @关联表: caseQuestionnaire
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public Boolean delCaseQuestionnaire(String caseQuestionnaireId) {
        return Boolean.FALSE;
    }

    private List<String> listQuestionIdOfCaseInstance(String caseInstanceId) {
        // list questionnaire of case-instance
        LambdaQueryWrapper<CaseQuestionnaireEntity> queryWrapper = new LambdaQueryWrapper<CaseQuestionnaireEntity>()
                .eq(CaseQuestionnaireEntity::getCaseInstanceId, caseInstanceId);
        List<CaseQuestionnaireEntity> caseQuestionnaireList = caseQuestionnaireService.list(queryWrapper);
        if (caseQuestionnaireList == null || caseQuestionnaireList.isEmpty()) {
            return new ArrayList<>();
        }

        // list section-item
        List<String> sectionIdList = caseQuestionnaireList.stream().map(CaseQuestionnaireEntity::getQuestionSectionId).toList();
        List<QuestionSectionItemResponse> itemResponseList = questionSectionBiz.listItem(sectionIdList);
        if (itemResponseList == null || itemResponseList.isEmpty()) {
            return new ArrayList<>();
        }

        // list question-id
        return itemResponseList.stream()
                .map(QuestionSectionItemResponse::getQuestionResponse)
                .map(QuestionResponse::getQuestionInstanceId)
                .distinct()
                .toList();
    }

    private List<QuestionResponse> listQuestionFromSource0(QuestionSearchRequest questionSearchRequest) {
        questionSearchRequest.setAppId(baseBiz.getAppId());
        return questionInstanceBiz.listQuestion(questionSearchRequest);
    }

    @NotNull
    private List<QuestionResponse> listUsableQuestionFromSource0(QuestionSearchRequest questionSearchRequest, String caseInstanceId) {
        // list from question source
        List<QuestionResponse> questionResponses = listQuestionFromSource0(questionSearchRequest);

        // list existing-question of case-instance
        List<String> existingIds = listQuestionIdOfCaseInstance(caseInstanceId);

        // filter usable question
        return filterUsableQuestion(questionResponses, existingIds);
    }

    @NotNull
    private List<QuestionResponse> filterUsableQuestion(List<QuestionResponse> questionResponses, List<String> existingIds) {
        Map<String, String> existingIdsMap = existingIds.stream().collect(Collectors.toMap(item -> item, item -> item, (v1, v2) -> v1));
        return questionResponses.stream()
                .filter(item -> {
                    String questionInstanceId = item.getQuestionInstanceId();
                    return existingIdsMap.get(questionInstanceId) == null;
                }).toList();
    }

    @NotNull
    private Map<String, List<QuestionResponse>> collectQuestionOfUsableQuestion0(QuestionSearchRequest questionSearchRequest, String caseInstanceId) {
        HashMap<String, List<QuestionResponse>> result = new HashMap<>();
        result.put(QuestionTypeEnum.RADIO_SELECT.getCode(), new ArrayList<>());
        result.put(QuestionTypeEnum.MULTIPLE_SELECT.getCode(), new ArrayList<>());
        result.put(QuestionTypeEnum.MATERIAL.getCode(), new ArrayList<>());

        List<QuestionResponse> questionResponses = listUsableQuestionFromSource0(questionSearchRequest, caseInstanceId);
        if (questionResponses.isEmpty()) {
            return result;
        }

        return questionResponses.stream()
                .collect(Collectors.groupingBy(item -> item.getQuestionType().getCode()));
    }

    private void fillCaseQuestionnaire(CaseQuestionnaireRequest caseQuestionnaire, CaseInstanceEntity caseInstance) {
        caseQuestionnaire.setCaseIdentifier(caseInstance.getCaseIdentifier());
        caseQuestionnaire.setVer(caseInstance.getVer());
        caseQuestionnaire.setAppId(baseBiz.getAppId());
        caseQuestionnaire.setCaseQuestionnaireId(baseBiz.getIdStr());
    }

    private String saveQuestionSection(CaseQuestionnaireRequest caseQuestionnaire) {
        QuestionSelectModeEnum addType = caseQuestionnaire.getAddType();
        CaseQuestionnaireHandler handler = CaseQuestionnaireFactory.get(addType);
        return handler.handle(caseQuestionnaire);
    }

    @NotNull
    private CaseQuestionnaireEntity saveCaseQuestionnaire(CaseQuestionnaireRequest caseQuestionnaire, String questionSectionId) {
        QuestionSectionEntity sectionEntity = questionSectionBiz.getById(questionSectionId);
        Integer questionCount = Optional.of(sectionEntity)
                .map(QuestionSectionEntity::getQuestionCount)
                .orElse(0);
        String questionStruct = Optional.of(sectionEntity)
                .map(QuestionSectionEntity::getQuestionSectionStructure)
                .orElse("");
        CaseQuestionnaireEntity caseQuestionnaireEntity = BeanUtil.copyProperties(caseQuestionnaire, CaseQuestionnaireEntity.class);
        caseQuestionnaireEntity.setQuestionSectionId(questionSectionId);
        caseQuestionnaireEntity.setQuestionCount(questionCount);
        caseQuestionnaireEntity.setQuestionSectionStructure(questionStruct);
        caseQuestionnaireService.save(caseQuestionnaireEntity);
        return caseQuestionnaireEntity;
    }

}