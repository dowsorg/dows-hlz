package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExperimentPeriodsResonse {

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "当前期数")
    private Integer currentPeriod;

    @Schema(title = "实验期数")
    private List<ExperimentPeriods> experimentPeriods;

    /**
     * 实验期数
     */
    @Data
    public static class ExperimentPeriods {
        @Schema(title = "实验实列ID")
        private String experimentInstanceId;

        @Schema(title = "暂停时长[暂停结束时间-暂停起始时间]")
        private Long duration;

        @Schema(title = "实验开始时间")
        private Long startTime;

        @Schema(title = "实验结束时间[如果有暂停，需加暂停时长]")
        private Long endTime;

        @Schema(title = "实验每期间隔：秒")
        private Long periodInterval;

        @Schema(title = "期数[根据期数生成对应的计时记录]")
        private Integer period;

        @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
        private Integer model;

        @Schema(title = "暂停次数[每次暂停++]")
        private Integer pauseCount;

        @Schema(title = "状态[0:未开始，1:进行中，2:已结束]")
        private Integer state;

        @Schema(title = "是否暂停")
        private Boolean paused;

        @Schema(title = "暂停开始时间")
        private Date pauseStartTime;
        @Schema(title = "暂停结束时间")
        private Date pauseEndTime;
    }

}
