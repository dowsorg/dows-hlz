package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.FoodCookbookNutrientEntity;
import org.dows.hep.entity.snapshot.SnapFoodCookbookNutrientEntity;
import org.dows.hep.service.FoodCookbookNutrientService;
import org.dows.hep.service.snapshot.SnapFoodCookbookNutrientService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapFoodCookbookNutrientWriter extends BaseSnapshotFullTableWriter<FoodCookbookNutrientEntity, FoodCookbookNutrientService, SnapFoodCookbookNutrientEntity, SnapFoodCookbookNutrientService> {
    public SnapFoodCookbookNutrientWriter() {
        super(EnumSnapshotType.FOODCookbookNutrient , SnapFoodCookbookNutrientEntity::new);
    }
}
