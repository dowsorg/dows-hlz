package org.dows.hep.biz.event;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : wuzl
 * @date : 2023/6/24 10:22
 */
@Slf4j
public class ThreadPoolAbortPolicy extends ThreadPoolExecutor.AbortPolicy {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        log.error(String.format("thread[%s] fullReject...pool[%s] task[%s]", Thread.currentThread().getName(),e,r));
        super.rejectedExecution(r, e);
    }
}
