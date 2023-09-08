package org.dows.hep.biz.eval;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.biz.dao.ExperimentEvalLogDao;
import org.dows.hep.biz.dao.ExperimentIndicatorLogDao;
import org.dows.hep.biz.eval.codec.EvalIndicatorValuesCodec;
import org.dows.hep.biz.eval.codec.EvalPersonOnceDataCodec;
import org.dows.hep.biz.eval.data.*;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.util.*;
import org.dows.hep.entity.*;
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
    public EvalIndicatorValues getIndicator(String indicatorId){
        EvalPersonOnceData cached=get();
        if(null==cached){
            return null;
        }
        return cached.getMapIndicators().get(indicatorId);
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
    //endregion

    //region put

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

    public boolean putFrom(EvalPersonOnceData src,int evalNo,boolean isPeriodInit){
        cacheData=src.flip(evalNo,isPeriodInit);
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
                .setEvalTime(ShareUtil.XDate.localDT2Date(timePoint.getRealTime()));
        saveToRD(header);
        String lastDaysId=PersonIndicatorIdCache.Instance().getSysIndicatorId(cacheKey.getExperimentPersonId(), EnumIndicatorType.DURATION);
        putCurVal(lastDaysId,String.valueOf( header.getLastDays()),true);
        return true;

    }
    public boolean syncHeaderIndicator(EvalPersonOnceData data) {
        if (ShareUtil.XObject.isEmpty(data)) {
            return false;
        }
        String hpId = PersonIndicatorIdCache.Instance().getSysIndicatorId(cacheKey.getExperimentPersonId(), EnumIndicatorType.HEALTH_POINT);
        String moneyId = PersonIndicatorIdCache.Instance().getSysIndicatorId(cacheKey.getExperimentPersonId(), EnumIndicatorType.MONEY);
        final EvalPersonOnceData.Header header = data.getHeader();
        Optional.ofNullable(data.getMapIndicators().get(hpId))
                .ifPresent(i -> header.setHealthIndex(i.getCurVal()));
        Optional.ofNullable(data.getMapIndicators().get(moneyId))
                .ifPresent(i -> header.setMoney(i.getCurVal()));
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
        return changed;
    }
    public void syncIndicator(EvalIndicatorValues src){
        if(ShareUtil.XObject.isEmpty(src)){
            return;
        }
        final boolean isChanged=src.isChanged();
        final int SCALE4Value=2;
        BigDecimal changingVal=src.getChangingVal();
        if(ShareUtil.XObject.notEmpty(changingVal)
                &&changingVal.compareTo(BigDecimal.ZERO)!=0){

            if(ShareUtil.XObject.allEmpty(src.getCurVal(),src.getLastVal())){
                src.setCurVal(BigDecimalUtil.formatRoundDecimal(changingVal, SCALE4Value));
            }else if(ShareUtil.XObject.isNumber(src.getCurVal())){
                src.setCurVal(BigDecimalOptional.valueOf(src.getCurVal()).add(changingVal).getString(SCALE4Value));
            }else if(ShareUtil.XObject.isNumber(src.getLastVal())){
                src.setCurVal(BigDecimalOptional.valueOf(src.getLastVal()).add(changingVal).getString(SCALE4Value));
            }
        }
        ExperimentIndicatorInstanceRsEntity cacheIndicator=PersonIndicatorIdCache.Instance().getIndicatorById(cacheKey.getExperimentPersonId(), src.getIndicatorId());
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
        syncIndicators(data);
        data.setSyncState(EnumEvalSyncState.SYNCED2RD);
        if (!saveToRD(data)) {
            data.setSyncState(EnumEvalSyncState.SYNCING);
        }
        EvalPersonToSavePack savePack = toSavePack(data);
        saveToDBAsync(savePack);
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
                .setFuncType(data.getHeader().getFuncType().getCode())
                .setExperimentInstanceId(cacheKey.getExperimentInstanceId())
                .setExperimentPersonId(cacheKey.getExperimentPersonId());
        logEval.setRiks(JacksonUtil.toJsonSilence(data.getRisks(), true));
        ExperimentEvalLogContentEntity logEvalContent = new ExperimentEvalLogContentEntity()
                .setEvalNo(logEval.getEvalNo())
                .setAppId(logEval.getAppId())
                .setIndicatorContent(JacksonUtil.toJsonSilence(data.getMapIndicators().values(), true))
                .setHealthIndexContent(JacksonUtil.toJsonSilence(data.getEvalRisks(), true));

        final Integer evalNo=data.getHeader().getEvalNo();
        List<EvalIndicatorValues> changed=data.getMapIndicators().values()
                .stream()
                .filter(i->evalNo.equals(i.getEvalNo()))
                .collect(Collectors.toList());
        final PersonIndicatorIdCache cacheIndicator=PersonIndicatorIdCache.Instance();
        List<ExperimentIndicatorLogEntity> logIndicators = ShareUtil.XCollection.map(changed, item ->
                ExperimentIndicatorLogEntity.builder()
                        .experimentInstanceId(cacheKey.getExperimentInstanceId())
                        .experimentPersonId(cacheKey.getExperimentPersonId())
                        .experimentIndicatorId(item.getIndicatorId())
                        .experimentIndicatorName(item.getIndicatorName())
                        .evalNo(cacheKey.getEvalNo())
                        .evalDay(data.getHeader().getEvalDay())
                        .evalTime(data.getHeader().getEvalTime())
                        .unit(Optional.ofNullable( cacheIndicator.getIndicatorById(cacheKey.getExperimentPersonId(), item.getIndicatorId()))
                                .map(ExperimentIndicatorInstanceRsEntity::getUnit)
                                .orElse(""))
                        .curVal(item.getCurVal())
                        .lastVal(item.getLastVal())
                        .periodInitVal(item.getPeriodInitVal())
                        .changeVal(item.getChangingVal())
                        .build());

        return new EvalPersonToSavePack()
                .setHeader(data.getHeader())
                .setLogEval(logEval)
                .setLogEvalContent(logEvalContent)
                .setLogIndicators(logIndicators);


    }

    public void saveToDBAsync(EvalPersonToSavePack pack){
        CompletableFuture.runAsync(()->saveToDB(pack));
    }
    public boolean saveToDB(EvalPersonToSavePack pack){
        if(!experimentEvalLogDao.tranSave(pack.getLogEval(), List.of(pack.getLogEvalContent()),false , ()->{
            if(ShareUtil.XObject.isEmpty(pack.getLogIndicators())){
                return true;
            }
            return experimentIndicatorLogDao.tranSaveBatch(pack.getLogIndicators());
        })){
            return false;
        }
        pack.getHeader().setSyncState(EnumEvalSyncState.SYNCED2DB);
        return true;
    }


    //endregion

    //region load

    public EvalPersonOnceData load() {
        EvalPersonOnceData cached=loadFromRD();
        if(isValid(cached)){
            return cached;
        }
        cached=loadFromDB();
        if(ShareUtil.XObject.isEmpty(cached)){
            return null;
        }
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
        );

        if(ShareUtil.XObject.notEmpty(rowLog.getRiks())) {
            rst.setRisks(JacksonUtil.fromJsonSilence(rowLog.getRiks(), new TypeReference<>() {
            }));
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


    //region 兼容老版本
    public Map<String,String> getCastMapCur(Set<String> indicatorIds){
        return fillCastMapCur(new HashMap<>(), indicatorIds);
    }
    public Map<String, ExperimentIndicatorValRsEntity> getCastMapCur(){
        return fillCastMapCur(null);
    }
    public Map<String,ExperimentIndicatorValRsEntity> getCastMapLast(){
        return fillCastMapLast(null);
    }

    public Map<String,String>  fillCastMapCur( Map<String,String>  rst,Set<String> indicatorIds) {
        EvalPersonOnceData cached=get();
        if(null==cached){
            return rst;
        }
        indicatorIds.forEach(i->{
            EvalIndicatorValues values= cached.getMapIndicators().get(i);
            if(null==values){
                return;
            }
            rst.put(i,values.getCurVal());
        });
        return rst;

    }
    @SneakyThrows
    public Map<String,ExperimentIndicatorValRsEntity> fillCastMapCur(Map<String,ExperimentIndicatorValRsEntity> src) {
        if (null == src) {
            src = new HashMap<>();
        } else {
            src.clear();
        }
        EvalPersonOnceData cached=get();
        if(null==cached){
            return src;
        }
        for (EvalIndicatorValues item : cached.getMapIndicators().values()) {
            src.computeIfAbsent(item.getIndicatorId(), k -> new ExperimentIndicatorValRsEntity())
                    .setIndicatorInstanceId(item.getIndicatorId())
                    .setCurrentVal(item.getCurVal())
                    .setInitVal(item.getPeriodInitVal());
        }
        return src;
    }

    @SneakyThrows
    public Map<String,ExperimentIndicatorValRsEntity> fillCastMapLast(Map<String,ExperimentIndicatorValRsEntity> src) {
        if (null == src) {
            src = new HashMap<>();
        } else {
            src.clear();
        }
        EvalPersonOnceData cached=get();
        if(null==cached){
            return src;
        }
        for (EvalIndicatorValues item : cached.getMapIndicators().values()) {
            src.computeIfAbsent(item.getIndicatorId(), k -> new ExperimentIndicatorValRsEntity())
                    .setIndicatorInstanceId(item.getIndicatorId())
                    .setCurrentVal(item.getLastVal())
                    .setInitVal(item.getPeriodInitVal());
        }
        return src;
    }
    //endregion




}
