package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.InterveneCategoryEntity;
import org.dows.hep.entity.snapshot.SnapInterveneCategoryEntity;
import org.dows.hep.service.InterveneCategoryService;
import org.dows.hep.service.snapshot.SnapInterveneCategoryService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapInterveneCategoryWriter extends BaseSnapshotFullTableWriter<InterveneCategoryEntity, InterveneCategoryService, SnapInterveneCategoryEntity, SnapInterveneCategoryService> {
    public SnapInterveneCategoryWriter() {
        super(EnumSnapshotType.CATEGIntervene, SnapInterveneCategoryEntity::new);
    }
}
