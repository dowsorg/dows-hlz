package org.dows.hep.service.impl.snapshot;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.snapshot.SnapFoodDishesNutrientEntity;
import org.dows.hep.mapper.snapshot.SnapFoodDishesNutrientMapper;
import org.dows.hep.service.snapshot.SnapFoodDishesNutrientService;
import org.springframework.stereotype.Service;

/**
 * @author : wuzl
 * @date : 2023/7/3 16:48
 */
@Service("snapFoodDishesNutrientService")
public class SnapFoodDishesNutrientServiceImpl extends MybatisCrudServiceImpl<SnapFoodDishesNutrientMapper, SnapFoodDishesNutrientEntity> implements SnapFoodDishesNutrientService {

}
