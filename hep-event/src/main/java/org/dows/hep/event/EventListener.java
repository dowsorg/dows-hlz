package org.dows.hep.event;

import lombok.RequiredArgsConstructor;
import org.dows.hep.event.handler.EventHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
@EnableAsync
public class EventListener implements ApplicationListener<ExperimentEvent> {

    private final Map<String, EventHandler> eventHandlerMap;

    /**
     * 当事件发生时，触发当前方法
     *
     * @param experimentEvent
     */
    @Override
    @Async
    public void onApplicationEvent(ExperimentEvent experimentEvent) {
        String eventName = experimentEvent.getEventName().name();
        eventHandlerMap.get(eventName).exec(experimentEvent.getSource());
    }
}
