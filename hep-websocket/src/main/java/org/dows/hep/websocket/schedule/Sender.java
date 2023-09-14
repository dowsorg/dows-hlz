package org.dows.hep.websocket.schedule;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageProto;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
@RequiredArgsConstructor
public class Sender implements Runnable {

    private final String msgId;
    private final Integer code;
    private final  Object mess;
    private final  Channel channel;

    private final AtomicInteger ttl=new AtomicInteger(5);
    @Override
    public void run() {
        String sc = HepClientManager.getMsgById(msgId);
        if (null == sc) {
            sc = MessageProto.buildSystProto(msgId, code, mess);
            HepClientManager.putMsg(msgId, sc);
        }
        channel.writeAndFlush(new TextWebSocketFrame(sc));

        if(ttl.decrementAndGet()<=0){
            MsgScheduler.remove(msgId);
        }

    }
}
