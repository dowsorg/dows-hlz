package org.dows.hep.biz.event.sysevent;

import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentSysEventDao;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.sysevent.data.EnumSysEventDealType;
import org.dows.hep.biz.event.sysevent.data.SysEventCollection;
import org.dows.hep.biz.event.sysevent.data.SysEventRow;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSysEventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * @author : wuzl
 * @date : 2023/8/22 11:55
 */

@Component
public class SysEventCache extends BaseLoadingCache<ExperimentCacheKey, SysEventCollection> {

    private static volatile SysEventCache s_instance;

    public static SysEventCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=2;
    protected final static int CACHEMaxSize=100;
    protected final static int CACHEExpireSeconds=60*60*12;

    @Autowired
    private ExperimentSysEventDao experimentSysEventDao;

    private SysEventCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

    @Override
    protected SysEventCollection load(ExperimentCacheKey key) {
        final String experimentId = key.getExperimentInstanceId();
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(key,true);
        if(ShareUtil.XObject.anyEmpty(exptColl,()->exptColl.getMode())){
           return null;
        }
        SysEventCollection rst = new SysEventCollection()
                .setExperimentInstanceId(experimentId);
        List<ExperimentSysEventEntity> rowsEvent = this.experimentSysEventDao.getByExperimentId(null, experimentId,null);
        if (ShareUtil.XCollection.notEmpty(rowsEvent)) {
            rst.setInitFlag(true);
        } else{
            rowsEvent = buildEvents(exptColl);
            rst.setInitFlag(false);
        }
        rst.setEventRows(ShareUtil.XCollection.map(rowsEvent, i -> new SysEventRow(i)));
        rowsEvent.clear();
        return rst;
    }

    public static List<ExperimentSysEventEntity> buildEvents(ExperimentSettingCollection exptColl) {
        List<ExperimentSysEventEntity> rst=new ArrayList<>();
        Arrays.stream(EnumSysEventDealType.values()).forEach(item->{
            if(item==EnumSysEventDealType.NONE){
                return;
            }
            List<ExperimentSysEventEntity> events=item.buildEvents(exptColl);
            if(ShareUtil.XObject.isEmpty(events)){
                return;
            }
            rst.addAll(events);
        });
        rst.sort(Comparator.comparingInt(ExperimentSysEventEntity::getPeriods)
                .thenComparingInt(ExperimentSysEventEntity::getDealSeq));
        return rst;
    }
}
