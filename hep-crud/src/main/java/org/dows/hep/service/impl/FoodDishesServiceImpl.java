package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodDishesMapper;
import org.dows.hep.entity.FoodDishesEntity;
import org.dows.hep.service.FoodDishesService;
import org.springframework.stereotype.Service;


/**
 * 菜肴(FoodDishes)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:12
 */
@Service("foodDishesService")
public class FoodDishesServiceImpl extends MybatisCrudServiceImpl<FoodDishesMapper, FoodDishesEntity> implements FoodDishesService {

}

