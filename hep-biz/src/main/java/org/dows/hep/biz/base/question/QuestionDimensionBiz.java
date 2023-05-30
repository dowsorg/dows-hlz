package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.request.QuestionDimensionRequest;
import org.dows.hep.api.base.question.response.QuestionDimensionResponse;
import org.dows.hep.entity.QuestionDimensionEntity;
import org.dows.hep.service.QuestionDimensionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lait.zhang
 * @description project descr:问题:问题-维度
 * @date 2023年4月23日 上午9:44:34
 */
@AllArgsConstructor
@Service
public class QuestionDimensionBiz {
    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionDimensionService questionDimensionService;

    /**
     * @param
     * @return
     * @说明: 关联问题维度
     * @关联表: QuestionDimension, QuestionInstance, QuestionSectionDimension
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    @DSTransactional
    public Boolean relateQuestionDimension(QuestionDimensionRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        String questionInstanceId = request.getQuestionInstanceId();
        if (StrUtil.isBlank(questionInstanceId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        List<String> questionSectionDimensionIds = request.getQuestionSectionDimensionIds();
        if (CollUtil.isEmpty(questionSectionDimensionIds)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        delByInstanceId(questionInstanceId);

        List<QuestionDimensionEntity> entityList = convertRequest2Entity(request);
        return questionDimensionService.saveBatch(entityList);
    }

    public Boolean delByInstanceId(String questionInstanceId) {
        LambdaQueryWrapper<QuestionDimensionEntity> remWrapper = new LambdaQueryWrapper<QuestionDimensionEntity>()
                .eq(QuestionDimensionEntity::getQuestionInstanceId, questionInstanceId);
        return questionDimensionService.remove(remWrapper);
    }

    private List<QuestionDimensionEntity> convertRequest2Entity(QuestionDimensionRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        List<String> questionSectionDimensionIds = request.getQuestionSectionDimensionIds();
        if (CollUtil.isEmpty(questionSectionDimensionIds)) {
            return new ArrayList<>();
        }

        List<QuestionDimensionEntity> resultList = new ArrayList<>();
        questionSectionDimensionIds.forEach(questionSectionDimensionId -> {
            QuestionDimensionEntity result = QuestionDimensionEntity.builder()
                    .questionDimensionId(baseBiz.getIdStr())
                    .questionInstanceId(request.getQuestionInstanceId())
                    .questionSectionDimensionId(questionSectionDimensionId)
                    .build();
            resultList.add(result);
        });

        return resultList;
    }

    /**
     * @param
     * @return
     * @说明: 获取问题下所有维度
     * @关联表: QuestionDimension, QuestionInstance, QuestionSectionDimension
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public QuestionDimensionResponse listQuestionDimension(String questionInstanceId) {
        if (StrUtil.isBlank(questionInstanceId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        QuestionDimensionResponse result = new QuestionDimensionResponse();
        List<QuestionDimensionEntity> entityList = listQuestionDimensionEntity(questionInstanceId);
        if (CollUtil.isEmpty(entityList)) {
            return result;
        }

        List<String> idList = entityList.stream().map(QuestionDimensionEntity::getQuestionSectionDimensionId).toList();
        result.setQuestionInstanceId(questionInstanceId);
        result.setQuestionSectionDimensionIds(idList);

        return result;
    }

    private List<QuestionDimensionEntity> listQuestionDimensionEntity(String questionInstanceId) {
        LambdaQueryWrapper<QuestionDimensionEntity> queryWrapper = new LambdaQueryWrapper<QuestionDimensionEntity>()
                .eq(QuestionDimensionEntity::getQuestionInstanceId, questionInstanceId);
        return questionDimensionService.list(queryWrapper);
    }
}