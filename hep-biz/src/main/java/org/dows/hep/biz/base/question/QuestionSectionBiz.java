package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.request.*;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.entity.QuestionSectionEntity;
import org.dows.hep.service.QuestionSectionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lait.zhang
 * @description project descr:问题:问题集[问卷]
 * @date 2023年4月23日 上午9:44:34
 */

@RequiredArgsConstructor
@Service
public class QuestionSectionBiz {

    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionSectionService questionSectionService;
    private final QuestionSectionItemBiz questionSectionItemBiz;
    private final QuestionSectionDimensionBiz questionSectionDimensionBiz;


    /**
     * @param
     * @return
     * @说明: 新增和更新问题集[问卷]
     * @关联表: QuestionSection, QuestionSectionItem, QuestionSectionDimension
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public String saveOrUpdQuestionSection(QuestionSectionRequest questionSection) {
        if (questionSection == null) {
            return "";
        }

        // save base-info
        if (StrUtil.isBlank(questionSection.getQuestionSectionId())) {
            questionSection.setAppId(baseBiz.getAppId());
            questionSection.setQuestionSectionId(baseBiz.getIdStr());
            questionSection.setQuestionSectionIdentifier(baseBiz.getIdStr());
            questionSection.setVer(baseBiz.getLastVer());
            questionSection.setSequence(baseBiz.getSequence());
        }
        QuestionSectionEntity questionSectionEntity = BeanUtil.copyProperties(questionSection, QuestionSectionEntity.class);
        questionSectionService.saveOrUpdate(questionSectionEntity);

        // save section dimension
        List<QuestionSectionDimensionRequest> questionSectionDimensionList = questionSection.getQuestionSectionDimensionList();
        if (questionSectionDimensionList != null && !questionSectionDimensionList.isEmpty()) {
            questionSectionDimensionList.forEach(item -> {
                item.setQuestionSectionId(questionSectionEntity.getQuestionSectionId());
                item.setAppId(questionSectionEntity.getAppId());
                item.setAccountId(questionSectionEntity.getAccountId());
                item.setAccountName(questionSectionEntity.getAccountName());
            });
            questionSectionDimensionBiz.batchSaveOrUpdQSDimension(questionSectionDimensionList);
        }

        // save section item
        String struct = "";
        int questionCount = 0;
        List<QuestionSectionItemRequest> sectionItemList = questionSection.getSectionItemList();
        if (sectionItemList != null && !sectionItemList.isEmpty()) {
            sectionItemList.forEach(item -> {
                item.setQuestionSectionId(questionSectionEntity.getQuestionSectionId());
                item.setAppId(questionSectionEntity.getAppId());
                item.setAccountId(questionSectionEntity.getAccountId());
                item.setAccountName(questionSectionEntity.getAccountName());
            });
            questionCount = sectionItemList.size();
            struct = questionSectionItemBiz.batchSaveOrUpdByMode(sectionItemList, questionSection.getGenerationMode());
        }

        // update struct and questionCount
        LambdaUpdateWrapper<QuestionSectionEntity> updateWrapper = new LambdaUpdateWrapper<QuestionSectionEntity>()
                .eq(QuestionSectionEntity::getQuestionSectionId, questionSectionEntity.getQuestionSectionId())
                .set(QuestionSectionEntity::getQuestionSectionStructure, struct)
                .set(QuestionSectionEntity::getQuestionCount, questionCount);
        questionSectionService.update(updateWrapper);

        return questionSectionEntity.getQuestionSectionId();
    }

    /**
    * @param
    * @return
    * @说明: 列出问题集[问卷]-无分页
    * @关联表: 
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<QuestionSectionResponse> listQuestionSection(List<String> sectionIds) {
        if (sectionIds == null || sectionIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<QuestionSectionEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionEntity>()
                .in(QuestionSectionEntity::getQuestionSectionId, sectionIds);
        List<QuestionSectionEntity> list = questionSectionService.list(queryWrapper);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        return list.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionSectionResponse.class))
                .toList();
    }

    /**
    * @param
    * @return
    * @说明: 根据ID获取详情
    * @关联表: QuestionSection,QuestionSectionItem,QuestionSectionDimension
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public QuestionSectionResponse getQuestionSection(String questionSectionId ) {
        if (StrUtil.isBlank(questionSectionId)) {
            return new QuestionSectionResponse();
        }

        // questionSectionResponse
        LambdaQueryWrapper<QuestionSectionEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionEntity>()
                .eq(QuestionSectionEntity::getQuestionSectionId, questionSectionId);
        QuestionSectionEntity entity = questionSectionService.getOne(queryWrapper);
        QuestionSectionResponse questionSectionResponse = BeanUtil.copyProperties(entity, QuestionSectionResponse.class);

        // questionSectionItemResponse
        List<QuestionSectionItemResponse> itemResponseList = questionSectionItemBiz.listBySectionId(questionSectionId);
        questionSectionResponse.setSectionItemList(itemResponseList);

        // questionSectionDimensionResponse
        List<QuestionSectionDimensionResponse> dimensionResponseList = questionSectionDimensionBiz.listQuestionSectionDimension(questionSectionId);
        questionSectionResponse.setQuestionSectionDimensionList(dimensionResponseList);

        return questionSectionResponse;
    }

    /**
    * @param
    * @return
    * @说明: 删除or批量删除问题集[问卷]
    * @关联表: QuestionSection
    * @工时: 6H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delQuestionSection(List<String> questionSectionIds ) {
        if (questionSectionIds == null || questionSectionIds.isEmpty()) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<QuestionSectionEntity> remWrapper = new LambdaQueryWrapper<QuestionSectionEntity>()
                .in(QuestionSectionEntity::getQuestionSectionId, questionSectionIds);
        return questionSectionService.remove(remWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 启用问题集-题目
     * @关联表: QuestionSection, QuestionSectionItem
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean enabledSectionQuestion(String questionSectionId, String questionSectionItemId) {
        if (StrUtil.isBlank(questionSectionId) || StrUtil.isBlank(questionSectionItemId)) {
            return false;
        }

        return questionSectionItemBiz.enabledSectionQuestion(questionSectionId, questionSectionItemId);
    }

    /**
     * @param
     * @return
     * @说明: 禁用问题集-题目
     * @关联表: QuestionSection, QuestionSectionItem
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean disabledSectionQuestion(String questionSectionId, String questionSectionItemId) {
        if (StrUtil.isBlank(questionSectionId) || StrUtil.isBlank(questionSectionItemId)) {
            return false;
        }

        return questionSectionItemBiz.disabledSectionQuestion(questionSectionId, questionSectionItemId);
    }

    /**
     * @param
     * @return
     * @说明: 删除or批量删除问题集-题目
     * @关联表: QuestionSection, QuestionSectionItem
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean delSectionQuestion(String questionSectionId, List<String> questionSectionItemIds) {
        if (StrUtil.isBlank(questionSectionId) || questionSectionItemIds == null || questionSectionItemIds.isEmpty()) {
            return false;
        }

        questionSectionItemBiz.delBatch(questionSectionId, questionSectionItemIds);
        return Boolean.FALSE;
    }
}