package org.dows.hep.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.user.experiment.response.IntervalResponse;
import org.dows.hep.api.user.experiment.vo.ExptTimePointVO;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentTimerEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author : wuzl
 * @date : 2023/8/29 16:38
 */
@Component
@Slf4j
public class ExperimentFlowRules {

    public IntervalResponse countdown(String appId, String experimentInstanceId){

        if(ShareUtil.XObject.isEmpty(appId)){
            appId="3";
        }
        ExperimentCacheKey exptKey=ExperimentCacheKey.create(appId,experimentInstanceId);
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(exptKey,true);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(exptColl))
                .throwMessage("未找到实验设置");
        return countdown(exptKey, exptColl);

    }
    public IntervalResponse countdown(ExperimentCacheKey exptKey,ExperimentSettingCollection exptColl){
        IntervalResponse rst=new IntervalResponse();
        rst.setAppId(exptColl.getAppId());
        rst.setExperimentInstanceId(exptColl.getExperimentInstanceId());
        rst.setModel(exptColl.getMode().getCode());
        rst.setDurationMap(exptColl.getDurationMap());
        rst.setPeriodMap(exptColl.getPeriodMap());
        rst.setMockRateMap(exptColl.getMockRateMap());
        rst.setSandTotalTime(exptColl.getTotalDays());

        fillTimeState(rst,exptKey,exptColl);
        return rst;
    }

    public ExptTimePointVO fillTimeState(ExptTimePointVO rst,ExperimentCacheKey exptKey,ExperimentSettingCollection exptColl){
        if(null==rst){
            return rst;
        }
        final Date dtNow=new Date();
        final LocalDateTime ldtNow=ShareUtil.XDate.localDT4Date(dtNow);
        final long nowTs=dtNow.getTime();
        rst.setServerTimeStamp(nowTs);
        //方案设计模式
        if(exptColl.hasSchemaMode()){
            if(exptColl.getSchemaEndTime().isBefore(ldtNow)){
                rst.setSchemeTotalTime(0L);
            }else {
                rst.setSchemeTotalTime(Duration.between(ldtNow,exptColl.getSchemaEndTime()).toMillis());
            }
            return rst;
        }
        if(!exptColl.hasSandMode()){
            return rst;
        }
        ExperimentTimerCache.CacheData cacheTimer=ExperimentTimerCache.Instance().getCacheData(exptKey);
        AssertUtil.trueThenThrow(ShareUtil.XObject.anyEmpty(cacheTimer,()->cacheTimer.getMapTimer()))
                .throwMessage("未找到沙盘时间设置");
        //沙盘模式
        rst.setSandTimeUnit("天");
        for(ExperimentTimerEntity item:cacheTimer.getMapTimer().values()){
            if(item.getPaused()){
                if(item.getStartTime().getTime()<=item.getPauseTime().getTime()&&item.getPauseTime().getTime()<=item.getEndTime().getTime()){
                    // 剩余时间
                    long rs = item.getEndTime().getTime() - item.getPauseTime().getTime();
                    // 持续时间
                    long ds = item.getPeriodDuration() - rs;
                    rst.setSandRemnantSecond(rs / 1000);
                    rst.setSandDurationSecond(ds / 1000);
                    rst.setPeriod(item.getPeriod());
                    rst.setState(item.getState());
                    return rst;
                }
                continue;
            }
            rst.setPeriod(item.getPeriod());
            rst.setState(item.getState());
            // 间隔期|倒计时
            if (nowTs < item.getStartTime().getTime() - item.getPeriodInterval()) {// 实验未开始
                rst.setPeriod(null);
                rst.setCountdownType(0);
                rst.setCountdown(item.getStartTime().getTime() - nowTs);
                return rst;
            } else if (nowTs >= item.getStartTime().getTime() - item.getPeriodInterval() && nowTs < item.getStartTime().getTime()) { // 一期开始倒计时
                rst.setCountdownType(0);
                rst.setCountdown(item.getStartTime().getTime() - nowTs);
                return rst;
            } else if (nowTs >= item.getStartTime().getTime() && nowTs <= item.getEndTime().getTime()) {// 期数中
                // 本期剩余时间 = 暂停推迟后的结束时间 - 当前时间
                long rs = item.getEndTime().getTime() - nowTs + 1;
                long ds = item.getPeriodDuration() - rs;
                rst.setSandRemnantSecond(rs / 1000);
                rst.setSandDurationSecond(ds / 1000);
                return rst;
            } else if (nowTs >= item.getEndTime().getTime() && nowTs <= item.getEndTime().getTime() + item.getPeriodInterval()) {// // 一期结束倒计时
                rst.setCountdown(item.getEndTime().getTime() + item.getPeriodInterval() - nowTs);
                rst.setCountdownType(1);
                return rst;
            }
        }
        final ExperimentTimerEntity lastTimer=cacheTimer.getTimerByPeriod(cacheTimer.getMapTimer().size());
        if(lastTimer.getState().equals(EnumExperimentState.FINISH.getState())){
            rst.setState(lastTimer.getState());
            rst.setPeriod(lastTimer.getPeriod());
            rst.setSandRemnantSecond(0L);
            rst.setSandDurationSecond(exptColl.getTotalSeconds());
        }
        return rst;

    }
}
