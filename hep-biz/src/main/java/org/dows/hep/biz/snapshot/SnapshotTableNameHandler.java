package org.dows.hep.biz.snapshot;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.dows.hep.biz.util.ShareUtil;

/**
 * @author : wuzl
 * @date : 2023/7/6 11:12
 */
public class SnapshotTableNameHandler implements TableNameHandler {
    @Override
    public String dynamicTableName(String sql, String tableName) {
        EnumSnapshotType snapshotType = EnumSnapshotType.ofSrcTableName(tableName);
        if (snapshotType == EnumSnapshotType.NONE) {
            return tableName;
        }
        if (!SnapshotRequestHolder.hasSnapshotRequest()) {
            return tableName;
        }
        return ShareUtil.XObject.notEmpty(snapshotType.getDstTableName()) ?
                snapshotType.getDstTableName() : "snap_".concat(snapshotType.getSrcTableName());
    }


}
