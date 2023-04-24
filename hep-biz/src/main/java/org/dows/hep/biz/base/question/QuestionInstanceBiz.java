package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionCloneEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionPageRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionOptionWithAnswerResponse;
import org.dows.hep.api.base.question.response.QuestionPageResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.handler.QuestionTypeFactory;
import org.dows.hep.biz.base.question.handler.QuestionTypeHandler;
import org.dows.hep.entity.QuestionAnswersEntity;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.service.QuestionAnswersService;
import org.dows.hep.service.QuestionInstanceService;
import org.dows.hep.service.QuestionOptionsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* @description project descr:问题:问题
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
@RequiredArgsConstructor
public class QuestionInstanceBiz{

    private final IdGenerator idGenerator;
    private final QuestionInstanceService questionInstanceService;
    private final QuestionOptionsService questionOptionsService;
    private final QuestionAnswersService questionAnswersService;
    /**
     * @param
     * @return
     * @说明: 新增和更新
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
            questionInstanceId = updQuestion(question);
        }
        return questionInstanceId;
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
    public Page<QuestionPageResponse> pageQuestion(QuestionPageRequest questionPageRequest) {
        Long pageNo = questionPageRequest.getPageNo();
        Long pageSize = questionPageRequest.getPageSize();
        Page<QuestionInstanceEntity> pageRequest = new Page<>(pageNo, pageSize);

        Page<QuestionPageResponse> result = new Page<>();
        Page<QuestionInstanceEntity> pageResult = questionInstanceService.lambdaQuery()
                .eq(questionPageRequest.getAppId() != null, QuestionInstanceEntity::getAppId, questionPageRequest.getAppId())
                .page(pageRequest);
        if (pageResult == null) {
            return result;
        }

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
                .eq(questionSearch.getAppId() != null, QuestionInstanceEntity::getAppId, questionSearch.getAppId())
                .list();

        return entityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionResponse.class))
                .collect(Collectors.toList());
    }
    /**
    * @param
    * @return
    * @说明: 根据ID获取详情
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public QuestionResponse getQuestion(String questionInstanceId ) {
        if (questionInstanceId == null) {
            return new QuestionResponse();
        }

        QuestionInstanceEntity questionInstanceEntity = questionInstanceService.getById(questionInstanceId);
        if (BeanUtil.isEmpty(questionInstanceEntity)) {
            return new QuestionResponse();
        }
        QuestionResponse result = BeanUtil.copyProperties(questionInstanceEntity, QuestionResponse.class);

        // 选择题
        String questionType = questionInstanceEntity.getQuestionType();
        if (QuestionTypeEnum.isSelect(questionType)) {
            List<QuestionAnswersEntity> answersEntityList = questionAnswersService.lambdaQuery()
                    .eq(QuestionAnswersEntity::getQuestionInstanceId, questionInstanceId)
                    .list();
            if (answersEntityList == null || answersEntityList.isEmpty()) {
                return result;
            }
            List<QuestionOptionWithAnswerResponse> owaResponse = answersEntityList.stream()
                    .map(item -> BeanUtil.copyProperties(item, QuestionOptionWithAnswerResponse.class))
                    .collect(Collectors.toList());
            result.setOptionWithAnswerList(owaResponse);
        }

        return result;
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

        LambdaUpdateWrapper<QuestionInstanceEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId)
                .set(QuestionInstanceEntity::getEnabled, 1);
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

        LambdaUpdateWrapper<QuestionInstanceEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId)
                .set(QuestionInstanceEntity::getEnabled, 0);
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

        QuestionInstanceEntity left = questionInstanceService.getById(leftQuestionInstanceId);
        QuestionInstanceEntity right = questionInstanceService.getById(rightQuestionInstanceId);
        if (BeanUtil.isEmpty(left) || BeanUtil.isEmpty(right)) {
            return false;
        }

        Integer leftSequence = left.getSequence();
        Integer rightSequence = right.getSequence();
        LambdaUpdateWrapper<QuestionInstanceEntity> leftUpdateWrapper = new LambdaUpdateWrapper<>();
        leftUpdateWrapper.eq(QuestionInstanceEntity::getQuestionInstanceId, left.getQuestionInstanceId())
                .set(QuestionInstanceEntity::getSequence, rightSequence);
        questionInstanceService.update(leftUpdateWrapper);
        LambdaUpdateWrapper<QuestionInstanceEntity> rightUpdateWrapper = new LambdaUpdateWrapper<>();
        rightUpdateWrapper.eq(QuestionInstanceEntity::getQuestionInstanceId, left.getQuestionInstanceId())
                .set(QuestionInstanceEntity::getSequence, leftSequence);
        questionInstanceService.update(rightUpdateWrapper);
        return Boolean.TRUE;
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
    public Boolean delQuestion(String questionInstanceIds) {
        String[] ids = questionInstanceIds.split(",");
        List<String> idList = Arrays.stream(ids).toList();
        return questionInstanceService.removeBatchByIds(idList);
    }

    private String saveQuestion(QuestionRequest question) {
        String appId = "3";
        String questionInstancePid = "0";
        question.setAppId(appId);
        question.setQuestionInstancePid(questionInstancePid);

        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        return questionTypeHandler.save(question);
    }

    private String updQuestion(QuestionRequest question) {
        boolean error = checkQuestionTypeIsError(question);
        if (error) {
            return question.getQuestionInstanceId();
        }

        // check ref-count, then update or clone
        String questionInstanceId = question.getQuestionInstanceId();
        boolean ref = checkQuestionRefCount(questionInstanceId);
        if (ref) {
            questionInstanceId = cloneQue2NewVer(question);
        } else {
            updateQue(question);
        }
        return questionInstanceId;
    }

    // 检查问题的类型是否有错-发生变更即为有错，大错特错，挨板子吧
    private boolean checkQuestionTypeIsError(QuestionRequest question) {
        String questionInstanceId = question.getQuestionInstanceId();
        String newQuestionType = question.getQuestionType().getCode();
        QuestionInstanceEntity questionInstanceEntity = questionInstanceService.getById(questionInstanceId);
        String oldQuestionType = questionInstanceEntity.getQuestionType();
        return !oldQuestionType.equals(newQuestionType);
    }

    // 检查问题是否被引用
    private boolean checkQuestionRefCount(String questionInstanceId) {
        QuestionInstanceEntity questionInstanceEntity = questionInstanceService.getById(questionInstanceId);
        Integer refCount = questionInstanceEntity.getRefCount();
        return refCount != null && refCount > 0;
    }

    // 克隆 Question 生成一个新 version
    private String cloneQue2NewVer(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return "";
        }

        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        return questionTypeHandler.clone(question, QuestionCloneEnum.TO_NEW_INSTANCE);
    }

    // 克隆 Question 生成新的 Question
    private String cloneQue2NewQue(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return "";
        }

        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        return questionTypeHandler.clone(question, QuestionCloneEnum.TO_NEW_INSTANCE);
    }

    private void updateQue(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return;
        }

        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
        questionTypeHandler.update(question);
    }
}