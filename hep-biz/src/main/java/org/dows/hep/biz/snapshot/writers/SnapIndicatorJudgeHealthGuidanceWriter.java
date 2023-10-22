package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.IndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeHealthGuidanceEntity;
import org.dows.hep.service.IndicatorJudgeHealthGuidanceService;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeHealthGuidanceService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:45
 */

@Service
public class SnapIndicatorJudgeHealthGuidanceWriter extends BaseSnapshotFullTableWriter<IndicatorJudgeHealthGuidanceEntity, IndicatorJudgeHealthGuidanceService, SnapIndicatorJudgeHealthGuidanceEntity, SnapIndicatorJudgeHealthGuidanceService> {
    public SnapIndicatorJudgeHealthGuidanceWriter() {
        super(EnumSnapshotType.INDICATORJudgeHealthGuidance, SnapIndicatorJudgeHealthGuidanceEntity::new);
    }
}
