package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.QuestionAccessAuthEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.QuestionDomainBaseBiz;
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

    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionInstanceService questionInstanceService;

    @PostConstruct
    @Override
    public void init() {
        QuestionTypeFactory.register(QuestionTypeEnum.SUBJECTIVE, this);
    }

    @Transactional
    @Override
    public String save(QuestionRequest questionRequest) {
        return traverseSave(questionRequest);
    }

    @Transactional
    @Override
    public boolean update(QuestionRequest questionRequest) {
        return traverseUpd(questionRequest);
    }

    @Override
    public QuestionResponse get(String questionInstanceId) {
        QuestionInstanceEntity questionInstance = getById(questionInstanceId);
        if (BeanUtil.isEmpty(questionInstance)) {
            return new QuestionResponse();
        }

        QuestionResponse questionResponse = BeanUtil.copyProperties(questionInstance, QuestionResponse.class);
        setChildren(questionResponse);

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
            qr.setBizCode(node.getBizCode());
            qr.setQuestionInstancePid(questionInstanceId);
            qr.setQuestionType(qr.getQuestionType() == null ? node.getQuestionType() : qr.getQuestionType());
            traverseSave(qr);
        }

        return questionInstanceId;
    }

    private Boolean traverseUpd(QuestionRequest node) {
        if (node == null) {
            return Boolean.FALSE;
        }

        // 处理当前节点
        Boolean updRes = updNode(node);

        // 判空
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return Boolean.FALSE;
        }

        // 遍历子节点
        for (QuestionRequest qr : node.getChildren()) {
            qr.setAppId(node.getAppId());
            qr.setBizCode(node.getBizCode());
            qr.setQuestionInstancePid(node.getQuestionInstanceId());
            qr.setQuestionType(qr.getQuestionType() == null ? node.getQuestionType() : qr.getQuestionType());
            traverseUpd(qr);
        }
        return updRes;
    }

    private String saveNode(QuestionRequest qr) {
        // save instance
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(qr, QuestionInstanceEntity.class);
        questionInstanceEntity.setBizCode(qr.getBizCode() == null ? QuestionAccessAuthEnum.PRIVATE_VIEWING.name() : qr.getBizCode().name());
        questionInstanceEntity.setAppId(qr.getAppId() == null ? baseBiz.getAppId() : qr.getAppId());
        questionInstanceEntity.setQuestionInstanceId(baseBiz.getIdStr());
        questionInstanceEntity.setQuestionIdentifier(baseBiz.getIdStr());
        questionInstanceEntity.setVer(baseBiz.getLastVer());
        questionInstanceEntity.setQuestionType(qr.getQuestionType().getCode());
        questionInstanceService.save(questionInstanceEntity);
        return questionInstanceEntity.getQuestionInstanceId();
    }

    private Boolean updNode(QuestionRequest qr) {
        if (StrUtil.isBlank(qr.getQuestionInstanceId())) {
            saveNode(qr);
        } else {
            QuestionInstanceEntity oriEntity = getById(qr.getQuestionInstanceId());
            if (BeanUtil.isEmpty(oriEntity)) {
                throw new BizException("数据不存在");
            }
            qr.setId(oriEntity.getId());
            qr.setAppId(baseBiz.getAppId());

            QuestionInstanceEntity updEntity = BeanUtil.copyProperties(qr, QuestionInstanceEntity.class);
            questionInstanceService.updateById(updEntity);
        }
        return true;
    }
}
