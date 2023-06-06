package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentViewBaseInfoSingleEntity;

/**
 * @author jx
 * @date 2023/6/6 11:47
 */
@Mapper
public interface ExperimentViewBaseInfoSingleMapper extends MybatisCrudMapper<ExperimentViewBaseInfoSingleEntity> {
}
