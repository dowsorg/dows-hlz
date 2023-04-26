package org.dows.hep.biz.cache;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author : wuzl
 * @date : 2023/4/22 19:18
 */
public abstract class BaseLocalMapCache<K,V> extends BaseLocalCache<ConcurrentHashMap<K,V>> {

    //region .ctor
    protected BaseLocalMapCache(LocalDateTime expireAt) {
        super(expireAt);
    }
    protected BaseLocalMapCache(LocalDateTime expireAt, long expireInMinutes){
        super(expireAt,expireInMinutes);
    }
    protected BaseLocalMapCache(long expireInMinutes){
        super(expireInMinutes);
    }
    protected BaseLocalMapCache(long expireInMinutes, int refreshLockMinutes){
        super(expireInMinutes,refreshLockMinutes);
    }
    protected BaseLocalMapCache(LocalDateTime expireAt, long expireInMinutes, int refreshLockMinutes) {
        super(expireAt, expireInMinutes, refreshLockMinutes);
    }
    //endregion

    //region facade
    public boolean containsKey(K key){
        return ensureCache().containsKey(key);
    }
    public V get(K key){
        return ensureCache().get(key);
    }
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return ensureCache().computeIfAbsent(key, mappingFunction);
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction){
        return ensureCache().computeIfPresent(key, remappingFunction);
    }
    //endregion


}
