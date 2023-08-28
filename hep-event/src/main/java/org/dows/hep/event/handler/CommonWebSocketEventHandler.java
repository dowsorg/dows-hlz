package org.dows.hep.event.handler;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.event.CommonWebSocketEventSource;
import org.dows.hep.biz.util.PushWebSocketUtil;
import org.springframework.stereotype.Component;

/**
 * @author : wuzl
 * @date : 2023/7/11 11:54
 */
@Component
@Slf4j
public class CommonWebSocketEventHandler<T> extends AbstractEventHandler implements EventHandler<CommonWebSocketEventSource<T>> {

    @Override
    public void exec(CommonWebSocketEventSource<T> obj) {

        PushWebSocketUtil.Instance().pushCommon(obj);

    }


}
