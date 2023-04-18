package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.FoodDishesEntity;

/**
 * 菜肴(FoodDishes)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:56:41
 */
@Mapper
public interface FoodDishesMapper extends MybatisCrudMapper<FoodDishesEntity> {

}

