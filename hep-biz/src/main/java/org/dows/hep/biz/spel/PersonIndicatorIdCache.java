package org.dows.hep.biz.spel;

import lombok.Getter;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentIndicatorExpressionRsDao;
import org.dows.hep.biz.dao.ExperimentIndicatorInstanceRsDao;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorExpressionItemRsEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionRsEntity;
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

    @Autowired
    private ExperimentIndicatorExpressionRsDao experimentIndicatorExpressionRsDao;


    public Set<String> getIndicatorIds(String exptPersonId){
        PersonIndicatorIdCollection coll= this.loadingCache().get(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return Collections.emptySet();
        }
        return coll.getMapExptIndicators().keySet();
    }

    public String getSysIndicatorId(String exptPersonId, EnumIndicatorType type){
        PersonIndicatorIdCollection coll= this.loadingCache().get(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        return coll.getMapSysIndicatorId().get(type);
    }

    public String getIndicatorIdBySourceId(String exptPersonId, String baseOrCaseIndicatorId){
        if(ShareUtil.XObject.anyEmpty(exptPersonId,baseOrCaseIndicatorId)) {
            return null;
        }
        PersonIndicatorIdCollection coll= this.loadingCache().get(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        return coll.getMapBaseCase2ExptId().get(baseOrCaseIndicatorId);
    }
    public ExperimentIndicatorInstanceRsEntity getIndicatorById(String exptPersonId,String indicatorId){
        if(ShareUtil.XObject.anyEmpty(exptPersonId,indicatorId)){
            return null;
        }
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
        if (ShareUtil.XObject.isEmpty(key)) {
            return null;
        }
        PersonIndicatorIdCollection rst = new PersonIndicatorIdCollection();
        List<ExperimentIndicatorInstanceRsEntity> rowsIndicator = experimentIndicatorInstanceRsDao.getByExperimentPersonId(key,
                ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId,
                ExperimentIndicatorInstanceRsEntity::getCaseIndicatorInstanceId,
                ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId,
                ExperimentIndicatorInstanceRsEntity::getExperimentPersonId,
                ExperimentIndicatorInstanceRsEntity::getDef
        );
        rowsIndicator.forEach(i -> {
            rst.getMapBaseCase2ExptId().put(i.getIndicatorInstanceId(), i.getExperimentIndicatorInstanceId());
            rst.getMapBaseCase2ExptId().put(i.getCaseIndicatorInstanceId(), i.getExperimentIndicatorInstanceId());
            rst.getMapExptIndicators().put(i.getExperimentIndicatorInstanceId(), i);
            if(EnumIndicatorType.USER_CREATED.getType().equals(i.getType())) {
                return;
            }
            EnumIndicatorType indicatorType=EnumIndicatorType.of(i.getType());
            if(null==indicatorType){
                return;
            }
            rst.getMapSysIndicatorId().put(indicatorType, i.getExperimentIndicatorInstanceId());
        });
        final List<ExperimentIndicatorExpressionRsEntity> rowsExperession = experimentIndicatorExpressionRsDao.getByExperimentIndicatorIds(rst.getMapExptIndicators().keySet(),
                EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource(),
                ExperimentIndicatorExpressionRsEntity::getPrincipalId,
                ExperimentIndicatorExpressionRsEntity::getMaxIndicatorExpressionItemId,
                ExperimentIndicatorExpressionRsEntity::getMinIndicatorExpressionItemId);
        final List<String> minMaxExpressionItemIds = new ArrayList<>();
        rowsExperession.forEach(i -> {
            if (ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())) {
                minMaxExpressionItemIds.add(i.getMinIndicatorExpressionItemId());
            }
            if (ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())) {
                minMaxExpressionItemIds.add(i.getMaxIndicatorExpressionItemId());
            }
        });
        final List<ExperimentIndicatorExpressionItemRsEntity> rowsExpressionItem = experimentIndicatorExpressionRsDao.getSubBySubIds(minMaxExpressionItemIds,
                ExperimentIndicatorExpressionItemRsEntity::getExperimentIndicatorExpressionItemId,
                ExperimentIndicatorExpressionItemRsEntity::getResultRaw);
        Map<String,String> mapExpressionItem=ShareUtil.XCollection.toMap(rowsExpressionItem,
                ExperimentIndicatorExpressionItemRsEntity::getExperimentIndicatorExpressionItemId,
                ExperimentIndicatorExpressionItemRsEntity::getResultRaw);
        rowsExperession.forEach(expression->{
            ExperimentIndicatorInstanceRsEntity indicator=rst.mapExptIndicators.get(expression.getPrincipalId());
            if(null==indicator){
                return;
            }
            if(ShareUtil.XObject.notEmpty( expression.getMinIndicatorExpressionItemId())){
                indicator.setMin(mapExpressionItem.get(expression.getMinIndicatorExpressionItemId()));
            }
            if(ShareUtil.XObject.notEmpty( expression.getMaxIndicatorExpressionItemId())){
                indicator.setMax(mapExpressionItem.get(expression.getMaxIndicatorExpressionItemId()));
            }
        });

        rowsIndicator.clear();
        rowsExperession.clear();
        rowsExpressionItem.clear();
        mapExpressionItem.clear();
        return rst;
    }

    public static class PersonIndicatorIdCollection {
        @Getter
        private final Map<String,String> mapBaseCase2ExptId=new HashMap<>();
        @Getter
        private final Map<String, ExperimentIndicatorInstanceRsEntity> mapExptIndicators=new HashMap<>();

        @Getter
        private final Map<EnumIndicatorType,String> mapSysIndicatorId=new HashMap<>();
    }



}
