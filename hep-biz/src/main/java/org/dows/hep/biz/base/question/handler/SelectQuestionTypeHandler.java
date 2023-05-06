package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionAccessAuthEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionOptionWithAnswerRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.response.QuestionOptionWithAnswerResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.BaseQuestionDomainBiz;
import org.dows.hep.entity.QuestionAnswersEntity;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.entity.QuestionOptionsEntity;
import org.dows.hep.service.QuestionAnswersService;
import org.dows.hep.service.QuestionInstanceService;
import org.dows.hep.service.QuestionOptionsService;
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

    private final BaseQuestionDomainBiz baseQuestionDomainBiz;
    private final QuestionInstanceService questionInstanceService;
    private final QuestionOptionsService questionOptionsService;
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
        questionRequest.setAppId(questionRequest.getAppId() == null ? baseQuestionDomainBiz.getAppId() : baseQuestionDomainBiz.getAppId());
        questionRequest.setQuestionInstancePid(baseQuestionDomainBiz.getQuestionInstancePid());
        questionRequest.setQuestionCategId(baseQuestionDomainBiz.getIdStr());
        questionRequest.setQuestionIdentifier(baseQuestionDomainBiz.getIdStr());
        questionRequest.setVer(baseQuestionDomainBiz.getLastVer());

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
                    questionAnswersEntity.setQuestionOptionsId(baseQuestionDomainBiz.getIdStr());
                    questionAnswersEntity.setQuestionAnswerId(baseQuestionDomainBiz.getIdStr());
                    return questionAnswersEntity;
                }).collect(Collectors.toList());
        questionAnswersService.saveBatch(answersEntityList);

        // save options
        List<QuestionOptionsEntity> optionsEntityList = answersEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                .collect(Collectors.toList());
        questionOptionsService.saveBatch(optionsEntityList);

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
                .map(item -> BeanUtil.copyProperties(item, QuestionAnswersEntity.class))
                .toList();
        boolean updAnswerRes = questionAnswersService.saveOrUpdateBatch(answerList);
        // save or upd options
        List<QuestionOptionsEntity> optionList = answerList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                .collect(Collectors.toList());
        boolean updOptionsRes = questionOptionsService.saveOrUpdateBatch(optionList);

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
        LambdaQueryWrapper<QuestionAnswersEntity> answersWrapper = new LambdaQueryWrapper<QuestionAnswersEntity>()
                .eq(QuestionAnswersEntity::getQuestionInstanceId, questionInstance);
        List<QuestionAnswersEntity> answersEntityList = questionAnswersService.list(answersWrapper);
        if (answersEntityList == null || answersEntityList.isEmpty()) {
            return result;
        }
        List<QuestionOptionWithAnswerResponse> optionWithAnswerResponses = answersEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionWithAnswerResponse.class))
                .collect(Collectors.toList());
        result.setOptionWithAnswerList(optionWithAnswerResponses);

        return result;
    }

}
