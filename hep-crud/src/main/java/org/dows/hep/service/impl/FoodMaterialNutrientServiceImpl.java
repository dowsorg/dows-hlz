package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodMaterialNutrientMapper;
import org.dows.hep.entity.FoodMaterialNutrientEntity;
import org.dows.hep.service.FoodMaterialNutrientService;
import org.springframework.stereotype.Service;


/**
 * 食材成分(FoodMaterialNutrient)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("foodMaterialNutrientService")
public class FoodMaterialNutrientServiceImpl extends MybatisCrudServiceImpl<FoodMaterialNutrientMapper, FoodMaterialNutrientEntity> implements FoodMaterialNutrientService {

}

