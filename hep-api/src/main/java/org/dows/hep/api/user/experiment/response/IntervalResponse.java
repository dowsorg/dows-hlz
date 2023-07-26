package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "CountDown 对象", title = "时间")
public class IntervalResponse {
    @Schema(title = "应用ID")
    private String appId;


    @Schema(title = "实验实例ID")
    private String experimentInstanceId;
    @Schema(title = "实验小组ID")
    private String experimentGroupId;
    @Schema(title = "模式")
    private Integer model;
    @Schema(title = "期数")
    private Integer period;

    @Schema(title = "实验倒计时时间")
    private Long countdown;


    @Schema(title = "沙盘总时长[天]")
    private Long sandTotalTime;
    @Schema(title = "沙盘时间单位")
    private String sandTimeUnit;
    @Schema(title = "沙盘持续时间(秒)")
    private Long sandDurationSecond;
    @Schema(title = "沙盘剩余时间(秒)")
    private Long sandRemnantSecond;


    @Schema(title = "方案设计总时长")
    private Long schemeTotalTime;
    @Schema(title = "方案设计时间单位")
    private String schemeTimeUnit;
    @Schema(title = "方案持续时间(秒)")
    private Long schemeDurationSecond;
    @Schema(title = "方案剩余时间(秒)")
    private Long schemeRemnantSecond;


    @Schema(title = "实验状态")
    private Integer state;
    /**
     * 用于前端计算
     */
    // 每期持续时长
    @Schema(title = "每期持续时长")
    private Map<String, Integer> durationMap;
    // 期数
    @Schema(title = "期数")
    private Map<String, Integer> periodMap;
    // 每期对应的mock比列
    @Schema(title = "每期对应的mock比列")
    private Map<String, Double> mockRateMap;


}
