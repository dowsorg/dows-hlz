package org.dows.hep.biz.spel;

import lombok.Getter;
import org.dows.hep.api.enums.EnumIndicatorDocType;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.enums.EnumIndicatorValueType;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.*;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;
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
    protected final static int CACHEInitCapacity=300;
    protected final static int CACHEMaxSize=3000;
    protected final static int CACHEExpireSeconds=60*60*24*7;

    private static final Set<String> INDICTATORNameBloodPressure=Set.of("收缩压","舒张压","心率");

    private PersonIndicatorIdCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

    @Autowired
    private ExperimentIndicatorInstanceRsDao experimentIndicatorInstanceRsDao;

    @Autowired
    private ExperimentIndicatorExpressionRsDao experimentIndicatorExpressionRsDao;

    @Autowired
    private ExperimentPersonDao experimentPersonDao;

    @Autowired
    private SnapCaseIndicatorExpressionDao snapCaseIndicatorExpressionDao;

    @Autowired
    private SnapCaseIndicatorExpressionItemDao snapCaseIndicatorExpressionItemDao;


    public PersonIndicatorIdCollection getCacheData(String exptPersonId){
        return this.getSet(exptPersonId,true);
    }

    public Set<String> getWatchIndicatos(String exptPersonId){
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        return coll.getWatchIndicatorIds();
    }
    public ExperimentPersonEntity getPerson(String exptPersonId){
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        return coll.getPersonEntity();
    }
    public List<ExperimentIndicatorInstanceRsEntity> getSortedIndicators(String exptPersonId){
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return Collections.emptyList();
        }
        return coll.getSortedIndicators();
    }
    public List<ExperimentIndicatorInstanceRsEntity> getCoreIndicators(String exptPersonId){
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return Collections.emptyList();
        }
        return coll.getCoreIndicators();
    }

    public ExperimentIndicatorInstanceRsEntity getBmiIndicator(String exptPersonId){
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        return coll.getBmiIndicator();
    }
    public Map<String,String> getMapBaseCase2ExptId(String exptPersonId){
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return Collections.emptyMap();
        }
        return coll.getMapBaseCase2ExptId();
    }

    public Set<String> getIndicatorIds(String exptPersonId){
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return Collections.emptySet();
        }
        return coll.getMapExptIndicators().keySet();
    }

    public String getSysIndicatorId(String exptPersonId, EnumIndicatorType type){
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        return coll.getMapSysIndicatorId().get(type);
    }
    public ExperimentIndicatorInstanceRsEntity getSysIndicator(String exptPersonId, EnumIndicatorType type){
        String indicatorId=getSysIndicatorId(exptPersonId, type);
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return null;
        }
        return getIndicatorById(exptPersonId,indicatorId);

    }

    public String getIndicatorIdBySourceId(String exptPersonId, String baseOrCaseIndicatorId){
        if(ShareUtil.XObject.anyEmpty(exptPersonId,baseOrCaseIndicatorId)) {
            return null;
        }
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        return coll.getMapBaseCase2ExptId().get(baseOrCaseIndicatorId);
    }
    public ExperimentIndicatorInstanceRsEntity getIndicatorById(String exptPersonId,String indicatorId){
        if(ShareUtil.XObject.anyEmpty(exptPersonId,indicatorId)){
            return null;
        }
        PersonIndicatorIdCollection coll= getCacheData(exptPersonId);
        if(ShareUtil.XObject.isEmpty(coll)){
            return null;
        }
        String exptIndicatorId=coll.getMapBaseCase2ExptId().getOrDefault(indicatorId, indicatorId);
        return coll.getMapExptIndicators().get(exptIndicatorId);
    }

    public int getScale(ExperimentIndicatorInstanceRsEntity src){
        if(ShareUtil.XObject.isEmpty(src)){
            return 2;
        }
        if(ShareUtil.XObject.notEmpty(src.getValueType())){
            return EnumIndicatorValueType.of(src.getValueType()).getScale();
        }
        return INDICTATORNameBloodPressure.contains(src.getIndicatorName())?0:2;
    }


    @Override
    protected PersonIndicatorIdCollection load(String key) {
        if (ShareUtil.XObject.isEmpty(key)) {
            return null;
        }
        ExperimentPersonEntity rowPerson=experimentPersonDao.getById(key).orElse(null);
        if(ShareUtil.XObject.isEmpty(rowPerson)){
            return null;
        }
        PersonIndicatorIdCollection rst = new PersonIndicatorIdCollection();
        rst.personEntity=rowPerson;

        List<ExperimentIndicatorInstanceRsEntity> rowsIndicator = experimentIndicatorInstanceRsDao.getByExperimentPersonId(key);
        rowsIndicator.forEach(i -> {
            if(ShareUtil.XObject.notEmpty(i.getIndicatorInstanceId())) {
                rst.getMapBaseCase2ExptId().put(i.getIndicatorInstanceId(), i.getExperimentIndicatorInstanceId());
            }
            if(ShareUtil.XObject.notEmpty(i.getCaseIndicatorInstanceId())) {
                rst.getMapBaseCase2ExptId().put(i.getCaseIndicatorInstanceId(), i.getExperimentIndicatorInstanceId());
            }
            rst.getMapExptIndicators().put(i.getExperimentIndicatorInstanceId(), i);
            rst.getSortedIndicators().add(i);
            i.setDocType(EnumIndicatorDocType.NONE).setMin(null).setMax(null);
            if(Optional.ofNullable( i.getCore()).orElse(0)>0){
                rst.getCoreIndicators().add(i);
            }
            EnumIndicatorType indicatorType=EnumIndicatorType.of(i.getType());
            if(null==indicatorType){
                return;
            }
            if(!EnumIndicatorType.USER_CREATED.getType().equals(i.getType())) {
                rst.getMapSysIndicatorId().put(indicatorType, i.getExperimentIndicatorInstanceId());
            }
            EnumIndicatorDocType docType=EnumIndicatorDocType.of(indicatorType,i.getIndicatorName());
            i.setDocType(docType);
            if(indicatorType!=EnumIndicatorType.USER_CREATED
                    ||docType!=EnumIndicatorDocType.NONE){
                rst.watchIndicatorIds.add(i.getExperimentIndicatorInstanceId());
                if(null==rst.getBmiIndicator()&&Optional.ofNullable(i.getIndicatorName()).map(String::toLowerCase).orElse("").equals("bmi")){
                    rst.bmiIndicator=i;
                }
            }

        });
        rowsIndicator.clear();
        rst.sortedIndicators.sort(Comparator.comparingInt(ExperimentIndicatorInstanceRsEntity::getRecalculateSeq));


        return rst;
    }

    @Override
    protected boolean isCompleted(PersonIndicatorIdCollection val) {
        return null != val && val.loadedExpression;
    }

    @Override
    protected PersonIndicatorIdCollection continueLoad(String key, PersonIndicatorIdCollection curVal) {
        if(null==curVal.getPersonEntity()){
            curVal.personEntity= experimentPersonDao.getById(key).orElse(null);
        }
        if(null==curVal.getPersonEntity()){
            return curVal;
        }
        SnapshotRefValidator refValidator=new SnapshotRefValidator(curVal.personEntity.getExperimentInstanceId());
        final String refExperimentId4Expression=refValidator.checkExpression().getExpressionId();
        final String refExperimentId4Item=refValidator.checkExpressionItem().getExpressionItemId();
        if(ShareUtil.XObject.anyEmpty(refExperimentId4Expression,refExperimentId4Item)){
            return curVal;
        }
        final List<String> caseIndicatorIds=ShareUtil.XCollection.map(curVal.sortedIndicators, ExperimentIndicatorInstanceRsEntity::getCaseIndicatorInstanceId);
        final List<SnapCaseIndicatorExpressionEntity> rowsExperession = snapCaseIndicatorExpressionDao.getByCaseIndicatorIds(refExperimentId4Expression,
                caseIndicatorIds,
                EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource(),
                SnapCaseIndicatorExpressionEntity::getCasePrincipalId,
                SnapCaseIndicatorExpressionEntity::getMaxIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionEntity::getMinIndicatorExpressionItemId);
        if(ShareUtil.XObject.isEmpty(rowsExperession)){
            return curVal;
        }
        final List<String> minMaxExpressionItemIds = new ArrayList<>();
        rowsExperession.forEach(i -> {
            if (ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())) {
                minMaxExpressionItemIds.add(i.getMinIndicatorExpressionItemId());
            }
            if (ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())) {
                minMaxExpressionItemIds.add(i.getMaxIndicatorExpressionItemId());
            }
        });
        final List<SnapCaseIndicatorExpressionItemEntity> rowsExpressionItem = snapCaseIndicatorExpressionItemDao.getByExpressionItemId(refExperimentId4Item,
                minMaxExpressionItemIds,
                SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionItemEntity::getResultRaw);
        Map<String, String> mapExpressionItem = ShareUtil.XCollection.toMap(rowsExpressionItem,
                SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionItemEntity::getResultRaw);
        rowsExperession.forEach(expression -> {
            String indicatorId=curVal.mapBaseCase2ExptId.get(expression.getCasePrincipalId());
            if(ShareUtil.XObject.isEmpty(indicatorId)){
                return;
            }
            ExperimentIndicatorInstanceRsEntity indicator = curVal.getMapExptIndicators().get(indicatorId);
            if (null == indicator) {
                return;
            }
            if (ShareUtil.XObject.notEmpty(expression.getMinIndicatorExpressionItemId())) {
                indicator.setMin(mapExpressionItem.get(expression.getMinIndicatorExpressionItemId()));
            }
            if (ShareUtil.XObject.notEmpty(expression.getMaxIndicatorExpressionItemId())) {
                indicator.setMax(mapExpressionItem.get(expression.getMaxIndicatorExpressionItemId()));
            }
        });
        rowsExperession.clear();
        rowsExpressionItem.clear();
        mapExpressionItem.clear();
        curVal.loadedExpression=true;
        return curVal;
    }

    public static class PersonIndicatorIdCollection {

        @Getter
        private volatile boolean loadedExpression=false;

        @Getter
        private volatile ExperimentPersonEntity personEntity;
        @Getter
        private final Map<String,String> mapBaseCase2ExptId=new HashMap<>();
        @Getter
        private final Map<String, ExperimentIndicatorInstanceRsEntity> mapExptIndicators=new HashMap<>();

        @Getter
        private final List<ExperimentIndicatorInstanceRsEntity> sortedIndicators=new ArrayList<>();

        @Getter
        private final Map<EnumIndicatorType,String> mapSysIndicatorId=new HashMap<>();

        @Getter
        private volatile ExperimentIndicatorInstanceRsEntity bmiIndicator=null;

        @Getter
        private final Set<String> watchIndicatorIds=new HashSet<>();

        @Getter
        private final List<ExperimentIndicatorInstanceRsEntity> coreIndicators=new ArrayList<>();

       /* @Getter
        private final Map<String,ExperimentIndicatorInstanceRsEntity> docEnergyIndicators=new LinkedHashMap<>();

        @Getter
        private final Map<String,ExperimentIndicatorInstanceRsEntity> docBasicIndicators=new LinkedHashMap<>();

        @Getter
        private final AtomicReference<ExperimentIndicatorInstanceRsEntity> docHPIndicator=new AtomicReference<>();*/

    }



}
