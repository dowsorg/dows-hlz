package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorValEntity;

/**
 * 指标值(IndicatorVal)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:57:46
 */
@Mapper
public interface IndicatorValMapper extends MybatisCrudMapper<IndicatorValEntity> {

}

