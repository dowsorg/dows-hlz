package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoDescrRefEntity;

/**
 * 指标基本信息描述表与指标关联关系(IndicatorViewBaseInfoDescrRef)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:50
 */
@Mapper
public interface IndicatorViewBaseInfoDescrRefMapper extends MybatisCrudMapper<IndicatorViewBaseInfoDescrRefEntity> {

}

