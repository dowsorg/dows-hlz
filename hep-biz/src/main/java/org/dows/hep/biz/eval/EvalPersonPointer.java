package org.dows.hep.biz.eval;

import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.enums.EnumEvalFuncType;
import org.dows.hep.biz.dao.ExperimentEvalLogDao;
import org.dows.hep.biz.eval.data.EvalPersonCacheKey;
import org.dows.hep.biz.eval.data.EvalPersonOnceData;
import org.dows.hep.biz.eval.data.EvalPersonSyncRequest;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEvalLogEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:17
 */


@Accessors(chain = true)
public class EvalPersonPointer {

    private static final int TRYLOCKSeconds4Write=4;

    public EvalPersonPointer(EvalPersonCacheKey cacheKey){
        this.cacheKey=cacheKey;
        this.experimentEvalLogDao= CrudContextHolder.getBean(ExperimentEvalLogDao.class);
    }

    private final ExperimentEvalLogDao experimentEvalLogDao;
    private final EvalPersonCacheKey cacheKey;

    private final AtomicInteger curEvalNo=new AtomicInteger();


    private final ReentrantLock rlock=new ReentrantLock();




    public EvalPersonOnceHolder getCurHolder() {
        return getHolder(curEvalNo.get());
    }

    public EvalPersonOnceHolder getLastHolder()  {
        int evalNo = Math.max(0, curEvalNo.get() - 1);
        return getHolder(evalNo);
    }

    public EvalPersonOnceHolder getNextHolder() {
        int evalNo = Math.max(0, curEvalNo.get() + 1);
        return getHolder(evalNo);
    }

    public boolean startSync(EvalPersonSyncRequest req) {
        return getCurHolder().startSync(req);
    }
    @SneakyThrows
    public boolean sync(EnumEvalFuncType funcType)  {
        boolean lockFlag = rlock.tryLock(TRYLOCKSeconds4Write, TimeUnit.SECONDS);
        try {
            final int evalNo = curEvalNo.get();
            final int nextEvalNo = evalNo + 1;
            EvalPersonOnceHolder curHolder = getHolder(evalNo);
            EvalPersonOnceHolder nextHolder = getHolder(nextEvalNo);
            Optional.ofNullable(curHolder.getPresent())
                    .map(EvalPersonOnceData::getHeader)
                    .ifPresent(i->i.setFuncType(funcType));
            if(funcType.isPeriodEnd()){
                curHolder.syncMoney();
            }
            curHolder.syncIndicators();
            nextHolder.putFrom(curHolder.getPresent(), nextEvalNo, funcType);
            if(funcType.isNewPeriod()){
                nextHolder.syncMoney();
            }
            curEvalNo.incrementAndGet();
            curHolder.save();
            return true;
        } finally {
            if (lockFlag) {
                rlock.unlock();
            }
        }
    }
    @SneakyThrows
    public boolean load()  {
        if(ShareUtil.XObject.isEmpty(cacheKey.getExperimentInstanceId())){
            Optional.ofNullable( PersonIndicatorIdCache.Instance().getPerson(cacheKey.getExperimentPersonId()))
                    .ifPresent(i->cacheKey.setExperimentInstanceId(i.getExperimentInstanceId()));
        }
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(ExperimentCacheKey.create("3",cacheKey.getExperimentInstanceId()),false);
        if(ShareUtil.XObject.isEmpty(exptColl)){
            return false;
        }
        rlock.lock();
        try {

            ExperimentEvalLogEntity rowLog= experimentEvalLogDao.getCurrentByPersonId(cacheKey.getExperimentPersonId(),
                    ExperimentEvalLogEntity::getEvalNo,
                    ExperimentEvalLogEntity::getPeriods);
            AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(rowLog))
                    .throwMessage(String.format("人物指标未初始化[id:%s]", cacheKey.getExperimentPersonId()));
            ExperimentTimePoint timePoint= ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create("3", cacheKey.getExperimentInstanceId()), LocalDateTime.now(),true );
            AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(timePoint))
                    .throwMessage(String.format("未找到实验当前时间点[id:%s]", cacheKey.getExperimentInstanceId()));

            final int evalNo=rowLog.getEvalNo();
            int nextEvalNo=evalNo+1;
            EvalPersonOnceHolder curHolder = getHolder(nextEvalNo);
            EvalPersonOnceData curData= curHolder.get(nextEvalNo,true);
            if(curHolder.isValid(curData)){
                final EnumEvalFuncType funcType=getFuncType(curData.getHeader().getPeriods(), timePoint.getPeriod());
                if(curData.getHeader().getPeriods()>exptColl.getPeriods()) {
                    curEvalNo.set(nextEvalNo);
                    return true;
                }
                if(curData.isSyncing()) {
                    curHolder.save();
                }
                if(curData.isSynced()) {
                    nextEvalNo++;
                    getHolder(nextEvalNo).putFrom(curData, nextEvalNo, funcType);
                    curEvalNo.set(nextEvalNo);
                    return true;
                }
                curEvalNo.set(nextEvalNo);
                return true;
            }
            curHolder = getHolder(evalNo);
            curData=curHolder.get(evalNo,true);
            getHolder(nextEvalNo).putFrom(curData,nextEvalNo,getFuncType(curData.getHeader().getPeriods(), timePoint.getPeriod()));
            curEvalNo.set(nextEvalNo);
            return true;
        } finally {
            if(rlock.isHeldByCurrentThread()){
                rlock.unlock();
            }
        }

    }

    private EnumEvalFuncType getFuncType(int syncedPeriod,int curPeriod){
        if(syncedPeriod<curPeriod){
            return EnumEvalFuncType.PERIODEnd;
        }
        return EnumEvalFuncType.INIT;
    }



    private EvalPersonOnceHolder getHolder(int evalNo) {
        return EvalPersonOnceCache.Instance().getHolder(cacheKey.getExperimentInstanceId(), cacheKey.getExperimentPersonId(), evalNo);
    }


}
