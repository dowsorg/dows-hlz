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
 * @description 材料类题型，需要处理嵌套子类
 * @date 2023/4/23 15:10
 */
@Component
@RequiredArgsConstructor
public class MaterialQuestionTypeHandler implements QuestionTypeHandler {

    private final BaseQuestionDomainBiz baseBiz;
    private final QuestionInstanceService questionInstanceService;

    @PostConstruct
    @Override
    public void init() {
        QuestionTypeFactory.register(QuestionTypeEnum.MATERIAL, this);
    }

    @Transactional
    @Override
    public String save(QuestionRequest questionRequest) {
        questionRequest.setBizCode(questionRequest.getBizCode() == null ? QuestionAccessAuthEnum.PRIVATE_VIEWING : questionRequest.getBizCode());
        questionRequest.setAppId(baseBiz.getAppId());
        questionRequest.setQuestionInstancePid(baseBiz.getQuestionInstancePid());
        questionRequest.setQuestionInstanceId(baseBiz.getIdStr());
        questionRequest.setQuestionIdentifier(baseBiz.getIdStr());
        questionRequest.setVer(baseBiz.getLastVer());

        // save baseInfo
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(questionRequest, QuestionInstanceEntity.class);
        questionInstanceEntity.setQuestionType(questionRequest.getQuestionType().getCode());
        questionInstanceService.save(questionInstanceEntity);

        // handle children
        List<QuestionRequest> children = questionRequest.getChildren();
        for (QuestionRequest qr : children) {
            qr.setQuestionInstancePid(questionInstanceEntity.getQuestionInstanceId());
            QuestionTypeEnum curQuestionTypeEnum = qr.getQuestionType();
            QuestionTypeHandler questionTypeHandler = QuestionTypeFactory.get(curQuestionTypeEnum);
            questionTypeHandler.save(qr);
        }
        return questionInstanceEntity.getQuestionInstanceId();
    }

    @Transactional
    @Override
    public boolean update(QuestionRequest questionRequest) {
        // update base
        QuestionInstanceEntity questionInstanceEntity = BeanUtil.copyProperties(questionRequest, QuestionInstanceEntity.class);
        boolean updInstanceRes = questionInstanceService.updateById(questionInstanceEntity);

        // children
        List<QuestionRequest> children = questionRequest.getChildren();
        for (QuestionRequest qr : children) {
            String curQuestionInstanceId = qr.getQuestionInstanceId();
            QuestionTypeEnum curQuestionTypeEnum = qr.getQuestionType();
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
    public QuestionResponse get(String questionInstanceId) {
        if (StrUtil.isBlank(questionInstanceId)) {
            return new QuestionResponse();
        }

        // instance
        LambdaQueryWrapper<QuestionInstanceEntity> instanceWrapper = new LambdaQueryWrapper<QuestionInstanceEntity>()
                .eq(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceId);
        QuestionInstanceEntity questionInstance = questionInstanceService.getOne(instanceWrapper);
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
        List<QuestionResponse> responseList = children.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionResponse.class))
                .toList();
        result.setChildren(responseList);

        return result;
    }

}
