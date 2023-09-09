package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsCalculateCompetitiveScoreRequestRs;
import org.dows.hep.api.base.indicator.response.GroupCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateCompetitiveScoreRsResponse;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/9/8 20:32
 */
@Component
@RequiredArgsConstructor
public class EvalCompetitiveScoreBiz {

    private final ExperimentPersonCache experimentPersonCache;
    private final EvalPersonCache evalPersonCache;

    public RsCalculateCompetitiveScoreRsResponse evalCompetitiveScore(RsCalculateCompetitiveScoreRequestRs rsCalculateCompetitiveScoreRequestRs) {
        List<GroupCompetitiveScoreRsResponse> groupCompetitiveScoreRsResponseList = new ArrayList<>();
        String experimentId = rsCalculateCompetitiveScoreRequestRs.getExperimentId();
        Integer periods = rsCalculateCompetitiveScoreRequestRs.getPeriods();
        Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap = new HashMap<>();


        Map<String, ExperimentPersonEntity> kExperimentPersonIdVCasePersonIdMap = experimentPersonCache.getMapPersons(experimentId);
        Map<String, List<ExperimentPersonEntity>> kExperimentGroupIdVExperimentPersonEntityListMap = experimentPersonCache.getMapGroupPersons(experimentId);

        Map<String,BigDecimal> mapPersonXHp=new HashMap<>();
        Map<String, BigDecimal> kCasePersonIdVMinHealthScoreMap = new HashMap<>();
        Map<String, BigDecimal> kCasePersonIdVMaxHealthScoreMap = new HashMap<>();
        kExperimentPersonIdVCasePersonIdMap.values().forEach(person->{
            String casePersonId = person.getCasePersonId();
            if(ShareUtil.XObject.isEmpty(casePersonId)){
                return;
            }
            EvalPersonOnceHolder evalHolder=evalPersonCache.getCurHolder(person.getExperimentPersonId());
            String currentVal = evalHolder.getSysIndicator(EnumIndicatorType.HEALTH_POINT).getCurVal();
            BigDecimal currentBigDecimal = BigDecimalUtil.tryParseDecimalElseZero(currentVal);
            mapPersonXHp.put(person.getExperimentPersonId(),currentBigDecimal);
            BigDecimal minBigDecimal = kCasePersonIdVMinHealthScoreMap.get(casePersonId);
            if (Objects.isNull(minBigDecimal)) {
                minBigDecimal = currentBigDecimal;
            } else {
                if (currentBigDecimal.compareTo(minBigDecimal) < 0) {
                    minBigDecimal = currentBigDecimal;
                }
            }
            kCasePersonIdVMinHealthScoreMap.put(casePersonId, minBigDecimal);
            BigDecimal maxBigDecimal = kCasePersonIdVMaxHealthScoreMap.get(casePersonId);
            if (Objects.isNull(maxBigDecimal)) {
                maxBigDecimal = currentBigDecimal;
            } else {
                if (currentBigDecimal.compareTo(maxBigDecimal) > 0) {
                    maxBigDecimal = currentBigDecimal;
                }
            }
            kCasePersonIdVMaxHealthScoreMap.put(casePersonId, maxBigDecimal);
        });

        kExperimentGroupIdVExperimentPersonEntityListMap.forEach((experimentGroupId, experimentPersonEntityList) -> {
            List<BigDecimal> valuedScores=new ArrayList<>();
            experimentPersonEntityList.forEach(experimentPersonEntity -> {
                String experimentPersonId = experimentPersonEntity.getExperimentPersonId();
                String casePersonId = experimentPersonEntity.getCasePersonId();
                BigDecimal minHealthScore = kCasePersonIdVMinHealthScoreMap.get(casePersonId);
                BigDecimal maxHealthScore = kCasePersonIdVMaxHealthScoreMap.get(casePersonId);
                ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = PersonIndicatorIdCache.Instance().getSysIndicator(experimentPersonId,EnumIndicatorType.HEALTH_POINT);
                String defHealthScore = experimentIndicatorInstanceRsEntity.getDef();
                BigDecimal curHealthScore=mapPersonXHp.get(experimentPersonId);

                BigDecimal resultHealthScore = evalCompetitiveScore(curHealthScore, BigDecimalUtil.valueOf(defHealthScore), minHealthScore, maxHealthScore);
                if(ShareUtil.XObject.notEmpty(resultHealthScore)) {
                    valuedScores.add(resultHealthScore);
                }
            });
            kExperimentGroupIdVGroupCompetitiveScoreMap.put(experimentGroupId, getAvg(valuedScores));
        });
        kExperimentGroupIdVGroupCompetitiveScoreMap.forEach((experimentGroupId, groupCompetitiveScore) -> {
            groupCompetitiveScoreRsResponseList.add(GroupCompetitiveScoreRsResponse
                    .builder()
                    .experimentGroupId(experimentGroupId)
                    .groupCompetitiveScore(groupCompetitiveScore)
                    .build());
        });
        return RsCalculateCompetitiveScoreRsResponse
                .builder()
                .groupCompetitiveScoreRsResponseList(groupCompetitiveScoreRsResponseList)
                .build();
    }
    private BigDecimal evalCompetitiveScore(BigDecimal currentHealthScore, BigDecimal defHealthScore, BigDecimal minHealthScore, BigDecimal maxHealthScore) {
        if(ShareUtil.XObject.anyEmpty(currentHealthScore,defHealthScore)){
            return null;
        }

        BigDecimal resultBigDecimal = null;
        if (currentHealthScore.compareTo(defHealthScore) >= 0) {
            if (maxHealthScore.compareTo(defHealthScore) == 0) {
                resultBigDecimal = currentHealthScore;
            } else {
                resultBigDecimal = (currentHealthScore.subtract(defHealthScore).divide(maxHealthScore.subtract(defHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100 - 60))).add(BigDecimal.valueOf(60));
            }
        } else {
            if (minHealthScore.compareTo(defHealthScore) == 0) {
                resultBigDecimal = currentHealthScore;
            } else {
                resultBigDecimal = currentHealthScore.subtract(minHealthScore).divide(defHealthScore.subtract(minHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(60));
            }
        }
        return resultBigDecimal;
    }
    private BigDecimal getAvg(List<BigDecimal> values){
        if(ShareUtil.XObject.isEmpty(values)){
            return BigDecimal.ONE;
        }
        BigDecimalOptional total=BigDecimalOptional.create();
        values.forEach(i->total.add(i));
        return total.div(BigDecimalUtil.valueOf(values.size()), 2, RoundingMode.DOWN).getValue();

    }

}
