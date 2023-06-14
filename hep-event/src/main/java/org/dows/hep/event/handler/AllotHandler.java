package org.dows.hep.event.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.event.EventName;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllotHandler extends AbstractEventHandler implements EventHandler {


    @Override
    public void exec(Object obj) {

        //todo 定时器
        log.info("开启调度....");




    }
}
