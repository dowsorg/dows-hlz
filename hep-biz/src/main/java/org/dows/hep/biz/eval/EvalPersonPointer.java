package org.dows.hep.biz.eval;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:17
 */

@Data
@Accessors(chain = true)
public class EvalPersonPointer {
    private String experimentId;
    private final AtomicInteger curEvalNo=new AtomicInteger();

    private final ReadWriteLock rwlock=new ReentrantReadWriteLock();
}
