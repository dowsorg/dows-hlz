package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.SportPlanEntity;
import org.dows.hep.entity.snapshot.SnapSportPlanEntity;
import org.dows.hep.service.SportPlanService;
import org.dows.hep.service.snapshot.SnapSportPlanService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapSportPlanWriter extends BaseSnapshotFullTableWriter<SportPlanEntity, SportPlanService, SnapSportPlanEntity, SnapSportPlanService> {
    public SnapSportPlanWriter() {
        super(EnumSnapshotType.SPORTPlan, SnapSportPlanEntity::new);
    }
}
