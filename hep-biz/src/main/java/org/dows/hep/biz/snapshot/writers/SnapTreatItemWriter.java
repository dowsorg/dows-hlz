package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.TreatItemEntity;
import org.dows.hep.entity.snapshot.SnapTreatItemEntity;
import org.dows.hep.service.TreatItemService;
import org.dows.hep.service.snapshot.SnapTreatItemService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapTreatItemWriter extends BaseSnapshotFullTableWriter<TreatItemEntity, TreatItemService, SnapTreatItemEntity, SnapTreatItemService> {
    public SnapTreatItemWriter() {
        super(EnumSnapshotType.TreatItem, SnapTreatItemEntity::new);
    }
}
