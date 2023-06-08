package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentViewMonitorFollowupRecordEntity;

/**
 * @author jx
 * @date 2023/6/7 17:00
 */
@Mapper
public interface ExperimentViewMonitorFollowupRecordMapper extends MybatisCrudMapper<ExperimentViewMonitorFollowupRecordEntity> {
}
