package org.dows.hep.biz.event;


import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentInstanceDao;
import org.dows.hep.biz.dao.ExperimentSettingDao;
import org.dows.hep.biz.dao.ExperimentTimerDao;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/6/18 17:30
 */
@Component
@Slf4j
public class ExperimentSettingCache extends BaseLoadingCache<ExperimentCacheKey, ExperimentSettingCollection> {
    private static volatile ExperimentSettingCache s_instance;

    public static ExperimentSettingCache Instance() {
        return s_instance;
    }

    protected final static int CACHEInitCapacity = 2;
    protected final static int CACHEMaxSize = 20;
    protected final static int CACHEExpireSeconds = 60 * 60 * 6;

    private ExperimentSettingCache() {
        super(CACHEInitCapacity, CACHEMaxSize, CACHEExpireSeconds, 0);
        s_instance = this;
    }

    @Autowired
    private ExperimentSettingDao experimentSettingDao;

    @Autowired
    private ExperimentTimerDao experimentTimerDao;

    @Autowired
    private ExperimentInstanceDao experimentInstanceDao;


    @Override
    protected ExperimentSettingCollection load(ExperimentCacheKey key) {
        if (ShareUtil.XObject.anyEmpty(key, () -> key.getExperimentInstanceId())) {
            return null;
        }
        ExperimentSettingCollection rst = new ExperimentSettingCollection().setExperimentInstanceId(key.getExperimentInstanceId());
        ExperimentInstanceEntity rowExpt = experimentInstanceDao.getById(key.getExperimentInstanceId(),
                ExperimentInstanceEntity::getAppId,
                ExperimentInstanceEntity::getModel,
                ExperimentInstanceEntity::getStartTime,
                ExperimentInstanceEntity::getState).orElse(null);
        if (null == rowExpt) {
            return null;
        }
        rst.setAppId(rowExpt.getAppId())
                .setMode(EnumExperimentMode.getByCode(rowExpt.getModel()))
                .setExperimentStartTime(ShareUtil.XDate.localDT4Date(rowExpt.getStartTime()));
        List<ExperimentSettingEntity> rowsSetting = experimentSettingDao.getByExperimentId(null, key.getExperimentInstanceId(),
                null,
                ExperimentSettingEntity::getConfigKey,
                ExperimentSettingEntity::getConfigJsonVals);
        if (ShareUtil.XObject.anyEmpty(rowsSetting, () -> rowsSetting.get(0).getConfigJsonVals())) {
            return rst;
        }
        ExperimentSetting.SchemeSetting schemeSetting=null;
        ExperimentSetting.SandSetting sandSetting=null;
        for(ExperimentSettingEntity item:rowsSetting){
            if(ExperimentSetting.SchemeSetting.class.getName().equals(item.getConfigKey())){
                schemeSetting=JSONUtil.toBean(item.getConfigJsonVals(), ExperimentSetting.SchemeSetting.class);
            }
            if(ExperimentSetting.SandSetting.class.getName().equals(item.getConfigKey())){
                sandSetting = JSONUtil.toBean(item.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
            }
        }
        if(null!=schemeSetting) {
            rst.setSchemaDurationMinutes(schemeSetting.getDuration())
                    .setSchemaEndTime(ShareUtil.XDate.localDT4Date(schemeSetting.getSchemeEndTime()))
                    .setSchemaAuditEndTime(ShareUtil.XDate.localDT4Date(schemeSetting.getAuditEndTime()));
        }
        if(null==sandSetting){
            return rst.setPeriods(0);
        }
        rst.setKnowledgeWeight(BigDecimal.valueOf(sandSetting.getKnowledgeWeight()) );
        rst.setHealthIndexWeight(BigDecimal.valueOf(sandSetting.getHealthIndexWeight()));
        rst.setMedicalRatioWeight(BigDecimal.valueOf(sandSetting.getMedicalRatioWeight()));
        rst.setDurationMap(sandSetting.getDurationMap());
        rst.setPeriodMap(sandSetting.getPeriodMap());
        Map<String, Double> mockRateMap = new HashMap<>();
        rst.setMockRateMap(mockRateMap);
        Map<Integer, ExperimentSettingCollection.ExperimentPeriodSetting> mapPeriod = new HashMap<>();
        RangeMap<Integer, Integer> rangePeriodSeconds = TreeRangeMap.create();
        RangeMap<Integer, Integer> rangePeriodDays = TreeRangeMap.create();

        final Integer periods = sandSetting.getPeriods();
        final Integer interval = sandSetting.getInterval().intValue();// sandSetting.getInterval().intValue()/1000
        int startSeconds, startDay;
        int endSeconds = 0;
        int endDay = 0;
        long totalSeconds=0;
        for (int i = 1; i <= periods; i++) {
            Integer lastMinutes = sandSetting.getDurationMap().get(String.valueOf(i));
            startSeconds = endSeconds;
            endSeconds = startSeconds + lastMinutes * 60;
            Integer lastDays = sandSetting.getPeriodMap().get(String.valueOf(i));
            mockRateMap.put(String.valueOf(i),lastDays/(lastMinutes*60d));
            startDay = endDay + 1;
            endDay = startDay + lastDays - 1;
            totalSeconds+=lastMinutes*60;
            mapPeriod.put(i, ExperimentSettingCollection.ExperimentPeriodSetting.builder()
                    .Period(i)
                    .startSecond(startSeconds)
                    .endSecond(endSeconds)
                    .startGameDay(startDay)
                    .endGameDay(endDay)
                    .build());
            if (i < periods) {
                endSeconds += interval;
            }
            rangePeriodSeconds.put(i < periods ? Range.closedOpen(startSeconds, endSeconds) : Range.closed(startSeconds, endSeconds), i);
            rangePeriodDays.put(Range.closed(startDay, endDay),i);
        }
        return rst.setPeriods(periods)
                .setRawEndSeconds(endSeconds)
                .setMapPeriod(mapPeriod)
                .setRangePeriodSeconds(rangePeriodSeconds)
                .setRangePeriodDays(rangePeriodDays)
                .setTotalDays(Long.valueOf(endDay))
                .setTotalSeconds(totalSeconds);
    }

    public ExperimentTimePoint getTimePointByRealTimeSilence(ExperimentCacheKey key, LocalDateTime dt, boolean fillGameDay) {
        return getTimePointByRealTimeSilence(getSet(key, true), key, dt, fillGameDay);
    }

    public ExperimentTimePoint getTimePointByRealTime(ExperimentCacheKey key, LocalDateTime dt, boolean fillGameDay) {
        return getTimePointByRealTime(getSet(key, true), key, dt, fillGameDay);
    }

    public static ExperimentTimePoint getTimePointByRealTimeSilence(ExperimentSettingCollection cached, ExperimentCacheKey key, LocalDateTime dt, boolean fillGameDay) {
        try {
            return getTimePointByRealTime(cached, key, dt, fillGameDay);
        } catch (Exception ex) {
            log.error(String.format("ExperimentSettingCache.getTimePointByRealTimeSilence expt:%s dt:%s fillGameDay:%s", key, dt, fillGameDay), ex);
            return null;
        }
    }

    public static ExperimentTimePoint getTimePointByRealTime(ExperimentSettingCollection cached, ExperimentCacheKey key, LocalDateTime dtNow, boolean fillGameDay) {
        ExperimentTimePoint rst = new ExperimentTimePoint().setRealTime(dtNow);
        if(EnumExperimentMode.SCHEME.equals(cached.getMode())){
            rst.setPeriod(0).setGameDay(0).setCntPauseSeconds(0L);
            return rst;
        }

        AssertUtil.trueThenThrow(ShareUtil.XObject.anyEmpty(cached, ()->cached.getMapPeriod()))
                .throwMessage("getTimePointByRealTime-未找到实验时间设置");
       /* AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(cached.getStartTime()))
                .throwMessage("getTimePointByRealTime-未找到实验开始时间");*/
        final Long nowTs = ShareUtil.XDate.localDT2UnixTS(dtNow, false);
        if (ShareUtil.XObject.isEmpty(cached.getSandStartTime())
                ||dtNow.isBefore(cached.getSandStartTime())) {
            return rst.setPeriod(1)
                    .setGameDay(1)
                    .setCntPauseSeconds(0L)
                    .setGameState(EnumExperimentState.UNBEGIN);
        }
        ExperimentTimerCache.CacheData timerCache= ExperimentTimerCache.Instance().loadingCache().get(key);
        ExperimentTimerEntity curTimer=null;
        if(ShareUtil.XObject.isEmpty(timerCache)||ShareUtil.XObject.isEmpty(curTimer=timerCache.getCurTimer(dtNow))) {
            return rst.setPeriod(1)
                    .setGameDay(1)
                    .setCntPauseSeconds(0L)
                    .setGameState(EnumExperimentState.UNBEGIN);
        }

        rst.setPeriod(curTimer.getPeriod());
        if (cached.getPeriods().equals(curTimer.getPeriod()) && curTimer.getEndTime().getTime() <= nowTs) {
            rst.setGameState(EnumExperimentState.FINISH);
        } else {
            rst.setGameState(EnumExperimentState.ONGOING);
        }
        AssertUtil.getNotNull(cached.getSettingByPeriod(rst.getPeriod()))
                .orElseThrow(String.format("getTimePointByRealTime-未找到实验第%s期设置", rst.getPeriod()));
        rst.setCntPauseSeconds(timerCache.getCntPausSeconds(dtNow));
        if (!fillGameDay) {
            return rst;
        }
        Integer rawSeconds = (int) (Duration.between(cached.getSandStartTime(), dtNow).toSeconds() + 1 - rst.getCntPauseSeconds());
        return rst.setGameDay(cached.getGameDayByRawSeconds(rawSeconds, rst.getPeriod()));

    }

    @Override
    protected void onRemoval(ExperimentCacheKey key, ExperimentSettingCollection value, RemovalCause cause) {

    }

    @Override
    protected boolean isCompleted(ExperimentSettingCollection val) {
        if(ShareUtil.XObject.isEmpty(val)){
            return false;
        }
        if(val.hasSchemaMode()){
            return true;
        }
        return ShareUtil.XObject.notEmpty(val.getSandStartTime());
    }

    @Override
    protected ExperimentSettingCollection cotinueLoad(ExperimentCacheKey key, ExperimentSettingCollection curVal) {
        if (ShareUtil.XObject.isEmpty(curVal)) {
            return curVal;
        }
        if (curVal.hasSandMode()&&ShareUtil.XObject.isEmpty(curVal.getSandStartTime())) {
            List<ExperimentTimerEntity> rowsTimer = experimentTimerDao.getByExperimentId(key.getAppId(), key.getExperimentInstanceId(),
                    1, ExperimentTimerEntity::getStartTime, ExperimentTimerEntity::getPauseCount, ExperimentTimerEntity::getState);
            curVal.setSandStartTime(rowsTimer.stream()
                    .filter(i -> i.getState() >= EnumExperimentState.ONGOING.getState())
                    .max(Comparator.comparingInt(ExperimentTimerEntity::getPauseCount))
                    .map(ExperimentTimerEntity::getStartTime)
                    .map(i -> ShareUtil.XDate.localDT4UnixTS(i.getTime(), false))
                    .orElse(null));

        }
        return curVal;
    }

}
