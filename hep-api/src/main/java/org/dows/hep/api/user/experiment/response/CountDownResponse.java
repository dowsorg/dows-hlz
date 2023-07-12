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
public class CountDownResponse {
    @Schema(title = "实验实例ID")
    private String experimentInstanceId;
    @Schema(title = "模式")
    private Integer model;
    @Schema(title = "期数")
    private Integer period;
    @Schema(title = "沙盘持续时间")
    private Double sandDuration;
    @Schema(title = "沙盘持续时间(秒)")
    private Long sandDurationSecond;

    @Schema(title = "沙盘实验时长")
    private Long sandTime;
    @Schema(title = "沙盘实验倒计时时间")
    private Long countdown;
    @Schema(title = "沙盘时间单位")
    private String sandTimeUnit;

    @Schema(title = "方案设计倒计时时间")
    private Long schemeTime;

    @Schema(title = "方案时间单位")
    private String schemeTimeUnit;

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
