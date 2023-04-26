package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionCloneEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionOptionWithAnswerRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.entity.QuestionAnswersEntity;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.entity.QuestionOptionsEntity;
import org.dows.hep.service.QuestionAnswersService;
import org.dows.hep.service.QuestionInstanceService;
import org.dows.hep.service.QuestionOptionsService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    private final IdGenerator idGenerator;
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
        // baseInfo
        String appId = questionRequest.getAppId();
        String questionInstanceId = idGenerator.nextIdStr();
        String questionIdentifier = idGenerator.nextIdStr();
        String ver = String.valueOf(new Date().getTime());
        QuestionTypeEnum questionTypeEnum = questionRequest.getQuestionType();
        String questionType = questionTypeEnum.getCode();

        // save baseInfo
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(questionRequest, QuestionInstanceEntity.class);
        questionInstanceEntity.setQuestionInstanceId(questionInstanceId);
        questionInstanceEntity.setQuestionType(questionType);
        questionInstanceEntity.setQuestionIdentifier(questionIdentifier);
        questionInstanceEntity.setVer(ver);
        questionInstanceService.save(questionInstanceEntity);

        // list options and answers
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = questionRequest.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return questionInstanceId;
        }

        // save answers
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

        // save options
        List<QuestionOptionsEntity> optionsEntityList = answersEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                .collect(Collectors.toList());
        questionOptionsService.saveBatch(optionsEntityList);
        return questionInstanceId;
    }

    @Transactional
    @Override
    public boolean update(QuestionRequest questionRequest) {
        // update base
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
    public String clone(QuestionRequest questionRequest, QuestionCloneEnum questionCloneEnum) {
        return null;
    }


}
