package org.dows.hep.websocket.proto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageBody<T> implements Serializable {
    // 应用ID
    private String appId;
    // 实验ID
    private String experimentId;
    // 小组ID
    private String groupId;
    // 期数
    private String period;
    // 账号ID
    private String accountId;
    // 消息ID
    private String msgId;
    // 发送时间
    private Date sendTime;
    // 数据体(map结构)
    private T data;
}
