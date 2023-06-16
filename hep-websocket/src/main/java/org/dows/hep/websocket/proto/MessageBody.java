package org.dows.hep.websocket.proto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageBody<T> implements Serializable {
    private String appId;
    private String bizCode;
    private String accountId;
    private Date sendTime;
    private T data;
}
