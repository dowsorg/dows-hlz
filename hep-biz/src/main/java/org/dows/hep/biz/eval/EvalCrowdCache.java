package org.dows.hep.biz.eval;

import lombok.Data;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.SnapCrowdsInstanceDao;
import org.dows.hep.biz.dao.SnapRiskModelDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.spel.SnapshotRefValidator;
import org.dows.hep.entity.snapshot.SnapCrowdsInstanceEntity;
import org.dows.hep.entity.snapshot.SnapRiskModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/9/14 14:54
 */
@Component
public class EvalCrowdCache extends BaseLoadingCache<ExperimentCacheKey, EvalCrowdCache.CacheData> {
    private static volatile EvalCrowdCache s_instance;

    public static EvalCrowdCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=2;
    protected final static int CACHEMaxSize=20;
    protected final static int CACHEExpireSeconds=60*60*24*7;

    private EvalCrowdCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

    @Autowired
    private SnapCrowdsInstanceDao snapCrowdsInstanceDao;

    @Autowired
    private SnapRiskModelDao snapRiskModelDao;

    private final static String APPId="3";

    public EvalCrowdCache.CacheData getCacheData(String experimentId){
        return getCacheData(ExperimentCacheKey.create(APPId, experimentId));
    }
    public EvalCrowdCache.CacheData getCacheData(ExperimentCacheKey key){
        return this.loadingCache().get(key);
    }

    public Collection<String> getCrowdIds(String experimentId){
        EvalCrowdCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptyList();
        }
        return cached.getMapCrowds().keySet();
    }
    public Collection<SnapCrowdsInstanceEntity> getCrowds(String experimentId){
        EvalCrowdCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptyList();
        }
        return cached.getMapCrowds().values();
    }
    public Collection<String> getRiskModelIds(String experimentId){
        EvalCrowdCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptyList();
        }
        return cached.getMapRiskModels().keySet();
    }

    public List<SnapRiskModelEntity> getRiskModelByCrowdId(String experimentId,String crowdId){
        EvalCrowdCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptyList();
        }
        return cached.getMapCrowdXRiskModels().get(crowdId);
    }



    @Override
    protected CacheData load(ExperimentCacheKey key) {
        CacheData rst=new CacheData();
        SnapshotRefValidator refValidator = new SnapshotRefValidator(key.getExperimentInstanceId());
        final String refExptId4Crowd = refValidator.checkCrowd().getCrowdId();
        final String refExptId4RiskModel = refValidator.checkRiskModel().getRiskModelId();
        List<SnapCrowdsInstanceEntity> rowsCrowd=snapCrowdsInstanceDao.getByExperimentId(refExptId4Crowd);
        rowsCrowd.forEach(i->rst.mapCrowds.put(i.getCrowdsId(), i));
        List<SnapRiskModelEntity> rowsRiskmodel=snapRiskModelDao.getByExperimentId(refExptId4RiskModel);
        rowsRiskmodel.forEach(i->{
            rst.mapRiskModels.put(i.getRiskModelId(),i);
            rst.mapCrowdXRiskModels.computeIfAbsent(i.getCrowdsCategoryId(), k->new ArrayList<>())
                    .add(i);
        });
        rowsCrowd.clear();
        rowsRiskmodel.clear();
        return rst;
    }

    @Data
    public static class CacheData {
        private final Map<String, SnapCrowdsInstanceEntity> mapCrowds=new HashMap<>();

        private final Map<String,List<SnapRiskModelEntity>> mapCrowdXRiskModels=new HashMap<>();

        private final Map<String, SnapRiskModelEntity> mapRiskModels=new HashMap<>();

    }
}
