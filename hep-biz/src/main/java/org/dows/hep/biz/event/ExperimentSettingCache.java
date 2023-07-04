package org.dows.hep.biz.event;


import cn.hutool.json.JSONUtil;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentSettingDao;
import org.dows.hep.biz.dao.ExperimentTimerDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:30
 */
@Component
@Slf4j
public class ExperimentSettingCache extends BaseLoadingCache<ExperimentCacheKey, ExperimentSettingCollection> {
    private static volatile ExperimentSettingCache s_instance;

    public static ExperimentSettingCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=1;
    protected final static int CACHEMaxSize=4;
    protected final static int CACHEExpireSeconds=60*60*6;

    private ExperimentSettingCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

    @Autowired
    private ExperimentSettingDao experimentSettingDao;

    @Autowired
    private ExperimentTimerDao experimentTimerDao;


    @Override
    protected ExperimentSettingCollection load(ExperimentCacheKey key) {
        if(ShareUtil.XObject.anyEmpty(key,()->key.getExperimentInstanceId())) {
            return null;
        }
        ExperimentSettingCollection rst=new ExperimentSettingCollection().setExperimentInstanceId(key.getExperimentInstanceId());
        List<ExperimentSettingEntity> rowsSetting=experimentSettingDao.getByExperimentId(key.getAppId(),key.getExperimentInstanceId(),
                ExperimentSetting.SandSetting.class.getName(),
                ExperimentSettingEntity::getConfigJsonVals);
        if(ShareUtil.XObject.anyEmpty(rowsSetting,()->rowsSetting.get(0).getConfigJsonVals())){
            return rst;
        }
        ExperimentSetting.SandSetting sandSetting= JSONUtil.toBean(rowsSetting.get(0).getConfigJsonVals(), ExperimentSetting.SandSetting.class);
        Map<Integer, ExperimentSettingCollection.ExperimentPeriodSetting> mapPeriod=new HashMap<>();
        RangeMap<Integer,Integer> mapPeriodSeconds= TreeRangeMap.create();

        final Integer periods=sandSetting.getPeriods();
        final Integer interval=sandSetting.getInterval().intValue()/1000;
        int startSeconds,startDay;
        int endSeconds=0;
        int endDay=0;
        for(int i=1;i<=periods;i++) {
            Integer lastMinutes = sandSetting.getDurationMap().get(String.valueOf(i));
            startSeconds = endSeconds;
            endSeconds =startSeconds+ lastMinutes * 60;
            Integer lastDays=sandSetting.getPeriodMap().get(String.valueOf(i));
            startDay=endDay+1;
            endDay=startDay+lastDays-1;
            mapPeriod.put(i,ExperimentSettingCollection.ExperimentPeriodSetting.builder()
                    .Period(i)
                    .startSecond(startSeconds)
                    .endSecond(endSeconds)
                    .startGameDay(startDay)
                    .endGameDay(endDay)
                    .build());
            if(i<periods) {
                endSeconds += interval;
            }
            mapPeriodSeconds.put(i<periods? Range.closedOpen(startSeconds, endSeconds):Range.closed(startSeconds, endSeconds), i);
        }
        return rst.setPeriods(periods)
                .setRawEndSeconds(endSeconds)
                .setMapPeriod(mapPeriod)
                .setMapPeriodSeconds(mapPeriodSeconds);
    }
    public ExperimentTimePoint getTimePointByRealTimeSilence(ExperimentCacheKey key,LocalDateTime dt,boolean fillGameDay){
        return getTimePointByRealTimeSilence(getSet(key,true),key,dt,fillGameDay);
    }

