package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentViewBaseInfoDescrRefEntity;

/**
 * @author jx
 * @date 2023/6/6 14:09
 */
@Mapper
public interface ExperimentViewBaseInfoDescrRefMapper extends MybatisCrudMapper<ExperimentViewBaseInfoDescrRefEntity> {
}
