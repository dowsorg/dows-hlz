package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.entity.snapshot.SnapIndicatorInstanceEntity;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.hep.service.snapshot.SnapIndicatorInstanceService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapIndicatorInstanceWriter extends BaseSnapshotFullTableWriter<IndicatorInstanceEntity, IndicatorInstanceService, SnapIndicatorInstanceEntity, SnapIndicatorInstanceService> {
    public SnapIndicatorInstanceWriter() {
        super(EnumSnapshotType.INDICATORInstance, SnapIndicatorInstanceEntity::new);
    }
}
