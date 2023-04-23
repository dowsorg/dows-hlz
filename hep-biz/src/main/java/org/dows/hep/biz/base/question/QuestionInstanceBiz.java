package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionOptionWithAnswerRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionOptionWithAnswerResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.entity.QuestionAnswersEntity;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.entity.QuestionOptionsEntity;
import org.dows.hep.service.QuestionAnswersService;
import org.dows.hep.service.QuestionInstanceService;
import org.dows.hep.service.QuestionOptionsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    public Page<QuestionInstanceEntity> pageQuestion(QuestionSearchRequest questionSearch) {
        Long pageNo = questionSearch.getPageNo();
        Long pageSize = questionSearch.getPageSize();
        Page<QuestionInstanceEntity> page = new Page<>(pageNo, pageSize);

        return questionInstanceService.lambdaQuery()
                .eq(questionSearch.getAppId() != null, QuestionInstanceEntity::getAppId, questionSearch.getAppId())
                .page(page);
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
        String questionInstanceId = idGenerator.nextIdStr();
        String questionIdentifier = idGenerator.nextIdStr();
        String ver = String.valueOf(new Date().getTime());

        // baseInfo
        QuestionTypeEnum questionType = question.getQuestionType();
        assert questionType != null;
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(question, QuestionInstanceEntity.class);
        questionInstanceEntity.setAppId(appId);
        questionInstanceEntity.setQuestionInstanceId(questionInstanceId);
        questionInstanceEntity.setQuestionType(questionType.getCode());
        questionInstanceEntity.setQuestionIdentifier(questionIdentifier);
        questionInstanceEntity.setVer(ver);
        questionInstanceService.save(questionInstanceEntity);


        // options and answers
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = question.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return questionInstanceId;
        }
        // answers
        List<QuestionAnswersEntity> answersEntityList = optionWithAnswerList.stream()
                .map(item -> {
                    String questionOptionsId = idGenerator.nextIdStr();
                    String questionAnswerId = idGenerator.nextIdStr();

                    QuestionAnswersEntity questionAnswersEntity = BeanUtil.copyProperties(item, QuestionAnswersEntity.class);
                    questionAnswersEntity.setAppId(appId);
                    questionAnswersEntity.setQuestionInstanceId(questionInstanceId);
                    questionAnswersEntity.setQuestionOptionsId(questionOptionsId);
                    questionAnswersEntity.setQuestionAnswerId(questionAnswerId);
                    questionAnswersEntity.setQuestionIdentifier(questionIdentifier);
                    questionAnswersEntity.setVer(ver);
                    return questionAnswersEntity;
                }).collect(Collectors.toList());
        questionAnswersService.saveBatch(answersEntityList);
        // options
        if (QuestionTypeEnum.isSelect(questionType.getCode())) {
            List<QuestionOptionsEntity> optionsEntityList = answersEntityList.stream()
                    .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                    .collect(Collectors.toList());
            questionOptionsService.saveBatch(optionsEntityList);
        }

        return questionInstanceId;
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

        String questionInstanceId = question.getQuestionInstanceId();
        QuestionResponse oriQuestion = getQuestion(questionInstanceId);
        QuestionResponse targetQuestion = oriQuestion.clone();
        // TODO
        return "";
    }

    // 克隆 Question 生成新的 Question
    private String cloneQue2NewQue(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return "";
        }

        String questionInstanceId = question.getQuestionInstanceId();
        QuestionResponse oriQuestion = getQuestion(questionInstanceId);
        QuestionResponse targetQuestion = oriQuestion.clone();
        // TODO
        return "";
    }

    private void updateQue(QuestionRequest question) {
        if (BeanUtil.isEmpty(question)) {
            return;
        }

        // baseInfo
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(question, QuestionInstanceEntity.class);
        questionInstanceService.updateById(questionInstanceEntity);

        // relation
        QuestionTypeEnum questionTypeEnum = question.getQuestionType();
        String code = questionTypeEnum.getCode();
        // 选择题
        if (QuestionTypeEnum.isSelect(code)) {
            updateSelectType(question, questionInstanceEntity);
        }
        // 判断题
        // 主观题
        // 材料题
    }

    // 更新选择题
    private void updateSelectType(QuestionRequest question, QuestionInstanceEntity questionInstanceEntity) {
        // new options and answers
        String questionInstanceId = questionInstanceEntity.getQuestionInstanceId();
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = question.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return;
        }

        // remove victims
        List<String> newOptionIdList = optionWithAnswerList.stream()
                .map(QuestionOptionWithAnswerRequest::getQuestionOptionsId)
                .filter(StrUtil::isBlank)
                .collect(Collectors.toList());
        List<QuestionAnswersEntity> answersEntityList = questionAnswersService.lambdaQuery()
                .eq(QuestionAnswersEntity::getQuestionInstanceId, questionInstanceId)
                .list();
        List<String> oldOptionIdList = answersEntityList.stream()
                .map(QuestionAnswersEntity::getQuestionOptionsId)
                .collect(Collectors.toList());
        List<String> victimsList = listVictims(oldOptionIdList, newOptionIdList);
        Wrapper<QuestionAnswersEntity> answerRemoveWrapper = new LambdaQueryWrapper<QuestionAnswersEntity>()
                .in(QuestionAnswersEntity::getQuestionOptionsId, victimsList);
        Wrapper<QuestionOptionsEntity> optionsRemoveWrapper = new LambdaQueryWrapper<QuestionOptionsEntity>()
                .in(QuestionOptionsEntity::getQuestionOptionsId, victimsList);
        questionAnswersService.remove(answerRemoveWrapper);
        questionOptionsService.remove(optionsRemoveWrapper);

        // save or upd answers and options
        // answers
        List<QuestionAnswersEntity> answerList = optionWithAnswerList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionAnswersEntity.class))
                .toList();
        questionAnswersService.updateBatchById(answersEntityList);
        // options
        List<QuestionOptionsEntity> optionList = answerList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                .collect(Collectors.toList());
        questionOptionsService.updateBatchById(optionList);

    }

    // 遇难者 - 找到新选项针对于旧选项的差集，即旧选项有的而新选项中没有的
    private List<String> listVictims(List<String> oldOptionIdList, List<String> newOptionIdList) {
        boolean isEmpty = oldOptionIdList == null || oldOptionIdList.isEmpty() || newOptionIdList == null || newOptionIdList.isEmpty();
        if (isEmpty) {
            return new ArrayList<>();
        }

        // oldOptionIdList 的差集
        return (List<String>) CollectionUtils.subtract(oldOptionIdList, newOptionIdList);
    }
}