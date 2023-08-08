package org.dows.hep.websocket;

import lombok.Data;

@Data
public class OnlineAccount {
    private String appId;
    // 账号ID
    private String accountId;
    // 实验ID
    private String experimentId;
    // 小组ID
    private String groupId;

    public String getOnlieFlag() {
        return appId + experimentId + accountId;
    }
}
