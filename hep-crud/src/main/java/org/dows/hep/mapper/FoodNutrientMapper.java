package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.FoodNutrientEntity;

/**
 * 食物成分(FoodNutrient)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:49
 */
@Mapper
public interface FoodNutrientMapper extends MybatisCrudMapper<FoodNutrientEntity> {

}

