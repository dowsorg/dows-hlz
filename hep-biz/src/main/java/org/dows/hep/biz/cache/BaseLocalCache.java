package org.dows.hep.biz.cache;

import org.apache.poi.ss.formula.functions.T;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 本地缓存基类
 *
 * @author : wuzl
 * @date : 2023/4/22 18:51
 */
public abstract class BaseLocalCache<T> {
    //region .ctor
    protected volatile T cache;
    //过期分钟数
    protected volatile long expireInMinutes;
    //刷新锁定分钟数
    protected final int refreshLockMinutes;
    protected volatile LocalDateTime expireAt;
    protected volatile LocalDateTime refreshLockAt;
    protected static final int COMMONRefreshLockMinutes=5;

    protected final ReentrantLock lock=new ReentrantLock();
    protected BaseLocalCache(LocalDateTime expireAt) {
        this(expireAt, ChronoUnit.MINUTES.between(LocalDateTime.now(), expireAt), COMMONRefreshLockMinutes);
    }
    protected BaseLocalCache(LocalDateTime expireAt, long expireInMinutes){
        this(expireAt,expireInMinutes,COMMONRefreshLockMinutes);
    }
    protected BaseLocalCache(long expireInMinutes){
        this(null, expireInMinutes,COMMONRefreshLockMinutes);
    }
    protected BaseLocalCache(long expireInMinutes,int refreshLockMinutes){
        this(null, expireInMinutes,refreshLockMinutes);
    }
    protected BaseLocalCache(LocalDateTime expireAt,long expireInMinutes, int refreshLockMinutes){
        this.expireAt=expireAt;
        this.expireInMinutes=expireInMinutes;
        this.refreshLockMinutes=refreshLockMinutes;
        if(null==this.expireAt&& this.expireInMinutes>0) {
            this.expireAt = LocalDateTime.now().plusMinutes(expireInMinutes);
        }
    }
    //endregion

    //region facade
    public boolean isExpired(){
        if(null==expireAt)
            return false;
        return LocalDateTime.now().compareTo(expireAt)>=0;
    }
    public void init(){
        ensureCache();
    }
    public void clear() {
        T cacheOld=cache;
        cache = null;
        clearOld(cacheOld);
    }

    public boolean tryRefresh(){
        if(null!=refreshLockAt&& LocalDateTime.now().compareTo(refreshLockAt)<0)
            return false;
        lock.lock();
        try{
            if(null!=refreshLockAt&&LocalDateTime.now().compareTo(refreshLockAt)<0)
                return false;
            this.refreshLockAt=LocalDateTime.now().plusMinutes(refreshLockMinutes);
            this.coreRefresh();
            return true;
        }finally {
            lock.unlock();
        }

    }
    public void refresh(){
       coreRefresh();
    }
    protected T coreRefresh(){
        T cacheNew = load();
        if(this.expireInMinutes>0) {
            this.expireAt = LocalDateTime.now().plusMinutes(expireInMinutes);
        }
        T cacheOld=cache;
        cache = cacheNew;
        clearOld(cacheOld);
        return cacheNew;
    }
    //endregion

    protected abstract T load();

    protected void clearOld(T old){

    }
    protected T ensureCache(){
        if(null!=cache&&!isExpired())
            return cache;
        lock.lock();
        try{
            if (null != cache && !isExpired())
                return cache;
            return coreRefresh();
        }finally {
            lock.unlock();
        }
    }
}
