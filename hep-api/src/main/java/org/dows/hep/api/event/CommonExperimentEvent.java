package org.dows.hep.api.event;

/**
 * @author : wuzl
 * @date : 2023/7/11 10:43
 */
public class CommonExperimentEvent<T> extends ExperimentEvent{
    private CommonExperimentEvent(EventName eventName, T data){
        super(data);
        this.eventName=eventName;
    }
    public static <T> CommonExperimentEvent<T> create(EventName eventName, T data){
        return new CommonExperimentEvent(eventName, data);
    }

    private final EventName eventName;
    @Override
    public EventName getEventName() {
        return eventName;
    }
}
