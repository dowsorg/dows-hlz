package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionOptionWithAnswerRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
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

    private String saveQuestion(QuestionRequest question) {
        // TODO generate AppId
        String appId = "3";
        String questionInstanceId = idGenerator.nextIdStr();

        // baseInfo
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(question, QuestionInstanceEntity.class);
        questionInstanceEntity.setAppId(appId);
        questionInstanceEntity.setQuestionInstanceId(questionInstanceId);
        questionInstanceService.save(questionInstanceEntity);

        // options and answers
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = question.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return questionInstanceId;
        }
        List<QuestionAnswersEntity> answersEntityList = optionWithAnswerList.stream()
                .map(item -> {
                    String questionOptionsId = idGenerator.nextIdStr();
                    String questionAnswerId = idGenerator.nextIdStr();

                    QuestionAnswersEntity questionAnswersEntity = BeanUtil.copyProperties(item, QuestionAnswersEntity.class);
                    questionAnswersEntity.setAppId(appId);
                    questionAnswersEntity.setQuestionInstanceId(questionInstanceId);
                    questionAnswersEntity.setQuestionOptionsId(questionOptionsId);
                    questionAnswersEntity.setQuestionAnswerId(questionAnswerId);
                    return questionAnswersEntity;
                }).collect(Collectors.toList());
        List<QuestionOptionsEntity> optionsEntityList = answersEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionOptionsEntity.class))
                .collect(Collectors.toList());
        questionOptionsService.saveBatch(optionsEntityList);
        questionAnswersService.saveBatch(answersEntityList);
        return questionInstanceId;
    }

    private String updQuestion(QuestionRequest question) {
        // baseInfo
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(question, QuestionInstanceEntity.class);
        questionInstanceService.updateById(questionInstanceEntity);

        // options and answers
        String questionInstanceId = questionInstanceEntity.getQuestionInstanceId();
        List<QuestionOptionWithAnswerRequest> optionWithAnswerList = question.getOptionWithAnswerList();
        if (optionWithAnswerList == null || optionWithAnswerList.isEmpty()) {
            return questionInstanceId;
        }

        // TODO 0420:1759
        List<QuestionOptionsEntity> optionsEntityList = questionOptionsService.lambdaQuery()
                .eq(QuestionOptionsEntity::getQuestionInstanceId, questionInstanceId)
                .list();
//        listSurvivor();

        return "";
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
    public QuestionResponse pageQuestion(QuestionSearchRequest questionSearch ) {
        return new QuestionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 条件查询-无分页
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<QuestionResponse> listQuestion(QuestionSearchRequest questionSearch ) {
        return new ArrayList<QuestionResponse>();
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
        return new QuestionResponse();
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
        return Boolean.FALSE;
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
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 排序
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean sortQuestion(String string, Integer sequence ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 交换
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean transposeQuestion(String leftQuestionInstanceId, String rightQuestionInstanceId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 删除or批量删除
    * @关联表: QuestionInstance,QuestionOptions,QuestionAnswers
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean delQuestion(String questionInstanceIds ) {
        return Boolean.FALSE;
    }
}