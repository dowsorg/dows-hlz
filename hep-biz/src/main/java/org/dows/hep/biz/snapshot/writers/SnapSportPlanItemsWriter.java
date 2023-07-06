package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.SportPlanItemsEntity;
import org.dows.hep.entity.snapshot.SnapSportPlanItemsEntity;
import org.dows.hep.service.SportPlanItemsService;
import org.dows.hep.service.snapshot.SnapSportPlanItemsService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapSportPlanItemsWriter extends BaseSnapshotFullTableWriter<SportPlanItemsEntity, SportPlanItemsService, SnapSportPlanItemsEntity, SnapSportPlanItemsService> {
    public SnapSportPlanItemsWriter() {
        super(EnumSnapshotType.SPORTPlanItems, SnapSportPlanItemsEntity::new);
    }
}
