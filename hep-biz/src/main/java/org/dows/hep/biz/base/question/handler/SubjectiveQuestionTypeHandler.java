package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionCloneEnum;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.entity.QuestionInstanceEntity;
import org.dows.hep.service.QuestionInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author fhb
 * @description 主观题-主观题会有嵌套主观题的情况
 * @date 2023/4/23 15:07
 */
@Component
@RequiredArgsConstructor
public class SubjectiveQuestionTypeHandler implements QuestionTypeHandler {

    private final IdGenerator idGenerator;
    private final QuestionInstanceService questionInstanceService;

    @Transactional
    @Override
    public String save(QuestionRequest questionRequest) {
        return traverseSave(questionRequest);
    }

    @Transactional
    @Override
    public Boolean update(QuestionRequest questionRequest) {
        return traverseUpd(questionRequest);
    }

    @Override
    public String clone(QuestionRequest questionRequest, QuestionCloneEnum questionCloneEnum) {
        return null;
    }

    private Boolean traverseUpd(QuestionRequest node) {
        if (node == null) {
            return false;
        }

        boolean updRes = updNode(node);
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

        String questionInstanceId = saveNode(node);
        for (QuestionRequest qr : node.getChildren()) {
            qr.setAppId(node.getAppId());
            qr.setQuestionInstancePid(node.getQuestionInstanceId());
            qr.setQuestionType(qr.getQuestionType() == null ? node.getQuestionType() : qr.getQuestionType());
            traverseSave(qr);
        }
        return questionInstanceId;
    }

    private String saveNode(QuestionRequest qr) {
        // generate id and ver
        String curQuestionInstanceId = idGenerator.nextIdStr();
        String curQuestionIdentifier = idGenerator.nextIdStr();
        String curVer = String.valueOf(new Date().getTime());
        QuestionTypeEnum questionTypeEnum = qr.getQuestionType();
        String questionType = questionTypeEnum.getCode();

        // save instance
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(qr, QuestionInstanceEntity.class);
        questionInstanceEntity.setQuestionInstanceId(curQuestionInstanceId);
        questionInstanceEntity.setQuestionType(questionType);
        questionInstanceEntity.setQuestionIdentifier(curQuestionIdentifier);
        questionInstanceEntity.setVer(curVer);
        questionInstanceService.save(questionInstanceEntity);
        return curQuestionInstanceId;
    }
}
