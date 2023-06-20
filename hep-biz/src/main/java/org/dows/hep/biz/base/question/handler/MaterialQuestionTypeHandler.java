package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.dto.QuestionRequestDTO;
import org.dows.hep.api.base.question.dto.QuestionResultRecordDTO;
import org.dows.hep.api.base.question.QuestionESCEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.QuestionBaseBiz;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.service.QuestionInstanceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhb
 * @description 材料类题型，需要处理嵌套子类
 * @date 2023/4/23 15:10
 */
@Component
@RequiredArgsConstructor
public class MaterialQuestionTypeHandler implements QuestionTypeHandler {

    private final QuestionBaseBiz baseBiz;
    private final BaseQuestionHandler baseQuestionHandler;
    private final QuestionInstanceService questionInstanceService;

    @PostConstruct
    @Override
    public void init() {
        QuestionTypeFactory.register(QuestionTypeEnum.MATERIAL, this);
    }

    @Transactional
    @Override
    public String save(QuestionRequestDTO request) {
        // save base-info
        QuestionRequest qr0 = request.getQuestionRequest();
        QuestionInstanceEntity questionInstance = QuestionInstanceEntity.builder()
                .questionType(QuestionTypeEnum.MATERIAL.getCode())
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
                .questionTitle(qr0.getQuestionTitle())
                .questionDescr(qr0.getQuestionDescr())
                .enabled(qr0.getEnabled())
                .refCount(0)
                .detailedAnswer(qr0.getDetailedAnswer())
                .build();
        questionInstanceService.save(questionInstance);

        // handle children
        List<QuestionRequest> children = qr0.getChildren();
        for (QuestionRequest qr : children) {
            String questionType = qr.getQuestionType();
            QuestionTypeEnum questionTypeEnum = QuestionTypeEnum.getByCode(questionType);
            QuestionRequestDTO itemDto = QuestionRequestDTO.builder()
                    .questionRequest(qr)
                    .questionInstanceId(qr.getQuestionInstanceId())
                    .questionType(questionTypeEnum)
                    .appId(questionInstance.getAppId())
                    .questionInstancePid(questionInstance.getQuestionInstanceId())
                    .accountId(questionInstance.getAccountId())
                    .accountName(questionInstance.getAccountName())
                    .source(questionInstance.getSource())
                    .bizCode(questionInstance.getBizCode())
                    .build();

            QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
            questionTypeHandler.save(itemDto);
        }

        // handle question-dimension
        baseQuestionHandler.saveOrUpdQuestionDimension(qr0, questionInstance.getQuestionInstanceId());

        return questionInstance.getQuestionInstanceId();
    }

    @Transactional
    @Override
    public boolean update(QuestionRequestDTO request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        String questionInstanceId = request.getQuestionInstanceId();
        if (StrUtil.isBlank(questionInstanceId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        QuestionInstanceEntity oriEntity = getById(questionInstanceId);
        if (BeanUtil.isEmpty(oriEntity)) {
            throw new BizException(QuestionESCEnum.DATA_NULL);
        }

        // update base-info
        QuestionRequest qr0 = request.getQuestionRequest();
        QuestionInstanceEntity questionInstance = QuestionInstanceEntity.builder()
                .id(oriEntity.getId())
                .questionInstanceId(qr0.getQuestionInstanceId())
                .questionCategId(qr0.getQuestionCategId())
                .questionTitle(qr0.getQuestionTitle())
                .questionDescr(qr0.getQuestionDescr())
                .enabled(qr0.getEnabled())
                .detailedAnswer(qr0.getDetailedAnswer())
                .build();
        boolean updInstanceRes = questionInstanceService.updateById(questionInstance);

        // update children
        List<QuestionRequest> children = qr0.getChildren();
        for (QuestionRequest qr : children) {
            String questionType = qr.getQuestionType();
            QuestionTypeEnum questionTypeEnum = QuestionTypeEnum.getByCode(questionType);
            QuestionRequestDTO itemDto = QuestionRequestDTO.builder()
                    .questionRequest(qr)
                    .questionInstanceId(qr.getQuestionInstanceId())
                    .questionType(questionTypeEnum)
                    .appId(questionInstance.getAppId())
                    .questionInstancePid(questionInstance.getQuestionInstanceId())
                    .accountId(questionInstance.getAccountId())
                    .accountName(questionInstance.getAccountName())
                    .source(questionInstance.getSource())
                    .bizCode(questionInstance.getBizCode())
                    .build();

            String curQuestionInstanceId = qr.getQuestionInstanceId();
            QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
            if (StrUtil.isBlank(curQuestionInstanceId)) {
                questionTypeHandler.save(itemDto);
            } else {
                questionTypeHandler.update(itemDto);
            }
        }

        // handle question-dimension
        baseQuestionHandler.saveOrUpdQuestionDimension(qr0, questionInstance.getQuestionInstanceId());

        return updInstanceRes;
    }

    @Override
    public QuestionResponse get(String questionInstanceId, QuestionResultRecordDTO questionResultRecordDTO) {
        if (StrUtil.isBlank(questionInstanceId)) {
            return new QuestionResponse();
        }

        // instance
        QuestionInstanceEntity questionInstance = getById(questionInstanceId);
        if (BeanUtil.isEmpty(questionInstance)) {
            return new QuestionResponse();
        }
        QuestionResponse result = BeanUtil.copyProperties(questionInstance, QuestionResponse.class);

        // children
        LambdaQueryWrapper<QuestionInstanceEntity> childrenWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstancePid, questionInstanceId);
        List<QuestionInstanceEntity> children = questionInstanceService.list(childrenWrapper);
        if (children == null || children.isEmpty()) {
            return result;
        }

        List<QuestionResponse> responseList = new ArrayList<>();
        children.forEach(item -> {
            QuestionTypeEnum questionTypeEnum = QuestionTypeEnum.getByCode(item.getQuestionType());
            QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(questionTypeEnum);
            QuestionResponse questionResponse = questionTypeHandler.get(item.getQuestionInstanceId(), questionResultRecordDTO);
            responseList.add(questionResponse);
        });
        result.setChildren(responseList);

        // question-dimension
        baseQuestionHandler.setDimensionId(result);
        baseQuestionHandler.setQuestionResult(result, questionResultRecordDTO);

        return result;
    }

    private QuestionInstanceEntity getById(String questionInstanceId) {
        LambdaQueryWrapper<QuestionInstanceEntity> instanceWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId);
        return questionInstanceService.getOne(instanceWrapper);
    }

}
