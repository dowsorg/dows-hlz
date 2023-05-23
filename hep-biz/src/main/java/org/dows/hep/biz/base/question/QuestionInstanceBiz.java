package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.dto.QuestionRequestDTO;
import org.dows.hep.api.base.question.enums.*;
import org.dows.hep.api.base.question.request.*;
import org.dows.hep.api.base.question.response.QuestionCategoryResponse;
import org.dows.hep.api.base.question.response.QuestionOptionWithAnswerResponse;
import org.dows.hep.api.base.question.response.QuestionPageResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.handler.QuestionTypeFactory;
import org.dows.hep.biz.base.question.handler.QuestionTypeHandler;
import org.dows.hep.entity.QuestionAnswersEntity;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.entity.QuestionOptionsEntity;
import org.dows.hep.service.QuestionAnswersService;
import org.dows.hep.service.QuestionInstanceService;
import org.dows.hep.service.QuestionOptionsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:问题:问题
 * @date 2023年4月18日 上午10:45:07
 */
@Service
@RequiredArgsConstructor
public class QuestionInstanceBiz {

    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionCategBiz questionCategBiz;
    private final QuestionInstanceService questionInstanceService;
    private final QuestionOptionsService optionsService;
    private final QuestionAnswersService answersService;

    /**
     * @param
     * @return
     * @说明: 新增和更新，如果 bizCode 为空， 则默认为 PRIVATE_VIEWING
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @Transactional
    public String saveOrUpdQuestion(QuestionRequest question, QuestionAccessAuthEnum questionAccessAuth, QuestionSourceEnum questionSource) {
        // check and convert
        QuestionRequestDTO questionRequestDTO = convertRequest2DTO(question, questionAccessAuth, questionSource);

        // save or upd
        String questionInstanceId = questionRequestDTO.getQuestionInstanceId();
        if (StrUtil.isBlank(questionInstanceId)) {
            questionInstanceId = saveQue(questionRequestDTO);
        } else {
            updQuestion(questionRequestDTO);
        }
        return questionInstanceId;
    }

    /**
     * @param
     * @return
     * @说明: 克隆
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public String cloneQue2NewQue(QuestionClonedRequest question, QuestionAccessAuthEnum questionAccessAuthEnum, QuestionSourceEnum questionSourceEnum) {
        // check
        if (BeanUtil.isEmpty(question)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        String oriQuestionInstanceId = question.getOriQuestionInstanceId();
        if (StrUtil.isBlank(oriQuestionInstanceId)) {
            throw new BizException(QuestionESCEnum.QUESTION_CLONED_ID_NON_NULL);
        }
        QuestionResponse oriQuestionResponse = getQuestion(oriQuestionInstanceId);
        if (BeanUtil.isEmpty(oriQuestionResponse)) {
            throw new BizException(QuestionESCEnum.QUESTION_CLONED_OBJ_NON_NULL);
        }

        // convert response 2 request
        QuestionRequest request = convertResponse2Request(oriQuestionResponse);
        if (StrUtil.isNotBlank(question.getTargetQuestionTitle())) {
            request.setQuestionTitle(question.getTargetQuestionTitle());
        }
        request.setQuestionInstanceId(null);

        // 新增新 data
        return saveOrUpdQuestion(request, questionAccessAuthEnum, questionSourceEnum);
    }

    /**
     * @param
     * @return
     * @说明: 分页
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public IPage<QuestionPageResponse> pageQuestion(QuestionPageRequest request) {
        Page<QuestionInstanceEntity> pageRequest = new Page<>(request.getPageNo(), request.getPageSize());
        Page<QuestionInstanceEntity> pageResult = questionInstanceService.lambdaQuery()
                .eq(QuestionInstanceEntity::getAppId, request.getAppId())
                .eq(QuestionInstanceEntity::getVer, baseBiz.getLastVer())
                .eq(QuestionInstanceEntity::getQuestionInstancePid, baseBiz.getQuestionInstancePid())
                .eq(QuestionInstanceEntity::getBizCode, QuestionAccessAuthEnum.PUBLIC_VIEWING.name())
                .eq(StrUtil.isNotBlank(request.getQuestionType()), QuestionInstanceEntity::getQuestionType, request.getQuestionType())
                .in(request.getCategIdList() != null && !request.getCategIdList().isEmpty(), QuestionInstanceEntity::getQuestionCategId, request.getCategIdList())
                .like(StrUtil.isNotBlank(request.getKeyword()), QuestionInstanceEntity::getQuestionTitle, request.getKeyword())
                .page(pageRequest);
        Page<QuestionPageResponse> result = baseBiz.convertPage(pageResult, QuestionPageResponse.class);
        fillResult(result);
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 条件查询-无分页
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public List<QuestionResponse> listQuestion(QuestionSearchRequest questionSearch ) {
        List<QuestionInstanceEntity> entityList = questionInstanceService.lambdaQuery()
                .eq(QuestionInstanceEntity::getAppId, questionSearch.getAppId())
                .eq(QuestionInstanceEntity::getVer, baseBiz.getLastVer())
                .eq(QuestionInstanceEntity::getQuestionInstancePid, baseBiz.getQuestionInstancePid())
                .eq(QuestionInstanceEntity::getBizCode, QuestionAccessAuthEnum.PUBLIC_VIEWING.name())
                .eq(StrUtil.isNotBlank(questionSearch.getQuestionType()), QuestionInstanceEntity::getQuestionType, questionSearch.getQuestionType())
                .like(StrUtil.isNotBlank(questionSearch.getKeyword()), QuestionInstanceEntity::getQuestionTitle, questionSearch.getKeyword())
                .in(questionSearch.getCategIdList() != null && !questionSearch.getCategIdList().isEmpty(), QuestionInstanceEntity::getQuestionCategId, questionSearch.getCategIdList())
                .list();

        return BeanUtil.copyToList(entityList, QuestionResponse.class);
    }

    /**
     * @param
     * @return
     * @说明: 根据ID获取详情
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public QuestionResponse getQuestion(String questionInstanceId) {
        if (questionInstanceId == null) {
            return new QuestionResponse();
        }

        QuestionInstanceEntity questionInstance = getById(questionInstanceId);
        QuestionTypeEnum questionTypeEnum = Optional.of(questionInstance)
                .map(QuestionInstanceEntity::getQuestionType)
                .map(QuestionTypeEnum::getByCode)
                .orElse(null);
        if (questionTypeEnum == null) {
            return new QuestionResponse();
        }

        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        QuestionResponse questionResponse = questionTypeHandler.get(questionInstanceId);
        setQuestionCategIds(questionResponse);
        return questionResponse;
    }

    /**
     * @param
     * @return
     * @说明: 根据ids获取题目
     * @关联表: QuestionInstance
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public List<QuestionInstanceEntity> listByIds(List<String> questionInstanceIds) {
        if (Objects.isNull(questionInstanceIds)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        return questionInstanceService.lambdaQuery()
                .in(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceIds)
                .list();
    }

    /**
     * @param
     * @return
     * @说明: 获取问题结构
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public String getStruct(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            return "";
        }

        // list question-instance
        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .in(QuestionInstanceEntity::getQuestionInstanceId, idList);
        List<QuestionInstanceEntity> instanceList = questionInstanceService.list(queryWrapper);
        // collect
        Map<String, Long> collect = instanceList.stream()
                .collect(Collectors.groupingBy(QuestionInstanceEntity::getQuestionType, Collectors.counting()));
        if (collect.isEmpty()) {
            return "";
        }
        // append 2 str
        StringBuilder sb = new StringBuilder();
        Arrays.stream(QuestionTypeEnum.values())
                .forEach(item -> {
                    String code = item.getCode();
                    String name = item.getName();
                    Long count = collect.get(code);
                    if (count != null && count != 0) {
                        sb.append(count)
                                .append(name)
                                .append("/");
                    }
                });
        // TODO remove the last 斜杠
        return sb.toString();
    }

    /**
     * @param
     * @return
     * @说明: 启用
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean enabledQuestion(String questionInstanceId ) {
        if (StrUtil.isBlank(questionInstanceId)) {
            return false;
        }

        return changeEnable(questionInstanceId, QuestionEnabledEnum.ENABLED);
    }

    /**
    * @param
    * @return
    * @说明: 禁用
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean disabledQuestion(String questionInstanceId ) {
        if (StrUtil.isBlank(questionInstanceId)) {
            return false;
        }

        return changeEnable(questionInstanceId, QuestionEnabledEnum.DISABLED);
    }

    /**
     * @param
     * @return
     * @说明: 排序
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean sortQuestion(String questionInstanceId, Integer sequence) {
        if (StrUtil.isBlank(questionInstanceId) || sequence == null) {
            return false;
        }

        LambdaUpdateWrapper<QuestionInstanceEntity> updateWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId)
                .set(QuestionInstanceEntity::getSequence, sequence);
        return questionInstanceService.update(updateWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 交换
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @DSTransactional
    public Boolean transposeQuestion(String leftQuestionInstanceId, String rightQuestionInstanceId) {
        if (StrUtil.isBlank(leftQuestionInstanceId) || StrUtil.isBlank(rightQuestionInstanceId)) {
            return false;
        }

        LambdaQueryWrapper<QuestionInstanceEntity> leftQueryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, leftQuestionInstanceId);
        LambdaQueryWrapper<QuestionInstanceEntity>rightQueryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, rightQuestionInstanceId);
        QuestionInstanceEntity left = questionInstanceService.getOne(leftQueryWrapper);
        QuestionInstanceEntity right = questionInstanceService.getOne(rightQueryWrapper);
        if (BeanUtil.isEmpty(left) || BeanUtil.isEmpty(right)) {
            return false;
        }

        Integer leftSequence = left.getSequence();
        Integer rightSequence = right.getSequence();
        LambdaUpdateWrapper<QuestionInstanceEntity> leftUpdateWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, left.getQuestionInstanceId())
                .set(QuestionInstanceEntity::getSequence, rightSequence);
        questionInstanceService.update(leftUpdateWrapper);

        LambdaUpdateWrapper<QuestionInstanceEntity> rightUpdateWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, left.getQuestionInstanceId())
                .set(QuestionInstanceEntity::getSequence, leftSequence);
        questionInstanceService.update(rightUpdateWrapper);
        return Boolean.TRUE;
    }

    /**
     * @param
     * @return
     * @说明: 删除单选和多选题的选项
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @DSTransactional
    public Boolean delQuestionOptions(String questionOptionId) {
        if (StrUtil.isBlank(questionOptionId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        LambdaQueryWrapper<QuestionOptionsEntity> remOpsWrapper = new LambdaQueryWrapper<QuestionOptionsEntity>()
                .eq(QuestionOptionsEntity::getQuestionOptionsId, questionOptionId);
        boolean removeOpsRes = optionsService.remove(remOpsWrapper);

        LambdaQueryWrapper<QuestionAnswersEntity> remAnsWrapper = new LambdaQueryWrapper<QuestionAnswersEntity>()
                .eq(QuestionAnswersEntity::getQuestionOptionsId, questionOptionId);
        boolean removeAnsRes = answersService.remove(remAnsWrapper);

        return removeOpsRes && removeAnsRes;
    }

    /**
     * @param
     * @return
     * @说明: 删除or批量删除
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean delQuestion(List<String> questionInstanceIds) {
        if (questionInstanceIds == null || questionInstanceIds.isEmpty()) {
            return Boolean.FALSE;
        }

        // check
        boolean canRemove = checkCanRemove(questionInstanceIds);
        if (!canRemove) {
            throw new BizException(QuestionESCEnum.CANNOT_DEL_FER_DATA);
        }

        // rem instance
        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .in(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceIds);
        boolean remInstanceRes = questionInstanceService.remove(queryWrapper);

        // rem relation
        boolean remRelationRes = removeRelationOfInstance(questionInstanceIds);

        return remInstanceRes && remRelationRes;
    }

    // TODO: 2023/5/11  
    private boolean removeRelationOfInstance(List<String> questionInstanceIds) {
        return Boolean.TRUE;
    }

    // TODO: 2023/5/11  
    private boolean checkCanRemove(List<String> questionInstanceIds) {
        return Boolean.TRUE;
    }

    // 映射
    public QuestionRequestDTO convertRequest2DTO(QuestionRequest request, QuestionAccessAuthEnum accessAuthEnum, QuestionSourceEnum questionSource) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        String questionType = request.getQuestionType();
        if (StrUtil.isBlank(questionType)) {
            throw new BizException(QuestionESCEnum.QUESTION_TYPE_NON_NULL);
        }
        QuestionTypeEnum questionTypeEnum = QuestionTypeEnum.getByCode(questionType);
        if (Objects.isNull(questionTypeEnum)) {
            throw new BizException(QuestionESCEnum.QUESTION_TYPE_NON_NULL);
        }

        return QuestionRequestDTO.builder()
                .questionRequest(request)
                .questionInstanceId(request.getQuestionInstanceId())
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .questionType(questionTypeEnum)
                .appId(baseBiz.getAppId())
                .questionInstancePid(baseBiz.getQuestionInstancePid())
                .source(questionSource.name())
                .bizCode(accessAuthEnum.name())
                .build();
    }

    @DSTransactional
    private String saveQue(QuestionRequestDTO question) {
        if (BeanUtil.isEmpty(question)) {
            return "";
        }

        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        return questionTypeHandler.save(question);
    }

    private void updQuestion(QuestionRequestDTO question) {
        boolean error = checkQuestionTypeIsError(question);
        if (error) {
            throw new BizException(QuestionESCEnum.QUESTION_TYPE_CANNOT_CHANGE);
        }

        // check ref-count, then update or clone
        boolean ref = checkQuestionRefCount(question.getQuestionInstanceId());
        if (ref) {
            cloneQue2NewVer(question);
        } else {
            updateQue(question);
        }
    }

    // 检查问题的类型是否有错-发生变更即为有错，大错特错，挨板子吧
    private boolean checkQuestionTypeIsError(QuestionRequestDTO question) {
        String questionInstanceId = Optional.of(question)
                .map(QuestionRequestDTO::getQuestionInstanceId)
                .orElse("");
        String newQuestionType = Optional.of(question)
                .map(QuestionRequestDTO::getQuestionType)
                .map(QuestionTypeEnum::getCode)
                .orElse("");
        QuestionInstanceEntity questionInstanceEntity = getById(questionInstanceId);
        String oldQuestionType = questionInstanceEntity.getQuestionType();
        return !Objects.equals(oldQuestionType, newQuestionType);
    }

    // 检查问题是否被引用
    private boolean checkQuestionRefCount(String questionInstanceId) {
        QuestionInstanceEntity questionInstanceEntity = getById(questionInstanceId);
        Integer refCount = questionInstanceEntity.getRefCount();
        return refCount != null && refCount > 0;
    }

    @DSTransactional
    private void updateQue(QuestionRequestDTO question) {
        if (BeanUtil.isEmpty(question)) {
            return;
        }

        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        questionTypeHandler.update(question);
    }

    // clone
    // 克隆 Question 生成一个新 version
    @DSTransactional
    private String cloneQue2NewVer(QuestionRequestDTO question) {
        // check
        if (BeanUtil.isEmpty(question)) {
            throw new BizException(QuestionESCEnum.QUESTION_CLONED_OBJ_NON_NULL);
        }
        String oriQuestionInstanceId = question.getQuestionInstanceId();
        if (StrUtil.isBlank(oriQuestionInstanceId)) {
            throw new BizException(QuestionESCEnum.QUESTION_CLONED_ID_NON_NULL);
        }
        QuestionInstanceEntity oriEntity = getById(oriQuestionInstanceId);
        if (BeanUtil.isEmpty(oriEntity)) {
            throw new BizException(QuestionESCEnum.QUESTION_CLONED_OBJ_NON_NULL);
        }
        String questionIdentifier = oriEntity.getQuestionIdentifier();
        if (StrUtil.isBlank(questionIdentifier)) {
            throw new BizException(QuestionESCEnum.QUESTION_CLONED_IDENTIFIER_NON_NULL);
        }
        QuestionTypeEnum questionType = question.getQuestionType();
        if (Objects.isNull(questionType)) {
            throw new BizException(QuestionESCEnum.QUESTION_TYPE_NON_NULL);
        }

        // 更新原 data 版本号
        String ver = baseBiz.getVer(oriEntity.getDt());
        LambdaUpdateWrapper<QuestionInstanceEntity> updateVerWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, oriQuestionInstanceId)
                .set(QuestionInstanceEntity::getVer, ver);
        questionInstanceService.update(updateVerWrapper);

        // 新增新 data
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionType);
        String newInstanceId = questionTypeHandler.save(question);

        // 更新新 data 标识符
        LambdaUpdateWrapper<QuestionInstanceEntity> updateIdentifierWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, newInstanceId)
                .set(QuestionInstanceEntity::getQuestionIdentifier, questionIdentifier);
        questionInstanceService.update(updateIdentifierWrapper);

        return newInstanceId;
    }

    private QuestionRequest convertResponse2Request(QuestionResponse questionResponse) {
        if (BeanUtil.isEmpty(questionResponse)) {
            return new QuestionRequest();
        }

        // children
        QuestionRequest result = convertResponse2Request0(questionResponse);

        // options with answer
        List<QuestionOptionWithAnswerResponse> optionWithAnswerList = questionResponse.getOptionWithAnswerList();
        List<QuestionOptionWithAnswerRequest> questionOptionWithAnswerRequests = BeanUtil.copyToList(optionWithAnswerList, QuestionOptionWithAnswerRequest.class);
        result.setOptionWithAnswerList(questionOptionWithAnswerRequests);

        return result;
    }

    private QuestionRequest convertResponse2Request0(QuestionResponse response) {
        QuestionRequest request = BeanUtil.copyProperties(response, QuestionRequest.class);
        List<QuestionRequest> nestedRequestList = new ArrayList<>();

        if (response.getChildren() != null) {
            for (QuestionResponse nestedResponse : response.getChildren()) {
                QuestionRequest nestedRequest = convertResponse2Request0(nestedResponse);
                nestedRequestList.add(nestedRequest);
            }
        }

        request.setChildren(nestedRequestList);
        return request;
    }

    private void fillResult(Page<QuestionPageResponse> result) {
        List<QuestionPageResponse> records = result.getRecords();
        if (records != null && !records.isEmpty()) {
            List<String> categIds = records.stream()
                    .map(QuestionPageResponse::getQuestionCategId)
                    .toList();
            List<QuestionCategoryResponse> questionCategoryResponses = questionCategBiz.listQuestionCategory(categIds);
            Map<String, String> collect = questionCategoryResponses.stream()
                    .collect(Collectors.toMap(QuestionCategoryResponse::getQuestionCategId, QuestionCategoryResponse::getQuestionCategName, (v1, v2) -> v1));
            records.forEach(item -> {
                item.setQuestionCategName(collect.get(item.getQuestionCategId()));
                item.setQuestionType(QuestionTypeEnum.getNameByCode(item.getQuestionType()));
            });
        }
    }

    private QuestionInstanceEntity getById(String questionId) {
        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionId);
        return questionInstanceService.getOne(queryWrapper);
    }

    private void setQuestionCategIds(QuestionResponse questionResponse) {
        String questionCategId = questionResponse.getQuestionCategId();
        String[] parentIds = questionCategBiz.getParentIds(questionCategId, QuestionCategGroupEnum.QUESTION.name());
        questionResponse.setQuestionCategIds(parentIds);
    }

    private boolean changeEnable(String questionInstanceId, QuestionEnabledEnum questionEnabledEnum) {
        LambdaUpdateWrapper<QuestionInstanceEntity> updateWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId)
                .set(QuestionInstanceEntity::getEnabled, questionEnabledEnum.getCode());
        return questionInstanceService.update(updateWrapper);
    }
}