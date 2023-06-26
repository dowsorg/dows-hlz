package org.dows.hep.biz.event;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : wuzl
 * @date : 2023/6/19 16:37
 */
@Slf4j
public class EventExecutor {
    static final int DFTPOOLNum=3;
    static final int DFTCorePoolSize=5;
    static final int DFTMaxPoolSize=8;

    static final int DFTQUEUESize=50;
    private static final EventExecutor s_instance=new EventExecutor(DFTPOOLNum, DFTCorePoolSize,DFTMaxPoolSize,DFTQUEUESize);
    public static EventExecutor Instance(){
        return s_instance;
    }


    private final ThreadPoolExecutor[] pools;

    private volatile int pointer=0;
    private EventExecutor(int poolNum,int corePoolSize,int maxPoolSize,int queueSize){
        pools=new ThreadPoolExecutor[poolNum];
        for(int i=0;i<poolNum;i++) {
           pools[i] = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(queueSize),
                    new ThreadFactoryBuilder().setNameFormat("EventExecutor-%d").build(),
                    new ThreadPoolAbortPolicy());
        }
    }
    public ExecutorService getThreadPool(){
        return pools[(pointer++)%pools.length];
    }

    public void shutDown(){
        for(ThreadPoolExecutor item: pools){
            try {
                item.shutdown();
            }catch (Exception ex){
                log.error("EventExecutor.shutDown",ex);
            }
        }
    }

    //region submit
    //endregion


}
