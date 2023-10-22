package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.IndicatorJudgeRiskFactorEntity;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeRiskFactorEntity;
import org.dows.hep.service.IndicatorJudgeRiskFactorService;
import org.dows.hep.service.snapshot.SnapIndicatorJudgeRiskFactorService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:45
 */

@Service
public class SnapIndicatorJudgeRiskFactorWriter extends BaseSnapshotFullTableWriter<IndicatorJudgeRiskFactorEntity, IndicatorJudgeRiskFactorService, SnapIndicatorJudgeRiskFactorEntity, SnapIndicatorJudgeRiskFactorService> {
    public SnapIndicatorJudgeRiskFactorWriter() {
        super(EnumSnapshotType.INDICATORJudgeRiskFactor, SnapIndicatorJudgeRiskFactorEntity::new);
    }
}
