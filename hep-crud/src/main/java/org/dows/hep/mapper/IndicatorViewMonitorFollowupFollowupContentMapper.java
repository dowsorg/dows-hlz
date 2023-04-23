package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewMonitorFollowupFollowupContentEntity;

/**
 * 查看指标监测随访内容(IndicatorViewMonitorFollowupFollowupContent)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface IndicatorViewMonitorFollowupFollowupContentMapper extends MybatisCrudMapper<IndicatorViewMonitorFollowupFollowupContentEntity> {

}

