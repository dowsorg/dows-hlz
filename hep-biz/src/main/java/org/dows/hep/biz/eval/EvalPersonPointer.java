package org.dows.hep.biz.eval;

import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.biz.dao.ExperimentEvalLogDao;
import org.dows.hep.biz.eval.data.EvalPersonCacheKey;
import org.dows.hep.biz.eval.data.EvalPersonOnceData;
import org.dows.hep.biz.eval.data.EvalPersonSyncRequest;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentEvalLogEntity;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:17
 */


@Accessors(chain = true)
public class EvalPersonPointer {
    private static final int TRYLOCKSeconds4Read=3;
    private static final int TRYLOCKSeconds4Write=5;

    public EvalPersonPointer(EvalPersonCacheKey cacheKey){
        this.cacheKey=cacheKey;
        this.experimentEvalLogDao= CrudContextHolder.getBean(ExperimentEvalLogDao.class);
    }

    private final ExperimentEvalLogDao experimentEvalLogDao;
    private final EvalPersonCacheKey cacheKey;

    private final AtomicInteger curEvalNo=new AtomicInteger();

    private final ReadWriteLock rwlock=new ReentrantReadWriteLock();



    @SneakyThrows
    public EvalPersonOnceHolder getCurHolder() {
        int evalNo=getCurEvalNoWithReadLock();
        return getHolder(evalNo);
    }
    @SneakyThrows
    public EvalPersonOnceHolder getLastHolder()  {
        int evalNo = Math.max(0, getCurEvalNoWithReadLock() - 1);
        return getHolder(evalNo);
    }
    @SneakyThrows
    public EvalPersonOnceHolder getNextHolder() {
        int evalNo = Math.max(0, getCurEvalNoWithReadLock() + 1);
        return getHolder(evalNo);
    }
    @SneakyThrows
    public boolean startSync(EvalPersonSyncRequest req) {
        return getCurHolder().startSync(req);
    }
    @SneakyThrows
    public boolean sync(boolean isPeriodInit)  {
        boolean lockFlag = rwlock.writeLock().tryLock(TRYLOCKSeconds4Write, TimeUnit.SECONDS);
        try {
            final int evalNo = curEvalNo.get();
            final int nextEvalNo = evalNo + 1;
            EvalPersonOnceHolder curHolder = getHolder(evalNo);
            EvalPersonOnceHolder nextHolder = getHolder(nextEvalNo);
            curHolder.save();
            nextHolder.putFrom(curHolder.getPresent(), nextEvalNo, isPeriodInit);
            curEvalNo.incrementAndGet();
            return true;
        } finally {
            if (lockFlag) {
                rwlock.writeLock().unlock();
            }
        }
    }
    @SneakyThrows
    public boolean load()  {
        boolean lockFlag = rwlock.writeLock().tryLock(TRYLOCKSeconds4Write, TimeUnit.SECONDS);
        try {
            ExperimentEvalLogEntity rowLog= experimentEvalLogDao.getCurrentByPersonId(cacheKey.getExperimentPersonId(),
                    ExperimentEvalLogEntity::getEvalNo,
                    ExperimentEvalLogEntity::getPeriods);
            AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(rowLog))
                    .throwMessage(String.format("人物指标未初始化[id:%s]", cacheKey.getExperimentPersonId()));
            ExperimentTimePoint timePoint= ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create("3", cacheKey.getExperimentInstanceId()), LocalDateTime.now(),false );
            AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(timePoint))
                    .throwMessage(String.format("未找到实验当前时间点[id:%s]", cacheKey.getExperimentInstanceId()));

            final int evalNo=rowLog.getEvalNo();
            int nextEvalNo=evalNo+1;
            EvalPersonOnceHolder curHolder = getHolder(nextEvalNo);
            EvalPersonOnceData curData= curHolder.get(nextEvalNo,true);
            if(curHolder.isValid(curData)){
                if(curData.isSyncing()) {
                    curHolder.save();
                }
                if(curData.isSynced()) {
                    nextEvalNo++;
                    getHolder(nextEvalNo).putFrom(curData, nextEvalNo, !rowLog.getPeriods().equals(curData.getHeader().getPeriods()));
                    curEvalNo.set(nextEvalNo);
                    return true;
                }
                curEvalNo.set(nextEvalNo);
                return true;
            }
            curHolder = getHolder(evalNo);
            curData=curHolder.get(evalNo,true);
            getHolder(nextEvalNo).putFrom(curData,nextEvalNo,!rowLog.getPeriods().equals(curData.getHeader().getPeriods()));
            curEvalNo.set(nextEvalNo);
            return true;
        } finally {
            if (lockFlag) {
                rwlock.writeLock().unlock();
            }
        }

    }


    private int getCurEvalNoWithReadLock() throws InterruptedException{
        boolean lockFlag= rwlock.readLock().tryLock(TRYLOCKSeconds4Read, TimeUnit.SECONDS);
        try{
            return curEvalNo.get();
        }finally{
            if(lockFlag){
                rwlock.readLock().unlock();
            }
        }
    }
    private EvalPersonOnceHolder getHolder(int evalNo) {
        return EvalPersonOnceCache.Instance().getHolder(cacheKey.getExperimentInstanceId(), cacheKey.getExperimentPersonId(), evalNo);
    }


}
