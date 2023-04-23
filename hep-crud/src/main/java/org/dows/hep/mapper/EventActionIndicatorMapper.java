package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.EventActionIndicatorEntity;

/**
 * 事件处理选项影响指标(EventActionIndicator)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:15
 */
@Mapper
public interface EventActionIndicatorMapper extends MybatisCrudMapper<EventActionIndicatorEntity> {

}

