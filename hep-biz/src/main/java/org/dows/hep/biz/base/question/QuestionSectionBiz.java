package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.enums.QuestionSectionAccessAuthEnum;
import org.dows.hep.api.base.question.enums.QuestionSourceEnum;
import org.dows.hep.api.base.question.request.*;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.entity.QuestionSectionEntity;
import org.dows.hep.service.QuestionSectionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final QuestionInstanceBiz questionInstanceBiz;


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
    @DSTransactional
    public String saveOrUpdQuestionSection(QuestionSectionRequest request, QuestionSectionAccessAuthEnum questionSectionAccessAuthEnum, QuestionSourceEnum questionSourceEnum) {
        if (request == null) {
            return "";
        }

        // check and save base-info
        QuestionSectionEntity questionSectionEntity = convertRequest2Entity(request, questionSectionAccessAuthEnum, questionSourceEnum);
        questionSectionService.saveOrUpdate(questionSectionEntity);

        // save section dimension
        List<QuestionSectionDimensionRequest> questionSectionDimensionList = request.getQuestionSectionDimensionList();
        if (questionSectionDimensionList != null && !questionSectionDimensionList.isEmpty()) {
            questionSectionDimensionBiz.batchSaveOrUpdQSDimension(questionSectionDimensionList, questionSectionEntity.getQuestionSectionId());
        }

        // save section item
        List<QuestionSectionItemRequest> sectionItemList = request.getSectionItemList();
        if (sectionItemList != null && !sectionItemList.isEmpty()) {
            sectionItemList.forEach(item -> {
                QuestionRequest questionRequest = item.getQuestion();
                questionRequest.setAccountId(questionSectionEntity.getAccountId());
                questionRequest.setAccountName(questionSectionEntity.getAccountName());
            });
            questionSectionItemBiz.batchSaveOrUpdByMode(sectionItemList, questionSectionEntity.getQuestionSectionId(), request.getGenerationMode(), questionSourceEnum);
        }

        // update struct and questionCount
        String struct = "";
        int questionCount = 0;
        List<QuestionSectionItemResponse> itemResponseList = listItem(List.of(questionSectionEntity.getQuestionSectionId()));
        if (Objects.nonNull(itemResponseList) && !itemResponseList.isEmpty()) {
            questionCount = itemResponseList.size();
            List<String> questionIds = itemResponseList.stream().map(QuestionSectionItemResponse::getQuestion).map(QuestionResponse::getQuestionInstanceId).toList();
            struct = questionInstanceBiz.getStruct(questionIds);
        }
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
     * @说明: 列出问题集[问卷]-无分页
     * @关联表:
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<QuestionSectionItemResponse> listItem(List<String> sectionIds) {
        if (sectionIds == null || sectionIds.isEmpty()) {
            return new ArrayList<>();
        }

        return questionSectionItemBiz.listBySectionIds(sectionIds);
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
        QuestionSectionEntity entity = getById(questionSectionId);
        QuestionSectionResponse questionSectionResponse = BeanUtil.copyProperties(entity, QuestionSectionResponse.class);

        // questionSectionItemResponse
        List<QuestionSectionItemResponse> itemResponseList = questionSectionItemBiz.listBySectionIds(List.of(questionSectionId));
        questionSectionResponse.setSectionItemList(itemResponseList);

        // questionSectionDimensionResponse
        List<QuestionSectionDimensionResponse> dimensionResponseList = questionSectionDimensionBiz.listQuestionSectionDimension(questionSectionId);
        questionSectionResponse.setQuestionSectionDimensionList(dimensionResponseList);
        if (!dimensionResponseList.isEmpty()) {
            Map<String, List<QuestionSectionDimensionResponse>> collect = dimensionResponseList.stream().collect(Collectors.groupingBy(QuestionSectionDimensionResponse::getDimensionName));
            questionSectionResponse.setQuestionSectionDimensionMap(collect);
        }

        return questionSectionResponse;
    }

    public QuestionSectionEntity getById(String questionSectionId) {
        LambdaQueryWrapper<QuestionSectionEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionEntity>()
                .eq(QuestionSectionEntity::getQuestionSectionId, questionSectionId);
        return questionSectionService.getOne(queryWrapper);
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
    public Boolean delSectionQuestion(QuestionSectionDelItemRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        String questionSectionId = request.getQuestionSectionId();
        List<String> questionSectionItemIds = request.getQuestionSectionItemIds();
        questionSectionItemBiz.delBatch(questionSectionId, questionSectionItemIds);
        return Boolean.FALSE;
    }

    private QuestionSectionEntity convertRequest2Entity(QuestionSectionRequest request, QuestionSectionAccessAuthEnum questionSectionAccessAuthEnum, QuestionSourceEnum questionSourceEnum) {
        QuestionSectionEntity result = QuestionSectionEntity.builder()
                .appId(baseBiz.getAppId())
                .questionSectionId(request.getQuestionSectionId())
                .questionSectionCategId(request.getQuestionSectionCategId())
                .name(request.getName())
                .tips(request.getTips())
                .descr(request.getDescr())
                .sequence(request.getSequence())
                .enabled(request.getEnabled())
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .bizCode(questionSectionAccessAuthEnum.name())
                .source(questionSourceEnum.name())
                .build();

        String uniqueId = result.getQuestionSectionId();
        if (StrUtil.isBlank(uniqueId)) {
            result.setQuestionSectionId(baseBiz.getIdStr());
            result.setQuestionSectionIdentifier(baseBiz.getIdStr());
            result.setVer(baseBiz.getLastVer());
        } else {
            QuestionSectionEntity oriEntity = getById(uniqueId);
            if (BeanUtil.isEmpty(oriEntity)) {
                throw new BizException(QuestionESCEnum.DATA_NULL);
            }
            result.setId(oriEntity.getId());
        }

        return result;
    }
}