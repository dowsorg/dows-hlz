package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.FoodMaterialEntity;
import org.dows.hep.entity.snapshot.SnapFoodMaterialEntity;
import org.dows.hep.service.FoodMaterialService;
import org.dows.hep.service.snapshot.SnapFoodMaterialService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapCaseIndicatorExpressionWriter extends BaseSnapshotFullTableWriter<FoodMaterialEntity, FoodMaterialService, SnapFoodMaterialEntity, SnapFoodMaterialService> {
    public SnapCaseIndicatorExpressionWriter() {
        super(EnumSnapshotType.FOODMaterial, SnapFoodMaterialEntity::new);
    }
}
