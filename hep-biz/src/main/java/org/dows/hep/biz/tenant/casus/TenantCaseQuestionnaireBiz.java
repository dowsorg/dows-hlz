package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.QuestionCategGroupEnum;
import org.dows.hep.api.base.question.QuestionESCEnum;
import org.dows.hep.api.base.question.QuestionEnabledEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.request.QuestionSectionDelItemRequest;
import org.dows.hep.api.base.question.response.QuestionCategoryResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.CaseQuestionSelectModeEnum;
import org.dows.hep.api.tenant.casus.request.*;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnairePageResponse;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.biz.base.question.QuestionCategBiz;
import org.dows.hep.biz.base.question.QuestionInstanceBiz;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.base.question.QuestionSectionItemBiz;
import org.dows.hep.biz.tenant.casus.handler.CaseQuestionnaireFactory;
import org.dows.hep.biz.tenant.casus.handler.CaseQuestionnaireHandler;
import org.dows.hep.entity.*;
import org.dows.hep.service.CaseOrgQuestionnaireService;
import org.dows.hep.service.CaseQuestionnaireService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private final QuestionSectionItemBiz questionSectionItemBiz;
    private final QuestionInstanceBiz questionInstanceBiz;
    private final CaseQuestionnaireService caseQuestionnaireService;
    private final CaseOrgQuestionnaireService caseOrgQuestionnaireService;
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
        this.checkQuestionnaireRequest(caseQuestionnaire);
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
        return list.stream()
                .map(item ->  BeanUtil.copyProperties(item, CaseQuestionnaireResponse.class))
                .sorted(Comparator.comparingInt(CaseQuestionnaireResponse::getPeriodSequence).thenComparing(Comparator.comparing(CaseQuestionnaireResponse::getDt).reversed()))
                .toList();
    }

    /**
     * @author fhb
     * @description
     * @date 2023/6/7 10:32
     * @param
     * @return
     */
    public List<CaseQuestionnaireResponse> listByIds(List<String> ids) {
        if (CollUtil.isEmpty(ids)) {
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
        CaseQuestionnaireResponse  caseQuestionnaireResponse = new CaseQuestionnaireResponse();
        if (BeanUtil.isEmpty(entity)) {
            return  caseQuestionnaireResponse;
        }

        List<CaseQuestionnaireRequest.RandomMode> randomModeList = new ArrayList<>();
        this.convertRandomModeResponseList(randomModeList, entity.getQuestionSectionId());
        BeanUtil.copyProperties(entity, caseQuestionnaireResponse);
        caseQuestionnaireResponse.setRandomModeList(randomModeList);
        return caseQuestionnaireResponse;

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
            throw new BizException(CaseESCEnum.CASE_USABLE_QUESTION_IS_NULL);
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
        List<CaseOrgQuestionnaireEntity> list = caseOrgQuestionnaireService.lambdaQuery()
                .eq(CaseOrgQuestionnaireEntity::getDeleted, false)
                .in(CaseOrgQuestionnaireEntity::getCaseQuestionnaireId, ids)
                .list();
        //试卷被使用不能删除
        if (!CollectionUtils.isEmpty(list)){
            throw new BizException(QuestionESCEnum.CANNOT_DEL_REF_DATA);
        }
        LambdaQueryWrapper<CaseQuestionnaireEntity> remWrapper = new LambdaQueryWrapper<CaseQuestionnaireEntity>()
                .in(CaseQuestionnaireEntity::getCaseQuestionnaireId, ids);
        return caseQuestionnaireService.remove(remWrapper);
    }

    /**
     * @param caseInstanceIds - 案例实例ID集合
     * @return java.lang.Boolean
     * @author fhb
     * @description 删除案例下知识答题
     * @date 2023/7/24 17:35
     */
    public Boolean delCaseQByCaseInstanceIds(List<String> caseInstanceIds) {
        if (CollUtil.isEmpty(caseInstanceIds)) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<CaseQuestionnaireEntity> remWrapper = new LambdaQueryWrapper<CaseQuestionnaireEntity>()
                .in(CaseQuestionnaireEntity::getCaseInstanceId, caseInstanceIds);
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
    public Boolean delQuestionnaireItem(CaseQuestionnaireDelItemRequest caseRequest) {
        String caseQuestionnaireId = caseRequest.getCaseQuestionnaireId();
        String questionSectionId = caseRequest.getQuestionSectionId();
        String questionSectionItemId = caseRequest.getQuestionSectionItemId();

        QuestionSectionDelItemRequest request = new QuestionSectionDelItemRequest();
        request.setQuestionSectionId(questionSectionId);
        request.setQuestionSectionItemIds(List.of(questionSectionItemId));
        Boolean res1 = questionSectionBiz.delSectionQuestion(request);

        QuestionSectionEntity sectionEntity = questionSectionBiz.getById(questionSectionId);
        Integer questionCount = Optional.of(sectionEntity)
                .map(QuestionSectionEntity::getQuestionCount)
                .orElse(0);
        String questionStruct = Optional.of(sectionEntity)
                .map(QuestionSectionEntity::getQuestionSectionStructure)
                .orElse("");
        LambdaUpdateWrapper<CaseQuestionnaireEntity> updateWrapper = new LambdaUpdateWrapper<CaseQuestionnaireEntity>()
                .eq(CaseQuestionnaireEntity::getCaseQuestionnaireId, caseQuestionnaireId)
                .set(CaseQuestionnaireEntity::getQuestionCount, questionCount)
                .set(CaseQuestionnaireEntity::getQuestionSectionStructure, questionStruct);
        boolean res2 = caseQuestionnaireService.update(updateWrapper);

        return res1 && res2;
    }

    /**
     * @param
     * @return
     * @说明: 禁用案例问卷item
     * @关联表: caseQuestionnaire
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public Boolean disableQuestionnaireItem(String questionSectionId, String questionSectionItemId) {
        return questionSectionBiz.disabledSectionQuestion(questionSectionId, questionSectionItemId);
    }

    //校验题目
    private CaseQuestionnaireRequest checkQuestionnaireRequest(CaseQuestionnaireRequest request) {
        List<CaseQuestionnaireRequest.RandomMode> randomModeList = request.getRandomModeList();
        if (CollectionUtils.isEmpty(randomModeList)) {
            return request;
        }
        Set<String> questionCategIdSet = randomModeList.stream().map(CaseQuestionnaireRequest.RandomMode::getL2CategId).collect(Collectors.toSet());
        List<QuestionInstanceEntity> questionInstanceList = questionInstanceBiz.listByQuestionCategIds(questionCategIdSet);
        Map<String, List<QuestionInstanceEntity>> questionCategIdMap = questionInstanceList.stream().collect(Collectors.groupingBy(QuestionInstanceEntity::getQuestionCategId));
        // questionCategId
        Map<String, CaseQuestionnaireRequest.RandomMode> maxNumRandomModeMap = new HashMap<>();
        questionCategIdMap.forEach((questionCategId, questionInstanceChildList) -> {
            CaseQuestionnaireRequest.RandomMode randomMode = new CaseQuestionnaireRequest.RandomMode();
            this.convertRandomMode(questionCategId, randomMode, questionInstanceChildList);
            maxNumRandomModeMap.put(questionCategId, randomMode);
        });
        List<CaseQuestionnaireRequest.RandomMode> resultRandomModeList = new ArrayList<>();
        Map<String, CaseQuestionnaireRequest.RandomMode> resultRandomModeMap = new HashMap<>();
        for (CaseQuestionnaireRequest.RandomMode randomMode : randomModeList) {
            //知识类别
            String questionCategId = randomMode.getL2CategId();
            //题目类型-数量
            Map<QuestionTypeEnum, Integer> numMap = randomMode.getNumMap();
            if (resultRandomModeMap.containsKey(questionCategId)) {
                Map<QuestionTypeEnum, Integer> maxNumMap = maxNumRandomModeMap.get(questionCategId).getNumMap();
                Map<QuestionTypeEnum, Integer> resultNumMap = resultRandomModeMap.get(questionCategId).getNumMap();
                this.countNum(resultNumMap, maxNumMap, numMap);
            } else {
                resultRandomModeMap.put(questionCategId, randomMode);
            }
        }
        resultRandomModeMap.forEach((questionCategId, randomMode) ->
                resultRandomModeList.add(randomMode)
        );

        request.setRandomModeList(resultRandomModeList);
        return request;
    }

    private void countNum(Map<QuestionTypeEnum, Integer> resultNumMap, Map<QuestionTypeEnum, Integer> maxNumMap, Map<QuestionTypeEnum, Integer> numMap) {
        if (CollectionUtils.isEmpty(numMap)) {
            return;
        }
        numMap.forEach((typeEum, num) -> {
            int maxNum = maxNumMap.get(typeEum);
            int result = resultNumMap.get(typeEum);
            resultNumMap.put(typeEum, Math.min(num + result, maxNum));
        });
    }

    private void convertRandomModeResponseList(List<CaseQuestionnaireRequest.RandomMode> randomModeList, String questionSectionId) {
        if (StringUtils.isBlank(questionSectionId)) {
            return;
        }
        List<QuestionSectionItemEntity> questionSectionItemEntityList = questionSectionItemBiz.queryBySectionId(questionSectionId);
        Set<String> questionInstanceIdSet = questionSectionItemEntityList.stream().map(QuestionSectionItemEntity::getQuestionInstanceId).collect(Collectors.toSet());
        //选中的题目
        List<QuestionInstanceEntity> questionInstanceList = questionInstanceBiz.listByIds(questionInstanceIdSet);
        //总题目数量
        Set<String> questionCategIdSet = questionInstanceList.stream().map(QuestionInstanceEntity::getQuestionCategId).collect(Collectors.toSet());
        List<QuestionInstanceEntity> maxQuestionInstanceList = questionInstanceBiz.listByQuestionCategIds(questionCategIdSet);

        Map<String, List<QuestionInstanceEntity>> questionCategIdMap = questionInstanceList.stream().collect(Collectors.groupingBy(QuestionInstanceEntity::getQuestionCategId));
        Map<String, List<QuestionInstanceEntity>> maQuestionCategIdMap =maxQuestionInstanceList.stream().collect(Collectors.groupingBy(QuestionInstanceEntity::getQuestionCategId));
        this.convertRandomModeResponseList(randomModeList, questionCategIdMap,maQuestionCategIdMap);
    }

    private void convertRandomModeResponseList(List<CaseQuestionnaireRequest.RandomMode> randomModeList,
                                       Map<String,List<QuestionInstanceEntity>> questionCategIdMap,
                                       Map<String,List<QuestionInstanceEntity>> maxQuestionCategIdMap) {
        if (CollectionUtils.isEmpty(questionCategIdMap) || randomModeList == null) {
            return;
        }
        questionCategIdMap.forEach((questionCategId, questionInstanceChildList) -> {
            CaseQuestionnaireRequest.RandomMode randomMode = new CaseQuestionnaireRequest.RandomMode();
            this.convertRandomModeResponse(questionCategId, randomMode, questionInstanceChildList,maxQuestionCategIdMap.get(questionCategId));
            randomModeList.add(randomMode);
        });
    }

    private void convertRandomModeResponse(String questionCategId, CaseQuestionnaireRequest.RandomMode randomMode,
                      List<QuestionInstanceEntity> questionInstanceChildList,
                      List<QuestionInstanceEntity> maxQuestionInstanceChildList) {
        if (StringUtils.isBlank(questionCategId) || randomMode == null) {
            return;
        }
        QuestionCategoryEntity questionCategory = questionCategBiz.getById(questionCategId);
        randomMode.setL2CategId(questionCategId);
        randomMode.setL1CategId(questionCategory.getQuestionCategPid());
        Map<QuestionTypeEnum, Integer> numMap = new HashMap<>();
        this.convertNumMap(numMap, questionInstanceChildList);
        randomMode.setNumMap(numMap);
        Map<QuestionTypeEnum, Integer> maxNumMap = new HashMap<>();
        this.convertNumMap(maxNumMap, maxQuestionInstanceChildList);
        randomMode.setMaxNumMap(maxNumMap);
    }

    private void convertRandomMode(String questionCategId, CaseQuestionnaireRequest.RandomMode randomMode, List<QuestionInstanceEntity> questionInstanceChildList) {
        if (StringUtils.isBlank(questionCategId) || randomMode == null) {
            return;
        }
        QuestionCategoryEntity questionCategory = questionCategBiz.getById(questionCategId);
        randomMode.setL2CategId(questionCategId);
        randomMode.setL1CategId(questionCategory.getQuestionCategPid());
        Map<QuestionTypeEnum, Integer> numMap = new HashMap<>();
        this.convertNumMap(numMap, questionInstanceChildList);
        randomMode.setNumMap(numMap);
    }

    private void convertNumMap(Map<QuestionTypeEnum, Integer> numMap, List<QuestionInstanceEntity> questionInstanceChildList) {
        if (CollectionUtils.isEmpty(questionInstanceChildList) || numMap == null) {
            return;
        }
        questionInstanceChildList.stream().collect(Collectors.groupingBy(QuestionInstanceEntity::getQuestionType))
                .forEach((questionType, list) ->
                        numMap.put(QuestionTypeEnum.getByCode(questionType), list.size())
                );
    }
    private CaseQuestionnaireEntity convertRequest2Entity(CaseQuestionnaireRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        CaseQuestionnaireEntity result = CaseQuestionnaireEntity.builder()
                .caseQuestionnaireId(request.getCaseQuestionnaireId())
                .caseInstanceId(request.getCaseInstanceId())
                .questionSectionName(request.getQuestionSectionName())
                .periods(request.getPeriods().getCode())
                .periodSequence(request.getPeriods().getSeq())
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
        CaseQuestionSelectModeEnum addType = request.getAddType();
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
                .enabled(QuestionEnabledEnum.ENABLED.getCode())
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
        List<QuestionCategoryResponse> children = questionCategBiz.getTreeChildrenByPid(l1CategoryId, QuestionCategGroupEnum.QUESTION.name());
        if (children != null && !children.isEmpty()) {
            List<String> childrenIds = children.stream().map(QuestionCategoryResponse::getQuestionCategId).toList();
            result.addAll(childrenIds);
        }
        result.add(l1CategoryId);
        return result;
    }

    private List<String> listQuestionIdOfCaseInstance(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            return new ArrayList<>();
        }

        // list questionnaire of case-instance
        List<CaseQuestionnaireEntity> caseQuestionnaireList = caseQuestionnaireService.lambdaQuery()
                .eq(CaseQuestionnaireEntity::getCaseInstanceId, caseInstanceId)
                .list();
        if (caseQuestionnaireList == null || caseQuestionnaireList.isEmpty()) {
            return new ArrayList<>();
        }

        // list section-item
        List<String> sectionIdList = caseQuestionnaireList.stream()
                .map(CaseQuestionnaireEntity::getQuestionSectionId)
                .toList();
        List<QuestionSectionItemEntity> entityList = questionSectionItemBiz.listBySectionIds(sectionIdList);
        if (CollUtil.isEmpty(entityList)) {
            return new ArrayList<>();
        }

        // list question-id
        return entityList.stream()
                .map(QuestionSectionItemEntity::getQuestionInstanceId)
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