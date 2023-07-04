package org.dows.hep.biz.snapshot;

/**
 * @author : wuzl
 * @date : 2023/6/27 14:29
 */
public interface ISnapshotWriter {
    boolean write(SnapshotRequest req);
}
