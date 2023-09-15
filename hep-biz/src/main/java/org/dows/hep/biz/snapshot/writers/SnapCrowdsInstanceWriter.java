package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.CrowdsInstanceEntity;
import org.dows.hep.entity.snapshot.SnapCrowdsInstanceEntity;
import org.dows.hep.service.CrowdsInstanceService;
import org.dows.hep.service.snapshot.SnapCrowdsInstanceService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/9/14 17:43
 */

@Service
public class SnapCrowdsInstanceWriter extends BaseSnapshotFullTableWriter<CrowdsInstanceEntity, CrowdsInstanceService, SnapCrowdsInstanceEntity, SnapCrowdsInstanceService> {
    public SnapCrowdsInstanceWriter() {
        super(EnumSnapshotType.CROWD, SnapCrowdsInstanceEntity::new);
    }
}
