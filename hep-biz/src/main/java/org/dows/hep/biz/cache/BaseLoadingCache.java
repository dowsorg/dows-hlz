package org.dows.hep.biz.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * @author : wuzl
 * @date : 2023/6/18 23:23
 */
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
        return builder.removalListener(this::onRemoval).build(this::load);
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
        rst = cotinueLoad(key,rst);
        if(!checkCompleted||isCompleted(rst)) {
            cache.put(key, rst);
        }
        return rst;
    }
    public V setGet(K key,boolean checkCompleted)
    {
        LoadingCache<K,V> cache=loadingCache();
        V rst = load(key);
        if(!checkCompleted||isCompleted(rst)) {
            cache.put(key, rst);
        }
        return rst;
    }
    //endregion

    //region virtual
    protected abstract V load(K key);
    protected boolean isCompleted(V val){
        return null!=val;
    }
    protected V cotinueLoad(K key,V curVal){
        return load(key);
    }
    //endregion

}
