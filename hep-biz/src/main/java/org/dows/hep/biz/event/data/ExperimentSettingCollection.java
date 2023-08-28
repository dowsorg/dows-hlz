package org.dows.hep.biz.event.data;

import com.google.common.collect.RangeMap;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.enums.EnumExperimentMode;
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
    @Schema(title = "appId")
    private String appId;
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验模式")
    private EnumExperimentMode mode;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "实验开始时间")
    private LocalDateTime experimentStartTime;


    @Schema(title = "方案设计时长")
    private Long schemaDurationMinutes;

    @Schema(title = "方案截止时间")
    private LocalDateTime schemaEndTime;

    @Schema(title = "沙盒开始时间")
    private LocalDateTime sandStartTime;

    @Schema(title = "初始沙盒结束秒数")
    private int rawEndSeconds;
    @Schema(title = "累计暂停秒数")
    private long cntPauseSeconds;
    @Schema(title = "上一次累计暂停秒数")
    private long cntPauseSecondsOld;

    @Schema(title = "期数设置")
    private Map<Integer,ExperimentPeriodSetting> mapPeriod;

    @Schema(title = "时间线")
    private RangeMap<Integer,Integer> mapPeriodSeconds;

    public boolean hasSchemaMode(){
        return mode ==EnumExperimentMode.SCHEME;
    }
    public boolean hasSandMode(){
        return mode ==EnumExperimentMode.SAND;
    }

    public LocalDateTime getRawEndTime(){
        return Optional.ofNullable(getSandStartTime())
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
        return getGameDayByRawSeconds(rawSeconds,null);
    }
    public Integer getGameDayByRawSeconds(Integer rawSeconds,Integer period){
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
        if(ShareUtil.XObject.isEmpty(period, true)){
            period=getPeriodByRawSeconds(rawSeconds);
        }
        ExperimentPeriodSetting setting=Optional.ofNullable(period)
                .map(this::getSettingByPeriod)
                .orElse(null);
        if(ShareUtil.XObject.isEmpty(setting)){
            return null;
        }
        double totalSeconds=setting.getTotalSeconds();
        double totalDays=setting.getTotalDays();
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

        @Schema(title = "间隔秒数")
        private Integer dueSeconds;


        public Integer getTotalSeconds(){
            return endSecond-startSecond;
        }
        public Integer getTotalDays(){
            return endGameDay-startGameDay+1;
        }


    }

}
