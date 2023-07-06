package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.FoodDishesMaterialEntity;
import org.dows.hep.entity.snapshot.SnapFoodDishesMaterialEntity;
import org.dows.hep.service.FoodDishesMaterialService;
import org.dows.hep.service.snapshot.SnapFoodDishesMaterialService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapFoodDishesMaterialWriter extends BaseSnapshotFullTableWriter<FoodDishesMaterialEntity, FoodDishesMaterialService, SnapFoodDishesMaterialEntity, SnapFoodDishesMaterialService> {
    public SnapFoodDishesMaterialWriter() {
        super(EnumSnapshotType.FOODDishesMaterial, SnapFoodDishesMaterialEntity::new);
    }
}
