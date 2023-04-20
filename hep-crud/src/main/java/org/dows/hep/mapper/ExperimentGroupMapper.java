package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentGroupEntity;

/**
 * 实验小组(ExperimentGroup)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:55:36
 */
@Mapper
public interface ExperimentGroupMapper extends MybatisCrudMapper<ExperimentGroupEntity> {

}

