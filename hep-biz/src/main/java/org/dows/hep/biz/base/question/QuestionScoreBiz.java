package org.dows.hep.biz.base.question;

import lombok.RequiredArgsConstructor;
import org.dows.hep.entity.QuestionScoreEntity;
import org.dows.hep.service.QuestionScoreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/5/23 11:44
 */
@Service
@RequiredArgsConstructor
public class QuestionScoreBiz {
    private final QuestionScoreService questionScoreService;

    public Boolean saveOrUpdBatch(List<QuestionScoreEntity> scoreList) {
        if (scoreList == null || scoreList.isEmpty()) {
            return Boolean.FALSE;
        }

        // check
        List<String> idList = scoreList.stream()
                .map(QuestionScoreEntity::getQuestionScoreId)
                .toList();
        List<QuestionScoreEntity> scoreEntityList = questionScoreService.lambdaQuery()
                .in(QuestionScoreEntity::getQuestionScoreId, idList)
                .list();

        // 全部是新增的
        if (scoreEntityList == null || scoreEntityList.isEmpty()) {
            return questionScoreService.saveBatch(scoreList);
        }

        // 新增更新都有的
        Map<String, Long> collect = scoreEntityList.stream()
                .collect(Collectors.toMap(QuestionScoreEntity::getQuestionScoreId, QuestionScoreEntity::getId, (v1, v2) -> v1));
        List<QuestionScoreEntity> entityList = scoreList.stream()
                .peek(score -> {
                    String questionScoreId = score.getQuestionScoreId();
                    Long id = collect.get(questionScoreId);
                    score.setId(id);
                })
                .toList();
        return questionScoreService.saveOrUpdateBatch(entityList);
    }
}
