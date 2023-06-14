package org.dows.hep.websocket;

import lombok.Data;

@Data
public class OnlineAccount {
    private String appId;
    private String accountId;
    private String experimentId;

    public String getOnlieFlag() {
        return appId + experimentId + accountId;
    }
}
