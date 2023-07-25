package org.dows.hep.api.notify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.notify.message.ExperimentMessage;

import java.util.List;

/**
 * 通知参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeContent<T extends ExperimentMessage> {
    // 待通知的用户列表
    private List<String> accountId;
    // 消息code类型
    private int messageCode;
    // 通知类型[广播|ping|pong...]
    private EnumNoticeType noticeType;

    // 载荷
    private T payload;


}