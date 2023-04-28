package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodCookbookNutrientMapper;
import org.dows.hep.entity.FoodCookbookNutrientEntity;
import org.dows.hep.service.FoodCookbookNutrientService;
import org.springframework.stereotype.Service;


/**
 * 食谱成分(FoodCookbookNutrient)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:12
 */
@Service("foodCookbookNutrientService")
public class FoodCookbookNutrientServiceImpl extends MybatisCrudServiceImpl<FoodCookbookNutrientMapper, FoodCookbookNutrientEntity> implements FoodCookbookNutrientService {

}

