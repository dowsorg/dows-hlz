package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorViewBaseInfoDescRsEntity;

/**
 * 查看指标基本信息类(ExperimentIndicatorViewBaseInfoDescrMapper)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:50
 */
@Mapper
public interface ExperimentIndicatorViewBaseInfoDescrRsMapper extends MybatisCrudMapper<ExperimentIndicatorViewBaseInfoDescRsEntity> {

}

