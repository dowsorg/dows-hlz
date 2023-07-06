package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.FoodDishesEntity;
import org.dows.hep.entity.snapshot.SnapFoodDishesEntity;
import org.dows.hep.service.FoodDishesService;
import org.dows.hep.service.snapshot.SnapFoodDishesService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapFoodDishesWriter extends BaseSnapshotFullTableWriter<FoodDishesEntity, FoodDishesService, SnapFoodDishesEntity, SnapFoodDishesService> {
    public SnapFoodDishesWriter() {
        super(EnumSnapshotType.FOODDishes, SnapFoodDishesEntity::new);
    }
}
