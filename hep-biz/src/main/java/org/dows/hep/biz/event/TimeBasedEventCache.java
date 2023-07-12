package org.dows.hep.biz.event;

import org.dows.hep.biz.cache.BaseManulCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.TimeBasedEventCollection;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:30
 */
@Component
public class TimeBasedEventCache extends BaseManulCache<ExperimentCacheKey, TimeBasedEventCollection> {
    private static volatile TimeBasedEventCache s_instance;

    public static TimeBasedEventCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=2;
    protected final static int CACHEMaxSize=10;
    protected final static int CACHEExpireSeconds=60*60*12;

    private TimeBasedEventCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

}
