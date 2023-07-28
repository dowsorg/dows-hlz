package org.dows.hep.event.handler;

import cn.hutool.core.collection.CollUtil;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.WsMessageResponse;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.event.source.ExptSchemeSubmittedEventSource;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fhb
 * @version 1.0
 * @description `实验-方案设计`组长分配完题目的时候`publish`该事件，以通知组内成员开始作答
 * @date 2023/6/19 23:18
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class ExptSchemeSubmittedHandler extends AbstractEventHandler implements EventHandler<ExptSchemeSubmittedEventSource>{
    @Override
    public void exec(ExptSchemeSubmittedEventSource obj) {
        List<String> accountIds = obj.getAccountIds();
        if (CollUtil.isEmpty(accountIds)) {
            return;
        }
        HashSet<String> accountIdSet = new HashSet<>(accountIds);

        // 通知客户端
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
        Set<Map.Entry<Channel, AccountInfo>> entries = userInfos.entrySet();
        for (Map.Entry<Channel, AccountInfo> entry : entries) {
            AccountInfo accountInfo = entry.getValue();
            if (accountIdSet.contains(accountInfo.getAccountName())) {
                Channel channel = entry.getKey();
                WsMessageResponse result = WsMessageResponse.builder()
                        .type(EnumWebSocketType.EXPT_SCHEME_SUBMITTED)
                        .data(EnumWebSocketType.EXPT_SCHEME_SUBMITTED.name())
                        .build();
                Response<WsMessageResponse> response = Response.ok(result);
                HepClientManager.sendInfoRetry(channel, MessageCode.MESS_CODE, response, null);
            }
        }
    }
}
