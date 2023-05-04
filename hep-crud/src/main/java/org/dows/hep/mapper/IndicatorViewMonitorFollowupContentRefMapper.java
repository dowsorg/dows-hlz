package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewMonitorFollowupContentRefEntity;

/**
 * 指标监测随访随访内容表与指标关联关系(IndicatorViewMonitorFollowupContentRef)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:50
 */
@Mapper
public interface IndicatorViewMonitorFollowupContentRefMapper extends MybatisCrudMapper<IndicatorViewMonitorFollowupContentRefEntity> {

}

