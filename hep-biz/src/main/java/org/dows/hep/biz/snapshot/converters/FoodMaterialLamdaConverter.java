package org.dows.hep.biz.snapshot.converters;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.hep.biz.snapshot.BaseSnapshotLambdaConverter;
import org.dows.hep.entity.snapshot.SnapFoodMaterialEntity;

import java.util.Arrays;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/3 12:05
 */
public class FoodMaterialLamdaConverter extends BaseSnapshotLambdaConverter<SnapFoodMaterialEntity> {
    private static final FoodMaterialLamdaConverter s_instance=new FoodMaterialLamdaConverter();
    public static final FoodMaterialLamdaConverter Instance(){
        return s_instance;
    }

    @Override
    public List<SFunction<SnapFoodMaterialEntity, ?>> getLambdaList() {
        return Arrays.asList(SnapFoodMaterialEntity::getExperimentInstanceId,
                SnapFoodMaterialEntity::getId,
                SnapFoodMaterialEntity::getFoodMaterialId,
                SnapFoodMaterialEntity::getAppId,
                SnapFoodMaterialEntity::getFoodMaterialName,
                SnapFoodMaterialEntity::getPic,
                SnapFoodMaterialEntity::getInterveneCategId,
                SnapFoodMaterialEntity::getCategName,
                SnapFoodMaterialEntity::getCategIdLv1,
                SnapFoodMaterialEntity::getCategNameLv1,
                SnapFoodMaterialEntity::getCategIdPath,
                SnapFoodMaterialEntity::getCategNamePath,
                SnapFoodMaterialEntity::getProtein,
                SnapFoodMaterialEntity::getCho,
                SnapFoodMaterialEntity::getFat,
                SnapFoodMaterialEntity::getEnergy,
                SnapFoodMaterialEntity::getState,
                SnapFoodMaterialEntity::getDescr,
                SnapFoodMaterialEntity::getDt
        );
    }

}
