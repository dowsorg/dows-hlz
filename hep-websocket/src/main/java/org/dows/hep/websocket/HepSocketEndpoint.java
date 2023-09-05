package org.dows.hep.websocket;

import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.websocket.*;
import org.dows.hep.websocket.proto.MessageBody;
import org.dows.hep.websocket.schedule.MsgScheduler;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Map;

/**
 * 测试：
 * http://www.jsons.cn/websocket/
 */
@Slf4j
@WebSocketEndpoint(path = "/hep")
public class HepSocketEndpoint {


    @BeforeHandshake
    public void handshake(NettySession nettySession, HttpHeaders headers, @RequestParam String req, @RequestParam MultiValueMap reqMap, @PathVariable String arg, @PathVariable Map pathMap) {
        nettySession.setSubprotocols("stomp");
        if (!"ok".equals(req)) {
            log.error("Authentication failed!");
            // nettySession.close();
        }
    }

    @OnOpen
    public void onOpen(NettySession nettySession, HttpHeaders headers, @RequestParam String req, @RequestParam Map reqMap, @PathVariable String arg, @PathVariable Map pathMap) {
        log.info("new connection");
        OnlineAccount onlineAccount = JSONUtil.toBean(JSONUtil.toJsonStr(reqMap), OnlineAccount.class);
        // 保存当前会话账号
        HepClientManager.saveUser(nettySession.channel(), onlineAccount);
    }

    @OnClose
    public void onClose(NettySession nettySession) throws IOException {
        Object attribute = nettySession.getAttribute(HepClientManager.ACCOUNT_IN_SESSION_ATTRIBUTE.name());
        log.info("one connection closed:{}", attribute);
        HepClientManager.removeChannel(nettySession.channel());

    }

    @OnError
    public void onError(NettySession nettySession, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(NettySession nettySession, String message) {
        try {

            Channel channel = nettySession.channel();
            //nettySession.getAttribute("");

            // todo 应该是dispatch模式，先简单实现
            MessageBody messageBody = JSONUtil.toBean(message, MessageBody.class);
            // 确定收到具体用户的信息，处理业务逻辑
            //AccountInfo accountInfo = HepClientManager.getAccountInfo(nettySession.channel());
            MsgScheduler.remove(messageBody.getMsgId());
        } catch (Exception e) {
            log.error("HepSocketEndpoint.onMessage",e);
        }
    }

    @OnBinary
    public void onBinary(NettySession nettySession, byte[] bytes) {
        //nettySession.sendBinary(bytes);
    }

    @OnEvent
    public void onEvent(NettySession nettySession, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }
}
