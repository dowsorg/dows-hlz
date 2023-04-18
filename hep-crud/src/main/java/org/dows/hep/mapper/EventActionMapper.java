package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.EventActionEntity;

/**
 * 突发事件处理选项(EventAction)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:54:34
 */
@Mapper
public interface EventActionMapper extends MybatisCrudMapper<EventActionEntity> {

}

