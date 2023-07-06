package org.dows.hep.biz.snapshot.writers;

import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.snapshot.BaseSnapshotWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRequest;
import org.dows.hep.entity.ExperimentEventEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/5 18:07
 */
@Service
@RequiredArgsConstructor
public class SnapCaseEventWriter extends BaseSnapshotWriter<SnapCaseEventWriter.SnapData> {


    @Override
    public EnumSnapshotType getSnapshotType() {
        return EnumSnapshotType.CASEEvent;
    }

    @Override
    public SnapData readSource(SnapshotRequest req) {

        return null;
    }

    @Override
    protected boolean saveSnapshotData(SnapshotRequest req, SnapData data) {
        return false;
    }

    public static class SnapData {
        private List<ExperimentEventEntity> events;

    }
}
