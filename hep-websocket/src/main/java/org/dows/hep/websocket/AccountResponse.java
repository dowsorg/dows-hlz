package org.dows.hep.websocket;

import lombok.Data;

import java.util.List;

@Data
public class AccountResponse {

    private String accountId;

    private String accountName;

    private List<String> roleIds;


}
