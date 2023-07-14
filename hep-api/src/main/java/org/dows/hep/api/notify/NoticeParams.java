package org.dows.hep.api.notify;

import lombok.Data;
import org.dows.hep.api.enums.EnumNoticeType;

import java.util.Date;
import java.util.List;

/**
 * 通知参数
 */
@Data
public class NoticeParams {
    // 待通知的用户列表
    private List<String> accountId;
    // 实验ID
    private String experimentInstanceId;
    // 实验小组ID
    private String experimentGroupId;

    // 本期开始时间
    private Date startTime;
    // 本期结束时间
    private Date endTime;
    // 当前期数
    private Integer currentPeriod;
    // 总期数
    private Integer periods;

    // 通知类型[广播|ping|pong...]
    private EnumNoticeType noticeType;

    // 消息code类型
    private String messageCode;
}