package org.dows.hep.biz.eval;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.biz.dao.ExperimentEvalLogDao;
import org.dows.hep.biz.dao.ExperimentIndicatorLogDao;
import org.dows.hep.biz.eval.codec.EvalIndicatorValuesCodec;
import org.dows.hep.biz.eval.codec.EvalPersonOnceDataCodec;
import org.dows.hep.biz.eval.data.*;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.util.*;
import org.dows.hep.entity.ExperimentEvalLogContentEntity;
import org.dows.hep.entity.ExperimentEvalLogEntity;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorLogEntity;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:42
 */
@Slf4j
public class EvalPersonOnceHolder {

    protected final static int RDCACHEExpireSeconds=60 * 60 * 24*7;
    protected final static String RDCACHEPrefix="eval-person-once:";

    public EvalPersonOnceHolder(EvalPersonOnceCacheKey cacheKey, RedissonClient redissonClient) {
        this.cacheKey=cacheKey;
        this.redissonClient=redissonClient;
        this.experimentEvalLogDao= CrudContextHolder.getBean(ExperimentEvalLogDao.class);
        this.experimentIndicatorLogDao=CrudContextHolder.getBean(ExperimentIndicatorLogDao.class);
    }


    private final RedissonClient redissonClient;
    private final ExperimentEvalLogDao experimentEvalLogDao;

    private final ExperimentIndicatorLogDao experimentIndicatorLogDao;

    private final EvalPersonOnceCacheKey cacheKey;

    private EvalPersonOnceData cacheData;



    //region holders
    public EvalPersonOnceHolder getLastHolder(){
        return getHolder(cacheKey.getEvalNo()-1);
    }
    public EvalPersonOnceHolder getNextHolder(){
        return getHolder(cacheKey.getEvalNo()+1);
    }
    private EvalPersonOnceHolder getHolder(int evalNo) {
        return EvalPersonOnceCache.Instance().getHolder(cacheKey.getExperimentInstanceId(), cacheKey.getExperimentPersonId(), evalNo);
    }
    //endregion

    public boolean isValid(EvalPersonOnceData data){
        return ShareUtil.XObject.notEmpty(data)
                &&data.isValued()
                &&data.equalsEvalNo(cacheKey.getEvalNo());
    }
    public EvalPersonOnceHolder setEvalNo(int evalNo){
        cacheKey.setEvalNo(evalNo);
        Optional.ofNullable(cacheData)
                .map(EvalPersonOnceData::getHeader)
                .ifPresent(i->i.setEvalNo(evalNo));
        return this;
    }
    //region get
    public EvalPersonOnceData getPresent(){
        return cacheData;
    }
    public EvalPersonOnceData get(){
        if(isValid(cacheData)){
            return cacheData;
        }
        return cacheData=load();
    }
    public EvalPersonOnceData get(int evalNo,boolean loadFlag){
        if(cacheKey.getEvalNo().equals(evalNo)){
            return get();
        }
        if(!loadFlag){
            return null;
        }
        cacheKey.setEvalNo(evalNo);
        return cacheData=load();
    }

