package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoSingleEntity;

/**
 * 指标基本信息与单一指标关系表(IndicatorViewBaseInfoSingle)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:58:06
 */
@Mapper
public interface IndicatorViewBaseInfoSingleMapper extends MybatisCrudMapper<IndicatorViewBaseInfoSingleEntity> {

}

