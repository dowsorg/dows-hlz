package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonHealthProblemEntity;

/**
 * @author jx
 * @date 2023/5/29 14:32
 */
@Mapper
public interface ExperimentPersonHealthProblemMapper extends MybatisCrudMapper<ExperimentPersonHealthProblemEntity> {
}
