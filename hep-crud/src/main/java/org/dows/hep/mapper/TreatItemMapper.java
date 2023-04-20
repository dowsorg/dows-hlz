package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.TreatItemEntity;

/**
 * 治疗项目(TreatItem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 14:00:01
 */
@Mapper
public interface TreatItemMapper extends MybatisCrudMapper<TreatItemEntity> {

}

