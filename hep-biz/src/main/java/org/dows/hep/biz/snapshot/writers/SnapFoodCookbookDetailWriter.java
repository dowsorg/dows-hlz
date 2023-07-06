package org.dows.hep.biz.snapshot.writers;

import org.dows.hep.biz.snapshot.BaseSnapshotFullTableWriter;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.entity.FoodCookbookDetailEntity;
import org.dows.hep.entity.snapshot.SnapFoodCookbookDetailEntity;
import org.dows.hep.service.FoodCookbookDetailService;
import org.dows.hep.service.snapshot.SnapFoodCookbookDetailService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:14
 */
@Service
public class SnapFoodCookbookDetailWriter extends BaseSnapshotFullTableWriter<FoodCookbookDetailEntity, FoodCookbookDetailService, SnapFoodCookbookDetailEntity, SnapFoodCookbookDetailService> {
    public SnapFoodCookbookDetailWriter() {
        super(EnumSnapshotType.FOODCookbookDetail, SnapFoodCookbookDetailEntity::new);
    }
}
