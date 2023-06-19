package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorViewBaseInfoMonitorRsEntity;

/**
 * 指标基本信息监测表(ExperimentIndicatorViewBaseInfoMonitorMapper)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:50
 */
@Mapper
public interface ExperimentIndicatorViewBaseInfoMonitorRsMapper extends MybatisCrudMapper<ExperimentIndicatorViewBaseInfoMonitorRsEntity> {

}

