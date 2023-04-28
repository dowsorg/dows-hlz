package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.SportItemIndicatorEntity;

/**
 * 运动项目关联指标(SportItemIndicator)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:53
 */
@Mapper
public interface SportItemIndicatorMapper extends MybatisCrudMapper<SportItemIndicatorEntity> {

}

