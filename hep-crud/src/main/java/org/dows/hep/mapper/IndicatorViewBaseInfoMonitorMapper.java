package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorEntity;

/**
 * 指标基本信息监测表(IndicatorViewBaseInfoMonitor)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:57:58
 */
@Mapper
public interface IndicatorViewBaseInfoMonitorMapper extends MybatisCrudMapper<IndicatorViewBaseInfoMonitorEntity> {

}

