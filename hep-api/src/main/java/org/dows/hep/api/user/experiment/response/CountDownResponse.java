package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

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


}
