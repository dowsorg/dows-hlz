package org.dows.hep.biz.event.sysevent;

import org.dows.hep.biz.cache.BaseManulCache;
import org.dows.hep.biz.dao.ExperimentSysEventDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.sysevent.data.SysEventCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author : wuzl
 * @date : 2023/8/22 11:55
 */

@Component
public class SysEventCache extends BaseManulCache<ExperimentCacheKey, SysEventCollection> {

    private static volatile SysEventCache s_instance;

    public static SysEventCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=2;
    protected final static int CACHEMaxSize=10;
    protected final static int CACHEExpireSeconds=60*60*12;

    @Autowired
    private ExperimentSysEventDao experimentSysEventDao;

    private SysEventCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

}
