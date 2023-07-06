package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.EventCategEntity;
import org.dows.hep.entity.snapshot.SnapEventCategEntity;
import org.dows.hep.service.EventCategService;
import org.dows.hep.service.snapshot.SnapEventCategService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapEventCategWriter extends BaseSnapshotFullTableWriter<EventCategEntity, EventCategService, SnapEventCategEntity, SnapEventCategService> {
    public SnapEventCategWriter() {
        super(EnumSnapshotType.CATEGEvent, SnapEventCategEntity::new);
    }
}
