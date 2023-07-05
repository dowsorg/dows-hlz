package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.annotation.CalcCode;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.entity.ExperimentScoringEntity;
import org.dows.hep.service.ExperimentScoringService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实验计分BIZ
 * todo
 * post/healthIndexScoring/健康指数分数计算
 * post/knowledgeScoring/知识考点分数得分
 * post/treatmentPercentScoring/医疗占比得分
 * post/operateRightScoring/操作准确度得分
 * post//竞争性得分
 * post/totalScoring/总分
 * 此处先计算分数，独立的功能点，供其他bean调用，并将生成的数据保存，用于排行和出报告
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentScoringBiz {

    private final ExperimentGroupBiz experimentGroupBiz;
    private final ExperimentQuestionnaireScoreBiz experimentQuestionnaireScoreBiz;
    private final IdGenerator idGenerator;
    private final ExperimentSettingBiz experimentSettingBiz;

    private final ExperimentScoringService experimentScoringService;

    private void saveOrUpd(String experimentInstanceId, Integer period) {
        // 获取该实验的实验小组
        List<ExperimentGroupResponse> experimentGroupResponses = experimentGroupBiz.listGroup(experimentInstanceId);
        if (CollUtil.isEmpty(experimentGroupResponses)) {
            return;
        }

        // 获取 该实验该期的 `知识答题得分`， 按照实验小组来分组
        Map<String, BigDecimal> questionnaireScoreMap = experimentQuestionnaireScoreBiz.listExptQuestionnaireScore(experimentInstanceId, period);
        // 获取 该实验该期的 `知识答题得分`， 按照实验小组来分组
        // 获取 该实验该期的 `知识答题得分`， 按照实验小组来分组
        // 获取 该实验该期的 `知识答题得分`， 按照实验小组来分组

        // 获取 该实验该期的 `得分记录`
        List<ExperimentScoringEntity> oriEntityList = listExptScoring(experimentInstanceId, period);
        // 按照 `实验小组` 分组
        Map<String, ExperimentScoringEntity> oriGroupIdCollect = new HashMap<>();
        if (CollUtil.isNotEmpty(oriEntityList)) {
            oriGroupIdCollect = oriEntityList.stream()
                    .collect(Collectors.toMap(ExperimentScoringEntity::getExperimentGroupId, item -> item, (v1, v2) -> v1));
        }

        // 存表
        List<ExperimentScoringEntity> result = new ArrayList<>();
        for (ExperimentGroupResponse group : experimentGroupResponses) {
            String experimentGroupId = group.getExperimentGroupId();
            // 该组 `知识考点` 得分
            BigDecimal questionnaireScore = questionnaireScoreMap.get(experimentGroupId);
            String knowledgeScore = String.valueOf(questionnaireScore.floatValue());

            ExperimentScoringEntity oriEntity = oriGroupIdCollect.get(experimentGroupId);
            if (BeanUtil.isEmpty(oriEntity)) {
                ExperimentScoringEntity entity = ExperimentScoringEntity.builder()
                        .experimentScoringId(idGenerator.nextIdStr())
                        .experimentInstanceId(experimentInstanceId)
                        .experimentGroupId(experimentGroupId)
                        .knowledgeScore(knowledgeScore)
                        .scoringCount(1)
                        .periods(period)
                        .build();
                result.add(entity);
            } else {
                Integer scoringCount = oriEntity.getScoringCount() == null ? 1 : oriEntity.getScoringCount();
                oriEntity.setScoringCount(scoringCount);
                oriEntity.setKnowledgeScore(knowledgeScore);
                result.add(oriEntity);
            }
        }
        experimentScoringService.saveOrUpdateBatch(result);
    }

    private List<ExperimentScoringEntity> listExptScoring(String exptInstanceId, Integer period) {
        return experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentScoringEntity::getPeriods, period)
                .list();
    }


    /**
     * 知识考点分数得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepKnowledgeCalculator)
    public void hepKnowledgeScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);
        //todo logic clac

    }

    /**
     * 医疗占比得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepTreatmentPercentCalculator)
    public void hepTreatmentPercentScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);
    }

    /**
     * 操作准确度得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepOperateRightCalculator)
    public void hepOperateRightScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);

    }

    /**
     * 总分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepTotalScoreCalculator)
    public void hepTotalScoring() {

    }


}
