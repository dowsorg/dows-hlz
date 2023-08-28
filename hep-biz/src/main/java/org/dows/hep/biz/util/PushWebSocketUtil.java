package org.dows.hep.biz.util;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.WsMessageResponse;
import org.dows.hep.api.enums.EnumNoticeType;
import org.dows.hep.api.enums.EnumWebSocketType;
import org.dows.hep.api.event.CommonWebSocketEventSource;
import org.dows.hep.api.notify.NoticeContent;
import org.dows.hep.api.notify.message.ExperimentPeriodMessage;
import org.dows.hep.biz.vo.PushWebScoketResult;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wuzl
 * @date : 2023/8/27 22:33
 */
@Slf4j
@Component
public class PushWebSocketUtil {

    private static volatile PushWebSocketUtil s_instance;

    public static PushWebSocketUtil Instance() {
        return s_instance;
    }

    private PushWebSocketUtil() {
        s_instance = this;
    }

    @Autowired
    private IdGenerator idGenerator;

    public <T> PushWebScoketResult pushCommon(EnumWebSocketType socketType, String experimentId, Set<String> clientIds, T data){
        return pushCommon(CommonWebSocketEventSource.builder()
                .socketType(socketType)
                .experimentInstanceId(experimentId)
                .clientIds(clientIds)
                .data(data)
                .build());
    }
    public <T> PushWebScoketResult pushCommon(CommonWebSocketEventSource<T> src){
        PushWebScoketResult rst=new PushWebScoketResult();
        try {
            if (ShareUtil.XObject.isEmpty(src)) {
                return rst.append("emptySource");
            }
            if (ShareUtil.XObject.anyEmpty(src.getExperimentInstanceId(), src.getClientIds())) {
                return rst.append("emptyClients");
            }
            rst.append(src.getSocketType().toString());
            ConcurrentMap<Channel, AccountInfo> clients = HepClientManager.getUserInfosByExperimentId(src.getExperimentInstanceId());
            if (ShareUtil.XObject.isEmpty(clients)) {
                return rst.append("missClients[%s]", src.getExperimentInstanceId());
            }
            WsMessageResponse wsMsg = WsMessageResponse.builder()
                    .type(src.getSocketType())
                    .data(src.getData())
                    .build();
            Response<WsMessageResponse> wsPack = Response.ok(wsMsg);
            final Set<String> clientIds = src.getClientIds();
            rst.getMissClients().addAll(src.getClientIds());
            clients.forEach((k,v)->{
                final String clientId=v.getAccountName();
                if(!clientIds.contains(clientId)){
                    return;
                }
                rst.getHitClients().add(clientId);
                rst.getMissClients().remove(clientId);
                //HepClientManager.sendInfo(k,MessageCode.MESS_CODE, wsPack);
                HepClientManager.sendInfoRetry(k, MessageCode.MESS_CODE, wsPack, idGenerator.nextIdStr(), null);

            });
        }catch (Exception ex){
            rst.append("error:%s",ex.getMessage());
            log.error(String.format("PushWebSocketUtil.pushCommon error. rst:%s", rst),ex);
        }finally {
            log.info(String.format("PushWebSocketUtil.pushCommon rst:%s", rst));
        }
        return rst;

    }

    public <T> PushWebScoketResult pushCommon(String experimentId, Set<String> clientIds,T data){
        PushWebScoketResult rst=new PushWebScoketResult();
        try {
            if (ShareUtil.XObject.isEmpty(data)) {
                return rst.append("emptySource");
            }
            if (ShareUtil.XObject.anyEmpty(experimentId, clientIds)) {
                return rst.append("emptyClients");
            }
            ConcurrentMap<Channel, AccountInfo> clients = HepClientManager.getUserInfosByExperimentId(experimentId);
            if (ShareUtil.XObject.isEmpty(clients)) {
                return rst.append("missClients[%s]", experimentId);
            }
            rst.getMissClients().addAll(clientIds);
            clients.forEach((k,v)->{
                final String clientId=v.getAccountName();
                if(!clientIds.contains(clientId)){
                    return;
                }
                rst.getHitClients().add(clientId);
                rst.getMissClients().remove(clientId);
                //HepClientManager.sendInfo(k,MessageCode.MESS_CODE, data);
                HepClientManager.sendInfoRetry(k, MessageCode.MESS_CODE, data, idGenerator.nextIdStr(), null);

            });
        }catch (Exception ex){
            rst.append("error:%s",ex.getMessage());
            log.error(String.format("PushWebSocketUtil.pushCommon error. rst:%s", rst),ex);
        }finally {
            log.info(String.format("PushWebSocketUtil.pushCommon rst:%s", rst));
        }
        return rst;

    }

    //region
    public PushWebScoketResult pushPeriodNotice(String experimentId, Set<String> clientIds,ExperimentPeriodMessage periodMessage){
        NoticeContent noticeContent = NoticeContent.builder()
                .payload(periodMessage)
                .type(EnumNoticeType.BoardCastSysEvent)
                .build();
        return pushCommon(experimentId,clientIds,noticeContent);
    }

    //endregion
}
