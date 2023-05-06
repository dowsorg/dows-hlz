package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionEnabledEnum;
import org.dows.hep.api.base.question.QuestionSectionGenerationModeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
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

    private final BaseQuestionDomainBiz baseBiz;
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

        return batchSaveOrUpd(itemRequestList, questionInstanceBiz::saveOrUpd);
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

    /**
     * @author fhb
     * @description 同步删除问题域时需要考虑问题的访问访问
     * @date 2023/4/25 14:55
     * @param
     * @return
     */
    public boolean  delBatch(String questionSectionId, List<String> questionSectionItemIds) {
        LambdaQueryWrapper<QuestionSectionItemEntity> remWrapper = new LambdaQueryWrapper<QuestionSectionItemEntity>()
                .eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId)
                .in(QuestionSectionItemEntity::getQuestionSectionItemId, questionSectionItemIds);
        return questionSectionItemService.remove(remWrapper);
    }
}
