package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentInstanceEntity;

/**
 * 实验实列(ExperimentInstance)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:55:40
 */
@Mapper
public interface ExperimentInstanceMapper extends MybatisCrudMapper<ExperimentInstanceEntity> {

}

