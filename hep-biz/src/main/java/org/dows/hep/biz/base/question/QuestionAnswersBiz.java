package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.hep.entity.QuestionAnswersEntity;
import org.dows.hep.service.QuestionAnswersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionAnswersBiz {
    private final QuestionAnswersService questionAnswersService;

    @DSTransactional
    public Boolean saveOrUpdBatch(List<QuestionAnswersEntity> answersList) {
        if (answersList == null || answersList.isEmpty()) {
            return Boolean.FALSE;
        }

        // check
        List<String> idList = answersList.stream().map(QuestionAnswersEntity::getQuestionAnswerId).toList();
        List<QuestionAnswersEntity> answersEntityList = questionAnswersService.lambdaQuery()
                .in(QuestionAnswersEntity::getQuestionAnswerId, idList)
                .list();

        // 都是新增的
        if (answersEntityList == null || answersEntityList.isEmpty()) {
            questionAnswersService.saveBatch(answersList);
            return Boolean.TRUE;
        }

        // 新增更新都有的
        // 数据库中已存在的
        Map<String, QuestionAnswersEntity> collect = answersEntityList.stream()
                .collect(Collectors.toMap(QuestionAnswersEntity::getQuestionAnswerId, v -> v, (v1, v2) -> v1));

        // 新增
        List<QuestionAnswersEntity> addList = answersList.stream()
                .filter(item -> BeanUtil.isEmpty(collect.get(item.getQuestionAnswerId())))
                .toList();
        if (!addList.isEmpty()) {
            questionAnswersService.saveBatch(addList);
        }

        // 更新
        List<QuestionAnswersEntity> updList = answersList.stream()
                .filter(item -> !BeanUtil.isEmpty(collect.get(item.getQuestionAnswerId())))
                .toList();
        if (!updList.isEmpty()) {
            updList.forEach(item -> {
                String questionAnswerId = item.getQuestionAnswerId();
                QuestionAnswersEntity questionAnswersEntity = collect.get(questionAnswerId);
                Long id = questionAnswersEntity.getId();
                item.setId(id);
            });
            questionAnswersService.updateBatchById(updList);
        }

        return Boolean.TRUE;
    }
}
