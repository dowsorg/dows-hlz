package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentViewBaseInfoMonitorEntity;

/**
 * @author jx
 * @date 2023/6/6 11:37
 */
@Mapper
public interface ExperimentViewBaseInfoMonitorMapper extends MybatisCrudMapper<ExperimentViewBaseInfoMonitorEntity> {
}
