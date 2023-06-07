package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.enums.QuestionCategGroupEnum;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.enums.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionCategoryResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.QuestionSelectModeEnum;
import org.dows.hep.api.tenant.casus.request.CaseQuestionSearchRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnairePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnairePageResponse;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.biz.base.question.QuestionCategBiz;
import org.dows.hep.biz.base.question.QuestionInstanceBiz;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.tenant.casus.handler.CaseQuestionnaireFactory;
import org.dows.hep.biz.tenant.casus.handler.CaseQuestionnaireHandler;
import org.dows.hep.entity.CaseInstanceEntity;
import org.dows.hep.entity.CaseQuestionnaireEntity;
import org.dows.hep.entity.QuestionSectionEntity;
import org.dows.hep.service.CaseQuestionnaireService;
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
    private final QuestionCategBiz questionCategBiz;
    private final QuestionSectionBiz questionSectionBiz;
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
    public String saveOrUpdCaseQuestionnaire(CaseQuestionnaireRequest caseQuestionnaire) {
        if (BeanUtil.isEmpty(caseQuestionnaire)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        CaseQuestionnaireEntity caseQuestionnaireEntity = convertRequest2Entity(caseQuestionnaire);
        caseQuestionnaireService.saveOrUpdate(caseQuestionnaireEntity);

        return caseQuestionnaireEntity.getCaseQuestionnaireId();
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
    public IPage<CaseQuestionnairePageResponse> pageCaseQuestionnaire(CaseQuestionnairePageRequest request) {
        if (BeanUtil.isEmpty(request)) {
            return new Page<>();
        }

        // page
        Page<CaseQuestionnaireEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<CaseQuestionnaireEntity> pageResult = caseQuestionnaireService.lambdaQuery()
                .eq(StrUtil.isNotBlank(request.getCaseInstanceId()), CaseQuestionnaireEntity::getCaseInstanceId, request.getCaseInstanceId())
                .page(page);
        // convert
        return baseBiz.convertPage(pageResult, CaseQuestionnairePageResponse.class);
    }

    /**
     * @param
     * @return
     * @说明: 列出案例问卷-无分页
     * @关联表: caseQuestionnaire
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public List<CaseQuestionnaireResponse> listCaseQuestionnaire(CaseQuestionnaireSearchRequest request) {
        if (BeanUtil.isEmpty(request)) {
            return new ArrayList<>();
        }

        List<CaseQuestionnaireEntity> list = caseQuestionnaireService.lambdaQuery()
                .eq(StrUtil.isNotBlank(request.getCaseInstanceId()), CaseQuestionnaireEntity::getCaseInstanceId, request.getCaseInstanceId())
                .list();
        // convert
        return BeanUtil.copyToList(list, CaseQuestionnaireResponse.class);
    }

    /**
     * @author fhb
     * @description
     * @date 2023/6/7 10:32
     * @param
     * @return
     */
    public List<CaseQuestionnaireResponse> listByIds(List<String> ids) {
        if (BeanUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }

        List<CaseQuestionnaireEntity> list = caseQuestionnaireService.lambdaQuery()
                .in(CaseQuestionnaireEntity::getCaseQuestionnaireId, ids)
                .list();
        List<CaseQuestionnaireResponse> caseQuestionnaireResponses = BeanUtil.copyToList(list, CaseQuestionnaireResponse.class);
        if (CollUtil.isEmpty(caseQuestionnaireResponses)) {
            return caseQuestionnaireResponses;
        }

        caseQuestionnaireResponses.forEach(item -> {
            String questionSectionId = item.getQuestionSectionId();
            QuestionSectionResponse questionSection = questionSectionBiz.getQuestionSection(questionSectionId);
            item.setQuestionSectionResponse(questionSection);
        });
        return caseQuestionnaireResponses;
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
    public CaseQuestionnaireResponse getCaseQuestionnaire(String caseQuestionnaireId ) {
        if (StrUtil.isBlank(caseQuestionnaireId)) {
            return new CaseQuestionnaireResponse();
        }

        // get entity
        CaseQuestionnaireEntity entity = getById(caseQuestionnaireId);
        if (BeanUtil.isEmpty(entity)) {
            return new CaseQuestionnaireResponse();
        }
        return BeanUtil.copyProperties(entity, CaseQuestionnaireResponse.class);
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
    public QuestionSectionResponse showCaseQuestionnaire(String caseQuestionnaireId ) {
        if (StrUtil.isBlank(caseQuestionnaireId)) {
            return new QuestionSectionResponse();
        }

        CaseQuestionnaireEntity entity = getById(caseQuestionnaireId);
        String questionSectionId = entity.getQuestionSectionId();
        return questionSectionBiz.getQuestionSection(questionSectionId, null);
    }

    /**
     * @param
     * @return
     * @说明: 复制案例问卷
     * @关联表: caseQuestionnaire
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public void copyCaseQuestionnaire(String oriCaseInstanceId, CaseInstanceEntity targetCaseInstance) {

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
    public List<QuestionResponse> listUsableQuestionFromSource(CaseQuestionSearchRequest request) {
        List<QuestionResponse> result = listUsableQuestionFromSource0(request);
        if (CollUtil.isEmpty(result)) {
            return result;
        }

        List<String> categIds = result.stream().map(QuestionResponse::getQuestionCategId).toList();
        List<QuestionCategoryResponse> curs = questionCategBiz.listQuestionCategory(categIds);
        Map<String, String> pidCollect = curs.stream()
                .collect(Collectors.toMap(QuestionCategoryResponse::getQuestionCategId, QuestionCategoryResponse::getQuestionCategPid, (v1, v2) -> v1));

        List<QuestionCategoryResponse> parents = questionCategBiz.listParents(categIds);
        Map<String, String> pNameCollect = parents.stream()
                .collect(Collectors.toMap(QuestionCategoryResponse::getQuestionCategId, QuestionCategoryResponse::getQuestionCategName, (v1, v2) -> v1));

        result.forEach(item -> {
            String pid = pidCollect.get(item.getQuestionCategId());
            String categoryName = pNameCollect.get(pid);
            item.setQuestionCategName(categoryName);
            item.setQuestionType(QuestionTypeEnum.getNameByCode(item.getQuestionType()));
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
    public Map<String, List<QuestionResponse>> collectQuestionOfUsableQuestion(CaseQuestionSearchRequest request) {
        return collectQuestionOfUsableQuestion0(request);
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
    public Map<String, Long> collectQuestionCountOfUsableQuestion(CaseQuestionSearchRequest request) {
        Map<String, List<QuestionResponse>> collectList = collectQuestionOfUsableQuestion0(request);
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
     * @说明: 删除案例问卷
     * @关联表: caseQuestionnaire
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public Boolean delCaseQuestionnaire(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<CaseQuestionnaireEntity> remWrapper = new LambdaQueryWrapper<CaseQuestionnaireEntity>()
                .in(CaseQuestionnaireEntity::getCaseQuestionnaireId, ids);
        return caseQuestionnaireService.remove(remWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 删除案例问卷item
     * @关联表: caseQuestionnaire
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public Boolean delQuestionnaireItem(String questionSectionId, String questionSectionItemId) {
        return questionSectionBiz.disabledSectionQuestion(questionSectionId, questionSectionItemId);
    }

    private CaseQuestionnaireEntity convertRequest2Entity(CaseQuestionnaireRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        CaseQuestionnaireEntity result = CaseQuestionnaireEntity.builder()
                .caseQuestionnaireId(request.getCaseQuestionnaireId())
                .caseInstanceId(request.getCaseInstanceId())
                .questionSectionName(request.getQuestionSectionName())
                .periods(request.getPeriods())
                .periodSequence(request.getPeriodSequence())
                .addType(request.getAddType().name())
                .build();

        String uniqueId = result.getCaseQuestionnaireId();
        if (StrUtil.isBlank(uniqueId)) {
            result.setCaseQuestionnaireId(baseBiz.getIdStr());
        } else {
            CaseQuestionnaireEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException(CaseESCEnum.DATA_NULL);
            }
            result.setId(entity.getId());
        }

        // generate question-section
        QuestionSelectModeEnum addType = request.getAddType();
        CaseQuestionnaireHandler handler = CaseQuestionnaireFactory.get(addType);
        String questionSectionId = handler.handle(request);

        // get question-struct and question-count
        QuestionSectionEntity sectionEntity = questionSectionBiz.getById(questionSectionId);
        Integer questionCount = Optional.of(sectionEntity)
                .map(QuestionSectionEntity::getQuestionCount)
                .orElse(0);
        String questionStruct = Optional.of(sectionEntity)
                .map(QuestionSectionEntity::getQuestionSectionStructure)
                .orElse("");
        result.setQuestionSectionId(questionSectionId);
        result.setQuestionCount(questionCount);
        result.setQuestionSectionStructure(questionStruct);

        return result;
    }

    private Map<String, List<QuestionResponse>> collectQuestionOfUsableQuestion0(CaseQuestionSearchRequest request) {
        // default
        HashMap<String, List<QuestionResponse>> result = new HashMap<>();
        result.put(QuestionTypeEnum.RADIO_SELECT.getCode(), new ArrayList<>());
        result.put(QuestionTypeEnum.MULTIPLE_SELECT.getCode(), new ArrayList<>());
        result.put(QuestionTypeEnum.MATERIAL.getCode(), new ArrayList<>());

        // new
        List<QuestionResponse> questionResponses = listUsableQuestionFromSource0(request);
        if (questionResponses.isEmpty()) {
            return result;
        }

        Map<String, List<QuestionResponse>> collect = questionResponses.stream()
                .collect(Collectors.groupingBy(QuestionResponse::getQuestionType));
        result.forEach((key, value) -> {
            List<QuestionResponse> list = collect.get(key);
            if (list != null && !list.isEmpty()) {
                result.replace(key, list);
            }
        });
        return result;
    }

    private List<QuestionResponse> listUsableQuestionFromSource0(CaseQuestionSearchRequest request) {
        // list from question source
        List<String> categoryIdList = getCategoryIdList(request);
        QuestionSearchRequest questionSearchRequest = QuestionSearchRequest.builder()
                .categIdList(categoryIdList)
                .appId(baseBiz.getAppId())
                .keyword(request.getKeyword())
                .questionType(request.getQuestionType())
                .build();
        List<QuestionResponse> questionResponses = questionInstanceBiz.listQuestion(questionSearchRequest);

        // list existing-question of case-instance
        String caseInstanceId = request.getCaseInstanceId();
        List<String> existingIds = listQuestionIdOfCaseInstance(caseInstanceId);

        // filter usable question
        return filterUsableQuestion(questionResponses, existingIds);
    }

    private List<String> getCategoryIdList(CaseQuestionSearchRequest request) {
        List<String> result = new ArrayList<>();
        if (StrUtil.isBlank(request.getL1CategId()) || StrUtil.isBlank(request.getL2CategId())) {
            return result;
        }

        // level-2
        String l2CategoryId = request.getL2CategId();
        if (StrUtil.isNotBlank(l2CategoryId)) {
            result.add(l2CategoryId);
            return result;
        }

        // level-1 convert to level-2
        String l1CategoryId = request.getL1CategId();
        List<QuestionCategoryResponse> children = questionCategBiz.getChildrenByPid(l1CategoryId, QuestionCategGroupEnum.QUESTION.name());
        if (children != null && !children.isEmpty()) {
            List<String> childrenIds = children.stream().map(QuestionCategoryResponse::getQuestionCategId).toList();
            result.addAll(childrenIds);
        }
        result.add(l1CategoryId);
        return result;
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
        List<QuestionSectionItemResponse> itemResponseList = questionSectionBiz.listQuestionSectionItem(sectionIdList);
        if (itemResponseList == null || itemResponseList.isEmpty()) {
            return new ArrayList<>();
        }

        // list question-id
        return itemResponseList.stream()
                .map(QuestionSectionItemResponse::getQuestion)
                .map(QuestionResponse::getQuestionInstanceId)
                .distinct()
                .toList();
    }

    private List<QuestionResponse> filterUsableQuestion(List<QuestionResponse> questionResponses, List<String> existingIds) {
        Map<String, String> existingIdsMap = existingIds.stream()
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toMap(item -> item, item -> item, (v1, v2) -> v1));
        return questionResponses.stream()
                .filter(item -> {
                    String questionInstanceId = item.getQuestionInstanceId();
                    return existingIdsMap.get(questionInstanceId) == null;
                }).toList();
    }

    private CaseQuestionnaireEntity getById(String caseQuestionnaireId) {
        LambdaQueryWrapper<CaseQuestionnaireEntity> queryWrapper = new LambdaQueryWrapper<CaseQuestionnaireEntity>()
                .eq(CaseQuestionnaireEntity::getCaseQuestionnaireId, caseQuestionnaireId);
        return caseQuestionnaireService.getOne(queryWrapper);
    }
}