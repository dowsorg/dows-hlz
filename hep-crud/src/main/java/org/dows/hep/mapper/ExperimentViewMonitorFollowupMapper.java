package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentViewMonitorFollowupEntity;

/**
 * @author jx
 * @date 2023/6/6 19:25
 */
@Mapper
public interface ExperimentViewMonitorFollowupMapper extends MybatisCrudMapper<ExperimentViewMonitorFollowupEntity> {
}
