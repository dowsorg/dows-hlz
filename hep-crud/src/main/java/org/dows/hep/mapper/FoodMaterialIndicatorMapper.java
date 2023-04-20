package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.FoodMaterialIndicatorEntity;

/**
 * 食材关联指标(FoodMaterialIndicator)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:56:56
 */
@Mapper
public interface FoodMaterialIndicatorMapper extends MybatisCrudMapper<FoodMaterialIndicatorEntity> {

}

