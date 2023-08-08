package org.dows.hep.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class HepClientMonitor {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public void start() {
        // 定时扫描所有的Channel，关闭失效的Channel
        executorService.scheduleAtFixedRate(() -> {
            HepClientManager.scanNotActiveChannel();
        }, 3, 60, TimeUnit.SECONDS);

        // 定时向所有客户端发送Ping消息
        executorService.scheduleAtFixedRate(() -> {
            HepClientManager.broadCastPing();
        }, 3, 50, TimeUnit.SECONDS);
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
