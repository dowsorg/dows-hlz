package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.dto.QuestionRequestDTO;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.enums.QuestionTypeEnum;
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

    private final QuestionDomainBaseBiz baseBiz;
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

    @DSTransactional
    @Override
    public String save(QuestionRequestDTO request) {
        // save base-info
        QuestionRequest qr0 = request.getQuestionRequest();
        QuestionInstanceEntity questionInstance = QuestionInstanceEntity.builder()
                .questionInstanceId(baseBiz.getIdStr())
                .questionIdentifier(baseBiz.getIdStr())
                .ver(baseBiz.getLastVer())
                .appId(request.getAppId())
                .source(request.getSource())
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .questionInstancePid(request.getQuestionInstancePid())
                .bizCode(request.getBizCode())
                .questionCategId(qr0.getQuestionCategId())
                .inputType(null)
                .questionType(qr0.getQuestionType())
                .questionTitle(qr0.getQuestionTitle())
                .questionDescr(qr0.getQuestionDescr())
                .enabled(qr0.getEnabled())
                .refCount(0)
                .detailedAnswer(qr0.getDetailedAnswer())
                .build();
        questionInstanceService.save(questionInstance);

        // save options and answers
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = qr0.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return questionInstance.getQuestionInstanceId();
        }

        // save answers
        List<QuestionAnswersEntity> answersEntityList = optionWithAnswerList.stream()
                .map(item -> QuestionAnswersEntity.builder()
                        .questionInstanceId(questionInstance.getQuestionInstanceId())
                        .questionAnswerId(baseBiz.getIdStr())
                        .questionOptionsId(baseBiz.getIdStr())
                        .optionTitle(item.getOptionTitle())
                        .optionValue(item.getOptionValue())
                        .rightAnswer(item.getRightAnswer())
                        .build())
                .collect(Collectors.toList());
        questionAnswersBiz.saveOrUpdBatch(answersEntityList);

        // save options
        List<QuestionOptionsEntity> optionsEntityList = answersEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                .collect(Collectors.toList());
        questionOptionsBiz.saveOrUpdBatch(optionsEntityList);

        return questionInstance.getQuestionInstanceId();
    }

    @DSTransactional
    @Override
    public boolean update(QuestionRequestDTO request) {
        // check
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        QuestionInstanceEntity oriEntity = getById(request.getQuestionInstanceId());
        if (BeanUtil.isEmpty(oriEntity)) {
            throw new BizException(QuestionESCEnum.DATA_NULL);
        }

        // update base-info
        QuestionRequest qr0 = request.getQuestionRequest();
        QuestionInstanceEntity questionInstance = QuestionInstanceEntity.builder()
                .id(oriEntity.getId())
                .questionCategId(qr0.getQuestionCategId())
                .questionTitle(qr0.getQuestionTitle())
                .questionDescr(qr0.getQuestionDescr())
                .enabled(qr0.getEnabled())
                .detailedAnswer(qr0.getDetailedAnswer())
                .build();
        boolean updInstanceRes = questionInstanceService.updateById(questionInstance);

        // list options and answers
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = qr0.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return Boolean.TRUE;
        }

        // save or upd answers
        List<QuestionAnswersEntity> answerList = optionWithAnswerList.stream()
                .map(item -> {
                    QuestionAnswersEntity questionAnswersEntity = QuestionAnswersEntity.builder()
                            .questionInstanceId(questionInstance.getQuestionInstanceId())
                            .questionAnswerId(item.getQuestionAnswerId())
                            .questionOptionsId(item.getQuestionOptionsId())
                            .optionTitle(item.getOptionTitle())
                            .optionValue(item.getOptionValue())
                            .rightAnswer(item.getRightAnswer())
                            .build();
                    if (StrUtil.isBlank(questionAnswersEntity.getQuestionAnswerId())) {
                        questionAnswersEntity.setQuestionOptionsId(baseBiz.getIdStr());
                        questionAnswersEntity.setQuestionAnswerId(baseBiz.getIdStr());
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
        QuestionInstanceEntity questionInstance = getById(questionInstanceId);
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

    private QuestionInstanceEntity getById(String questionInstanceId) {
        LambdaQueryWrapper<QuestionInstanceEntity> instanceWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId);
        return questionInstanceService.getOne(instanceWrapper);
    }

}
