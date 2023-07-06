package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.CaseIndicatorExpressionRefEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionRefEntity;
import org.dows.hep.service.CaseIndicatorExpressionRefService;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionRefService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapCaseIndicatorExpressionRefWriter extends BaseSnapshotFullTableWriter<CaseIndicatorExpressionRefEntity, CaseIndicatorExpressionRefService, SnapCaseIndicatorExpressionRefEntity, SnapCaseIndicatorExpressionRefService> {
    public SnapCaseIndicatorExpressionRefWriter() {
        super(EnumSnapshotType.CASEIndicatorExpressionRef, SnapCaseIndicatorExpressionRefEntity::new);
    }
}
