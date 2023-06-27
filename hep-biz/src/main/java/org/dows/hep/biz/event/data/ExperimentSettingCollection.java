package org.dows.hep.biz.event.data;

import com.google.common.collect.RangeMap;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.biz.cache.ICacheClear;
import org.dows.hep.biz.util.ShareUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/6/18 22:41
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentSettingCollection implements ICacheClear {
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "沙盒开始时间")
    private LocalDateTime startTime;

    @Schema(title = "初始沙盒结束秒数")
    private Integer rawEndSeconds;
    @Schema(title = "累计暂停秒数")
    private long cntPauseSeconds;
    @Schema(title = "上一次累计暂停秒数")
    private long cntPauseSecondsOld;

    @Schema(title = "期数设置")
    private Map<Integer,ExperimentPeriodSetting> mapPeriod;

    @Schema(title = "时间线")
    private RangeMap<Integer,Integer> mapPeriodSeconds;

    public LocalDateTime getRawEndTime(){
        return Optional.ofNullable(getStartTime())
                .map(i->i.plusSeconds(rawEndSeconds))
                .orElse(null);
    }

    public ExperimentSettingCollection setPauseSeconds(long val){
        this.cntPauseSecondsOld=cntPauseSeconds;
        this.cntPauseSeconds=val;
        return this;
    }
    public ExperimentPeriodSetting getSettingByPeriod(Integer period){
        if(ShareUtil.XObject.isEmpty(mapPeriod)){
            return null;
        }
        return mapPeriod.get(period);
    }

    /**
     * 按相对秒数获取当前期
     * @param rawSeconds
     * @return
     */
    public Integer getPeriodByRawSeconds(Integer rawSeconds){
        if(rawSeconds.equals(0)){
            return 1;
        }
        if(rawEndSeconds<=rawSeconds){
            return periods;
        }
        if(ShareUtil.XObject.isEmpty(mapPeriodSeconds)){
            return null;
        }
        return mapPeriodSeconds.get(rawSeconds);
    }

    /**
     * 按相对秒数获取游戏内天数
     * @param rawSeconds
     * @return
     */
    public Integer getGameDayByRawSeconds(Integer rawSeconds){
        if(rawSeconds.equals(0)){
            return 1;
        }
        if(ShareUtil.XObject.isEmpty(mapPeriod)){
            return null;
        }
        if(rawEndSeconds<=rawSeconds){
            ExperimentPeriodSetting setting= getSettingByPeriod(this.periods);
            if(null==setting){
                return null;
            }
            return setting.endGameDay;
        }
        ExperimentPeriodSetting setting=Optional.ofNullable(getPeriodByRawSeconds(rawSeconds))
                .map(this::getSettingByPeriod)
                .orElse(null);
        if(ShareUtil.XObject.isEmpty(setting)){
            return null;
        }
        double totalSeconds=setting.endSecond-setting.startSecond;
        double totalDays=setting.getEndGameDay()-setting.getStartGameDay()+1;
        double rate=Math.min(1,(rawSeconds-setting.startSecond)/totalSeconds);
        return setting.getStartGameDay()-1+(int)Math.ceil(rate*totalDays);
    }

    public void clear(){
        if(null!=this.mapPeriod){
            this.mapPeriod.clear();
        }
        if(null!=this.mapPeriodSeconds){
            this.mapPeriodSeconds.clear();
        }
    }
    @Data
    @Builder
    @Accessors(chain = true)
    public static class ExperimentPeriodSetting {
        @Schema(title = "期数")
        private Integer Period;

        @Schema(title = "开始秒数")
        private Integer startSecond;

        @Schema(title = "结束秒数")
        private Integer endSecond;


        @Schema(title = "开始游戏天数")
        private Integer startGameDay;

        @Schema(title = "结束游戏天数")
        private Integer endGameDay;



    }

}
