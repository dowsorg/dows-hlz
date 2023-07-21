package org.dows.hep.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.event.ExperimentEvent;
import org.dows.hep.event.handler.EventHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
//@EnableAsync
public class ExperimentEventListener implements ApplicationListener<ExperimentEvent> {

    private final Map<String, EventHandler> eventHandlerMap;


    /**
     * 当事件发生时，触发当前方法
     *
     * @param experimentEvent
     */
    @Override
//    @Async
    public void onApplicationEvent(ExperimentEvent experimentEvent) {
        log.info("触发事件：{}", experimentEvent.getEventName().getHandler());
        String eventName = experimentEvent.getEventName().getHandler();
        EventHandler eventHandler = eventHandlerMap.get(eventName);
        if (null == eventHandler) {
            throw new BizException("未找到对应的事件处理器");
        }
        try {
            eventHandler.exec(experimentEvent.getSource());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
