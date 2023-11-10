package org.dows.hep.biz.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : wuzl
 * @date : 2023/6/18 23:23
 */
@Slf4j
public abstract class BaseLoadingCache <K,V> extends BaseManulCache<K,V>{

    //region .ctor
    protected BaseLoadingCache(Caffeine<K,V> builder){
        super(builder);
    }
    protected BaseLoadingCache(CaffineCacheSpec spec) {
        super(spec);
    }
    protected BaseLoadingCache (int initCapacity,int maxSize,long expireAfterAccessSeconds,long expireAfterWriteSeconds){
        super(initCapacity,maxSize,expireAfterAccessSeconds,expireAfterWriteSeconds);
    }

    @Override
    protected Cache<K, V> coreBuild(Caffeine<K, V> builder) {
        return builder.removalListener(this::onRemoval).build(this::wrapLoad);
    }
    //endregion


    //region facacde
    public LoadingCache<K,V> loadingCache(){
        return (LoadingCache<K,V>)coreCache;
    }
    public V getSet(K key,boolean checkCompleted)
    {
        LoadingCache<K,V> cache=loadingCache();
        V rst = cache.get(key);
        if (isCompleted(rst))
            return rst;
        rst = wrapContinueLoad(key,rst);
        if(!checkCompleted||isCompleted(rst)) {
            cache.put(key, rst);
        }
        return rst;
    }
    public V setGet(K key,boolean checkCompleted)
    {
        LoadingCache<K,V> cache=loadingCache();
        V rst = wrapContinueLoad(key,load(key));
        if(!checkCompleted||isCompleted(rst)) {
            cache.put(key, rst);
        }
        return rst;
    }
    protected V wrapLoad(K key){
        StringBuilder sb=new StringBuilder();
        long ts=logCostTime(sb,String.format("loadStart-%s",key));
        try{
            return load(key);
        }catch (Exception ex){
            ts=logCostTime(sb,String.format("error-%s",ex.getMessage()),ts);
            return exceptionally(key, ex);
        }finally {
            logCostTime(sb,"loadEnd",ts);
            log.info(sb.toString());
            sb.setLength(0);
        }
    }
    protected V wrapContinueLoad(K key,V curVal){
        StringBuilder sb=new StringBuilder();
        long ts=logCostTime(sb,String.format("continueStart-%s",key));
        try{
            return continueLoad(key,curVal);
        }catch (Exception ex){
            ts=logCostTime(sb,String.format("error-%s",ex.getMessage()),ts);
            return exceptionally(key, ex);
        }finally {
            logCostTime(sb,"continueEnd",ts);
            log.info(sb.toString());
            sb.setLength(0);
        }
    }
    //endregion

    //region virtual
    protected abstract V load(K key);

    protected V exceptionally(K key,Exception ex){
        log.error(String.format("%s.load key:%s", getClass().getName(),key),ex);
        return null;
    }
    protected boolean isCompleted(V val){
        return null!=val;
    }
    protected V continueLoad(K key, V curVal){
        return curVal;
    }
    //endregion

    private long logCostTime(StringBuilder sb,String start){
        sb.append(this.getClass().getName()).append("--").append(start);
        return System.currentTimeMillis();
    }
    private long logCostTime(StringBuilder sb,String func,long ts){
        long newTs=System.currentTimeMillis();
        sb.append(" ").append(func).append(":").append((newTs - ts));
        return newTs;
    }

}
