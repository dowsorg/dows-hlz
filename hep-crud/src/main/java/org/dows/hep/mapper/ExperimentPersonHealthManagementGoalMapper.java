package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentPersonHealthManagementGoalEntity;

/**
 * @author jx
 * @date 2023/5/29 17:21
 */
@Mapper
public interface ExperimentPersonHealthManagementGoalMapper extends MybatisCrudMapper<ExperimentPersonHealthManagementGoalEntity> {
}
