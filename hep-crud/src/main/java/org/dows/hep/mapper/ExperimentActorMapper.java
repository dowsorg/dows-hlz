package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentActorEntity;

/**
 * 实验扮演者(ExperimentActor)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:55:24
 */
@Mapper
public interface ExperimentActorMapper extends MybatisCrudMapper<ExperimentActorEntity> {

}

