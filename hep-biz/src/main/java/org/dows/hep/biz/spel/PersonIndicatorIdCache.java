package org.dows.hep.biz.spel;

import lombok.Getter;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentIndicatorInstanceRsDao;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/7/20 16:11
 */
@Component
public class PersonIndicatorIdCache extends BaseLoadingCache<String,PersonIndicatorIdCache.PersonIndicatorIdCollection> {

    private static volatile PersonIndicatorIdCache s_instance;

    public static PersonIndicatorIdCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=10;
    protected final static int CACHEMaxSize=50;
    protected final static int CACHEExpireSeconds=60*60*24;

    private PersonIndicatorIdCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

    @Autowired
    private ExperimentIndicatorInstanceRsDao experimentIndicatorInstanceRsDao;


    public Set<String> getIndicatorIds(String exptPersonId){
        PersonIndicatorIdCollection coll= this.loadingCache().get(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return Collections.emptySet();
        }
        return coll.getMapExptIndicators().keySet();
    }

    public String getIndicatorIdBySourceId(String exptPersonId, String baseOrCaseIndicatorId){
        PersonIndicatorIdCollection coll= this.loadingCache().get(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        return coll.getMapBaseCase2ExptId().get(baseOrCaseIndicatorId);
    }
    public ExperimentIndicatorInstanceRsEntity getIndicatorById(String exptPersonId,String indicatorId){
        PersonIndicatorIdCollection coll= this.loadingCache().get(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        String exptIndicatorId=coll.getMapBaseCase2ExptId().getOrDefault(indicatorId, indicatorId);
        return coll.getMapExptIndicators().get(exptIndicatorId);
    }

    public Collection<ExperimentIndicatorInstanceRsEntity> getIndicators(String exptPersonId){
        PersonIndicatorIdCollection coll= this.loadingCache().get(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return Collections.emptySet();
        }
        return coll.getMapExptIndicators().values();
    }

    @Override
    protected PersonIndicatorIdCollection load(String key) {
        if(ShareUtil.XObject.isEmpty(key)){
            return null;
        }
        PersonIndicatorIdCollection rst=new PersonIndicatorIdCollection();
        List<ExperimentIndicatorInstanceRsEntity> rowsIndicator=experimentIndicatorInstanceRsDao.getByExperimentPersonId(key,
                ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId,
                ExperimentIndicatorInstanceRsEntity::getCaseIndicatorInstanceId,
                ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId,
                ExperimentIndicatorInstanceRsEntity::getExperimentPersonId,
                ExperimentIndicatorInstanceRsEntity::getDef,
                ExperimentIndicatorInstanceRsEntity::getMin,
                ExperimentIndicatorInstanceRsEntity::getMax
                );
        rowsIndicator.forEach(i->{
            rst.getMapBaseCase2ExptId().put(i.getIndicatorInstanceId(), i.getExperimentIndicatorInstanceId());
            rst.getMapBaseCase2ExptId().put(i.getCaseIndicatorInstanceId(), i.getExperimentIndicatorInstanceId());
            rst.getMapExptIndicators().put(i.getExperimentIndicatorInstanceId(),i);
        });
        rowsIndicator.clear();
        return rst;
    }

    public static class PersonIndicatorIdCollection {
        @Getter
        private final Map<String,String> mapBaseCase2ExptId=new HashMap<>();
        @Getter
        private final Map<String, ExperimentIndicatorInstanceRsEntity> mapExptIndicators=new HashMap<>();
    }



}
