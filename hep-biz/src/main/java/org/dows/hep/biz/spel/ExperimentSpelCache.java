package org.dows.hep.biz.spel;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentIndicatorInstanceRsDao;
import org.dows.hep.biz.dao.SnapCrowdsInstanceDao;
import org.dows.hep.biz.dao.SnapRiskModelDao;
import org.dows.hep.biz.dao.SnapTreatItemDao;
import org.dows.hep.biz.eval.ExperimentPersonCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.spel.loaders.FromSnapshotLoader;
import org.dows.hep.biz.spel.meta.ISpelLoad;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.snapshot.SnapCrowdsInstanceEntity;
import org.dows.hep.entity.snapshot.SnapRiskModelEntity;
import org.dows.hep.entity.snapshot.SnapTreatItemEntity;
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
    protected final static int CACHEMaxSize=20;
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

    private final static String APPId="3";



    @Override
    public SpelInput withReasonId(String experimentId, String experimentPersonId, String reasonId, Integer source) {
        ExperimentCacheKey cacheKey=ExperimentCacheKey.create(APPId,experimentId);
        CacheData cached=loadingCache().get(cacheKey);
        if(null==cached){
            return fromSnapshotLoader.withReasonId(experimentId,experimentPersonId,reasonId,source);
        }
        final SpelCacheKey spelCacheKey=SpelCacheKey.create(experimentPersonId,reasonId,source);
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
    public List<SpelInput> withReasonId(String experimentId, String experimentPersonId, Collection<String> reasonIds, Integer source) {
        ExperimentCacheKey cacheKey=ExperimentCacheKey.create(APPId,experimentId);
        CacheData cached=loadingCache().get(cacheKey);
        if(null==cached){
            return fromSnapshotLoader.withReasonId(experimentId,experimentPersonId,reasonIds,source);
        }
        final List<SpelInput> rst=new ArrayList<>();
        final List<String> missReasonIds=new ArrayList<>();
        for(String reasonId:reasonIds){
            SpelCacheKey spelCacheKey=SpelCacheKey.create(experimentPersonId,reasonId,source);
            List<SpelInput> inputs=cached.get(spelCacheKey);
            if(ShareUtil.XObject.notEmpty(inputs)){
                rst.addAll(inputs);
            }else{
                missReasonIds.add(reasonId);
            }
        }
        if(ShareUtil.XObject.notEmpty(missReasonIds)) {
            rst.addAll(fillInput(cached, experimentId, experimentPersonId, missReasonIds, EnumIndicatorExpressionSource.of(source)));
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
            final List<String> treatItemIds = ShareUtil.XCollection.map(snapTreatItemDao.getByExperimentId(refExptId4Treat, SnapTreatItemEntity::getTreatItemId),
                    SnapTreatItemEntity::getTreatItemId);
            ts=logCostTime(sb, "7-treatIds",ts);
            for (String personId : personIds) {
                fillInput(rst, experimentId, personId, treatItemIds, EnumIndicatorExpressionSource.INDICATOR_OPERATOR_NO_REPORT_TWO_LEVEL);
            }
            ts=logCostTime(sb, "8-treatSpels-".concat(String.valueOf(rst.mapReasonInput.size())),ts);

        }catch (Exception ex){
            ts=logCostTime(sb, String.format("error:%s", ex.getMessage()));
            log.error(String.format("SPELTRACE--load--error exptKey[%s]",key), ex);
        }finally {
            log.error(sb.toString());
            log.info(sb.toString());
            sb.setLength(0);
        }
        return rst;
    }

    private List<SpelInput> fillInput(CacheData rst,String experimentId, String personId, Collection<String> reasonIds, EnumIndicatorExpressionSource source){
        List<SpelInput> inputs=fromSnapshotLoader.withReasonId(experimentId,personId,reasonIds, source.getSource());
        Map<String,List<SpelInput>> mapReasons= ShareUtil.XCollection.groupBy(inputs, SpelInput::getReasonId);
        mapReasons.forEach((reasonId,vInputs)->rst.mapReasonInput.put(SpelCacheKey.create(personId,reasonId,source.getSource()),vInputs));
        reasonIds.forEach(reasonId->{
            rst.mapReasonInput.computeIfAbsent(SpelCacheKey.create(personId,reasonId,source.getSource()),k->List.of(new SpelInput(source).setReasonId(reasonId)));
        });
        inputs.clear();
        mapReasons.clear();;
        return inputs;
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

        private String experimentPersonId;
        private String reasonId;

        private Integer source;

        public static SpelCacheKey create(String experimentPersonId,String reasonId,Integer source){
            return new SpelCacheKey()
                    .setExperimentPersonId(experimentPersonId)
                    .setReasonId(reasonId)
                    .setSource(source);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SpelCacheKey cacheKey = (SpelCacheKey) o;

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
            return Objects.hash(experimentPersonId, reasonId, source);
        }
    }

}
