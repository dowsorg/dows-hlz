package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentGroupResourceEntity;

/**
 * 实验小组资源(ExperimentGroupResource)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:45
 */
@Mapper
public interface ExperimentGroupResourceMapper extends MybatisCrudMapper<ExperimentGroupResourceEntity> {

}

