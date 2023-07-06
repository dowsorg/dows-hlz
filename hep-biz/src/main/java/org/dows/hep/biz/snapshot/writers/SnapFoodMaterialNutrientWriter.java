package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.FoodMaterialNutrientEntity;
import org.dows.hep.entity.snapshot.SnapFoodMaterialNutrientEntity;
import org.dows.hep.service.FoodMaterialNutrientService;
import org.dows.hep.service.snapshot.SnapFoodMaterialNutrientService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapFoodMaterialNutrientWriter extends BaseSnapshotFullTableWriter<FoodMaterialNutrientEntity, FoodMaterialNutrientService, SnapFoodMaterialNutrientEntity, SnapFoodMaterialNutrientService> {
    public SnapFoodMaterialNutrientWriter() {
        super(EnumSnapshotType.FOODMaterialNutrient, SnapFoodMaterialNutrientEntity::new);
    }
}
