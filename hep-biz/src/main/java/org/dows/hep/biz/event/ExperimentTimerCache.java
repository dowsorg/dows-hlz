package org.dows.hep.biz.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentTimerDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/8/29 10:18
 */

@Component
@Slf4j
public class ExperimentTimerCache extends BaseLoadingCache<ExperimentCacheKey, ExperimentTimerCache.CacheData> {

    private static volatile ExperimentTimerCache s_instance;

    public static ExperimentTimerCache Instance() {
        return s_instance;
    }

    protected final static int CACHEInitCapacity = 2;
    protected final static int CACHEMaxSize = 20;
    protected final static int CACHEExpireSeconds = 60 * 60 *6;

    @Autowired
    private ExperimentTimerDao experimentTimerDao;

    protected ExperimentTimerCache() {
        super(CACHEInitCapacity, CACHEMaxSize, CACHEExpireSeconds, 0);
        s_instance = this;
    }

    public CacheData getCacheData(String experimentId){
        return getCacheData(null,experimentId);
    }
    public CacheData getCacheData(String appId, String experimentId){
        return getCacheData(ExperimentCacheKey.create(appId, experimentId));
    }
    public CacheData getCacheData(ExperimentCacheKey key){
        key.setAppId(ShareBiz.checkAppId(key.getAppId(), key.getExperimentInstanceId()));
        return this.getSet(key, false);
    }

    public void remove(String experimentId){
        remove(ExperimentCacheKey.create("3", experimentId));
    }
    public void remove(String appId, String experimentId){
        remove(ExperimentCacheKey.create(appId, experimentId));
    }

    public void remove(ExperimentCacheKey key){
        key.setAppId(ShareBiz.checkAppId(key.getAppId(), key.getExperimentInstanceId()));
        this.loadingCache().invalidate(key);
    }

    @Override
    protected CacheData load(ExperimentCacheKey key) {
        if(ShareUtil.XObject.isEmpty(key)){
            return null;
        }
        CacheData rst=new CacheData();
        ExperimentSettingCollection exptColl=ExperimentSettingCache.Instance().loadingCache().get(key);
        if(ShareUtil.XObject.isEmpty(exptColl)||!exptColl.hasSandMode()){
            return rst;
        }
        Map<Integer, ExperimentTimerEntity> mapTimer=experimentTimerDao.getMapByExperimentId(null, key.getExperimentInstanceId(), null );
        rst.setMapTimer(mapTimer);
        rst.setSandStartTime(rst.mapTimer.values().stream()
                .filter(i -> i.getState() >= EnumExperimentState.ONGOING.getState())
                .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                .map(ExperimentTimerEntity::getStartTime)
                .map(i -> ShareUtil.XDate.localDT4UnixTS(i.getTime(), false))
                .orElse(null));
        final LocalDateTime ldtNow=LocalDateTime.now();
        if(ShareUtil.XObject.isEmpty(rst.getSandStartTime())||ldtNow.isBefore(rst.getSandStartTime())){
            return rst.setCntPauseSeconds(0);
        }
        final ExperimentTimerEntity curTimer=getCurTimer(ldtNow, mapTimer);
        if(ShareUtil.XObject.isEmpty(curTimer)){
            return rst.setCntPauseSeconds(0);
        }
        rst.setPaused(Optional.ofNullable(curTimer.getPaused()).orElse(false));
        final int curPeriod=curTimer.getPeriod();
        ExperimentSettingCollection.ExperimentPeriodSetting setting =exptColl.getSettingByPeriod(curPeriod);
        if(null==setting){
            return rst;
        }
        long pausingSeconds =rst.isPaused?Math.max(0,
                ShareUtil.XDate.localDT2UnixTS(ldtNow, true)
                        -curTimer.getPauseTime().getTime()/1000):0;
        long pausedSeconds=Math.max( 0, curTimer.getEndTime().getTime()/1000
                -ShareUtil.XDate.localDT2UnixTS(rst.getSandStartTime(),true)
                -setting.getEndSecond());
        return rst.setCntPauseSeconds(pausingSeconds+pausedSeconds);
    }


    private static ExperimentTimerEntity getCurTimer(LocalDateTime ldtNow,Map<Integer, ExperimentTimerEntity> mapTimer){
        final long nowTs=ShareUtil.XDate.localDT2UnixTS(ldtNow, false);
        ExperimentTimerEntity curTimer=null;
        for(ExperimentTimerEntity item:mapTimer.values()){
            if(item.getPaused()){
                if(item.getStartTime().getTime()<=item.getPauseTime().getTime()&&item.getPauseTime().getTime()<=item.getEndTime().getTime()){
                    curTimer=item;
                    break;
                }
                continue;
            }
            if(nowTs<item.getStartTime().getTime()){
                break;
            }
            curTimer=item;
        }
        return curTimer;
    }

    @Override
    protected CacheData cotinueLoad(ExperimentCacheKey key, CacheData curVal) {
        if(null==curVal||curVal.isPaused){
            return load(key);
        }
        return curVal;
    }

    @Data
    @Accessors(chain = true)
    public static class CacheData {

        private Map<Integer, ExperimentTimerEntity> mapTimer;


        @Schema(title = "累计暂停秒数")
        private long cntPauseSeconds;

        @Schema(title = "是否暂停中")
        private boolean isPaused;

        @Schema(title = "沙盒开始时间")
        private LocalDateTime sandStartTime;

        public ExperimentTimerEntity getCurTimer(LocalDateTime ldtNow){
            return ExperimentTimerCache.getCurTimer(ldtNow,mapTimer);
        }


        public List<ExperimentTimerEntity> getTimerList(){
            if(ShareUtil.XObject.isEmpty(mapTimer)){
                return Collections.emptyList();
            }
            return mapTimer.values().stream().toList();
        }

        public ExperimentTimerEntity getTimerByPeriod(Integer period){
            return Optional.ofNullable(mapTimer)
                    .map(i->i.get(period))
                    .orElse(null);
        }

    }
}
