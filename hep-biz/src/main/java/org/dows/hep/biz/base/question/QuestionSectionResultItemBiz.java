package org.dows.hep.biz.base.question;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.dto.QuestionResultRecordDTO;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.request.QuestionSectionResultItemRequest;
import org.dows.hep.entity.QuestionSectionResultItemEntity;
import org.dows.hep.service.QuestionSectionResultItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/6/1 14:40
 */
@Service
@RequiredArgsConstructor
public class QuestionSectionResultItemBiz {
    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionSectionResultItemService questionSectionResultItemService;

    public Boolean saveOrUpdBatch(List<QuestionSectionResultItemRequest> requests, String questionSectionResultId) {
        if (StrUtil.isBlank(questionSectionResultId) || CollUtil.isEmpty(requests)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        // check
        List<String> idList = requests.stream()
                .map(QuestionSectionResultItemRequest::getQuestionSectionResultItemId)
                .filter(StrUtil::isNotBlank)
                .toList();
        List<QuestionSectionResultItemEntity> oriItemEntityList = questionSectionResultItemService.lambdaQuery()
                .in(QuestionSectionResultItemEntity::getQuestionSectionResultItemId, idList)
                .list();

        // convert requests 2 entity-list
        List<QuestionSectionResultItemEntity> itemEntityList = new ArrayList<>();
        requests.forEach(request -> {
            QuestionSectionResultItemEntity itemEntity = QuestionSectionResultItemEntity.builder()
                    .questionSectionResultItemId(request.getQuestionSectionResultItemId())
                    .questionInstanceId(request.getQuestionInstanceId())
                    .questionSectionResultId(questionSectionResultId)
                    .build();
            String questionSectionResultItemId = itemEntity.getQuestionSectionResultItemId();
            if (StrUtil.isBlank(questionSectionResultItemId)) {
                itemEntity.setQuestionSectionResultItemId(baseBiz.getIdStr());
            }
            itemEntityList.add(itemEntity);
        });

        // 都是新增的
        if (CollUtil.isEmpty(oriItemEntityList)) {
            return questionSectionResultItemService.saveBatch(itemEntityList);
        }

        // 新增和更新都有的
        Map<String, Long> collect = itemEntityList.stream()
                .collect(Collectors.toMap(QuestionSectionResultItemEntity::getQuestionSectionResultItemId, QuestionSectionResultItemEntity::getId, (v1, v2) -> v1));
        List<QuestionSectionResultItemEntity> entityList = itemEntityList.stream()
                .peek(option -> {
                    String questionSectionResultItemId = option.getQuestionSectionResultItemId();
                    Long id = collect.get(questionSectionResultItemId);
                    option.setId(id);
                })
                .toList();

        return questionSectionResultItemService.saveOrUpdateBatch(entityList);
    }

    public QuestionResultRecordDTO listQuestionResult(String questionSectionResultId) {
        if (StrUtil.isBlank(questionSectionResultId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        QuestionResultRecordDTO result = new QuestionResultRecordDTO();
        List<QuestionSectionResultItemEntity> entityList = questionSectionResultItemService.lambdaQuery()
                .eq(QuestionSectionResultItemEntity::getQuestionSectionResultId, questionSectionResultId)
                .list();
        if (CollUtil.isEmpty(entityList)) {
            return result;
        }

        Map<String, String> resultMap = new HashMap<>();
        entityList.forEach(entity -> {
            resultMap.put(entity.getQuestionInstanceId(), entity.getAnswerValue());
        });
        result.setQuestionResultMap(resultMap);

        return result;
    }
}
