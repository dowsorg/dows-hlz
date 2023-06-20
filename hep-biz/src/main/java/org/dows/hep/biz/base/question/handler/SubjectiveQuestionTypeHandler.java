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

import java.util.List;

/**
 * @author fhb
 * @description 主观题-主观题会有嵌套主观题的情况
 * @date 2023/4/23 15:07
 */
@Component
@RequiredArgsConstructor
public class SubjectiveQuestionTypeHandler implements QuestionTypeHandler {

    private final QuestionBaseBiz baseBiz;
    private final BaseQuestionHandler baseQuestionHandler;
    private final QuestionInstanceService questionInstanceService;

    @PostConstruct
    @Override
    public void init() {
        QuestionTypeFactory.register(QuestionTypeEnum.SUBJECTIVE, this);
    }

    @Transactional
    @Override
    public String save(QuestionRequestDTO questionRequest) {
        return traverseSave(questionRequest);
    }

    @Transactional
    @Override
    public boolean update(QuestionRequestDTO questionRequest) {
        return traverseUpd(questionRequest);
    }

    @Override
    public QuestionResponse get(String questionInstanceId, QuestionResultRecordDTO questionResultRecordDTO) {
        QuestionInstanceEntity questionInstance = getById(questionInstanceId);
        if (BeanUtil.isEmpty(questionInstance)) {
            return new QuestionResponse();
        }

        QuestionResponse questionResponse = BeanUtil.copyProperties(questionInstance, QuestionResponse.class);
        setChildren(questionResponse);
        baseQuestionHandler.setDimensionId(questionResponse);
        baseQuestionHandler.setQuestionResult(questionResponse, questionResultRecordDTO);

        return questionResponse;
    }

    private QuestionInstanceEntity getById(String questionInstanceId) {
        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId);
        return questionInstanceService.getOne(queryWrapper);
    }

    private void setChildren(QuestionResponse questionResponse) {
        // 判空
        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstancePid, questionResponse.getQuestionInstanceId());
        List<QuestionInstanceEntity> childrenEntity = questionInstanceService.list(queryWrapper);
        if (childrenEntity == null || childrenEntity.isEmpty()) {
            return;
        }

        // 处理当前节点
        List<QuestionResponse> childrenResponse = childrenEntity.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionResponse.class))
                .toList();
        questionResponse.setChildren(childrenResponse);

        // 遍历子节点
        childrenResponse.forEach(this::setChildren);
    }

    private String traverseSave(QuestionRequestDTO node) {
        if (node == null) {
            return "";
        }

        // 处理当前节点
        String questionInstanceId = saveNode(node);
        node.setQuestionInstanceId(questionInstanceId);

        // 判空
        QuestionRequest questionRequest = node.getQuestionRequest();
        List<QuestionRequest> children = questionRequest.getChildren();
        if (children == null || children.isEmpty()) {
            return questionInstanceId;
        }

        // 遍历子节点
        for (QuestionRequest qr : children) {
            QuestionRequestDTO itemDto = QuestionRequestDTO.builder()
                    .questionRequest(qr)
                    .questionInstanceId(qr.getQuestionInstanceId())
                    .questionType(QuestionTypeEnum.SUBJECTIVE)
                    .appId(node.getAppId())
                    .questionInstancePid(node.getQuestionInstanceId())
                    .accountId(node.getAccountId())
                    .accountName(node.getAccountName())
                    .source(node.getSource())
                    .bizCode(node.getBizCode())
                    .build();
            traverseSave(itemDto);
        }

        return questionInstanceId;
    }

    private Boolean traverseUpd(QuestionRequestDTO node) {
        if (node == null) {
            return Boolean.FALSE;
        }

        // 处理当前节点
        Boolean updRes = updNode(node);

        // 判空
        QuestionRequest questionRequest = node.getQuestionRequest();
        List<QuestionRequest> children = questionRequest.getChildren();
        if (children == null || children.isEmpty()) {
            return Boolean.FALSE;
        }

        // 遍历子节点
        for (QuestionRequest qr : children) {
            QuestionRequestDTO itemDto = QuestionRequestDTO.builder()
                    .questionRequest(qr)
                    .questionInstanceId(qr.getQuestionInstanceId())
                    .questionType(QuestionTypeEnum.SUBJECTIVE)
                    .appId(node.getAppId())
                    .accountId(node.getAccountId())
                    .accountName(node.getAccountName())
                    .questionInstancePid(node.getQuestionInstanceId())
                    .source(node.getSource())
                    .bizCode(node.getBizCode())
                    .build();
            traverseUpd(itemDto);
        }
        return updRes;
    }

    private String saveNode(QuestionRequestDTO request) {
        QuestionRequest qr0 = request.getQuestionRequest();
        QuestionInstanceEntity questionInstance = QuestionInstanceEntity.builder()
                .questionType(QuestionTypeEnum.SUBJECTIVE.getCode())
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
                .questionTitle(qr0.getQuestionTitle())
                .questionDescr(qr0.getQuestionDescr())
                .enabled(qr0.getEnabled())
                .detailedAnswer(qr0.getDetailedAnswer())
                .refCount(0)
                .build();
        questionInstanceService.save(questionInstance);
        baseQuestionHandler.saveOrUpdQuestionDimension(qr0, questionInstance.getQuestionInstanceId());
        return questionInstance.getQuestionInstanceId();
    }

    private Boolean updNode(QuestionRequestDTO request) {
        String questionInstanceId = request.getQuestionInstanceId();
        if (StrUtil.isBlank(questionInstanceId)) {
            saveNode(request);
        } else {
            QuestionInstanceEntity entity = getById(questionInstanceId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException(QuestionESCEnum.DATA_NULL);
            }

            QuestionRequest qr0 = request.getQuestionRequest();
            QuestionInstanceEntity questionInstance = QuestionInstanceEntity.builder()
                    .questionType(QuestionTypeEnum.SUBJECTIVE.getCode())
                    .id(entity.getId())
                    .questionCategId(qr0.getQuestionCategId())
                    .questionTitle(qr0.getQuestionTitle())
                    .questionDescr(qr0.getQuestionDescr())
                    .enabled(qr0.getEnabled())
                    .detailedAnswer(qr0.getDetailedAnswer())
                    .build();
            questionInstanceService.updateById(questionInstance);
            baseQuestionHandler.saveOrUpdQuestionDimension(qr0, questionInstance.getQuestionInstanceId());
        }
        return true;
    }
}
