package org.dows.hep.biz.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.Builder;
import lombok.Data;
import org.dows.hep.biz.util.ShareUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author : wuzl
 * @date : 2023/6/18 23:00
 */
public class BaseManulCache <K,V>{
    protected final Cache<K,V> coreCache;

    protected BaseManulCache(Caffeine<K,V> builder){
        coreCache=coreBuild(builder);
    }
    protected BaseManulCache(CaffineCacheSpec spec){
        coreCache=coreBuild(spec.build());
    }
    protected BaseManulCache(int initCapacity,int maxSize,long expireAfterAccessSeconds,long expireAfterWriteSeconds){
        coreCache=coreBuild(CaffineCacheSpec.builder()
                .initCapacity(initCapacity)
                .maxSize(maxSize)
                .expireAfterAccessSeconds(expireAfterAccessSeconds)
                .expireAfterWriteSeconds(expireAfterWriteSeconds)
                .build()
                .build());
    }
    protected Cache<K,V> coreBuild(Caffeine<K,V> builder){
        return builder.removalListener(this::onRemoval).build();
    }


    public Cache<K,V> caffineCache(){
        return coreCache;
    }


    protected void onRemoval(K key,  V value,  RemovalCause cause){
        if(ShareUtil.XObject.isEmpty(value)) return;
        if(value instanceof ICacheClear clearable){
            clearable.clear();
        }
    }
    @Data
    @Builder
    public static class CaffineCacheSpec {

        private int initCapacity;

        private int maxSize;
        private long expireAfterAccessSeconds;
        private long expireAfterWriteSeconds;

        private long refreshAfterWriteSeconds;

        public Caffeine build(){
            Caffeine builder= Caffeine.newBuilder();
            if(initCapacity>0){
                builder=builder.initialCapacity(initCapacity);
            }
            if(maxSize>0){
                builder=builder.maximumSize(maxSize);
            }
            if(expireAfterAccessSeconds>0){
                builder=builder.expireAfterAccess(expireAfterAccessSeconds, TimeUnit.SECONDS);
            }
            if(expireAfterWriteSeconds>0){
                builder=builder.expireAfterWrite(expireAfterWriteSeconds,TimeUnit.SECONDS);
            }
            if(refreshAfterWriteSeconds>0){
                builder=builder.refreshAfterWrite(refreshAfterWriteSeconds,TimeUnit.SECONDS);
            }
            return builder;
        }
    }
}
