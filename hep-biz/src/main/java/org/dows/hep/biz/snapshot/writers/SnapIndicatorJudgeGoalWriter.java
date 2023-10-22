package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.IndicatorJudgeGoalEntity;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeGoalEntity;
import org.dows.hep.service.IndicatorJudgeGoalService;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeGoalService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 13:26
 */

@Service
public class SnapIndicatorJudgeGoalWriter extends BaseSnapshotFullTableWriter<IndicatorJudgeGoalEntity, IndicatorJudgeGoalService, SnapIndicatorJudgeGoalEntity, SnapIndicatorJudgeGoalService> {
    public SnapIndicatorJudgeGoalWriter() {
        super(EnumSnapshotType.INDICATORJudgeGoal, SnapIndicatorJudgeGoalEntity::new);
    }
}
