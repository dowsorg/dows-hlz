package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.SportPlanItemsEntity;

/**
 * 运动方案项目列表(SportPlanItems)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:16
 */
@Mapper
public interface SportPlanItemsMapper extends MybatisCrudMapper<SportPlanItemsEntity> {

}

