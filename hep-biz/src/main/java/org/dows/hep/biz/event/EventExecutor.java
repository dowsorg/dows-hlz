package org.dows.hep.biz.event;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.util.ShareUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : wuzl
 * @date : 2023/6/19 16:37
 */
@Slf4j
public class EventExecutor {
    static final int DFTPOOLNum=3;
    static final int DFTCorePoolSize=5;
    static final int DFTMaxPoolSize=6;

    static final int DFTQUEUESize=500;
    private static final EventExecutor s_instance=new EventExecutor(DFTPOOLNum, DFTCorePoolSize,DFTMaxPoolSize,DFTQUEUESize);
    public static EventExecutor Instance(){
        return s_instance;
    }


    private final ThreadPoolExecutor[] roundPools;

    private final ThreadPoolExecutor fixedPool;


    private final AtomicInteger pointer=new AtomicInteger();
    private EventExecutor(int poolNum,int corePoolSize,int maxPoolSize,int queueSize){
        roundPools =new ThreadPoolExecutor[poolNum];
        for(int i=0;i<poolNum;i++) {
           roundPools[i] = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(queueSize),
                    new ThreadFactoryBuilder().setNameFormat("EventRoundPool-%d").build(),
                    new ThreadPoolAbortPolicy());
        }
        fixedPool= new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueSize),
                new ThreadFactoryBuilder().setNameFormat("EventFixedPool-%d").build(),
                new ThreadPoolAbortPolicy());
    }
    public ExecutorService getThreadPool(){
        return roundPools[pointer.getAndIncrement()% roundPools.length];
    }

    public ExecutorService getFixedThreadPool(){
        return fixedPool;
    }

    public void shutDown(){
        for(ThreadPoolExecutor item: roundPools){
            shutDown(item);
        }
        shutDown(fixedPool);
    }
    private void shutDown(ThreadPoolExecutor pool){
        if(ShareUtil.XObject.isEmpty(pool)){
            return;
        }
        StringBuilder sb=new StringBuilder();
        long ts=logCostTime(sb,"EventExecutor--");
        try {
            sb.append(" closing. {").append(pool).append("}");
            pool.shutdown();
            ts=logCostTime(sb,"closed",ts);

        }catch (Exception ex){
            ts=logCostTime(sb,"failed",ts);
            log.error(sb.toString() ,ex);
        }finally {
            log.info(sb.toString());
        }
    }

    private long logCostTime(StringBuilder sb,String start){
        sb.append(start);
        return System.currentTimeMillis();
    }

    long logCostTime(StringBuilder sb,String func,long ts){
        long newTs=System.currentTimeMillis();
        sb.append(" ").append(func).append(":").append((newTs-ts));
        return newTs;
    }




}
