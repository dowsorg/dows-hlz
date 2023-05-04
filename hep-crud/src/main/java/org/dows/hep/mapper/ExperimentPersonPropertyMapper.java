package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonPropertyEntity;

/**
 * 实验人物数据(ExperimentPersonProperty)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:47
 */
@Mapper
public interface ExperimentPersonPropertyMapper extends MybatisCrudMapper<ExperimentPersonPropertyEntity> {

}

