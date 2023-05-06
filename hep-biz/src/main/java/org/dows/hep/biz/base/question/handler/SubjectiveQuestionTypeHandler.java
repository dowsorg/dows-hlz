package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionAccessAuthEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.BaseQuestionDomainBiz;
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

    private final BaseQuestionDomainBiz baseQuestionDomainBiz;
    private final QuestionInstanceService questionInstanceService;

    @PostConstruct
    @Override
    public void init() {
        QuestionTypeFactory.register(QuestionTypeEnum.SUBJECTIVE, this);
    }

    @Transactional
    @Override
    public String save(QuestionRequest questionRequest) {
        questionRequest.setBizCode(questionRequest.getBizCode() == null ? QuestionAccessAuthEnum.PRIVATE_VIEWING : questionRequest.getBizCode());
        questionRequest.setAppId(baseQuestionDomainBiz.getAppId());
        questionRequest.setQuestionInstancePid(baseQuestionDomainBiz.getQuestionInstancePid());
        return traverseSave(questionRequest);
    }

    @Transactional
    @Override
    public boolean update(QuestionRequest questionRequest) {
        return traverseUpd(questionRequest);
    }

    @Override
    public QuestionResponse get(String questionInstanceId) {
        LambdaQueryWrapper<QuestionInstanceEntity> queryWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId);
        QuestionInstanceEntity questionInstance = questionInstanceService.getOne(queryWrapper);
        if (BeanUtil.isEmpty(questionInstance)) {
            return new QuestionResponse();
        }

        QuestionResponse questionResponse = BeanUtil.copyProperties(questionInstance, QuestionResponse.class);
        setChildren(questionResponse);

        return questionResponse;
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

    private Boolean traverseUpd(QuestionRequest node) {
        if (node == null) {
            return Boolean.FALSE;
        }

        // 处理当前节点
        boolean updRes = updNode(node);

        // 判空
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return Boolean.FALSE;
        }

        // 遍历子节点
        for (QuestionRequest qr : node.getChildren()) {
            if (StrUtil.isBlank(qr.getQuestionInstanceId())) {
                qr.setAppId(node.getAppId());
                qr.setQuestionInstancePid(node.getQuestionInstanceId());
                qr.setQuestionType(qr.getQuestionType() == null ? node.getQuestionType() : qr.getQuestionType());
            }
            traverseUpd(qr);
        }
        return updRes;
    }

    private Boolean updNode(QuestionRequest qr) {
        if (StrUtil.isBlank(qr.getQuestionInstanceId())) {
            saveNode(qr);
        } else {
            QuestionInstanceEntity updEntity = BeanUtil.copyProperties(qr, QuestionInstanceEntity.class);
            questionInstanceService.updateById(updEntity);
        }
        return true;
    }

    private String traverseSave(QuestionRequest node) {
        if (node == null) {
            return "";
        }

        // 处理当前节点
        String questionInstanceId = saveNode(node);

        // 判空
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return "";
        }

        // 遍历子节点
        for (QuestionRequest qr : node.getChildren()) {
            qr.setAppId(node.getAppId());
            qr.setQuestionInstancePid(node.getQuestionInstanceId());
            qr.setQuestionType(qr.getQuestionType() == null ? node.getQuestionType() : qr.getQuestionType());
            traverseSave(qr);
        }

        return questionInstanceId;
    }

    private String saveNode(QuestionRequest qr) {
        // save instance
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(qr, QuestionInstanceEntity.class);
        questionInstanceEntity.setQuestionInstanceId(baseQuestionDomainBiz.getIdStr());
        questionInstanceEntity.setQuestionIdentifier(baseQuestionDomainBiz.getIdStr());
        questionInstanceEntity.setVer(baseQuestionDomainBiz.getLastVer());
        questionInstanceEntity.setQuestionType(qr.getQuestionType().getCode());
        questionInstanceService.save(questionInstanceEntity);
        return questionInstanceEntity.getQuestionInstanceId();
    }
}
