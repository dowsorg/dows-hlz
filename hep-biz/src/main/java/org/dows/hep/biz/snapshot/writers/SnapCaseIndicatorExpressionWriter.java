package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.CaseIndicatorExpressionEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionEntity;
import org.dows.hep.service.CaseIndicatorExpressionService;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapCaseIndicatorExpressionWriter extends BaseSnapshotFullTableWriter<CaseIndicatorExpressionEntity, CaseIndicatorExpressionService, SnapCaseIndicatorExpressionEntity, SnapCaseIndicatorExpressionService> {
    public SnapCaseIndicatorExpressionWriter() {
        super(EnumSnapshotType.CASEIndicatorExpression, SnapCaseIndicatorExpressionEntity::new);
    }
}
