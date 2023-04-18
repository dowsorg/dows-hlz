package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorCategoryEntity;

/**
 * 指标类别(IndicatorCategory)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:57:10
 */
@Mapper
public interface IndicatorCategoryMapper extends MybatisCrudMapper<IndicatorCategoryEntity> {

}

