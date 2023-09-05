package org.dows.hep.biz.event.followupplan;

import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentFollowupPlanDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/2 19:14
 */
@Component
public class FollowupPlanCache extends BaseLoadingCache<ExperimentCacheKey, FollowupPlanCollection> {

    private static volatile FollowupPlanCache s_instance;

    public static FollowupPlanCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=2;
    protected final static int CACHEMaxSize=100;
    protected final static int CACHEExpireSeconds=60*60*12;

    private FollowupPlanCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }
    @Autowired
    private ExperimentFollowupPlanDao experimentFollowupPlanDao;

    @Override
    protected FollowupPlanCollection load(ExperimentCacheKey key) {
        FollowupPlanCollection rst=new FollowupPlanCollection()
                .setExperimentInstanceId(key.getExperimentInstanceId());
        List<ExperimentFollowupPlanEntity> rowsPlan=experimentFollowupPlanDao.getByExperimentId(key.getExperimentInstanceId());
        if(ShareUtil.XObject.isEmpty(rowsPlan)){
            return rst;
        }
        rst.setPlanRows(ShareUtil.XCollection.map(rowsPlan, FollowupPlanRow::new));
        rowsPlan.clear();
        return rst;
    }


}
