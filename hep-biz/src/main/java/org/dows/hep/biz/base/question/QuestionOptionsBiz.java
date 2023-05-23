package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.dows.hep.entity.QuestionOptionsEntity;
import org.dows.hep.service.QuestionOptionsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionOptionsBiz {
    private final QuestionOptionsService questionOptionsService;

    public Boolean saveOrUpdBatch(List<QuestionOptionsEntity> optionList) {
        if (optionList == null || optionList.isEmpty()) {
            return Boolean.FALSE;
        }

        // check
        List<String> idList = optionList.stream().map(QuestionOptionsEntity::getQuestionOptionsId).toList();
        List<QuestionOptionsEntity> optionsEntityList = questionOptionsService.lambdaQuery()
                .in(QuestionOptionsEntity::getQuestionOptionsId, idList)
                .list();

        // 都是新增的
        if (optionsEntityList == null || optionsEntityList.isEmpty()) {
            questionOptionsService.saveBatch(optionList);
            return Boolean.TRUE;
        }

        // 新增更新都有的
        Map<String, QuestionOptionsEntity> collect = optionsEntityList.stream()
                .collect(Collectors.toMap(QuestionOptionsEntity::getQuestionOptionsId, v -> v, (v1, v2) -> v1));

        // 新增
        List<QuestionOptionsEntity> addList = optionList.stream()
                .filter(item -> BeanUtil.isEmpty(collect.get(item.getQuestionOptionsId())))
                .toList();
        if (!addList.isEmpty()) {
            questionOptionsService.saveBatch(addList);
        }

        // 更新
        List<QuestionOptionsEntity> updList = optionList.stream()
                .filter(item -> !BeanUtil.isEmpty(collect.get(item.getQuestionOptionsId())))
                .toList();
        if (!updList.isEmpty()) {
            updList.forEach(item -> {
                String questionOptionsId = item.getQuestionOptionsId();
                QuestionOptionsEntity questionOptionsEntity = collect.get(questionOptionsId);
                Long id = questionOptionsEntity.getId();
                item.setId(id);
            });
            questionOptionsService.updateBatchById(updList);
        }

        return Boolean.TRUE;
    }
}
