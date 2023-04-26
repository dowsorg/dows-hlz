package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.*;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.entity.QuestionSectionEntity;
import org.dows.hep.entity.QuestionSectionItemEntity;
import org.dows.hep.service.QuestionSectionItemService;
import org.dows.hep.service.QuestionSectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:问题:问题集[问卷]
 * @date 2023年4月23日 上午9:44:34
 */

@RequiredArgsConstructor
@Service
public class QuestionSectionBiz {

    private final QuestionBaseBiz baseBiz;
    private final QuestionSectionService questionSectionService;
    private final QuestionSectionItemBiz questionSectionItemBiz;
    private final QuestionSectionItemService questionSectionItemService;
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
        String questionSectionId = questionSection.getQuestionSectionId();
        if (StrUtil.isBlank(questionSectionId)) {
            questionSectionId = save(questionSection);
        } else {
            update(questionSection);
        }
        return questionSectionId;
    }

    /**
    * @param
    * @return
    * @说明: 分页问题集[问卷]
    * @关联表: QuestionSection,QuestionSectionItem,QuestionSectionDimension
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public QuestionSectionResponse pageQuestionSection(QuestionSectionSearchRequest questionSectionSearch ) {
        return new QuestionSectionResponse();
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
    public List<QuestionSectionResponse> listQuestionSection(QuestionSectionSearchRequest questionSectionSearch ) {
        return new ArrayList<QuestionSectionResponse>();
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
        return new QuestionSectionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 启用问题集[问卷]
    * @关联表: QuestionSection
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean enabledQuestionSection(String questionSectionId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 禁用问题集[问卷]
    * @关联表: QuestionSection
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean disabledQuestionSection(String questionSectionId ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 排序问题集[问卷]
    * @关联表: 
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean sortQuestionSection(String questionSectionId, Integer sequence ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 交换问题集[问卷]
    * @关联表: QuestionSection
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean transposeQuestionSection(String leftSectionId, String rightSectionId ) {
        return Boolean.FALSE;
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
    public Boolean delQuestionSection(String questionSectionIds ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 复制问题集[问卷]
    * @关联表: caseInstance
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String copyQuestionSection(String oriQuestionSectionId ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 自动生成问题集[问卷]
    * @关联表: QuestionSection,QuestionSectionItem,QuestionInstance
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String generateQuestionSectionAutomatic(QuestionnaireGenerateElementsRequest questionnaireGenerateElements ) {
        return new String();
    }

    /**
    * @param
    * @return
    * @说明: 查询问题集-问题
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public QuestionSectionResponse listSectionQuestion(QuestionsInSectionRequest questionsInSection ) {
        return new QuestionSectionResponse();
    }
    /**
    * @param
    * @return
    * @说明: 排序问题集-题目
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean sortSectionQuestion(String questionSectionId, String questionSectionItemId, Integer sequence ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 交换问题集-题目顺序
    * @关联表: QuestionSection,QuestionSectionItem
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean transposeSectionQuestion(String questionSectionId, String leftQuestionSectionItemId, String rightQuestionSectionItemId ) {
        return Boolean.FALSE;
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

        LambdaUpdateWrapper<QuestionSectionItemEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId)
                .set(QuestionSectionItemEntity::getQuestionSectionItemId, questionSectionItemId)
                .set(QuestionSectionItemEntity::getEnabled, 1);
        return questionSectionItemService.update(updateWrapper);
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

        LambdaUpdateWrapper<QuestionSectionItemEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId)
                .set(QuestionSectionItemEntity::getQuestionSectionItemId, questionSectionItemId)
                .set(QuestionSectionItemEntity::getEnabled, 0);
        return questionSectionItemService.update(updateWrapper);
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

    @Transactional
    private String save(QuestionSectionRequest questionSection) {
        if (questionSection == null) {
            return "";
        }

        // base info
        questionSection.setAppId(baseBiz.getAppId());
        questionSection.setQuestionSectionId(baseBiz.getIdStr());
        questionSection.setQuestionSectionIdentifier(baseBiz.getIdStr());
        questionSection.setVer(baseBiz.getVer());
        questionSection.setSequence(baseBiz.getSequence());

        // save section item
        List<QuestionSectionItemRequest> sectionItemList = questionSection.getSectionItemList();
        questionSectionItemBiz.saveBatch(questionSection, sectionItemList);

        // generate question section structure
        String struct = generateStruct(sectionItemList);

        // save section dimension
        List<QuestionSectionDimensionRequest> questionSectionDimensionList = questionSection.getQuestionSectionDimensionList();
        questionSectionDimensionBiz.saveBatch(questionSection, questionSectionDimensionList);

        // save base info
        QuestionSectionEntity questionSectionEntity = BeanUtil.copyProperties(questionSection, QuestionSectionEntity.class);
        questionSectionEntity.setQuestionCount(sectionItemList.size());
        questionSectionEntity.setQuestionSectionStructure(struct);
        questionSectionService.save(questionSectionEntity);

        return questionSection.getQuestionSectionId();
    }

    @Transactional
    private boolean update(QuestionSectionRequest questionSection) {
        if (questionSection == null) {
            return Boolean.FALSE;
        }

        // update section item
        List<QuestionSectionItemRequest> sectionItemList = questionSection.getSectionItemList();
        questionSectionItemBiz.updateBatch(questionSection, sectionItemList);

        // generate question section structure
        String struct = generateStruct(sectionItemList);

        // update section dimension
        List<QuestionSectionDimensionRequest> questionSectionDimensionList = questionSection.getQuestionSectionDimensionList();
        questionSectionDimensionBiz.updateBatch(questionSection, questionSectionDimensionList);

        // update base info
        QuestionSectionEntity questionSectionEntity = BeanUtil.copyProperties(questionSection, QuestionSectionEntity.class);
        questionSectionEntity.setQuestionCount(sectionItemList.size());
        questionSectionEntity.setQuestionSectionStructure(struct);
        questionSectionService.updateById(questionSectionEntity);

        return Boolean.TRUE;
    }

    private String generateStruct(List<QuestionSectionItemRequest> sectionItemList) {
        if (sectionItemList == null || sectionItemList.isEmpty()) {
            return "";
        }

        Map<QuestionTypeEnum, Long> collect = sectionItemList.stream()
                .map(QuestionSectionItemRequest::getQuestionRequest)
                .collect(Collectors.groupingBy(QuestionRequest::getQuestionType, Collectors.counting()));
        if (collect.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Arrays.stream(QuestionTypeEnum.values()).forEach(item -> {
            String name = item.getName();
            Long count = collect.get(item);
            if (count != null && count != 0) {
                sb.append(count)
                        .append(name)
                        .append("/");
            }
        });
        // TODO remove the last 斜杠
        return sb.toString();
    }
}