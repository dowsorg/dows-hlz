package org.dows.hep.event.handler;

import org.dows.hep.api.event.EventName;

public interface EventHandler<T> {

    default void exec(T obj) {

    }
}
