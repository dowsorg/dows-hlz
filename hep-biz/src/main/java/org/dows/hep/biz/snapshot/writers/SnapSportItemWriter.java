package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.SportItemEntity;
import org.dows.hep.entity.snapshot.SnapSportItemEntity;
import org.dows.hep.service.SportItemService;
import org.dows.hep.service.snapshot.SnapSportItemService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapSportItemWriter extends BaseSnapshotFullTableWriter<SportItemEntity, SportItemService, SnapSportItemEntity, SnapSportItemService> {
    public SnapSportItemWriter() {
        super(EnumSnapshotType.SPORTItem, SnapSportItemEntity::new);
    }
}
