package org.dows.hep.biz.eval;

import lombok.Data;
import org.dows.hep.biz.base.indicator.ExperimentOrgModuleBiz;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentGroupDao;
import org.dows.hep.biz.dao.ExperimentPersonDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentOrgEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.service.ExperimentOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
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

    protected final static int CACHEInitCapacity = 100;
    protected final static int CACHEMaxSize = 1500;
    protected final static int CACHEExpireSeconds = 60 * 60*24*7 ;
    protected final static String APPId="3";

    @Autowired
    private ExperimentPersonDao experimentPersonDao;

    @Autowired
    private ExperimentGroupDao experimentGroupDao;

    @Autowired
    private ExperimentOrgService experimentOrgService;

    @Autowired
    private ExperimentOrgModuleBiz experimentOrgModuleBiz;

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

    public ExperimentGroupEntity getGroup(String experimentId,String experimentGroupId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return null;
        }
        return cached.getMapGroups().get(experimentGroupId);
    }
    public ExperimentOrgEntity getOrg(String experimentId,String experimentOrgId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return null;
        }
        return cached.getMapOrgs().get(experimentOrgId);
    }
    public ExperimentPersonEntity getPerson(String experimentId,String experimentPersonId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return null;
        }
        return cached.getMapPersons().get(experimentPersonId);
    }
    public Map<String, List<ExperimentPersonEntity>> getMapGroupPersons(String experimentId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptyMap();
        }
        return cached.getMapGroupPersons();
    }
    public List<ExperimentPersonEntity> getPersonsByGroupId(String experimentId,String experimentGroupId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptyList();
        }
        return cached.getMapGroupPersons().get(experimentGroupId);
    }
    public List<ExperimentPersonEntity> getPersonsByOrgId(String experimentId,String experimentOrgId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptyList();
        }
        return cached.getMapOrgPersons().get(experimentOrgId);
    }
    public Map<String,ExperimentPersonEntity> getMapPersons(String experimentId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptyMap();
        }
        return cached.getMapPersons();
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
    public Set<String> getMonitorOrgIds(String experimentId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptySet();
        }
        return cached.getSetMonitorOrgId();
    }

    public Set<String> getCasePersonIdSet(String experimentId){
        ExperimentPersonCache.CacheData cached=getCacheData(experimentId);
        if(null==cached){
            return Collections.emptySet();
        }
        Set<String> rst=new HashSet<>();
        cached.getMapPersons().values().forEach(i->rst.add(i.getCasePersonId()));
        return rst;
    }

    public void remove(String experimentId){
        remove(ExperimentCacheKey.create("3", experimentId));
    }
    public void remove(String appId, String experimentId){
        remove(ExperimentCacheKey.create(appId, experimentId));
    }

    private void remove(ExperimentCacheKey key){
        key.setAppId(ShareBiz.checkAppId(key.getAppId(), key.getExperimentInstanceId()));
        this.loadingCache().invalidate(key);
    }

    @Override
    protected CacheData load(ExperimentCacheKey key) {
        if(ShareUtil.XObject.isEmpty(key)){
            return null;
        }
        CacheData rst=new CacheData();
        List<ExperimentPersonEntity> rowsPerson= experimentPersonDao.getByExperimentId(key.getAppId(), key.getExperimentInstanceId() );
        rowsPerson.forEach(i->{
            rst.mapPersons.put(i.getExperimentPersonId(),i);
            rst.mapGroupPersons.computeIfAbsent(i.getExperimentGroupId(), k->new ArrayList<>()).add(i);
            rst.mapOrgPersons.computeIfAbsent(i.getExperimentOrgId(), k->new ArrayList<>()).add(i);
        });
        List<ExperimentGroupEntity> rowsGroup=experimentGroupDao.getByExperimentId(key.getExperimentInstanceId() );
        rowsGroup.forEach(i->{
            rst.mapGroups.put(i.getExperimentGroupId(),i);
        });
        List<ExperimentOrgEntity> rowsOrg=experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentInstanceId, key.getExperimentInstanceId())
                .list();
        rowsOrg.forEach(i->{
            rst.mapOrgs.put(i.getExperimentOrgId(),i);
        });
        rst.getSetMonitorOrgId().addAll(experimentOrgModuleBiz.getMonitorOrgIds(rst.mapOrgs.keySet()));
        return rst;
    }

    @Data
    public static class CacheData {
        private final ConcurrentMap<String, ExperimentPersonEntity> mapPersons=new ConcurrentHashMap<>();

        private final Map<String, List<ExperimentPersonEntity>> mapGroupPersons=new HashMap<>();

        private final Map<String,List<ExperimentPersonEntity>> mapOrgPersons=new HashMap<>();

        private final Map<String, ExperimentGroupEntity> mapGroups=new HashMap<>();

        private final Map<String, ExperimentOrgEntity> mapOrgs=new HashMap<>();

        //监测随访机构id
        private final Set<String> setMonitorOrgId=new HashSet<>();
    }
}
