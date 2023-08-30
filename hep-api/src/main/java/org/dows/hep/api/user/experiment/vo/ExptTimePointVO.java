package org.dows.hep.api.user.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author : wuzl
 * @date : 2023/8/29 15:38
 */

@Data
public class ExptTimePointVO {

    @Schema(title = "期数")
    private Integer period;
    @Schema(title = "实验倒计时时间")
    private Long countdown;
    @Schema(title = "实验倒计时时间类型[0:开始倒计时,1:结束倒计时]")
    private Integer countdownType;

    @Schema(title = "方案设计总时长")
    private Long schemeTotalTime;
    @Schema(title = "方案设计时间单位")
    private String schemeTimeUnit;
    @Schema(title = "方案持续时间(秒)")
    private Long schemeDurationSecond;
    @Schema(title = "方案剩余时间(秒)")
    private Long schemeRemnantSecond;


    @Schema(title = "沙盘总时长[天]")
    private Long sandTotalTime;
    @Schema(title = "沙盘时间单位")
    private String sandTimeUnit;


    @Schema(title = "沙盘持续时间(秒)")
    private Long sandDurationSecond;
    @Schema(title = "沙盘剩余时间(秒)")
    private Long sandRemnantSecond;

    @Schema(title = "服务器当前时间戳")
    private Long serverTimeStamp;

    @Schema(title = "实验状态")
    private Integer state;

}
