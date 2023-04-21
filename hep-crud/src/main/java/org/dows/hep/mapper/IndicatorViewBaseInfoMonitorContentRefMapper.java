package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorContentRefEntity;

/**
 * 指标基本信息监测内容表与指标关联关系(IndicatorViewBaseInfoMonitorContentRef)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface IndicatorViewBaseInfoMonitorContentRefMapper extends MybatisCrudMapper<IndicatorViewBaseInfoMonitorContentRefEntity> {

}

