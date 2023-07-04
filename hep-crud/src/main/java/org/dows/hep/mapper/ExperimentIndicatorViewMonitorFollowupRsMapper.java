package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorViewMonitorFollowupRsEntity;
import org.dows.hep.entity.IndicatorViewMonitorFollowupEntity;

/**
 * 查看指标监测随访类(IndicatorViewMonitorFollowup)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:50
 */
@Mapper
public interface ExperimentIndicatorViewMonitorFollowupRsMapper extends MybatisCrudMapper<ExperimentIndicatorViewMonitorFollowupRsEntity> {

}

