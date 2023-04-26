package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionEnabledEnum;
import org.dows.hep.api.base.question.QuestionAccessAuthEnum;
import org.dows.hep.api.base.question.QuestionRequiredEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.entity.QuestionSectionItemEntity;
import org.dows.hep.service.QuestionSectionItemService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description 主要用于为 QuestionSectionBiz 提供聚合支持，正常情况不应被别的地方调用
 * @date 2023/4/25 10:36
 */
@RequiredArgsConstructor
@Service
public class QuestionSectionItemBiz {
    private final IdGenerator idGenerator;
    private final QuestionInstanceBiz questionInstanceBiz;
    private final QuestionSectionItemService questionSectionItemService;

    public boolean saveBatch(QuestionSectionRequest questionSection, List<QuestionSectionItemRequest> sectionItemList) {
        if (questionSection == null || sectionItemList == null || sectionItemList.isEmpty()) {
            return Boolean.FALSE;
        }

        // get base-info from question-section
        String appId = questionSection.getAppId();
        String questionSectionId = questionSection.getQuestionSectionId();
        String questionSectionName = questionSection.getName();
        String accountId = questionSection.getAccountId();
        String accountName = questionSection.getAccountName();
        String questionSectionIdentifier = questionSection.getQuestionSectionIdentifier();
        String ver = questionSection.getVer();
        Integer enabled = QuestionEnabledEnum.ENABLED.getCode();
        Integer required = QuestionRequiredEnum.REQUIRED.getCode();

        sectionItemList.forEach(sectionItem -> {
            // get base-info from question
            QuestionRequest questionRequest = sectionItem.getQuestionRequest();
            String questionInstanceId = questionRequest.getQuestionInstanceId();
            String questionTitle = questionRequest.getQuestionTitle();
            String questionDescr = questionRequest.getQuestionDescr();
            String questionSectionItemId = idGenerator.nextIdStr();

            QuestionSectionItemRequest itemRequest = QuestionSectionItemRequest.builder()
                    .questionSectionItemId(questionSectionItemId)
                    .appId(appId)
                    .questionSectionId(questionSectionId)
                    .questionSectionName(questionSectionName)
                    .questionSectionIdentifier(questionSectionIdentifier)
                    .ver(ver)
                    .questionInstanceId(questionInstanceId)
                    .questionTitle(questionTitle)
                    .questionDescr(questionDescr)
                    .enabled(enabled)
                    .required(required)
                    .accountId(accountId)
                    .accountName(accountName)
                    .build();
            QuestionSectionItemEntity questionSectionItemEntity = BeanUtil.copyProperties(itemRequest, QuestionSectionItemEntity.class);

            // save questionInstance if is-private-view
            QuestionAccessAuthEnum bizCode = questionRequest.getBizCode();
            boolean isPrivateView = QuestionAccessAuthEnum.PRIVATE_VIEWING.equals(bizCode);
            if (isPrivateView) {
                questionInstanceId = questionInstanceBiz.saveOrUpdQuestion(questionRequest, bizCode);
                questionSectionItemEntity.setQuestionInstanceId(questionInstanceId);
            }

            // save questionSectionItem
            questionSectionItemService.save(questionSectionItemEntity);
        });

        return Boolean.TRUE;
    }

    public boolean updateBatch(QuestionSectionRequest questionSection, List<QuestionSectionItemRequest> sectionItemList) {
        if (questionSection == null || sectionItemList == null || sectionItemList.isEmpty()) {
            return Boolean.FALSE;
        }

        Map<Boolean, List<QuestionSectionItemRequest>> collect = sectionItemList.stream()
                .collect(Collectors.groupingBy(item -> StrUtil.isBlank(item.getQuestionSectionItemId())));
        List<QuestionSectionItemRequest> emptyIdList = collect.get(Boolean.TRUE);
        List<QuestionSectionItemRequest> existedIdList = collect.get(Boolean.FALSE);

        saveBatch(questionSection, emptyIdList);
        if (existedIdList.isEmpty()) {
            return Boolean.TRUE;
        }
        existedIdList.forEach(item -> {
            QuestionSectionItemEntity questionSectionItemEntity = BeanUtil.copyProperties(item, QuestionSectionItemEntity.class);
            questionSectionItemService.updateById(questionSectionItemEntity);
        });

        return Boolean.TRUE;
    }

    /**
     * @author fhb
     * @description 同步删除问题域时需要考虑问题的访问访问
     * @date 2023/4/25 14:55
     * @param
     * @return
     */
    public boolean  delBatch(String questionSectionId, List<String> questionSectionItemIds) {
        return Boolean.TRUE;
    }
}
