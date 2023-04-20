package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodNutrientMapper;
import org.dows.hep.entity.FoodNutrientEntity;
import org.dows.hep.service.FoodNutrientService;
import org.springframework.stereotype.Service;


/**
 * 食物成分(FoodNutrient)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:57:03
 */
@Service("foodNutrientService")
public class FoodNutrientServiceImpl extends MybatisCrudServiceImpl<FoodNutrientMapper, FoodNutrientEntity> implements FoodNutrientService {

}

