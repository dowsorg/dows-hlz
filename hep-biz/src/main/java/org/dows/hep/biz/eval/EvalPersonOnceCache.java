package org.dows.hep.biz.eval;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.eval.data.EvalPersonOnceCacheKey;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:42
 */
@Component
@Slf4j
public class EvalPersonOnceCache extends BaseLoadingCache<EvalPersonOnceCacheKey, EvalPersonOnceHolder> {
    private static volatile EvalPersonOnceCache s_instance;

    public static EvalPersonOnceCache Instance() {
        return s_instance;
    }

    protected final static int CACHEInitCapacity = 500;
    protected final static int CACHEMaxSize = 9000;
    protected final static int CACHEExpireSeconds = 60 * 60 * 24*3;


    private EvalPersonOnceCache() {
        super(CACHEInitCapacity, CACHEMaxSize, CACHEExpireSeconds, 0);
        s_instance = this;
    }

    @Autowired
    private RedissonClient redissonClient;

    public EvalPersonOnceHolder getHolder(String experimentInstanceId, String experimentPersonId, Integer evalNo){
        EvalPersonOnceCacheKey cacheKey=new EvalPersonOnceCacheKey(experimentInstanceId,experimentPersonId)
                .setEvalNo(evalNo);
        EvalPersonOnceHolder holder=loadingCache().get(cacheKey);
        return holder.setEvalNo(evalNo);
    }

    @Override
    protected EvalPersonOnceHolder load(EvalPersonOnceCacheKey key) {
        return new EvalPersonOnceHolder(key, redissonClient);
    }
}
