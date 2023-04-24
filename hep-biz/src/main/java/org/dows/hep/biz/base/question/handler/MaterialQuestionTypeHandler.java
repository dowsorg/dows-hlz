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

import java.util.*;

/**
 * @author fhb
 * @description 材料类题型，需要处理嵌套子类
 * @date 2023/4/23 15:10
 */
@Component
@RequiredArgsConstructor
public class MaterialQuestionTypeHandler implements QuestionTypeHandler {

    private final IdGenerator idGenerator;
    private final QuestionInstanceService questionInstanceService;

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

        // children
        List<QuestionRequest> children = questionRequest.getChildren();
        for (QuestionRequest qr : children) {
            qr.setAppId(appId);
            qr.setQuestionInstancePid(questionInstanceId);

            QuestionTypeEnum curQuestionTypeEnum = qr.getQuestionType();
            assert curQuestionTypeEnum != null;
            QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(curQuestionTypeEnum);
            questionTypeHandler.save(qr);
        }
        return questionInstanceId;
    }

    @Transactional
    @Override
    public Boolean update(QuestionRequest questionRequest) {
        // update base
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(questionRequest, QuestionInstanceEntity.class);
        boolean updInstanceRes = questionInstanceService.updateById(questionInstanceEntity);

        // children
        List<QuestionRequest> children = questionRequest.getChildren();
        for (QuestionRequest qr : children) {
            String curQuestionInstanceId = qr.getQuestionInstanceId();
            QuestionTypeEnum curQuestionTypeEnum = qr.getQuestionType();
            assert curQuestionTypeEnum != null;
            QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(curQuestionTypeEnum);

            if (StrUtil.isBlank(curQuestionInstanceId)) {
                questionTypeHandler.save(qr);
            } else {
                questionTypeHandler.update(qr);
            }
        }

        return updInstanceRes;
    }

    @Override
    public String clone(QuestionRequest questionRequest, QuestionCloneEnum questionCloneEnum) {
        return null;
    }

}
