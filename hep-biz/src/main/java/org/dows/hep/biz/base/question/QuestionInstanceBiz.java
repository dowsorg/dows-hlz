package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionCloneEnum;
import org.dows.hep.api.base.question.QuestionEnabledEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionPageRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionPageResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.handler.QuestionTypeFactory;
import org.dows.hep.biz.base.question.handler.QuestionTypeHandler;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.service.QuestionInstanceService;
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

    private final QuestionInstanceService questionInstanceService;

    /**
     * @param
     * @return
     * @说明: 新增
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @Transactional
    public String saveQuestion(QuestionRequest question) {
        return saveQue(question);
    }

    /**
     * @param
     * @return
     * @说明: 更新
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @Transactional
    public Boolean updQuestion(QuestionRequest question) {
        boolean error = checkQuestionTypeIsError(question);
        if (error) {
            return Boolean.FALSE;
        }

        // check ref-count, then update or clone
        boolean ref = checkQuestionRefCount(question.getQuestionInstanceId());
        if (ref) {
            cloneQue(question, QuestionCloneEnum.TO_NEW_VERSION);
        } else {
            updateQue(question);
        }
        return Boolean.TRUE;
    }

    /**
     * @param
     * @return
     * @说明: 新增
     * @关联表: QuestionInstance, QuestionOptions, QuestionAnswers
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @Transactional
    public String saveOrUpdQuestion(QuestionRequest question) {
        String questionInstanceId = question.getQuestionInstanceId();
        if (StrUtil.isBlank(questionInstanceId)) {
            questionInstanceId = saveQuestion(question);
        } else {
            updQuestion(question);
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
    public String cloneQuestion(QuestionRequest question) {
        return cloneQue(question, QuestionCloneEnum.TO_NEW_INSTANCE);
    }

    /**
     * @param
     * @return
     * @说明: 批量新增和更新
     * @关联表: QuestionInstance, QuestionOptions
     * @工时: 2H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean saveOrUpdQuestionBatch(List<QuestionRequest> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return Boolean.FALSE;
        }

        Map<Boolean, List<QuestionRequest>> collect = questionList.stream()
                .collect(Collectors.groupingBy(questionRequest -> StrUtil.isBlank(questionRequest.getQuestionInstanceId())));
        List<QuestionRequest> addList = collect.get(Boolean.TRUE);
        List<QuestionRequest> updList = collect.get(Boolean.FALSE);
        this.saveQuestionBatch(addList);
        this.updQuestionBatch(updList);
        return Boolean.TRUE;
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
    public IPage<QuestionPageResponse> pageQuestion(QuestionPageRequest questionPageRequest) {
        Page<QuestionPageResponse> result = new Page<>();

        Page<QuestionInstanceEntity> pageRequest = new Page<>(questionPageRequest.getPageNo(), questionPageRequest.getPageSize());
        Page<QuestionInstanceEntity> pageResult = questionInstanceService.lambdaQuery()
                .eq(questionPageRequest.getAppId() != null, QuestionInstanceEntity::getAppId, questionPageRequest.getAppId())
                .eq(QuestionInstanceEntity::getVer, baseBiz.getLastVer())
                .eq(QuestionInstanceEntity::getQuestionInstancePid, baseBiz.getQuestionInstancePid())
                .like(StrUtil.isNotBlank(questionPageRequest.getKeyword()), QuestionInstanceEntity::getQuestionTitle, questionPageRequest.getKeyword())
                .like(StrUtil.isNotBlank(questionPageRequest.getKeyword()), QuestionInstanceEntity::getQuestionDescr, questionPageRequest.getKeyword())
                .like(StrUtil.isNotBlank(questionPageRequest.getQuestionType()), QuestionInstanceEntity::getQuestionType, questionPageRequest.getQuestionType())
                .page(pageRequest);

        List<QuestionInstanceEntity> records = pageResult.getRecords();
        if (records == null || records.isEmpty()) {
            return result;
        }

        List<QuestionPageResponse> pageResponseList = records.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionPageResponse.class))
                .collect(Collectors.toList());
        result.setRecords(pageResponseList);
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
                .like(StrUtil.isNotBlank(questionSearch.getKeyword()), QuestionInstanceEntity::getQuestionTitle, questionSearch.getKeyword())
                .like(StrUtil.isNotBlank(questionSearch.getKeyword()), QuestionInstanceEntity::getQuestionDescr, questionSearch.getKeyword())
                .like(StrUtil.isNotBlank(questionSearch.getQuestionType()), QuestionInstanceEntity::getQuestionType, questionSearch.getQuestionType())
                .in(questionSearch.getCategIdList() != null && !questionSearch.getCategIdList().isEmpty(), QuestionInstanceEntity::getQuestionCategId, questionSearch.getCategIdList())
                .list();

        return entityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionResponse.class))
                .collect(Collectors.toList());
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
        return questionTypeHandler.get(questionInstanceId);
    }

    /**
    * @param
    * @return
    * @说明: 启用
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean enabledQuestion(String questionInstanceId ) {
        if (StrUtil.isBlank(questionInstanceId)) {
            return false;
        }

        LambdaUpdateWrapper<QuestionInstanceEntity> updateWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId)
                .set(QuestionInstanceEntity::getEnabled, QuestionEnabledEnum.ENABLED.getCode());
        return questionInstanceService.update(updateWrapper);
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

        LambdaUpdateWrapper<QuestionInstanceEntity> updateWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId)
                .set(QuestionInstanceEntity::getEnabled, QuestionEnabledEnum.DISABLED.getCode());
        return questionInstanceService.update(updateWrapper);
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
        return Boolean.FALSE;
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
    @Transactional
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

        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .in(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceIds);
        return questionInstanceService.remove(queryWrapper);
    }

    // 检查问题的类型是否有错-发生变更即为有错，大错特错，挨板子吧
    private boolean checkQuestionTypeIsError(QuestionRequest question) {
        String questionInstanceId = Optional.of(question)
                .map(QuestionRequest::getQuestionInstanceId)
                .orElse("");
        String newQuestionType = Optional.of(question)
                .map(QuestionRequest::getQuestionType)
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

    private String saveQue(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return "";
        }

        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        return questionTypeHandler.save(question);
    }

    private void updateQue(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return;
        }

        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        questionTypeHandler.update(question);
    }

    @Transactional
    private String cloneQue(QuestionRequest questionRequest, QuestionCloneEnum questionCloneEnum) {
        if (BeanUtil.isEmpty(questionRequest)) {
            return questionRequest.getQuestionInstanceId();
        }

        String questionInstanceId = "";
        switch (questionCloneEnum) {
            case TO_NEW_VERSION -> {
                questionInstanceId = cloneQue2NewVer(questionRequest);
            }
            case TO_NEW_INSTANCE -> {
                questionInstanceId = cloneQue2NewQue(questionRequest);
            }
            default -> {
            }
        }
        return questionInstanceId;
    }

    @Transactional
    // 克隆 Question 生成一个新 version
    private String cloneQue2NewVer(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return "";
        }

        String oriInstanceId = Optional.of(question)
                .map(QuestionRequest::getQuestionInstanceId)
                .orElse("");
        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, oriInstanceId);
        QuestionInstanceEntity oriInstance = questionInstanceService.getOne(queryWrapper);
        String questionIdentifier = oriInstance.getQuestionIdentifier();

        // 更新原 data 版本号
        updateVer(oriInstance);

        // 新增新 data
        QuestionTypeEnum questionType = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionType);
        String newInstanceId = questionTypeHandler.save(question);

        // 更新新 data 标识符
        updateIdentifier(newInstanceId, questionIdentifier);

        return newInstanceId;
    }

    // 克隆 Question 生成新的 Question
    private String cloneQue2NewQue(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return "";
        }

        // 新增新 data
        QuestionTypeEnum questionType = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionType);
        return questionTypeHandler.save(question);
    }

    private void updateVer(QuestionInstanceEntity instance) {
        Date dt = instance.getDt();
        String ver = baseBiz.getVer(dt);
        instance.setVer(ver);

        // update instance-ver
        questionInstanceService.updateById(instance);
    }

    private void updateIdentifier(String newInstanceId, String questionIdentifier) {
        LambdaUpdateWrapper<QuestionInstanceEntity> updateWrapper = new LambdaUpdateWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, newInstanceId)
                .eq(QuestionInstanceEntity::getQuestionIdentifier, questionIdentifier);
        questionInstanceService.update(updateWrapper);
    }

    @Transactional
    private Boolean saveQuestionBatch(List<QuestionRequest> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return Boolean.FALSE;
        }

        questionList.forEach(this::saveQuestion);
        return Boolean.TRUE;
    }

    @Transactional
    private Boolean updQuestionBatch(List<QuestionRequest> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return Boolean.FALSE;
        }

        questionList.forEach(this::updQuestion);
        return Boolean.TRUE;
    }

    private QuestionInstanceEntity getById(String questionId) {
        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionId);
        return questionInstanceService.getOne(queryWrapper);
    }
}