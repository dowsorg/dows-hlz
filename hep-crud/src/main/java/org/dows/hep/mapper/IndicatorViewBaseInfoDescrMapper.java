package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoDescrEntity;

/**
 * 指标基本信息描述表(IndicatorViewBaseInfoDescr)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:57:53
 */
@Mapper
public interface IndicatorViewBaseInfoDescrMapper extends MybatisCrudMapper<IndicatorViewBaseInfoDescrEntity> {

}

