package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.RiskModelEntity;
import org.dows.hep.entity.snapshot.SnapRiskModelEntity;
import org.dows.hep.service.RiskModelService;
import org.dows.hep.service.snapshot.SnapRiskModelService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/9/14 17:43
 */

@Service
public class SnapRiskModelWriter extends BaseSnapshotFullTableWriter<RiskModelEntity, RiskModelService, SnapRiskModelEntity, SnapRiskModelService> {

    public SnapRiskModelWriter() {
        super(EnumSnapshotType.RISKModel, SnapRiskModelEntity::new);
    }
}
