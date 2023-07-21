package org.dows.hep.event.handler;

import org.dows.hep.api.event.EventName;

import java.util.concurrent.ExecutionException;

public interface EventHandler<T> {

    default void exec(T obj) throws ExecutionException, InterruptedException {

    }
}
