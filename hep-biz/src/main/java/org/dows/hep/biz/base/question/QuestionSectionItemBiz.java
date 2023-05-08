package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionEnabledEnum;
import org.dows.hep.api.base.question.QuestionSectionGenerationModeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.entity.QuestionSectionItemEntity;
import org.dows.hep.service.QuestionSectionItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    public String batchSaveOrUpdByMode(List<QuestionSectionItemRequest> itemList, QuestionSectionGenerationModeEnum generationModeEnum) {
        if (itemList == null || itemList.isEmpty()) {
            return "";
        }

        String struct = "";
        switch (generationModeEnum) {
            case SELECT -> struct = batchSaveOrUpdSelectMode(itemList);
            case ADD_NEW -> struct = batchSaveOrUpdAddNewMode(itemList);
            default -> {
            }
        }
        return struct;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/6 10:42
     */
    public List<QuestionSectionItemResponse> listBySectionId(String questionSectionId) {
        if (StrUtil.isBlank(questionSectionId)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<QuestionSectionItemEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionItemEntity>()
                .eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId);
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
        LambdaUpdateWrapper<QuestionSectionItemEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId)
                .set(QuestionSectionItemEntity::getQuestionSectionItemId, questionSectionItemId)
                .set(QuestionSectionItemEntity::getEnabled, QuestionEnabledEnum.ENABLED.getCode());
        return questionSectionItemService.update(updateWrapper);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 禁用
     * @date 2023/4/25 14:55
     */
    public Boolean disabledSectionQuestion(String questionSectionId, String questionSectionItemId) {
        LambdaUpdateWrapper<QuestionSectionItemEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId)
                .set(QuestionSectionItemEntity::getQuestionSectionItemId, questionSectionItemId)
                .set(QuestionSectionItemEntity::getEnabled, QuestionEnabledEnum.DISABLED.getCode());
        return questionSectionItemService.update(updateWrapper);
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

    private String batchSaveOrUpdSelectMode(List<QuestionSectionItemRequest> itemRequestList) {
        if (itemRequestList == null || itemRequestList.isEmpty()) {
            return "";
        }

        return batchSaveOrUpd(itemRequestList, QuestionRequest::getQuestionInstanceId);
    }

    private String batchSaveOrUpdAddNewMode(List<QuestionSectionItemRequest> itemRequestList) {
        if (itemRequestList == null || itemRequestList.isEmpty()) {
            return "";
        }

        return batchSaveOrUpd(itemRequestList, questionInstanceBiz::saveOrUpdQuestion);
    }

    private String batchSaveOrUpd(List<QuestionSectionItemRequest> itemRequestList, Function<QuestionRequest, String> function) {
        // save or update item
        int sequence = 0;
        List<QuestionSectionItemEntity> entityList = new ArrayList<>();
        for (QuestionSectionItemRequest item : itemRequestList) {
            QuestionSectionItemEntity itemEntity = BeanUtil.copyProperties(item, QuestionSectionItemEntity.class);
            String questionSectionItemId = itemEntity.getQuestionSectionItemId();
            if (StrUtil.isBlank(questionSectionItemId)) {
                itemEntity.setQuestionSectionItemId(baseBiz.getIdStr());
                itemEntity.setSequence(sequence++);
                itemEntity.setEnabled(QuestionEnabledEnum.ENABLED.getCode());
            }
            QuestionRequest questionRequest = item.getQuestionRequest();
            String questionInstanceId = function.apply(questionRequest);
            itemEntity.setQuestionInstanceId(questionInstanceId);

            entityList.add(itemEntity);
        }
        questionSectionItemService.saveOrUpdateBatch(entityList);

        // get struct
        List<String> questionIdList = itemRequestList.stream()
                .map(QuestionSectionItemRequest::getQuestionRequest)
                .map(QuestionRequest::getQuestionInstanceId)
                .toList();
        return questionInstanceBiz.getStruct(questionIdList);
    }
}
