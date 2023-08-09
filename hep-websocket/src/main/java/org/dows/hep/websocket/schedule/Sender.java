package org.dows.hep.websocket.schedule;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageProto;

@Data
@RequiredArgsConstructor
public class Sender implements Runnable {

    private final String msgId;
    private final Integer code;
    private final  Object mess;
    private final  Channel channel;
    @Override
    public void run() {
        String sc = HepClientManager.getMsgById(msgId);
        if (null == sc) {
            sc = MessageProto.buildSystProto(msgId, code, mess);
            HepClientManager.putMsg(msgId, sc);
        }
        channel.writeAndFlush(new TextWebSocketFrame(sc));
    }
}
