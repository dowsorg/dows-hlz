package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentViewBaseInfoMonitorContentEntity;

/**
 * @author jx
 * @date 2023/6/6 15:21
 */
@Mapper
public interface ExperimentViewBaseInfoMonitorContentMapper extends MybatisCrudMapper<ExperimentViewBaseInfoMonitorContentEntity> {
}