    public EvalIndicatorValues getSysIndicator(EnumIndicatorType type){
        String indicatorId=PersonIndicatorIdCache.Instance().getSysIndicatorId(cacheKey.getExperimentPersonId(), type);
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return null;
        }
        return getIndicator(indicatorId);
    }
    public String getHealthPoint(boolean lastFlag){
        EvalIndicatorValues values=getSysIndicator(EnumIndicatorType.HEALTH_POINT);
        return Optional.ofNullable(values).map(i->lastFlag?i.getLastVal():i.getCurVal()).orElse("1");
    }
    public String getMoney(boolean lastFlag){
        EvalIndicatorValues values=getSysIndicator(EnumIndicatorType.MONEY);
        return Optional.ofNullable(values).map(i->lastFlag?i.getLastVal():i.getCurVal()).orElse("");
    }

    public EvalIndicatorValues getMoneyValues() {
        EvalPersonOnceData cached = cacheData;
        if (!isValid(cached)) {
            return null;
        }
        return getMoneyValues(cached.getHeader().getPeriods());
    }
    public EvalIndicatorValues getMoneyValues(Integer period) {
        EvalPersonOnceData cached = cacheData;
        if (!isValid(cached)) {
            return null;
        }
        EvalIndicatorValues rst = cached.getMapPeriodMoney().get(period);
        if (null == rst && period.equals(cached.getHeader().getPeriods())) {
            rst = getSysIndicator(EnumIndicatorType.MONEY);
            if (null != rst) {
                cached.getMapPeriodMoney().put(period, rst);
            }
        }
        return rst;
    }

    public EvalIndicatorValues syncMoney(){
        EvalPersonOnceData cached=get();
        if(null==cached){
            return null;
        }
        EvalIndicatorValues moneyVals=getSysIndicator(EnumIndicatorType.MONEY);
        if(null==moneyVals){
            return null;
        }
        cached.syncMoney(moneyVals);
        return moneyVals;
    }

    public EvalIndicatorValues getIndicator(String indicatorId){
        EvalPersonOnceData cached=get();
        if(null==cached){
            return null;
        }
        return cached.getMapIndicators().get(indicatorId);
    }
    public String getIndicatorVal(String indicatorId,boolean lastFlag){
        return Optional.ofNullable(getIndicator(indicatorId))
                .map(i->lastFlag?i.getLastVal():i.getCurVal())
                .orElse("");
    }
    public Map<String,String> fillCurVal(Map<String,String> mapCurVal,Set<String> indicatorIds){
        EvalPersonOnceData cached=get();
        if(null==cached){
            return mapCurVal;
        }
        return cached.fillCurVal(mapCurVal, indicatorIds);

    }


    public List<EvalIndicatorValues> getChangedIndicators(){
        EvalPersonOnceData cached=cacheData;
        if(!isValid(cached)){
            return null;
        }
        return getChangedIndicators(cached);
    }
    public List<EvalIndicatorValues> getChangedIndicators(EvalPersonOnceData data){
        if(null==data){
            return null;
        }
        return data.getMapIndicators().values()
                .stream()
                .filter(EvalIndicatorValues::isChanged)
                .collect(Collectors.toList());

    }

    public List<EvalIndicatorValues> getWatchIndicators(EvalPersonOnceData data){
        if(null==data){
            return null;
        }
        Set<String> watchedIds=PersonIndicatorIdCache.Instance().getWatchIndicatos(cacheKey.getExperimentPersonId());

        return data.getMapIndicators().values()
                .stream()
                .filter(i->watchedIds.contains(i.getIndicatorId())
                        ||Optional.ofNullable(i.getEvalNo()).orElse(0)>=data.getHeader().getEvalNo())
                .collect(Collectors.toList());
    }
    //endregion

    //region put
    public boolean putCurVal(EnumIndicatorType type, String val, boolean saveToRD){
        String indicatorId=PersonIndicatorIdCache.Instance().getSysIndicatorId(cacheKey.getExperimentPersonId(), type);
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return false;
        }
        return putCurVal(indicatorId, val,saveToRD);
    }

    public boolean putCurVal(String indicatorId, String val, boolean saveToRD){
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return false;
        }
        EvalIndicatorValues values=getIndicator(indicatorId);
        if(null==values){
            return false;
        }
        values.setCurVal(val);
        if(saveToRD){
            saveToRD(values);
        }
        return true;
    }
    public boolean putCurVal(Map<String,String> mapVals, boolean saveToRD){
        if(ShareUtil.XObject.isEmpty(mapVals)){
            return false;
        }
        Map<String,EvalIndicatorValues> map=new ConcurrentHashMap<>();
        mapVals.forEach((k,v)->{
            EvalIndicatorValues values=getIndicator(k);
            if(null==values){
                return;
            }
            values.setCurVal(v);
            if(saveToRD){
                map.put(k, values);
            }
        });
        if(saveToRD){
            saveToRD(map);
        }
        return true;
    }
    public boolean putChangeVal(String indicatorId,BigDecimal val,boolean saveToRD) {
        EvalIndicatorValues values = getIndicator(indicatorId);
        if (null == values) {
            return false;
        }
        values.setChangingVal(val);
        if (saveToRD) {
            saveToRD(values);
        }
        return true;
    }

    public boolean putFrom(EvalPersonOnceData src,int evalNo,EnumEvalFuncType funcType){
        cacheData=src.flip(evalNo,funcType);
        cacheKey.setEvalNo(evalNo);
        saveToRD(cacheData);
        return true;
    }



    public boolean startSync(EvalPersonSyncRequest req) {
        EvalPersonOnceData cached = get();
        if (null == cached) {
            return false;
        }
        final ExperimentTimePoint timePoint=req.getTimePoint();
        final EvalPersonOnceData.Header header = cached.getHeader();
        header.setEvalNo(cacheKey.getEvalNo())
                .setSyncState(EnumEvalSyncState.SYNCING)
                .setFuncType(req.getFuncType())
                .setPeriods(timePoint.getPeriod())
                .setEvalDay(timePoint.getGameDay())
                .setEvalingTime(ShareUtil.XDate.localDT2Date(timePoint.getRealTime()));
        saveToRD(header);
        String lastDaysId=PersonIndicatorIdCache.Instance().getSysIndicatorId(cacheKey.getExperimentPersonId(), EnumIndicatorType.DURATION);
        putCurVal(lastDaysId,String.valueOf( header.getLastDays()),true);
        return true;

    }
    public boolean syncHeaderIndicator(EvalPersonOnceData data) {
        if (ShareUtil.XObject.isEmpty(data)) {
            return false;
        }
        final EvalPersonOnceData.Header header = data.getHeader();
        String hpId = PersonIndicatorIdCache.Instance().getSysIndicatorId(cacheKey.getExperimentPersonId(), EnumIndicatorType.HEALTH_POINT);
        String moneyId = PersonIndicatorIdCache.Instance().getSysIndicatorId(cacheKey.getExperimentPersonId(), EnumIndicatorType.MONEY);
        if(ShareUtil.XObject.notEmpty(hpId)) {
            Optional.ofNullable(data.getMapIndicators().get(hpId))
                    .ifPresent(i -> header.setHealthIndex(i.getCurVal()));
        }
        if(ShareUtil.XObject.notEmpty(moneyId)) {
            Optional.ofNullable(data.getMapIndicators().get(moneyId))
                    .ifPresent(i -> header.setMoney(i.getCurVal()));
        }
        header.setEvaledTime(new Date());
        saveToRD(header);
        return true;

    }

    public List<EvalIndicatorValues> syncIndicators(EvalPersonOnceData data){
        if(ShareUtil.XObject.isEmpty(data)){
            return null;
        }
        List<EvalIndicatorValues> changed=getChangedIndicators(data);
        if(ShareUtil.XObject.notEmpty(changed)){
            changed.forEach(item->syncIndicator(item));
        }
        syncHeaderIndicator(data);
        data.getOldMap(true);
        return changed;
    }


    public void syncIndicator(EvalIndicatorValues src){
        if(ShareUtil.XObject.isEmpty(src)){
            return;
        }
        final boolean isChanged=src.isChanged();
        ExperimentIndicatorInstanceRsEntity cacheIndicator=PersonIndicatorIdCache.Instance().getIndicatorById(cacheKey.getExperimentPersonId(), src.getIndicatorId());
        final int SCALE4Value=PersonIndicatorIdCache.Instance().getScale(cacheIndicator);
        BigDecimal changingVal=src.getChangingVal();
        if(ShareUtil.XObject.notEmpty(changingVal)
                &&changingVal.compareTo(BigDecimal.ZERO)!=0){
            if(ShareUtil.XObject.isEmpty(src.getCurVal())){
                src.setCurVal(BigDecimalUtil.formatRoundDecimal(changingVal, SCALE4Value));
            }else if(ShareUtil.XObject.isNumber(src.getCurVal())) {
                src.setCurVal(BigDecimalOptional.valueOf(src.getCurVal()).add(changingVal).getString(SCALE4Value));
            }
        }

        if(ShareUtil.XObject.notEmpty(cacheIndicator)
                &&ShareUtil.XObject.isNumber(src.getCurVal())){
            src.setCurVal(BigDecimalOptional.valueOf(src.getCurVal())
                    .min(BigDecimalUtil.tryParseDecimalElseNull( cacheIndicator.getMin()))
                    .max(BigDecimalUtil.tryParseDecimalElseNull( cacheIndicator.getMax()))
                    .getString(SCALE4Value));
        }
        if(isChanged){
            src.setEvalNo(cacheKey.getEvalNo());
        }
        src.setSynced();

    }

    //endregion

    //region save
    public boolean save(){
        return save(cacheData);
    }
    public boolean save(EvalPersonOnceData data) {
        if (data.isSynced()) {
            return true;
        }
        data.setSyncState(EnumEvalSyncState.SYNCING);
       /* syncIndicators(data);
        data.setSyncState(EnumEvalSyncState.SYNCED2RD);
        if (!saveToRD(data)) {
            data.setSyncState(EnumEvalSyncState.SYNCING);
        }*/
        saveAsync(data);
        return true;
    }

    public boolean saveToRD(EvalPersonOnceData data){
        RMap<String,String> rmap=getRDMap();
        Map<String,String> rddata=EvalPersonOnceDataCodec.Instance().toRDMap(data);
        rmap.putAll(rddata);
        rmap.expire(Duration.ofSeconds(RDCACHEExpireSeconds));
        return true;
    }
    public boolean saveToRD(EvalPersonOnceData.Header header){
        RMap<String,String> rmap=getRDMap();
        String rdStr=EvalPersonOnceDataCodec.headerCodec().toRDString(header);
        rmap.put(EvalPersonOnceDataCodec.HASHKey4Header,rdStr);
        return true;
    }
    public boolean saveToRD(EvalIndicatorValues values) {
        RMap<String, String> rmap = getRDMap();
        String rdstr = EvalIndicatorValuesCodec.Instance().toRDString(values);
        rmap.put(values.getIndicatorId(), rdstr);
        return true;
    }
    public boolean saveToRD(Map<String,EvalIndicatorValues> mapVals) {
        RMap<String, String> rmap = getRDMap();
        Map<String, String> rddata = new HashMap<>();
        mapVals.forEach((k, v) -> {
            rddata.put(k, EvalIndicatorValuesCodec.Instance().toRDString(v));
        });
        rmap.putAll(rddata);
        return true;
    }

    public EvalPersonToSavePack toSavePack(EvalPersonOnceData data) {
        if (ShareUtil.XObject.isEmpty(data)) {
            return null;
        }
        ExperimentEvalLogEntity logEval = CopyWrapper.create(ExperimentEvalLogEntity::new)
                .endFrom(data.getHeader())
                .setFuncType(Optional.ofNullable( data.getHeader().getFuncType()).map(EnumEvalFuncType::getCode).orElse(EnumEvalFuncType.FUNCCommon.getCode()))
                .setExperimentInstanceId(cacheKey.getExperimentInstanceId())
                .setExperimentPersonId(cacheKey.getExperimentPersonId());
        logEval.setRisks(JacksonUtil.toJsonSilence(data.getRisks(), true));
        logEval.setPeriodMoney(JacksonUtil.toJsonSilence(data.getMapPeriodMoney(), true));
        final List<EvalIndicatorValues> sortIndicators=data.getMapIndicators().values().stream()
                .sorted(Comparator.comparing(i->Optional.ofNullable(i.getIndicatorName()).orElse("")))
                .collect(Collectors.toList());
        ExperimentEvalLogContentEntity logEvalContent = new ExperimentEvalLogContentEntity()
                .setEvalNo(logEval.getEvalNo())
                .setAppId(logEval.getAppId())
                .setIndicatorContent(JacksonUtil.toJsonSilence(sortIndicators, true))
                .setHealthIndexContent(JacksonUtil.toJsonSilence(data.getEvalRisks(), true));


        List<EvalIndicatorValues> watched=getWatchIndicators(data);
        final PersonIndicatorIdCache cacheIndicator=PersonIndicatorIdCache.Instance();
        List<ExperimentIndicatorLogEntity> logIndicators = ShareUtil.XCollection.map(watched, item -> {
            ExperimentIndicatorLogEntity log = ExperimentIndicatorLogEntity.builder()
                    .experimentInstanceId(cacheKey.getExperimentInstanceId())
                    .experimentPersonId(cacheKey.getExperimentPersonId())
                    .experimentIndicatorId(item.getIndicatorId())
                    .experimentIndicatorName(item.getIndicatorName())
                    .evalNo(cacheKey.getEvalNo())
                    .evalDay(data.getHeader().getEvalDay())
                    .evalTime(data.getHeader().getEvaledTime())
                    .curVal(item.getCurVal())
                    .lastVal(item.getLastVal())
                    .periodInitVal(item.getPeriodInitVal())
                    .changeVal(item.getChangingVal())
                    .build();
            Optional.ofNullable(cacheIndicator.getIndicatorById(cacheKey.getExperimentPersonId(), item.getIndicatorId()))
                    .ifPresent(i -> log.setUnit(i.getUnit())
                            .setDocType(i.getDocType().getCode())
                            .setSysType(i.getType()));
            return log;
        });

        return new EvalPersonToSavePack()
                .setHeader(data.getHeader())
                .setLogEval(logEval)
                .setLogEvalContent(logEvalContent)
                .setLogIndicators(logIndicators);


    }

    public void saveAsync(EvalPersonOnceData data){
        CompletableFuture.runAsync(()->{
            try {
                data.setSyncState(EnumEvalSyncState.SYNCED2RD);
                if (!saveToRD(data)) {
                    data.setSyncState(EnumEvalSyncState.SYNCING);
                }
                saveToDB(toSavePack(data));
            }catch (Exception ex) {
                String risks=JacksonUtil.toJsonSilence(data.getEvalRisks(), true);
                StringBuilder sb = new StringBuilder("EVALTrace--")
                        .append(this.getClass().getName())
                        .append(".saveAsync error")
                        .append(" key:").append(cacheKey)
                        .append(" header:").append(data.getHeader())
                        .append(" evalRisks:").append(risks.length()).append("-").append(risks);

                log.error(sb.toString(), ex);
                sb.setLength(0);
            }
        });
    }
    public void saveToDBAsync(EvalPersonToSavePack pack){
        CompletableFuture.runAsync(()->saveToDB(pack));
    }
    public boolean saveToDB(EvalPersonToSavePack pack) {
        experimentEvalLogDao.tranSave(pack.getLogEval(), List.of(pack.getLogEvalContent()),false);
        experimentIndicatorLogDao.tranSaveBatch(pack.getLogIndicators());
        pack.getHeader().setSyncState(EnumEvalSyncState.SYNCED2DB);
        return true;
    }


    //endregion

    //region load

    public EvalPersonOnceData load() {
        if(ShareUtil.XObject.isEmpty(cacheKey.getExperimentInstanceId())){
            Optional.ofNullable( PersonIndicatorIdCache.Instance().getPerson(cacheKey.getExperimentPersonId()))
                    .ifPresent(i->cacheKey.setExperimentInstanceId(i.getExperimentInstanceId()));
        }
        EvalPersonOnceData cached=loadFromRD();
        if(isValid(cached)){
            cached.getOldMap(true);
            return cached;
        }
        cached=loadFromDB();
        if(ShareUtil.XObject.isEmpty(cached)){
            return null;
        }
        cached.getOldMap(true);
        saveToRD(cached);
        return cached;
    }

    public EvalPersonOnceData loadFromRD() {
        RMap<String, String> rmap = getRDMap();
        EvalPersonOnceData cached = EvalPersonOnceDataCodec.Instance().fromRDMap(rmap);
        return isValid(cached)?cached:null;
    }

    public EvalPersonOnceData loadFromDB(){
        ExperimentEvalLogEntity rowLog= experimentEvalLogDao.getByPersonIdXEvalNo(cacheKey.getExperimentPersonId(), cacheKey.getEvalNo());
        if(ShareUtil.XObject.isEmpty(rowLog)){
            return null;
        }
        return loadFromDB(rowLog);
    }
    public EvalPersonOnceData loadFromDB(ExperimentEvalLogEntity rowLog){
        EvalPersonOnceData rst=new EvalPersonOnceData();
        rst.setHeader(CopyWrapper.create(EvalPersonOnceData.Header::new)
                .endFrom(rowLog)
                .setSyncState(EnumEvalSyncState.SYNCED2DB)
                .setFuncType(EnumEvalFuncType.of(rowLog.getFuncType()))
        );

        if(ShareUtil.XObject.notEmpty(rowLog.getRisks())) {
            rst.setRisks(JacksonUtil.fromJsonSilence(rowLog.getRisks(), new TypeReference<>() {}));
        }
        if(ShareUtil.XObject.notEmpty(rowLog.getPeriodMoney())){
            rst.getMapPeriodMoney().putAll(ShareUtil.XObject.defaultIfNull(JacksonUtil.fromJsonSilence(rowLog.getPeriodMoney(), new TypeReference<>() {}),Collections.emptyMap()));
        }
        ExperimentEvalLogContentEntity rowLogContent=experimentEvalLogDao.getByExperimentEvalLogId(rowLog.getExperimentEvalLogId(),
                ExperimentEvalLogContentEntity::getIndicatorContent);
        if(ShareUtil.XObject.allNotEmpty(rowLogContent,()->rowLogContent.getIndicatorContent())){
            List<EvalIndicatorValues> indicators=JacksonUtil.fromJsonSilence(rowLogContent.getIndicatorContent(),new TypeReference<>() {
            });
            indicators.forEach(item->rst.getMapIndicators().put(item.getIndicatorId(), item));

        }
        return rst;

    }
    //endregion

    private RMap<String,String> getRDMap() {
        return redissonClient.getMap(RDCACHEPrefix.concat(cacheKey.getKeyString()));
    }







}
