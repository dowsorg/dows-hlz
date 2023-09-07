package org.dows.hep.biz.eval;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.eval.data.EvalPersonCacheKey;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:09
 */
@Component
@Slf4j
public class EvalPersonCache extends BaseLoadingCache<EvalPersonCacheKey, EvalPersonPointer> {
    private static volatile EvalPersonCache s_instance;

    public static EvalPersonCache Instance() {
        return s_instance;
    }

    protected final static int CACHEInitCapacity = 100;
    protected final static int CACHEMaxSize = 1000;
    protected final static int CACHEExpireSeconds = 60 * 60 * 24*7;


    private EvalPersonCache() {
        super(CACHEInitCapacity, CACHEMaxSize, CACHEExpireSeconds, 0);
        s_instance = this;
    }

    public EvalPersonPointer getPointer(String experimentInstanceId,String experimentPersonId){
        EvalPersonCacheKey cacheKey=new EvalPersonCacheKey(experimentInstanceId,experimentPersonId);
        return loadingCache().get(cacheKey);
    }

    @Override
    @SneakyThrows
    protected EvalPersonPointer load(EvalPersonCacheKey key)  {
        EvalPersonPointer pointer= new EvalPersonPointer(key);
        pointer.load();
        return pointer;
    }
}
