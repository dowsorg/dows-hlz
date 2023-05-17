package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.request.QuestionSectionDimensionRequest;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.entity.QuestionSectionDimensionEntity;
import org.dows.hep.service.QuestionSectionDimensionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:问题:问题集-维度
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@Service
public class QuestionSectionDimensionBiz{
    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionSectionDimensionService questionSectionDimensionService;
    /**
    * @param
    * @return
    * @说明: 新增和更新问题集维度
    * @关联表: QuestionSection,QuestionSectionDimension
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean batchSaveOrUpdQSDimension(List<QuestionSectionDimensionRequest> questionSectionDimensionList ) {
        if (questionSectionDimensionList == null || questionSectionDimensionList.isEmpty()) {
            return Boolean.FALSE;
        }

        List<QuestionSectionDimensionEntity> entityList = questionSectionDimensionList.stream()
                .map(item -> {
                    checkBeforeSaveOrUpd(item);
                    return BeanUtil.copyProperties(item, QuestionSectionDimensionEntity.class);
                })
                .toList();
        return questionSectionDimensionService.saveOrUpdateBatch(entityList);
    }

    /**
    * @param
    * @return
    * @说明: 获取问题集所有维度
    * @关联表: QuestionSection,QuestionSectionDimension
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<QuestionSectionDimensionResponse> listQuestionSectionDimension(String questionSectionId ) {
        if (StrUtil.isBlank(questionSectionId)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<QuestionSectionDimensionEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionDimensionEntity>()
                .eq(QuestionSectionDimensionEntity::getQuestionSectionId, questionSectionId);
        List<QuestionSectionDimensionEntity> entityList = questionSectionDimensionService.list(queryWrapper);
        if (entityList == null || entityList.isEmpty()) {
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionSectionDimensionResponse.class))
                .toList();
    }

    /**
     * @param
     * @return
     * @说明: 删除问题集维度
     * @关联表: QuestionSection, QuestionSectionDimension
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean delQuestionSectionDimension(List<String> questionSectionDimensionIds) {
        if (questionSectionDimensionIds == null || questionSectionDimensionIds.isEmpty()) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<QuestionSectionDimensionEntity> remWrapper = new LambdaQueryWrapper<QuestionSectionDimensionEntity>()
                .in(QuestionSectionDimensionEntity::getQuestionSectionDimensionId, questionSectionDimensionIds);
        return questionSectionDimensionService.remove(remWrapper);
    }

    public QuestionSectionDimensionEntity getById(String uniqueId) {
        LambdaQueryWrapper<QuestionSectionDimensionEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionDimensionEntity>()
                .eq(QuestionSectionDimensionEntity::getQuestionSectionDimensionId, uniqueId);
        return questionSectionDimensionService.getOne(queryWrapper);
    }

    private void checkBeforeSaveOrUpd(QuestionSectionDimensionRequest request) {
        String uniqueId = request.getQuestionSectionDimensionId();
        if (StrUtil.isBlank(uniqueId)) {
            request.setQuestionSectionDimensionId(baseBiz.getIdStr());
        } else {
            QuestionSectionDimensionEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException("数据不存在");
            }
            request.setId(entity.getId());
        }
    }
}