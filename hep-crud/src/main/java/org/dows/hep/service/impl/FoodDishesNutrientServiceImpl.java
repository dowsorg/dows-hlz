package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodDishesNutrientMapper;
import org.dows.hep.entity.FoodDishesNutrientEntity;
import org.dows.hep.service.FoodDishesNutrientService;
import org.springframework.stereotype.Service;


/**
 * 菜肴成分(FoodDishesNutrient)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:42
 */
@Service("foodDishesNutrientService")
public class FoodDishesNutrientServiceImpl extends MybatisCrudServiceImpl<FoodDishesNutrientMapper, FoodDishesNutrientEntity> implements FoodDishesNutrientService {

}

