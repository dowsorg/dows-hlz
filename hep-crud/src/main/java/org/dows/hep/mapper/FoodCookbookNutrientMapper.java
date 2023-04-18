package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.FoodCookbookNutrientEntity;

/**
 * 食谱成分(FoodCookbookNutrient)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:56:36
 */
@Mapper
public interface FoodCookbookNutrientMapper extends MybatisCrudMapper<FoodCookbookNutrientEntity> {

}

