package org.dows.hep.api.notify.message;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 实验期数开始结束消息
 */
@Builder
@Data
public class ExperimentPeriodMessage implements ExperimentMessage {
    // 实验ID
    private String experimentInstanceId;
    // 实验小组ID
    private String experimentGroupId;
    // 当前期数
    private Integer currentPeriod;
    // 总期数
    private Integer periods;
    // 本期开始时间
    private Date startTime;
    // 本期结束时间
    private Date endTime;
    // 状态[0:开始，1：结束]
    private Integer state;

}
