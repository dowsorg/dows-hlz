package org.dows.hep.biz.eval;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.dows.hep.biz.event.ThreadPoolAbortPolicy;

import java.util.concurrent.*;

/**
 * @author : wuzl
 * @date : 2023/9/10 20:26
 */
public class EvalPersonExecutor {
    static final int DFTCorePoolSize = 6;
    static final int DFTMaxPoolSize = 6;

    static final int DFTQUEUESize = 500;

    private static final EvalPersonExecutor s_instance = new EvalPersonExecutor(DFTCorePoolSize, DFTMaxPoolSize, DFTQUEUESize);

    public static EvalPersonExecutor Instance() {
        return s_instance;
    }

    private EvalPersonExecutor(int corePoolSize, int maxPoolSize, int queueSize) {
        threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueSize),
                new ThreadFactoryBuilder().setNameFormat("EvalPersonExecutor-%d").build(),
                new ThreadPoolAbortPolicy());
    }

    private final ThreadPoolExecutor threadPool;

    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
