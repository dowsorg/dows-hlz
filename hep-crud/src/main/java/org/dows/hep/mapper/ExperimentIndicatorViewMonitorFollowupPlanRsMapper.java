package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorViewMonitorFollowupPlanRsEntity;

/**
 * (ExperimentIndicatorViewMonitorFollowupPlanRs)表数据库访问层
 *
 * @author lait
 * @since 2023-07-24 14:55:16
 */
@Mapper
public interface ExperimentIndicatorViewMonitorFollowupPlanRsMapper extends MybatisCrudMapper<ExperimentIndicatorViewMonitorFollowupPlanRsEntity> {

}

