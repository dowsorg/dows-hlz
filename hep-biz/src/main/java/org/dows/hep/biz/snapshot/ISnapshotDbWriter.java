package org.dows.hep.biz.snapshot;

/**
 * @author : wuzl
 * @date : 2023/6/27 14:34
 */
public interface ISnapshotDbWriter<T> extends ISnapshotWriter {
    default boolean autoInjectFlag() {
        return true;
    }
    EnumSnapshotType getSnapshotType();

    T readSource(SnapshotRequest req);

    boolean write(SnapshotRequest req, T data);

    default boolean write(SnapshotRequest req) {
        return write(req, readSource(req));
    }
}
