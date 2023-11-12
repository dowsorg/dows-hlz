package org.dows.hep.biz.spel;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.*;
import org.dows.hep.biz.eval.ExperimentPersonCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.spel.loaders.FromSnapshotLoader;
import org.dows.hep.biz.spel.meta.ISpelLoad;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.snapshot.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wuzl
 * @date : 2023/9/13 23:38
 */
@Component
@Slf4j
public class ExperimentSpelCache extends BaseLoadingCache<ExperimentCacheKey, ExperimentSpelCache.CacheData>
        implements ISpelLoad {

    private static volatile ExperimentSpelCache s_instance;

    public static ExperimentSpelCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=2;
    protected final static int CACHEMaxSize=35;
    protected final static int CACHEExpireSeconds=60*60*24*7;

    private ExperimentSpelCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

    @Autowired
    private FromSnapshotLoader fromSnapshotLoader;

    @Autowired
    private ExperimentIndicatorInstanceRsDao experimentIndicatorInstanceRsDao;

    @Autowired
    private SnapCrowdsInstanceDao snapCrowdsInstanceDao;

    @Autowired
    private SnapRiskModelDao snapRiskModelDao;

    @Autowired
    private SnapTreatItemDao snapTreatItemDao;

    @Autowired
    private SnapIndicatorJudgeGoalDao snapIndicatorJudgeGoalDao;

    @Autowired
    private SnapIndicatorJudgeHealthGuidanceDao snapIndicatorJudgeHealthGuidanceDao;

    @Autowired
    private SnapIndicatorJudgeHealthProblemDao snapIndicatorJudgeHealthProblemDao;

    @Autowired
    private SnapIndicatorJudgeRiskFactorDao snapIndicatorJudgeRiskFactorDao;


    private final static String APPId="3";



    @Override
    public SpelInput withReasonId(String experimentId, String experimentPersonId, String reasonId, Integer source,Integer... sources) {
        ExperimentCacheKey cacheKey=ExperimentCacheKey.create(APPId,experimentId);
        CacheData cached=loadingCache().get(cacheKey);
        if (isRawExpression(source)){
            experimentPersonId=null;
        }
        if(null==cached){
            return fromSnapshotLoader.withReasonId(experimentId,experimentPersonId,reasonId,source,sources);
        }
        final SpelCacheKey spelCacheKey=SpelCacheKey.create(experimentId,experimentPersonId,reasonId,source);
        List<SpelInput> inputs=cached.get(spelCacheKey);
        if(ShareUtil.XObject.notEmpty(inputs)){
            return inputs.get(0);
        }
        inputs=Optional.ofNullable(fromSnapshotLoader.withReasonId(experimentId,experimentPersonId,List.of(reasonId),source))
                        .orElse(List.of(new SpelInput(source).setReasonId(reasonId)));
        cached.mapReasonInput.put(spelCacheKey,inputs);
        return inputs.size()>0?inputs.get(0):null;
    }

    @Override
    public List<SpelInput> withReasonId(String experimentId, String experimentPersonId, Collection<String> reasonIds, Integer source,Integer... sources) {
        ExperimentCacheKey cacheKey=ExperimentCacheKey.create(APPId,experimentId);
        CacheData cached=loadingCache().get(cacheKey);
        if (isRawExpression(source)){
            experimentPersonId=null;
        }
        if(null==cached){
            return fromSnapshotLoader.withReasonId(experimentId,experimentPersonId,reasonIds,source,sources);
        }
        final List<SpelInput> rst=new ArrayList<>();
        final List<String> missReasonIds=new ArrayList<>();
        for(String reasonId:reasonIds){
            SpelCacheKey spelCacheKey=SpelCacheKey.create(experimentId,experimentPersonId,reasonId,source);
            List<SpelInput> inputs=cached.get(spelCacheKey);
            if(ShareUtil.XObject.notEmpty(inputs)){
                rst.addAll(inputs);
            }else{
                missReasonIds.add(reasonId);
            }
        }
        if(ShareUtil.XObject.notEmpty(missReasonIds)) {
            rst.addAll(fillInput(cached, experimentId, experimentPersonId, missReasonIds, EnumIndicatorExpressionSource.of(source),sources));
        }
        return rst;
    }

    @Override
    public SpelInput withExpressionId(String experimentId, String experimentPersonId, String expressionId, Integer source) {
        return fromSnapshotLoader.withExpressionId(experimentId,experimentPersonId,expressionId,source);
    }

    @Override
    public List<SpelInput> withExpressionId(String experimentId, String experimentPersonId, Collection<String> expressionIds, Integer source) {
        return fromSnapshotLoader.withExpressionId(experimentId, experimentPersonId, expressionIds, source);
    }

    @Override
    protected CacheData load(ExperimentCacheKey key) {
        CacheData rst=new CacheData();
        StringBuilder sb=new StringBuilder();
        long ts=logCostTime(sb, "SPELTRACE--load--");
        try {
            final String experimentId = key.getExperimentInstanceId();
            final Set<String> personIds = ExperimentPersonCache.Instance().getPersondIdSet(experimentId, null);
            List<ExperimentIndicatorInstanceRsEntity> rowsIndicators = experimentIndicatorInstanceRsDao.getByExperimentId(experimentId,
                    ExperimentIndicatorInstanceRsEntity::getExperimentPersonId,
                    ExperimentIndicatorInstanceRsEntity::getCaseIndicatorInstanceId);
            Map<String, List<String>> mapIndicatorIds = new HashMap<>();
            rowsIndicators.forEach(i -> mapIndicatorIds.computeIfAbsent(i.getExperimentPersonId(), k -> new ArrayList<>())
                    .add(i.getCaseIndicatorInstanceId()));
            ts=logCostTime(sb, "1-indicatorIds",ts);
            mapIndicatorIds.forEach((personId, reasonIds) -> {
                fillInput(rst, experimentId, personId, reasonIds, EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT);
            });
            mapIndicatorIds.clear();
            ts=logCostTime(sb, "2-indicatorsSpel-".concat(String.valueOf(rst.mapReasonInput.size())),ts);

            SnapshotRefValidator refValidator = new SnapshotRefValidator(experimentId);
            final String refExptId4Crowd = refValidator.checkCrowd().getCrowdId();
            final String refExptId4RiskModel = refValidator.checkRiskModel().getRiskModelId();
            final List<String> crowdIds = ShareUtil.XCollection.map(snapCrowdsInstanceDao.getByExperimentId(refExptId4Crowd, SnapCrowdsInstanceEntity::getCrowdsId),
                    SnapCrowdsInstanceEntity::getCrowdsId);
            ts=logCostTime(sb, "3-crowdIds",ts);
            for (String personId : personIds) {
                fillInput(rst, experimentId, personId, crowdIds, EnumIndicatorExpressionSource.CROWDS);
            }
            ts=logCostTime(sb, "4-crowdSpels-".concat(String.valueOf(rst.mapReasonInput.size())),ts);
            final List<String> riskModelIds = ShareUtil.XCollection.map(snapRiskModelDao.getByExperimentId(refExptId4RiskModel, SnapRiskModelEntity::getRiskModelId),
                    SnapRiskModelEntity::getRiskModelId);
            ts=logCostTime(sb, "5-riskIds",ts);
            for (String personId : personIds) {
                fillInput(rst, experimentId, personId, riskModelIds, EnumIndicatorExpressionSource.RISK_MODEL);
            }
            ts=logCostTime(sb, "6-riskSpels-".concat(String.valueOf(rst.mapReasonInput.size())),ts);
            final String refExptId4Treat = refValidator.checkTreatItem().getTreatItemId();
            final List<SnapTreatItemEntity> treatItems = snapTreatItemDao.getByExperimentId(refExptId4Treat, SnapTreatItemEntity::getTreatItemId,SnapTreatItemEntity::getIndicatorCategoryId);
            final List<String> lv2TreatItemIds=new ArrayList<>();
            final List<String> lv4TreatItemIds=new ArrayList<>();
            treatItems.forEach(i->{
                if(EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_TREATMENT.getCode().equals(i.getIndicatorCategoryId())){
                    lv4TreatItemIds.add(i.getTreatItemId());
                }else{
                    lv2TreatItemIds.add(i.getTreatItemId());
                }
            });
            ts=logCostTime(sb, "7-treatIds",ts);
            for (String personId : personIds) {
                fillInput(rst, experimentId, personId, lv2TreatItemIds, EnumIndicatorExpressionSource.INDICATOR_OPERATOR_NO_REPORT_TWO_LEVEL);
                fillInput(rst, experimentId, personId, lv4TreatItemIds, EnumIndicatorExpressionSource.INDICATOR_OPERATOR_HAS_REPORT_FOUR_LEVEL);
            }
            ts=logCostTime(sb, "8-treatSpels-".concat(String.valueOf(rst.mapReasonInput.size())),ts);
            final String refExptId4JudgeGoal = refValidator.getRefExperimentId(EnumSnapshotType.INDICATORJudgeGoal);
            final String refExptId4JudgeGuidance = refValidator.getRefExperimentId(EnumSnapshotType.INDICATORJudgeHealthGuidance);
            final String refExptId4JudgeProblem = refValidator.getRefExperimentId(EnumSnapshotType.INDICATORJudgeHealthProblem);
            final String refExptId4JudgeRiskFactor = refValidator.getRefExperimentId(EnumSnapshotType.INDICATORJudgeRiskFactor);
            final List<String> judgeGoalIds=ShareUtil.XCollection.map(snapIndicatorJudgeGoalDao.getByExperimentId(refExptId4JudgeGoal, SnapIndicatorJudgeGoalEntity::getIndicatorJudgeGoalId),
                    SnapIndicatorJudgeGoalEntity::getIndicatorJudgeGoalId);
            final List<String> JudgeGuidanceIds=ShareUtil.XCollection.map(snapIndicatorJudgeHealthGuidanceDao.getByExperimentId(refExptId4JudgeGuidance, SnapIndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId),
                    SnapIndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId);
            final List<String> judgeProblemIds=ShareUtil.XCollection.map(snapIndicatorJudgeHealthProblemDao.getByExperimentId(refExptId4JudgeProblem,  SnapIndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId),
                    SnapIndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId);
            final List<String> judgeRiskFactorIds=ShareUtil.XCollection.map(snapIndicatorJudgeRiskFactorDao.getByExperimentId(refExptId4JudgeRiskFactor, SnapIndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId),
                    SnapIndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId);
            ts=logCostTime(sb, "9-judgeIds",ts);
            fillInput(rst, experimentId, null, judgeGoalIds, EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE,EnumIndicatorExpressionSource.INDICATOR_JUDGE_REFINDICATOR.getSource());
            fillInput(rst, experimentId, null, JudgeGuidanceIds, EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE,EnumIndicatorExpressionSource.INDICATOR_JUDGE_REFINDICATOR.getSource());
            fillInput(rst, experimentId, null, judgeProblemIds, EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE,EnumIndicatorExpressionSource.INDICATOR_JUDGE_REFINDICATOR.getSource());
            fillInput(rst, experimentId, null, judgeRiskFactorIds, EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE,EnumIndicatorExpressionSource.INDICATOR_JUDGE_REFINDICATOR.getSource());
            ts=logCostTime(sb, "10-judgeSpels",ts);

        } catch (Exception ex) {
            ts = logCostTime(sb, String.format("error-%s %s",key, ex.getMessage()), ts);
            log.error(sb.toString());
            throw ex;
        } finally {
            log.info(sb.toString());
            sb.setLength(0);
        }
        return rst;
    }

    private List<SpelInput> fillInput(CacheData rst,String experimentId, String personId, Collection<String> reasonIds, EnumIndicatorExpressionSource source,Integer... sources){
        List<SpelInput> inputs=fromSnapshotLoader.withReasonId(experimentId,personId,reasonIds, source.getSource(),sources);
        inputs.forEach(input-> {
            rst.mapReasonInput.computeIfAbsent(SpelCacheKey.create(experimentId, personId, input.getReasonId(),
                            Optional.ofNullable(input.getSource()).map(EnumIndicatorExpressionSource::getSource).orElse(null)), k -> new ArrayList<>())
                    .add(input);
        });
        reasonIds.forEach(reasonId->{
            rst.mapReasonInput.computeIfAbsent(SpelCacheKey.create(experimentId,personId,reasonId,source.getSource()),k->List.of(new SpelInput(source).setReasonId(reasonId)));
            if(null==sources||sources.length==0){
                return;
            }
            for(Integer item:sources){
                rst.mapReasonInput.computeIfAbsent(SpelCacheKey.create(experimentId,personId,reasonId,item),k->List.of(new SpelInput(item).setReasonId(reasonId)));
            }
        });
        return inputs;
    }

    private Set<EnumIndicatorExpressionSource> rawSources=Set.of(EnumIndicatorExpressionSource.INDICATOR_JUDGE_RISK_FACTOR,
            EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE,
            EnumIndicatorExpressionSource.INDICATOR_JUDGE_REFINDICATOR,
            EnumIndicatorExpressionSource.INDICATOR_JUDGE_GOAL_CHECKRULE,
            EnumIndicatorExpressionSource.INDICATOR_JUDGE_GOAL_REFINDICATOR
    );
    protected boolean isRawExpression(Integer source){
        if(ShareUtil.XObject.isEmpty(source)){
            return false;
        }
        return rawSources.contains(EnumIndicatorExpressionSource.of(source));
    }
    private long logCostTime(StringBuilder sb,String start){
        sb.append(start);
        return System.currentTimeMillis();
    }
    private long logCostTime(StringBuilder sb,String func,long ts){
        long newTs=System.currentTimeMillis();
        sb.append(" ").append(func).append(":").append((newTs - ts));
        return newTs;
    }


    public static class CacheData {
        private final ConcurrentMap<SpelCacheKey, List<SpelInput>> mapReasonInput=new ConcurrentHashMap<>();

        public List<SpelInput> get(SpelCacheKey key){
            return mapReasonInput.get(key);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class SpelCacheKey {

        private String experimentId;

        private String experimentPersonId;
        private String reasonId;

        private Integer source;

        public static SpelCacheKey create(String experimentId, String experimentPersonId,String reasonId,Integer source){
            return new SpelCacheKey()
                    .setExperimentId(experimentId)
                    .setExperimentPersonId(experimentPersonId)
                    .setReasonId(reasonId)
                    .setSource(source);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SpelCacheKey cacheKey = (SpelCacheKey) o;

            if (!Objects.equals(experimentId, cacheKey.experimentId)) {
                return false;
            }
            if (!Objects.equals(experimentPersonId, cacheKey.experimentPersonId)) {
                return false;
            }
            if (!Objects.equals(reasonId, cacheKey.reasonId)) {
                return false;
            }
            return Objects.equals(source, cacheKey.source);
        }

        @Override
        public int hashCode() {
            return Objects.hash(experimentId, experimentPersonId, reasonId, source);
        }

        @Override
        public String toString() {
            return String.format("%s-%s-%s-%s", experimentId,experimentPersonId,reasonId,source);
        }
    }

}
