package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionAccessAuthEnum;
import org.dows.hep.api.base.question.QuestionEnabledEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionOptionWithAnswerRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.response.QuestionOptionWithAnswerResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.QuestionAnswersBiz;
import org.dows.hep.biz.base.question.QuestionDomainBaseBiz;
import org.dows.hep.biz.base.question.QuestionOptionsBiz;
import org.dows.hep.entity.QuestionAnswersEntity;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.entity.QuestionOptionsEntity;
import org.dows.hep.service.QuestionAnswersService;
import org.dows.hep.service.QuestionInstanceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description 选择类题型[多选|单选]，需要堆选项以及答案进行额外处理
 * @date 2023/4/23 15:09
 */
@Component
@RequiredArgsConstructor
public class SelectQuestionTypeHandler implements QuestionTypeHandler {

    private final QuestionDomainBaseBiz questionDomainBaseBiz;
    private final QuestionInstanceService questionInstanceService;
    private final QuestionOptionsBiz questionOptionsBiz;
    private final QuestionAnswersBiz questionAnswersBiz;
    private final QuestionAnswersService questionAnswersService;

    @PostConstruct
    @Override
    public void init() {
        QuestionTypeFactory.register(QuestionTypeEnum.MULTIPLE_SELECT, this);
        QuestionTypeFactory.register(QuestionTypeEnum.RADIO_SELECT, this);
    }

    @Transactional
    @Override
    public String save(QuestionRequest questionRequest) {
        // base-info
        questionRequest.setBizCode(questionRequest.getBizCode() == null ? QuestionAccessAuthEnum.PRIVATE_VIEWING : questionRequest.getBizCode());
        questionRequest.setAppId(questionRequest.getAppId() == null ? questionDomainBaseBiz.getAppId() : questionDomainBaseBiz.getAppId());
        questionRequest.setQuestionInstancePid(questionDomainBaseBiz.getQuestionInstancePid());
        questionRequest.setQuestionInstanceId(questionDomainBaseBiz.getIdStr());
        questionRequest.setQuestionIdentifier(questionDomainBaseBiz.getIdStr());
        questionRequest.setVer(questionDomainBaseBiz.getLastVer());
        questionRequest.setEnabled(QuestionEnabledEnum.ENABLED.getCode());

        // save base-info
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(questionRequest, QuestionInstanceEntity.class);
        questionInstanceEntity.setQuestionType(questionRequest.getQuestionType().getCode());
        questionInstanceService.save(questionInstanceEntity);

        // list options and answers
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = questionRequest.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return questionInstanceEntity.getQuestionInstanceId();
        }

        // save answers
        List<QuestionAnswersEntity> answersEntityList = optionWithAnswerList.stream()
                .map(item -> {
                    QuestionAnswersEntity questionAnswersEntity = BeanUtil.copyProperties(item, QuestionAnswersEntity.class);
                    questionAnswersEntity.setAppId(questionInstanceEntity.getAppId());
                    questionAnswersEntity.setQuestionInstanceId(questionInstanceEntity.getQuestionInstanceId());
                    questionAnswersEntity.setQuestionOptionsId(questionDomainBaseBiz.getIdStr());
                    questionAnswersEntity.setQuestionAnswerId(questionDomainBaseBiz.getIdStr());
                    return questionAnswersEntity;
                }).collect(Collectors.toList());
        questionAnswersBiz.saveOrUpdBatch(answersEntityList);

        // save options
        List<QuestionOptionsEntity> optionsEntityList = answersEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                .collect(Collectors.toList());
        questionOptionsBiz.saveOrUpdBatch(optionsEntityList);

        return questionInstanceEntity.getQuestionInstanceId();
    }

    @Transactional
    @Override
    public boolean update(QuestionRequest questionRequest) {
        // update base-info
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(questionRequest, QuestionInstanceEntity.class);
        boolean updInstanceRes = questionInstanceService.updateById(questionInstanceEntity);

        // list options and answers
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = questionRequest.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return true;
        }

        // save or upd answers and options
        // save or upd answers
        List<QuestionAnswersEntity> answerList = optionWithAnswerList.stream()
                .map(item -> {
                    QuestionAnswersEntity questionAnswersEntity = BeanUtil.copyProperties(item, QuestionAnswersEntity.class);
                    if (StrUtil.isBlank(questionAnswersEntity.getQuestionAnswerId())) {
                        questionAnswersEntity.setAppId(questionInstanceEntity.getAppId());
                        questionAnswersEntity.setQuestionInstanceId(questionInstanceEntity.getQuestionInstanceId());
                        questionAnswersEntity.setQuestionOptionsId(questionDomainBaseBiz.getIdStr());
                        questionAnswersEntity.setQuestionAnswerId(questionDomainBaseBiz.getIdStr());
                    }
                    return questionAnswersEntity;
                })
                .toList();
        boolean updAnswerRes = questionAnswersBiz.saveOrUpdBatch(answerList);
        // save or upd options
        List<QuestionOptionsEntity> optionList = answerList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                .collect(Collectors.toList());
        boolean updOptionsRes = questionOptionsBiz.saveOrUpdBatch(optionList);

        return updInstanceRes && updOptionsRes && updAnswerRes;
    }

    @Override
    public QuestionResponse get(String questionInstanceId) {
        // instance
        LambdaQueryWrapper<QuestionInstanceEntity> instanceWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId);
        QuestionInstanceEntity questionInstance = questionInstanceService.getOne(instanceWrapper);
        if (BeanUtil.isEmpty(questionInstance)) {
            return new QuestionResponse();
        }
        QuestionResponse result = BeanUtil.copyProperties(questionInstance, QuestionResponse.class);


        // options with answers
        List<QuestionAnswersEntity> answersEntityList = questionAnswersService.lambdaQuery()
                .eq(QuestionAnswersEntity::getQuestionInstanceId, questionInstance.getQuestionInstanceId())
                .list();
        if (answersEntityList == null || answersEntityList.isEmpty()) {
            return result;
        }

        List<QuestionOptionWithAnswerResponse> optionWithAnswerResponses = BeanUtil.copyToList(answersEntityList, QuestionOptionWithAnswerResponse.class);
        result.setOptionWithAnswerList(optionWithAnswerResponses);
        return result;
    }

}
