package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentViewBaseInfoDescrEntity;

/**
 * @author jx
 * @date 2023/6/6 11:23
 */
@Mapper
public interface ExperimentViewBaseInfoDescrMapper extends MybatisCrudMapper<ExperimentViewBaseInfoDescrEntity> {
}
