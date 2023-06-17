package org.dows.hep.event.handler;

import cn.hutool.core.collection.CollUtil;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.api.user.experiment.response.ExptSchemeSyncResponse;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExptSchemeSyncHandler extends AbstractEventHandler implements EventHandler<ExptSchemeSyncResponse>{
    @Override
    public void exec(ExptSchemeSyncResponse obj) {
        List<String> accountIds = obj.getAccountIds();
        if (CollUtil.isEmpty(accountIds)) {
            return;
        }
        Set<String> accountIdSet = new HashSet<>(accountIds);
        ExperimentSchemeResponse experimentSchemeResponse = obj.getExperimentSchemeResponse();

        // 通知客户端
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
        Set<Map.Entry<Channel, AccountInfo>> entries = userInfos.entrySet();
        for (Map.Entry<Channel, AccountInfo> entry : entries) {
            AccountInfo accountInfo = entry.getValue();
            if (accountIdSet.contains(accountInfo.getAccountName())) {
                Channel channel = entry.getKey();
                HepClientManager.sendInfo(channel, MessageCode.MESS_CODE, experimentSchemeResponse);
            }
        }
    }
}
