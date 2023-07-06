package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.CaseIndicatorInstanceEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorInstanceEntity;
import org.dows.hep.service.CaseIndicatorInstanceService;
import org.dows.hep.service.snapshot.SnapCaseIndicatorInstanceService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapCaseIndicatorInstanceWriter extends BaseSnapshotFullTableWriter<CaseIndicatorInstanceEntity, CaseIndicatorInstanceService, SnapCaseIndicatorInstanceEntity, SnapCaseIndicatorInstanceService> {
    public SnapCaseIndicatorInstanceWriter() {
        super(EnumSnapshotType.CASEIndicatorInstance, SnapCaseIndicatorInstanceEntity::new);
    }
}
