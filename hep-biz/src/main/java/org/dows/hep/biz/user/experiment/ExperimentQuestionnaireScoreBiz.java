package org.dows.hep.biz.user.experiment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.casus.CaseScoreModeEnum;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.dto.ExptQuestionnaireOptionDTO;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.entity.ExperimentQuestionnaireItemEntity;
import org.dows.hep.service.ExperimentQuestionnaireItemService;
import org.dows.hep.service.ExperimentQuestionnaireService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @version 1.0
 * @description 实验知识答题得分相关
 * @date 2023/6/25 9:55
 **/

@RequiredArgsConstructor
@Service
public class ExperimentQuestionnaireScoreBiz {
    private final ExperimentQuestionnaireService experimentQuestionnaireService;
    private final ExperimentQuestionnaireItemService experimentQuestionnaireItemService;
    private final ExperimentCaseInfoBiz experimentCaseInfoBiz;

    private static final Integer SCORE_GRADE_ALL_ERROR = 0;
    private static final Integer SCORE_GRADE_DEFAULT = SCORE_GRADE_ALL_ERROR;
    private static final Integer SCORE_GRADE_HALF_RIGHT = 1;
    private static final Integer SCORE_GRADE_ALL_RIGHT = 2;

    private String scoreMode;

    public void calculateExptQuestionnaireScore(String experimentInstanceId, Integer period) {
        Assert.notBlank(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(period, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        setScoreMode(experimentInstanceId);

        List<ExperimentQuestionnaireEntity> result = new ArrayList<>();

        // list expt-questionnaire
        List<ExperimentQuestionnaireEntity> questionnaireList = experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentQuestionnaireEntity::getPeriodSequence, period)
                .list();
        if (CollUtil.isEmpty(questionnaireList)) {
            return;
        }
        Map<String, Long> questionnaireIdMapId = questionnaireList.stream()
                .collect(Collectors.toMap(ExperimentQuestionnaireEntity::getExperimentQuestionnaireId, ExperimentQuestionnaireEntity::getId));

        // list expt-questionnaire item
        List<String> exptQuestionnaireIdList = questionnaireList.stream()
                .map(ExperimentQuestionnaireEntity::getExperimentQuestionnaireId)
                .toList();
        List<ExperimentQuestionnaireItemEntity> itemEntityList = experimentQuestionnaireItemService.lambdaQuery()
                .in(ExperimentQuestionnaireItemEntity::getExperimentQuestionnaireId, exptQuestionnaireIdList)
                .list();

        // check item score
        checkItemRight(itemEntityList);

        // compute
        Map<String, List<ExperimentQuestionnaireItemEntity>> collect = itemEntityList.stream()
                .collect(Collectors.groupingBy(ExperimentQuestionnaireItemEntity::getExperimentQuestionnaireId));
        collect.forEach((k, v) -> {
            Float score = computeScore(v);
            ExperimentQuestionnaireEntity entity = ExperimentQuestionnaireEntity.builder()
                    .id(questionnaireIdMapId.get(k))
                    .score(score)
                    .build();
            result.add(entity);
        });

        experimentQuestionnaireService.updateBatchById(result);
    }

    public BigDecimal getExptQuestionnaireScore(String experimentInstanceId, Integer period, String groupId) {
        Assert.notBlank(experimentInstanceId, "获取实验知识答题分数，实验ID不能为空");
        Assert.notNull(period, "获取实验知识答题分数，实验期数不能为空");

        List<ExperimentQuestionnaireEntity> list = experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentQuestionnaireEntity::getPeriodSequence, period)
                .eq(ExperimentQuestionnaireEntity::getExperimentGroupId, groupId)
                .list();
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }

        double average = list.stream()
                .map(ExperimentQuestionnaireEntity::getScore)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.00);
        return BigDecimal.valueOf(average);
    }

    // 计分，在得出结果默认10位小数
    private Float computeScore(List<ExperimentQuestionnaireItemEntity> itemList) {
        float zeroScore = 0.00f;
        float fullScore = 100.00F;
        if (CollUtil.isEmpty(itemList)) {
            return zeroScore;
        }

        // get unit-score
        int totalNum = itemList.size();
        float unitScore = (float) NumberUtil.div(fullScore, totalNum);

        // get right-num
        int allRightNum = 0;
        int halfRightNum = 0;
        for (ExperimentQuestionnaireItemEntity item : itemList) {
            int scoreGrade = item.getScoreGrade();
            if (scoreGrade == SCORE_GRADE_ALL_RIGHT) {
                allRightNum += 1;
            } else if (scoreGrade == SCORE_GRADE_HALF_RIGHT) {
                halfRightNum += 1;
            }
        }

        // compute
        if (StrUtil.isBlank(this.scoreMode)) {
            throw new BizException(ExperimentESCEnum.DATA_NULL);
        }
        if (allRightNum == totalNum) {
            return fullScore;
        }
        if (allRightNum == 0 && halfRightNum == 0) {
            return zeroScore;
        }
        if (CaseScoreModeEnum.STRICT.name().equals(this.scoreMode)) {
            return allRightNum * unitScore;
        }
        if (CaseScoreModeEnum.HALF.name().equals(this.scoreMode)) {
            return allRightNum * unitScore + halfRightNum * (float) NumberUtil.div(unitScore, 2);
        }
        return zeroScore;
    }

    private void checkItemRight(List<ExperimentQuestionnaireItemEntity> itemList) {
        List<ExperimentQuestionnaireItemEntity> result = new ArrayList<>();
        for (ExperimentQuestionnaireItemEntity item : itemList) {
            Integer scoreGrade = SCORE_GRADE_DEFAULT;

            // right value
            String rightValue = item.getRightValue();
            List<ExptQuestionnaireOptionDTO> rightValueList = JSONUtil.toList(rightValue, ExptQuestionnaireOptionDTO.class);
            if (CollUtil.isEmpty(rightValueList)) {
                break;
            }
            List<String> rightIdList = rightValueList.stream().map(ExptQuestionnaireOptionDTO::getId).toList();

            // question result
            String questionResult = item.getQuestionResult();
            if (StrUtil.isBlank(questionResult)) {
                break;
            }
            String[] resultArr = questionResult.split(",");
            List<String> resultIdList = List.of(resultArr);

            // check
            List<String> commonElements = findCommonElements(rightIdList, resultIdList);
            if (commonElements.size() == rightIdList.size()) {
                scoreGrade = SCORE_GRADE_ALL_RIGHT;
            }
            if (commonElements.size() > 0) {
                scoreGrade = SCORE_GRADE_HALF_RIGHT;
            }
            if (commonElements.size() == 0) {
                scoreGrade = SCORE_GRADE_ALL_ERROR;
            }

            item.setScoreGrade(scoreGrade);
            ExperimentQuestionnaireItemEntity itemEntity = ExperimentQuestionnaireItemEntity.builder()
                    .id(item.getId())
                    .scoreGrade(scoreGrade)
                    .build();
            result.add(itemEntity);
        }

        experimentQuestionnaireItemService.updateBatchById(result);
    }

    private void setScoreMode(String experimentInstanceId) {
        this.scoreMode = experimentCaseInfoBiz.getQuestionnaireScoreMode(experimentInstanceId);
    }

    private static List<String> findCommonElements(List<String> list1, List<String> list2) {
        Set<String> set1 = new HashSet<>(list1);
        Set<String> set2 = new HashSet<>(list2);
        set1.retainAll(set2);
        return new ArrayList<>(set1);
    }

    public static void main(String[] args) {
        System.out.println(NumberUtil.div(10, 3, 2));
    }
}
