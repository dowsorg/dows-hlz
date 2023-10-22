package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.IndicatorJudgeHealthProblemEntity;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeHealthProblemEntity;
import org.dows.hep.service.IndicatorJudgeHealthProblemService;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeHealthProblemService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:45
 */
@Service
public class SnapIndicatorJudgeHealthProblemWriter extends BaseSnapshotFullTableWriter<IndicatorJudgeHealthProblemEntity, IndicatorJudgeHealthProblemService, SnapIndicatorJudgeHealthProblemEntity, SnapIndicatorJudgeHealthProblemService> {
    public SnapIndicatorJudgeHealthProblemWriter() {
        super(EnumSnapshotType.INDICATORJudgeHealthProblem, SnapIndicatorJudgeHealthProblemEntity::new);
    }

}
