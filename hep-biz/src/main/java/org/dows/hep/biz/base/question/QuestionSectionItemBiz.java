package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.enums.*;
import org.dows.hep.api.base.question.request.QuestionClonedRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.entity.QuestionSectionItemEntity;
import org.dows.hep.service.QuestionSectionItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/4/25 10:36
 */
@RequiredArgsConstructor
@Service
public class QuestionSectionItemBiz {

    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionInstanceBiz questionInstanceBiz;
    private final QuestionSectionItemService questionSectionItemService;

    /**
     * @param
     * @return
     * @author fhb
     * @description 批量保存或更新，返回问题结构
     * @date 2023/4/25 14:55
     */
    public boolean batchSaveOrUpdByMode(List<QuestionSectionItemRequest> itemList, String questionSectionId, QuestionSectionGenerationModeEnum generationModeEnum, QuestionSourceEnum questionSourceEnum) {
        if (itemList == null || itemList.isEmpty()) {
            return Boolean.FALSE;
        }
        if (generationModeEnum == null) {
            throw new BizException(QuestionESCEnum.QUESTION_SECTION_GENERATION_MODE_NON_NULL);
        }

        boolean res = false;
        switch (generationModeEnum) {
            case SELECT -> res = batchSaveSelectMode(itemList, questionSectionId, questionSourceEnum);
            case ADD_NEW -> res = batchSaveOrUpdAddNewMode(itemList, questionSectionId, questionSourceEnum);
            default -> {
            }
        }
        return res;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/6 10:42
     */
    public List<QuestionSectionItemResponse> listBySectionIds(List<String> questionSectionIds) {
        if (questionSectionIds == null || questionSectionIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<QuestionSectionItemEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionItemEntity>()
                .eq(QuestionSectionItemEntity::getEnabled, QuestionEnabledEnum.ENABLED.getCode())
                .in(QuestionSectionItemEntity::getQuestionSectionId, questionSectionIds);
        List<QuestionSectionItemEntity> itemList = questionSectionItemService.list(queryWrapper);
        if (itemList == null || itemList.isEmpty()) {
            return new ArrayList<>();
        }

        return itemList.stream()
                .map(item -> {
                    QuestionSectionItemResponse itemResponse = BeanUtil.copyProperties(item, QuestionSectionItemResponse.class);
                    QuestionResponse question = questionInstanceBiz.getQuestion(item.getQuestionInstanceId());
                    itemResponse.setQuestionResponse(question);
                    return itemResponse;
                })
                .toList();
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 启用
     * @date 2023/4/25 14:55
     */
    public Boolean enabledSectionQuestion(String questionSectionId, String questionSectionItemId) {
        return changeEnable(questionSectionId, questionSectionItemId, QuestionEnabledEnum.ENABLED);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 禁用
     * @date 2023/4/25 14:55
     */
    public Boolean disabledSectionQuestion(String questionSectionId, String questionSectionItemId) {
        return changeEnable(questionSectionId, questionSectionItemId, QuestionEnabledEnum.DISABLED);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 批量删除
     * @date 2023/4/25 14:55
     */
    public boolean delBatch(String questionSectionId, List<String> questionSectionItemIds) {
        LambdaQueryWrapper<QuestionSectionItemEntity> remWrapper = new LambdaQueryWrapper<QuestionSectionItemEntity>()
                .eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId)
                .in(QuestionSectionItemEntity::getQuestionSectionItemId, questionSectionItemIds);
        return questionSectionItemService.remove(remWrapper);
    }

    // 选择模式的不可以更新题目，仅可以新增
    private boolean batchSaveSelectMode(List<QuestionSectionItemRequest> itemRequestList, String questionSectionId, QuestionSourceEnum questionSourceEnum) {
        if (StrUtil.isBlank(questionSectionId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        if (itemRequestList == null || itemRequestList.isEmpty()) {
            return Boolean.FALSE;
        }

        List<QuestionSectionItemRequest> addList = itemRequestList.stream()
                .filter(item -> StrUtil.isBlank(item.getQuestionSectionItemId()))
                .toList();
        if (addList.isEmpty()) {
            return Boolean.FALSE;
        }

        List<QuestionSectionItemEntity> itemList = new ArrayList<>();
        addList.forEach(item -> {
            QuestionRequest questionRequest = item.getQuestionRequest();
            QuestionClonedRequest clonedRequest = QuestionClonedRequest.builder()
                    .oriQuestionInstanceId(questionRequest.getQuestionInstanceId())
                    .build();
            String questionInstanceId = questionInstanceBiz.cloneQue2NewQue(clonedRequest, QuestionAccessAuthEnum.PRIVATE_VIEWING, questionSourceEnum);

            QuestionSectionItemEntity entity = QuestionSectionItemEntity.builder()
                    .questionSectionItemId(baseBiz.getIdStr())
                    .questionSectionId(questionSectionId)
                    .required(item.getRequired())
                    .questionInstanceId(questionInstanceId)
                    .enabled(QuestionEnabledEnum.ENABLED.getCode())
                    .build();
            itemList.add(entity);
        });

        questionSectionItemService.saveBatch(itemList);
        return Boolean.TRUE;
    }

    // 新增模式的可以新增题目，也可以更新
    private boolean batchSaveOrUpdAddNewMode(List<QuestionSectionItemRequest> itemRequestList, String questionSectionId, QuestionSourceEnum questionSourceEnum) {
        if (StrUtil.isBlank(questionSectionId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        if (itemRequestList == null || itemRequestList.isEmpty()) {
            return Boolean.FALSE;
        }

        // addList
        List<QuestionSectionItemRequest> addList = itemRequestList.stream()
                .filter(item -> StrUtil.isBlank(item.getQuestionSectionItemId()))
                .toList();
        if (!addList.isEmpty()) {
            List<QuestionSectionItemEntity> addItemList = new ArrayList<>();
            addList.forEach(item -> {
                QuestionRequest questionRequest = item.getQuestionRequest();
                String questionInstanceId = questionInstanceBiz.saveOrUpdQuestion(questionRequest, QuestionAccessAuthEnum.PRIVATE_VIEWING, questionSourceEnum);

                QuestionSectionItemEntity entity = QuestionSectionItemEntity.builder()
                        .questionSectionItemId(baseBiz.getIdStr())
                        .required(item.getRequired())
                        .enabled(item.getEnabled())
                        .questionSectionId(questionSectionId)
                        .questionInstanceId(questionInstanceId)
                        .build();
                addItemList.add(entity);
            });
            questionSectionItemService.saveBatch(addItemList);
        }


        // updList
        List<QuestionSectionItemRequest> updList = itemRequestList.stream()
                .filter(item -> !StrUtil.isBlank(item.getQuestionSectionItemId()))
                .toList();
        if (!updList.isEmpty()) {

            List<String> ids = updList.stream().map(QuestionSectionItemRequest::getQuestionSectionItemId).toList();
            List<QuestionSectionItemEntity> entityList = listByIds(ids);
            if (!entityList.isEmpty()) {
                Map<String, Long> collect = entityList.stream().collect(Collectors.toMap(QuestionSectionItemEntity::getQuestionSectionItemId, QuestionSectionItemEntity::getId));
                List<QuestionSectionItemEntity> updItemList = new ArrayList<>();
                updList.forEach(item -> {
                    QuestionRequest questionRequest = item.getQuestionRequest();
                    String questionInstanceId = questionInstanceBiz.saveOrUpdQuestion(questionRequest, QuestionAccessAuthEnum.PRIVATE_VIEWING, questionSourceEnum);

                    QuestionSectionItemEntity entity = QuestionSectionItemEntity.builder()
                            .id(collect.get(item.getQuestionSectionItemId()))
                            .questionSectionItemId(item.getQuestionSectionItemId())
                            .required(item.getRequired())
                            .enabled(item.getEnabled())
                            .questionSectionId(questionSectionId)
                            .questionInstanceId(questionInstanceId)
                            .build();
                    updItemList.add(entity);
                });
                questionSectionItemService.updateBatchById(updItemList);
            }
        }

        return Boolean.TRUE;
    }

    private List<QuestionSectionItemEntity> listByIds(List<String> ids) {
        LambdaQueryWrapper<QuestionSectionItemEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionItemEntity>()
                .in(QuestionSectionItemEntity::getQuestionSectionItemId, ids);
        return questionSectionItemService.list(queryWrapper);
    }

    private QuestionSectionItemEntity getById(String uniqueId) {
        LambdaQueryWrapper<QuestionSectionItemEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionItemEntity>()
                .eq(QuestionSectionItemEntity::getQuestionSectionItemId, uniqueId);
        return questionSectionItemService.getOne(queryWrapper);
    }

    private boolean changeEnable(String questionSectionId, String questionSectionItemId, QuestionEnabledEnum questionEnabledEnum) {
        LambdaUpdateWrapper<QuestionSectionItemEntity> updateWrapper = new LambdaUpdateWrapper<QuestionSectionItemEntity>()
                .eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId)
                .eq(QuestionSectionItemEntity::getQuestionSectionItemId, questionSectionItemId)
                .set(QuestionSectionItemEntity::getEnabled, questionEnabledEnum.getCode());
        return questionSectionItemService.update(updateWrapper);
    }
}
