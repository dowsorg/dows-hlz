package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;
import org.dows.hep.service.CaseIndicatorExpressionItemService;
import org.dows.hep.service.snapshot.SnapCaseIndicatorExpressionItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapCaseIndicatorExpressionItemWriter extends BaseSnapshotFullTableWriter<CaseIndicatorExpressionItemEntity, CaseIndicatorExpressionItemService, SnapCaseIndicatorExpressionItemEntity, SnapCaseIndicatorExpressionItemService> {
    public SnapCaseIndicatorExpressionItemWriter() {
        super(EnumSnapshotType.CASEIndicatorExpressionItem, SnapCaseIndicatorExpressionItemEntity::new);
    }

    @Override
    public List<CaseIndicatorExpressionItemEntity> readSource(SnapshotRequest req) {
        return null;
    }
}
