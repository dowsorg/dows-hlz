package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.FoodCookbookEntity;
import org.dows.hep.entity.snapshot.SnapFoodCookbookEntity;
import org.dows.hep.service.FoodCookbookService;
import org.dows.hep.service.snapshot.SnapFoodCookbookService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapFoodCookbookWriter extends BaseSnapshotFullTableWriter<FoodCookbookEntity, FoodCookbookService, SnapFoodCookbookEntity, SnapFoodCookbookService> {
    public SnapFoodCookbookWriter() {
        super(EnumSnapshotType.FOODCookbook, SnapFoodCookbookEntity::new);
    }
}
