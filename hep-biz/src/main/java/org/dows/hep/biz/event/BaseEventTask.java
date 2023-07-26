package org.dows.hep.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.biz.dao.ExperimentEventDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : wuzl
 * @date : 2023/7/19 16:21
 */
@Slf4j
public abstract class BaseEventTask implements Callable<Integer>,Runnable {
    protected final ExperimentCacheKey experimentKey;
    protected final int RUNCode4Fail=-1;
    protected final int RUNCode4Silence=0;
    protected final int RUNCode4Succ=1;

    protected BaseEventTask(ExperimentCacheKey experimentKey){
        this.experimentKey=experimentKey;
    }

    @Override
    public void run() {
        try {
            call();
        }catch (Exception ex) {
            exceptionally(ex);
        }
    }
    protected void exceptionally(Exception ex){
        logError(ex, "run", ex.getMessage());
    }

    public abstract Integer call() throws Exception;

    //region dao

    private ExperimentEventDao experimentEventDao;
    protected ExperimentEventDao getExperimentEventDao(){
        if(null==experimentEventDao){
            experimentEventDao=getDao(ExperimentEventDao.class);
        }
        return experimentEventDao;
    }
    protected  <T> T getDao(Class<T> clazz){
        return CrudContextHolder.getBean(clazz);
    }
    //endregion

    //region tools
    protected void logError(String func, String msg,Object... args){
        logError(null, func,msg,args);
    }
    protected void logError(Throwable ex, String func, String msg,Object... args){
        String str=String.format("%s.%s@%s[%s] input:%s %s", this.getClass().getName(), func, LocalDateTime.now(),this.hashCode(),
                this.experimentKey, String.format(Optional.ofNullable(msg).orElse(""), args));
        log.error(str,ex);
        //log.info(str);
    }
    protected void logInfo(String func, String msg,Object... args) {
        String str = String.format("%s.%s@%s[%s] input:%s %s", this.getClass().getName(), func, LocalDateTime.now(),this.hashCode(),
                this.experimentKey, String.format(Optional.ofNullable(msg).orElse(""), args));
        log.info(str);
    }

    public static class RunStat {
        public RunStat(Integer threadSize){
            theadCounter=new AtomicInteger(threadSize);
        }
        public final AtomicInteger theadCounter;
        public final AtomicInteger failTheadCounter=new AtomicInteger();
        public final AtomicInteger doneCounter=new AtomicInteger();

        public final AtomicInteger todoCounter=new AtomicInteger();
    }
}
