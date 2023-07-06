package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.FoodDishesNutrientEntity;
import org.dows.hep.entity.snapshot.SnapFoodDishesNutrientEntity;
import org.dows.hep.service.FoodDishesNutrientService;
import org.dows.hep.service.snapshot.SnapFoodDishesNutrientService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapFoodDishesNutrientWriter extends BaseSnapshotFullTableWriter<FoodDishesNutrientEntity, FoodDishesNutrientService, SnapFoodDishesNutrientEntity, SnapFoodDishesNutrientService> {
    public SnapFoodDishesNutrientWriter() {
        super(EnumSnapshotType.FOODDishesNutrient, SnapFoodDishesNutrientEntity::new);
    }
}
