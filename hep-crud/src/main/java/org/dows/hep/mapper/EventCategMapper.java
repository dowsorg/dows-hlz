package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.EventCategEntity;

/**
 * 事件类别管理(EventCateg)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:54:35
 */
@Mapper
public interface EventCategMapper extends MybatisCrudMapper<EventCategEntity> {

}

