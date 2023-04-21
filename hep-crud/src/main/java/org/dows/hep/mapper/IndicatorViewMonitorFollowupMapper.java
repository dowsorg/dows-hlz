package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewMonitorFollowupEntity;

/**
 * 查看指标监测随访类(IndicatorViewMonitorFollowup)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface IndicatorViewMonitorFollowupMapper extends MybatisCrudMapper<IndicatorViewMonitorFollowupEntity> {

}

