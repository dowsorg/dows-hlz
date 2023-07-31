import io.netty.channel.Channel;
import org.dows.framework.api.Response;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.websocket.HepClientManager;
import org.dows.hep.websocket.proto.MessageCode;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class MsgTest {

    public static void main(String[] args) {
        // 通知客户端
        ConcurrentMap<Channel, AccountInfo> userInfos = HepClientManager.getUserInfos();
        Set<Channel> channels = userInfos.keySet();
        for (Channel channel : channels) {
            HepClientManager.sendInfoRetry(channel, MessageCode.MESS_CODE, Response.ok("ok"),null);
        }
    }
}
