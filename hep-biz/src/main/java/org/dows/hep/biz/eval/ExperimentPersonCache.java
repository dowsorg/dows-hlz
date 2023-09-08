package org.dows.hep.biz.eval;

import lombok.Data;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentPersonDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wuzl
 * @date : 2023/9/7 20:07
 */
@Component
public class ExperimentPersonCache extends BaseLoadingCache<ExperimentCacheKey,ExperimentPersonCache.CacheData> {

    private static volatile ExperimentPersonCache s_instance;

    public static ExperimentPersonCache Instance() {
        return s_instance;
    }

    protected final static int CACHEInitCapacity = 2;
    protected final static int CACHEMaxSize = 20;
    protected final static int CACHEExpireSeconds = 60 * 60*24*7 ;
    protected final static String APPId="3";

    @Autowired
    private ExperimentPersonDao experimentPersonDao;

    protected ExperimentPersonCache() {
        super(CACHEInitCapacity, CACHEMaxSize, CACHEExpireSeconds, 0);
        s_instance = this;
    }

    public ExperimentPersonCache.CacheData getCacheData(String experimentId){
        return getCacheData(ExperimentCacheKey.create(APPId, experimentId));
    }

    public ExperimentPersonCache.CacheData getCacheData(ExperimentCacheKey key){
        return this.loadingCache().get(key);
    }

    public Set<String> getPersondIdSet(String experimentId, Set<String> src){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(ShareUtil.XObject.isEmpty(src)){
            return cached.getMapPersons().keySet();
        }
        Set<String> rst=new HashSet<>();
        src.forEach(i->{
            if(cached.getMapPersons().containsKey(i)){
                rst.add(i);
            }
        });
        return rst;
    }

    @Override
    protected CacheData load(ExperimentCacheKey key) {
        if(ShareUtil.XObject.isEmpty(key)){
            return null;
        }
        CacheData rst=new CacheData();
        List<ExperimentPersonEntity> rowsPerson= experimentPersonDao.getByExperimentId(key.getAppId(), key.getExperimentInstanceId() );
        rowsPerson.forEach(i->rst.mapPersons.put(i.getExperimentPersonId(),i));
        return rst;
    }

    @Data
    public static class CacheData {
        private final ConcurrentMap<String, ExperimentPersonEntity> mapPersons=new ConcurrentHashMap<>();


    }
}