    public ExperimentTimePoint getTimePointByRealTime(ExperimentCacheKey key,LocalDateTime dt,boolean fillGameDay){
        return getTimePointByRealTime(getSet(key,true),key,dt,fillGameDay);
    }
    public static ExperimentTimePoint getTimePointByRealTimeSilence(ExperimentSettingCollection cached, ExperimentCacheKey key,LocalDateTime dt,boolean fillGameDay){
        try{
            return getTimePointByRealTime(cached,key,dt,fillGameDay);
        }catch (Exception ex){
            log.error(String.format("ExperimentSettingCache.getTimePointByRealTimeSilence expt:%s dt:%s fillGameDay:%s",key,dt,fillGameDay) ,ex);
            return null;
        }
    }
    public static ExperimentTimePoint getTimePointByRealTime(ExperimentSettingCollection cached,  ExperimentCacheKey key,LocalDateTime dt,boolean fillGameDay) {
        ExperimentTimePoint rst = new ExperimentTimePoint().setRealTime(dt);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(cached.getMapPeriod()))
                .throwMessage("未找到实验时间设置");
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(cached.getStartTime()))
                .throwMessage("未找到实验开始时间");
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(cached.getStartTime())) {
            return rst.setPeriod(1)
                    .setGameDay(1)
                    .setCntPauseSeconds(0L);
        }
        LocalDateTime rawEndTime = cached.getRawEndTime();
        if (now.isAfter(rawEndTime)) {
            List<ExperimentTimerEntity> rowsTime = s_instance.experimentTimerDao.getByExperimentId(key.getAppId(), key.getExperimentInstanceId(), cached.getPeriods(),
                    ExperimentTimerEntity::getStartTime, ExperimentTimerEntity::getEndTime);
            AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(rowsTime))
                    .throwMessage("未找到实验结束计时器");
            rst.setPeriod(cached.getPeriods())
                    .setCntPauseSeconds(rowsTime.get(0).getEndTime() / 1000 - ShareUtil.XDate.localDT2UnixTS(rawEndTime, true));

        }
        ExperimentTimerEntity rowTime = AssertUtil.getNotNull(s_instance.experimentTimerDao.getCurPeriodByExperimentId(key.getAppId(), key.getExperimentInstanceId(),
                        ShareUtil.XDate.localDT2UnixTS(rawEndTime, false),
                        ExperimentTimerEntity::getPeriod,
                        ExperimentTimerEntity::getStartTime,
                        ExperimentTimerEntity::getEndTime))
                .orElseThrow("未找到当前实验计时器");
        rst.setPeriod(rowTime.getPeriod());
        ExperimentSettingCollection.ExperimentPeriodSetting setting = AssertUtil.getNotNull(cached.getSettingByPeriod(rst.getPeriod()))
                .orElseThrow(String.format("未找到实验第%s期设置", rst.getPeriod()));
        long pausingSeconds=0;
        if(Optional.ofNullable( rowTime.getPaused()).orElse(false)){
            pausingSeconds=(ShareUtil.XDate.localDT2UnixTS(dt,false)- rowTime.getPauseStartTime().getTime())/1000;
        }
        rst.setCntPauseSeconds(pausingSeconds+rowTime.getEndTime() / 1000 - ShareUtil.XDate.localDT2UnixTS(cached.getStartTime().plusSeconds(setting.getEndSecond()), true));
        if (!fillGameDay || ShareUtil.XObject.isEmpty(rst.getCntPauseSeconds())) {
            return rst;
        }
        Integer rawSeconds = (int) (Duration.between(cached.getStartTime(), dt).toSeconds() + 1 - rst.getCntPauseSeconds());
        return rst.setGameDay(cached.getGameDayByRawSeconds(rawSeconds));

    }



    @Override
    protected boolean isCompleted(ExperimentSettingCollection val) {
        return ShareUtil.XObject.notEmpty(val.getStartTime())
                &ShareUtil.XObject.notEmpty(val.getMapPeriod());
    }

    @Override
    protected ExperimentSettingCollection cotinueLoad(ExperimentCacheKey key, ExperimentSettingCollection curVal) {
        if(ShareUtil.XObject.isEmpty(curVal.getStartTime())){
            List<ExperimentTimerEntity> rowsTimer=experimentTimerDao.getByExperimentId(key.getAppId(), key.getExperimentInstanceId(),
                    1, ExperimentTimerEntity::getStartTime);
            curVal.setStartTime(rowsTimer.stream()
                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                    .map(ExperimentTimerEntity::getStartTime)
                    .map(i->ShareUtil.XDate.localDT4UnixTS(i,false))
                    .orElse(null));

        }
        return curVal;
    }

}
