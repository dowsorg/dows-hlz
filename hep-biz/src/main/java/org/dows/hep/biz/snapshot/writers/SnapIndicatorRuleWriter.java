package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.IndicatorRuleEntity;
import org.dows.hep.entity.snapshot.SnapIndicatorRuleEntity;
import org.dows.hep.service.IndicatorRuleService;
import org.dows.hep.service.snapshot.SnapIndicatorRuleService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapIndicatorRuleWriter extends BaseSnapshotFullTableWriter<IndicatorRuleEntity, IndicatorRuleService, SnapIndicatorRuleEntity, SnapIndicatorRuleService> {
    public SnapIndicatorRuleWriter() {
        super(EnumSnapshotType.INDICATORRule, SnapIndicatorRuleEntity::new);
    }
}
